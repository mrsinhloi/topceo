package com.topceo.post

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.bumptech.glide.Glide
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.db.TinyDB
import com.topceo.fragments.Fragment_1_Home_User
import com.topceo.fragments.GlideCircleTransform
import com.topceo.group.models.GroupInfo
import com.topceo.link_preview_2.LinkPreviewCallback
import com.topceo.link_preview_2.SourceContent
import com.topceo.link_preview_2.TextCrawler
import com.topceo.objects.image.ImageItem
import com.topceo.objects.image.Item
import com.topceo.objects.image.ItemData
import com.topceo.objects.image.LinkPreview
import com.topceo.objects.other.User
import com.topceo.profile.Fragment_5_User_Profile_Grid
import com.topceo.retrofit.ParserWc
import com.topceo.retrofit.PostImageParam
import com.topceo.services.ReturnResult
import com.topceo.socialspost.common.DialogUtils
import com.topceo.socialspost.facebook.Page
import com.topceo.socialspost.facebook.PermissionRequest
import com.topceo.utils.MyUtils
import com.topceo.utils.ProgressUtils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.JsonObject
import com.permissionx.guolindev.PermissionX
import com.smartapp.collage.CollageAdapterUrls
import com.smartapp.collage.MediaLocal
import com.smartapp.collage.OnItemClickListener
import com.smartapp.collage.RegexUtils
import com.smartapp.post_like_facebook.EditImageActivity
import com.sonhvp.utilities.standard.isNotBlankAndNotEmpty
import com.topceo.profile.Fragment_Profile_Owner
import com.workchat.core.chat.locations.MyLocation
import com.workchat.core.chat.locations.SearchLocationActivity
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import io.github.ponnamkarthik.richlinkpreview.MetaData
import kotlinx.android.synthetic.main.activity_post_like_facebook.*
import kotlinx.android.synthetic.main.layout_header.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class PostLikeFacebookActivity : AppCompatActivity() {
    val context: Context = this
    var groupId: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_like_facebook)
        initHeader()
        initUI()

        groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
        if (groupId < 0) {
            groupId = 0
        }
        txtDes.requestFocus()

        textCrawler = TextCrawler()
    }


    private var selectedUriList: ArrayList<Uri>? = null
    fun isHaveImages(): Boolean {
        return !selectedUriList.isNullOrEmpty()
    }

    fun initSelectImage() {
        permissionCamera()
    }


    var selectLocation: MyLocation? = null
    val EDIT_LIST_IMAGE_CODE = 11
    val ACTION_SELECT_LOCATION = 12
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callbackManager != null) {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EDIT_LIST_IMAGE_CODE -> {
                    selectedUriList =
                        data?.extras?.getParcelableArrayList(EditImageActivity.LIST_ITEM)
                    loadImages()
                }
                ACTION_SELECT_LOCATION -> {
                    val b = data!!.extras
                    if (b != null) {
                        val location: MyLocation? = b.getParcelable(MyLocation.MY_LOCATION)
                        if (location != null) {
                            selectLocation = location
                            setLocation()
                        }
                    }
                }
            }
        } else {
            txtDes.setHint(getString(R.string.are_you_thinking))
        }
    }


    fun loadImages() {
        if (isHaveImages()) {
            txtDes.setHint(getString(R.string.write_description_images))
            isParsedLink = true
            val list: List<MediaLocal> = MediaLocal.getListMediaLocal(selectedUriList, context)
            collageAdapter = CollageAdapterUrls(context, list)
            collageAdapter!!.onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val intent = Intent(context, EditImageActivity::class.java)
                    intent.putParcelableArrayListExtra(EditImageActivity.LIST_ITEM, selectedUriList)
                    intent.putExtra(EditImageActivity.ITEM_POSITION, position)
                    startActivityForResult(intent, EDIT_LIST_IMAGE_CODE)
                }
            }
            rvCollage.adapter = collageAdapter


            /*val b = Compressor(context)
                    .setMaxWidth(ImageSize.ORIGINAL_WIDTH)
                    .setMaxHeight(ImageSize.ORIGINAL_HEIGHT)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(File(list.get(0).path))
            imgDemo.setImageBitmap(b)*/

        } else {
//            feed_post.images = emptyList()
            rvCollage.adapter = null
        }
    }


    private var item: ImageItem? = null
    var collageAdapter: CollageAdapterUrls? = null
    lateinit var postUtils: PostUtils
    fun initUI() {

        //data
        restorePost()

        //UI
        postUtils = PostUtils(context)
        imgBack.setOnClickListener { finish() }
        txtShare.setOnClickListener {
            if (isEdit) {
                updateDescription()
            } else {//post moi
                uploadImages()
            }
        }
        txtDes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val link = RegexUtils.getList(p0?.toString(), RegexUtils.URL_REGEX)
                if (link.size > 0) {
                    var url = link.get(0).toLowerCase()
                    val isValid = Patterns.WEB_URL.matcher(url).find()
                    if (isValid) {

                        //neu link nay chua parse hoac doi link moi thi moi parse lai
                        if (parsedLink.isEmpty() || !parsedLink.contains(url)) {
                            //thu vien loi neu ko co http:// https://github.com/ponnamkarthik/RichLinkPreview
                            if (
                                !url.contains(refix1, true) &&
                                !url.contains(refix2, true)
                            ) {
                                url = refix1 + url
                            }
                            parseLink(url)
                        }
                    }
                } else {
                    //tuc la ko co link
                    if (findViewById<RelativeLayout>(R.id.layoutPreviewLink).visibility == View.VISIBLE) {
                        findViewById<ImageView>(R.id.imgClosePreview).performClick()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        //bottom controls
        if (isEdit) {
            //an chon hinh
            imgPhoto.visibility = View.GONE

            imgLocation.setOnClickListener { selectLocation() }
            txtAddPhoto.setOnClickListener { imgLocation.performClick() }
        } else {
            imgPhoto.setOnClickListener { initSelectImage() }
            txtAddPhoto.setOnClickListener { imgPhoto.performClick() }
            imgLocation.setOnClickListener { selectLocation() }
        }


        //share facebook page
        initSharePage()

    }

    fun uploadImages() {
        if (isHaveImages()) {
            if (MyUtils.checkInternetConnection(context)) {
                postUtils.uploadImageToServer(
                    groupId,
                    selectedUriList,
                    object : UploadImageListener {
                        override fun onUploadImageSuccess(
                            GUID: String?,
                            itemContent: java.util.ArrayList<Item>?
                        ) {
                            //co image thi ko truyen phan parse link
                            postToServer(itemContent, GUID, null, groupId)
                        }

                    })
            } else {
                MyUtils.showThongBao(context)
            }
        } else {
            //post 1 text hoac 1 link
            val textOrLink = txtDes.text.toString()
            if (textOrLink.isNotBlankAndNotEmpty()) {
                var itemData: ItemData? = getItemData()

                //chuan bi lay duong dan sas truoc khi upload anh len azure
                val GUID = UUID.randomUUID().toString().toLowerCase()
                postToServer(ArrayList<Item>(), GUID, itemData, groupId)
            }
        }
    }

    private fun getItemData(): ItemData? {
        var itemData: ItemData? = null
        if (linkData != null) {
            //link
            val preview = LinkPreview()
            preview.link = link
            preview.title = linkData?.title
            preview.caption = linkData?.description
            preview.image = linkData?.imageurl
            preview.siteName = linkData?.sitename

            //data
            itemData = ItemData()
            itemData.linkPreview = preview

        }
        return itemData
    }

    //{"ErrorCode":0,"Message":"","Data":{}}
    private fun parseJson(response: String) {
        if (!TextUtils.isEmpty(response)) {
            val result = ParserWc.parseJson(response, ImageItem::class.java, false)
            if (result != null) {
                if (result.errorCode == ReturnResult.SUCCESS) {
                    //upload thanh cong
                    MyUtils.showToast(context, R.string.toast_upload_success)
                    val item = result.data as ImageItem
                    uploadSuccess(true, item)
                } else {
                    MyUtils.showAlertDialog(context, result.message)
                }
            }
        }
    }

    private fun uploadSuccess(isFinish: Boolean, item: ImageItem) {
        try {
            //refresh onefragment
            sendBroadcast(Intent(Fragment_1_Home_User.ACTION_REFRESH))
            sendBroadcast(Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST))

            //Neu post moi thi tang so luong post trong profile
            if (!isEdit) {
                sendBroadcast(Intent(Fragment_Profile_Owner.ACTION_WHEN_HAVE_POST))
            }


            if (groupId > 0) {
                setResult(RESULT_OK)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (switchFacebook.isChecked) {
            show(R.string.facebook_posting)

            if (item.itemContent.size > 1) {
                postMultiPhotosUrl(item)
            } else {
                val url = item.imageLarge
                postPhotoUrl(item.description, url)
            }

        } else {
            if (isFinish) {
                finish()
            }
        }
    }

    var refix1 = "http://"
    var refix2 = "https://"
    var link: String? = null
    var linkData: MetaData? = null
    var isParsedLink: Boolean = false
    lateinit var textCrawler: TextCrawler
    fun parseLink(url: String) {
        //chi parse 1 lan
        if (!isParsedLink) {
            /*val richPreview = RichPreview(object : ResponseListener {
                override fun onData(metaData: MetaData) {
                    isParsedLink = true

                    //Implement your Layout
                    linkData = metaData
                    showUIPreview(metaData)

                    ProgressUtils.hide()

                    //edit post
                    if (item != null) {
                        var itemData: ItemData? = getItemData()
                        if (itemData != null) {
                            item?.itemData = itemData
                        }
                    }

                }

                override fun onError(e: java.lang.Exception?) {
                    //handle error
                    isParsedLink = false
                    link = null
                    ProgressUtils.hide()
                }
            })
            richPreview.getPreview(url)
            link = url
            ProgressUtils.show(context)*/

            textCrawler.makePreview(object : LinkPreviewCallback {
                override fun onPre() {
                    ProgressUtils.show(context)
                }

                override fun onPos(content: SourceContent?, isNull: Boolean) {
                    if (isNull || content!!.finalUrl.equals("")) {
                        //ko parse dc
                        isParsedLink = false
                        link = null
                        ProgressUtils.hide()
                    } else {
                        //set ChatLog 1 guid truoc, chut co socket updatemessage tra ve thi so sanh de update
                        ProgressUtils.hide()
                        if (content.title.isNotEmpty()) {
                            isParsedLink = true

                            //Implement your Layout
                            linkData = MetaData()
                            linkData!!.title = content.title
                            linkData!!.description = content.description
                            linkData!!.url = content.url
                            if (content.images != null && content.images.size > 0) {
                                linkData!!.imageurl = content.images[0]
                            }
                            showUIPreview(linkData!!)

                            //edit post
                            if (item != null) {
                                var itemData: ItemData? = getItemData()
                                if (itemData != null) {
                                    item?.itemData = itemData
                                }
                            }
                        }
                    }
                }

            }, url)
            link = url


        }
    }

    private fun showUIPreview(metaData: MetaData) {
        val sitename = metaData.sitename
        val title = metaData.title
        val description = metaData.description
        val imgUrl = metaData.imageurl

        //set layout
        findViewById<RelativeLayout>(R.id.layoutPreviewLink).visibility = View.VISIBLE
        findViewById<TextView>(R.id.txt1Preview).text = title
        findViewById<TextView>(R.id.txt2Preview).text = description

        val image = findViewById<ImageView>(R.id.imgPreview)
        if (imgUrl.isNotEmpty()) {
            image.visibility = View.VISIBLE
            val width = MyUtils.getScreenWidth(context)
            val height = resources.getDimensionPixelOffset(R.dimen.imgPreviewHeight)
            Glide.with(context)
                .load(imgUrl)
                .override(width, height)
                .centerCrop()
                .placeholder(R.drawable.no_media)
                .into(image)
        } else {
            image.visibility = View.GONE
        }

        findViewById<ImageView>(R.id.imgClosePreview).setOnClickListener {
            findViewById<RelativeLayout>(R.id.layoutPreviewLink).visibility = View.GONE
            linkData = null
            link = null
        }
    }


    fun initHeader() {
        imgMenu2.visibility = View.GONE
        imgVip.visibility = View.GONE

        //user
        val db = TinyDB(this)
        val obj = db.getObject(User.USER, User::class.java)
        if (obj != null) {
            val user = obj as User
            val width = context.resources.getDimensionPixelOffset(R.dimen.avatar_size_small)
            //set avatar
            Glide.with(context)
                .load(user.getAvatarSmall())
                .placeholder(R.drawable.ic_no_avatar)
                .override(width, width)
                .transform(GlideCircleTransform(context))
                .into(imageView1)

            textView1.text = user.userName
            textView2.visibility = View.GONE
        }

    }


    var isEdit: Boolean = false
    var parsedLink: String = ""
    fun restorePost() {
//        val b = intent.extras
//        if (b != null) {
        item = MyApplication.imgEdit //b.getParcelable<Parcelable>(ImageItem.IMAGE_ITEM) as ImageItem?
        if (item != null) {
            isEdit = true

            //text
            txtTitle.text = getText(R.string.edit)
            txtDes.setText(MyUtils.fromHtml(item?.description))
            if (!TextUtils.isEmpty(item!!.location)) {
                textView2.setText(item!!.location)
                textView2.setVisibility(View.VISIBLE)
            } else {
                textView2.setVisibility(View.GONE)
            }

            //link neu co
            if (item?.itemData != null) {
                val link = item?.itemData?.linkPreview
                if (link != null) {
                    linkData = MetaData()
                    linkData?.apply {
                        title = link.title
                        url = link.link
                        sitename = link.siteName
                        imageurl = link.image

                        //link da parse
                        parsedLink = link.link
                    }
                    showUIPreview(linkData!!)
                }
            }


            //view lai image, nhung khong cho click va thay doi
            rvCollage.isEnabled = false
            txtDes.setHint(getString(R.string.write_description_images))
            val list = item?.getListMediaLocal()
            if (list != null && list.size > 0) {
                isParsedLink = true
                collageAdapter = CollageAdapterUrls(context, list)
                rvCollage.adapter = collageAdapter
                collageAdapter!!.onItemClickListener = null
            }


            //restore location
            if (!item?.location.isNullOrEmpty()) {
                selectLocation = MyLocation()
                selectLocation?.apply {
                    address = item!!.location
                    lat = item!!.lat
                    lon = item!!.long
                    setLocation()
                }

            } else {
                textView2.visibility = View.GONE
            }

        }
//        }
    }


    private fun selectLocation() {
        val intent = Intent(context, SearchLocationActivity::class.java)
        startActivityForResult(intent, ACTION_SELECT_LOCATION)
    }

    private fun setLocation() {
        if (selectLocation != null) {
            if (!selectLocation?.address.isNullOrEmpty()) {
                textView2.text = selectLocation?.address
                textView2.visibility = View.VISIBLE
                textView2.setOnClickListener { selectLocation() }

                //clear
                imgClearLocation.visibility = View.VISIBLE
                imgClearLocation.setOnClickListener {
                    selectLocation = null
                    textView2.text = ""
                    imgClearLocation.visibility = View.GONE

                    //neu la dang edit thi xoa bo vi tri da chon
                    if (item != null) {
                        item?.location = ""
                        item?.lat = 0.0
                        item?.long = 0.0
                    }
                }
            }


        }
    }


    private fun postToServer(
        itemContent: ArrayList<Item>?,
        GUID: String?,
        itemData: ItemData?,
        groupId: Long
    ) {
        if (itemContent != null) {
            ProgressUtils.show(context)
            val itemType: String = ImageItem.ITEM_TYPE_FACEBOOK
            //upload hinh xong thi upload cac thong tin con lai
            var text = txtDes.text.toString()
            text = MyUtils.replaceDescriptionForServer(text)
            val post = PostImageParam(
                GUID,
                false,
                text,
                if (selectLocation != null) selectLocation!!.address else "",
                if (selectLocation != null) selectLocation!!.lat else 0.0,
                if (selectLocation != null) selectLocation!!.lon else 0.0,
                itemContent,
                txtDes.getHashtags(),
                txtDes.getMentions(),
                itemType,
                itemData
            )
            if (groupId > 0) {
                post.groupId = groupId
            }
            MyApplication.apiManager.addImageItem(post, object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    val obj = response.body()
                    parseJson(obj.toString())
                    ProgressUtils.hide()
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    ProgressUtils.hide()
                }
            })
        }
    }

    private fun updateDescription() {
        if (item != null) {
            val isLocationChange =
                selectLocation != null && !item?.location.equals(selectLocation?.address)
            //neu mota khac nhau thi moi update
            val txt: String = txtDes.text.toString()
            item?.description = txt
            if (selectLocation != null) {
                item?.location = selectLocation?.address
                item?.lat = selectLocation!!.lat
                item?.long = selectLocation!!.lon
            }
            postUtils.updateDescriptionAndLocation(
                item,
                txt,
                if (selectLocation != null) selectLocation!!.address else "",
                if (selectLocation != null) selectLocation!!.lat else 0.0,
                if (selectLocation != null) selectLocation!!.lon else 0.0,
                txtDes.getHashtags(),
                txtDes.getMentions()
            ) {
                MyUtils.hideKeyboard(this)
                //finish
                finish()
            }


        }
    }


    private val perms = arrayOf(Manifest.permission.CAMERA)
    private fun permissionCamera() {
        PermissionX.init(this)
            .permissions(*perms)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    getString(R.string.deny_permission_notify),
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(R.string.deny_permission_notify),
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    TedImagePicker.with(this)
                        .mediaType(MediaType.IMAGE)
                        .dropDownAlbum()
                        .errorListener { message -> Log.d("ted", "message: $message") }
                        .selectedUri(selectedUriList)
                        .startMultiImage { list: List<Uri> ->
                            selectedUriList = ArrayList(list)
                            loadImages()
                        }
                } else {
                    MyUtils.showAlertDialog(this, R.string.deny_permission_notify, true)
                }
            }
    }


    //POST PAGE FACEBOOK//////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    lateinit var callbackManager: CallbackManager
    private fun initSharePage() {
        callbackManager = CallbackManager.Factory.create()
        //checbox facebook
        switchFacebook.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                loginIfNot()
            }
        }
        setTextPages()
    }

    private fun setTextPages() {
        if (selectedPages.size == 0) {
            txtPages.setText(getString(R.string.to_pages, ""))
            txtPages.visibility = View.GONE
        } else {
            var pages = ""
            for (item in selectedPages) {
                if (pages == "") {
                    pages = item.name
                } else {
                    pages = pages + ", " + item.name
                }
            }
            txtPages.setText(getString(R.string.to_pages, pages))
            txtPages.visibility = View.VISIBLE
        }

    }


    private fun loginIfNot() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (!isLoggedIn) {
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code
//                            val token = loginResult.accessToken
                        getProfile()
                    }

                    override fun onCancel() {
                        // App code
                        switchFacebook.setChecked(false)
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        switchFacebook.setChecked(false)
                    }
                })
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList(*PermissionRequest.PERMISSION_PAGE_AND_USER_PROFILE)
            )
        } else {
            getProfile()
        }
    }


    private fun getProfile() {
        val profile = Profile.getCurrentProfile()
        if (profile != null) {
            MyApplication.facebookApi.userId = profile.id
            val accessToken = AccessToken.getCurrentAccessToken()
            MyApplication.facebookApi.accessToken = accessToken
            getPages()
        } else {
            //Do lay asyn nen chua tra ve thong tin profile, track de don thong tin tra ve
            val profileTracker: ProfileTracker = object : ProfileTracker() {
                override fun onCurrentProfileChanged(
                    oldProfile: Profile?,
                    currentProfile: Profile?
                ) {
                    stopTracking()
                    Profile.setCurrentProfile(currentProfile)
                    getProfile()
                }

            }
            profileTracker.startTracking()
        }
    }

    private fun postPhotoUrl(message: String, url: String) {
//        String url = "https://photo2.tinhte.vn/data/attachment-files/2020/07/5069141_09B406D8-CA87-4A41-9BE8-853AB83C5879.jpeg";
        var currentPage: Page? = null
        if (selectedPages.size > 0) {
            currentPage = selectedPages.get(0)
            selectedPages.removeAt(0)
        }
        if (currentPage != null) {
            MyApplication.facebookApi.postPhotoUrl(
                message,
                url,
                currentPage.id,
                currentPage.access_token,
                true
            ) { response ->
                if (response.error == null) {
                    postPhotoUrl(message, url)
                } else {
                    hide()
                }
            }
        } else {
            MyUtils.showToast(context, R.string.post_facebook_success)
            hide()
            finish()
        }
    }

    private fun postMultiPhotosUrl(post: ImageItem) {
//        String url = "https://photo2.tinhte.vn/data/attachment-files/2020/07/5069141_09B406D8-CA87-4A41-9BE8-853AB83C5879.jpeg";
        var currentPage: Page? = null
        if (selectedPages.size > 0) {
            currentPage = selectedPages.get(0)
            selectedPages.removeAt(0)
        }
        if (currentPage != null) {

            try {//post tung hinh luu lai id
                var ids = java.util.ArrayList<String>()
                for (item in post.itemContent) {
                    val url = item.large.link
                    MyApplication.facebookApi.postPhotoUrl(
                        post.description,
                        url,
                        currentPage.id,
                        currentPage.access_token,
                        false
                    ) { response ->
                        if (response.error == null) {
                            val obj = response.jsonObject
                            val id = obj.getString("id")
                            ids.add(id)

                            //neu la vi tri cuoi cung thi tiep tuc buoc 2
                            if (ids.size == post.itemContent.size) {
                                //tao feed album
                                MyApplication.facebookApi.postAlbumToPage(
                                    post.description,
                                    ids,
                                    currentPage.id,
                                    currentPage.access_token
                                ) { res ->
                                    if (res.error == null) {
                                        //tiep tuc post page khac
                                        postMultiPhotosUrl(post)
                                    } else {
                                        hide()
                                    }
                                }

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                hide()
            }


        } else {
            hide()
            MyUtils.showToast(context, R.string.post_facebook_success)
            finish()
        }
    }

    private fun postVideo(message: String, url: String) {
        var currentPage: Page? = null
        if (selectedPages != null && selectedPages.size > 0) {
            currentPage = selectedPages.get(0)
            selectedPages.removeAt(0)
        }
        if (currentPage != null) {
            ProgressUtils.show(context, getString(R.string.facebook_posting))
            MyApplication.facebookApi.postVideoUrl(
                message,
                url,
                currentPage.id,
                currentPage.access_token
            ) { response ->
                if (response.error == null) {
                    ProgressUtils.hide()
                    postPhotoUrl(message, url)
                }
            }
        } else {
            MyUtils.showToast(context, R.string.post_facebook_success)
            finish()
        }
    }

    private var selectedPages = java.util.ArrayList<Page>()
    private fun getPages() {
        MyApplication.facebookApi.getPages { response: GraphResponse ->
            if (response.error == null) {
                /* handle the result */
                val list = MyApplication.facebookApi.parseListPage(response.jsonObject)
                if (list.size > 0) {
                    //show dialog user chon 1 page, tam thoi lay page dau tien post len
                    selectedPages = Page.getPageSelected(list, context)
                    setTextPages()
                    //neu chua co page nao chon thi hien thi chon page
                    if (selectedPages.size == 0) {
                        showPageConfig(list)
                    }
                    imgFacebookConfig.setVisibility(View.VISIBLE)
                    imgFacebookConfig.setOnClickListener(View.OnClickListener { //Chon page nao muon dang
                        showPageConfig(list)
                    })
                } else {
                    logoutFacebook()
                    MyUtils.showAlertDialog(context, R.string.user_has_not_page)
                }
            }
        }
    }

    private fun confirmLogoutFacebook() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(R.string.confirm_logout)
        alertDialogBuilder.setPositiveButton(
            R.string.ok,
            DialogInterface.OnClickListener { arg0, arg1 ->
                arg0.dismiss()
                //revoke toan bo quyen roi logout
                ProgressUtils.show(context, getString(R.string.logout))
                PermissionRequest.makeRevokePermissionPage {
                    logoutFacebook()
                    ProgressUtils.hide()
                }
            })
        alertDialogBuilder.setNeutralButton(
            R.string.cancel,
            DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun logoutFacebook() {
        LoginManager.getInstance().logOut()

        //clear cache
        switchFacebook.setChecked(false)
        Page.saveNameSelected(null, null, context)
        selectedPages.clear()
        imgFacebookConfig.setVisibility(View.GONE)
    }

    private fun showPageConfig(list: java.util.ArrayList<Page>) {
        val arrayName = Page.getArrayName(list)
        val checkedItems = Page.getCheckedItems(list, context)
        DialogUtils.selectMultipleItemDialogWithLogoutFacebook(context,
            R.string.facebook_select_page_title,
            arrayName,
            checkedItems,
            object : DialogUtils.SelectMultipleCallbackFacebook {
                override fun onOK(checkedItems: BooleanArray) {
                    Page.saveNameSelected(list, checkedItems, context)
                    //lay lai danh sach da chon
                    selectedPages = Page.getPageSelected(list, context)
                    setTextPages()
                    //khong chon cong dong nao thi off
                    switchFacebook.setChecked(selectedPages.size > 0)
                }

                override fun onLogout() {
                    confirmLogoutFacebook()
                }

                override fun onCancel() {}
            })
    }

    //POST PAGE FACEBOOK//////////////////////////////////////////////////////////////////////////
    var dialog: ACProgressFlower? = null
    fun show(message: String) {
        hide()
        if (!TextUtils.isEmpty(message)) {
            val size = context.resources.getDimensionPixelSize(R.dimen.dialog_text_size)
            dialog = ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(message)
                .textSize(size)
                .fadeColor(Color.TRANSPARENT).build() //DKGRAY
            dialog?.setCanceledOnTouchOutside(true)
            dialog?.show()
        }
    }

    fun show(message: Int) {
        show(getString(message))
    }

    fun hide() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }


}




