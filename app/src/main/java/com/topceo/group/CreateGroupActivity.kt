package com.topceo.group

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.db.TinyDB
import com.topceo.fragments.Fragment_1_Home_SonTung
import com.topceo.group.models.GroupInfo
import com.topceo.objects.image.Item
import com.topceo.objects.other.User
import com.topceo.post.UploadImageListener
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.permissionx.guolindev.PermissionX
import com.sonhvp.utilities.standard.isNotBlankAndNotEmpty
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.android.synthetic.main.activity_all_group.toolbar
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_create_group_sheet_cover.*
import kotlinx.android.synthetic.main.activity_create_group_sheet_privacy.*
import kotlinx.android.synthetic.main.activity_create_group_sheet_private.*
import kotlinx.android.synthetic.main.layout_progress_loading.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateGroupActivity : AppCompatActivity() {
    companion object {
        fun openActivity(context: Context) {
            val intent = Intent(context, CreateGroupActivity::class.java)
            context.startActivity(intent)
        }

        //edit
        fun openActivityEditGroup(context: Context, group: GroupInfo) {
            val intent = Intent(context, CreateGroupActivity::class.java)
            intent.putExtra(GroupInfo.GROUP_INFO, group)
            context.startActivity(intent)
        }
    }

    lateinit var context: Context
    lateinit var user: User
    lateinit var db: TinyDB
    var group: GroupInfo? = null
    var isEdit: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        setSupportActionBar(toolbar)
//        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        setTitle("")

        context = this
        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User

        initUI()

        //edit neu co
        if (intent.hasExtra(GroupInfo.GROUP_INFO)) {
            group = intent.getParcelableExtra(GroupInfo.GROUP_INFO)
            if (group != null) {
                isEdit = true
                initUIEdit(group!!)
            }
        }


    }

    fun initUI() {
        postUtils = GroupCoverUtils(this)
        linearCover.setOnClickListener {
            if (uriImage == null) {
                selectImage()
                showCoverSetting(false)
            } else {
                //todo hien thi xoa hoac them anh cover
                showCoverSetting(true)
            }
        }

        relativePrivacy.setOnClickListener { showPrivacySetting(true) }
        relativeGroupShowHide.setOnClickListener { showPrivateSetting(true) }

        btnCreateGroup.setOnClickListener { createGroup() }

        txtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    btnCreateGroup.isEnabled = s.isNotEmpty()
                }
            }

        })
    }

    var uriImage: Uri? = null
    private val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun selectImage() {
        PermissionX.init(this)
                .permissions(*perms)
                .onExplainRequestReason { scope, deniedList -> scope.showRequestReasonDialog(deniedList, getString(R.string.deny_permission_notify), "OK", "Cancel") }
                .onForwardToSettings { scope, deniedList -> scope.showForwardToSettingsDialog(deniedList, getString(R.string.deny_permission_notify), "OK", "Cancel") }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        TedImagePicker.with(this)
                                .mediaType(MediaType.IMAGE)
                                .dropDownAlbum()
                                .start { uri -> showImage(uri) }
                    } else {
                        MyUtils.showAlertDialog(this, R.string.deny_permission_notify, true)
                    }
                }
    }

    fun showImage(uri: Uri?) {
        uriImage = uri

        if (uri != null) {
            val corners = resources.getDimensionPixelSize(R.dimen.margin_8dp)
            val imgSize = resources.getDimensionPixelSize(R.dimen.cover_preview_size)
            Glide.with(this)
                    .load(uri)
                    .override(imgSize, imgSize)
                    .transform(CenterCrop(), RoundedCorners(corners))
                    .into(imgCover)
            imgCover.setPadding(0, 0, 0, 0)
            txtCover.text = getText(R.string.change_cover_photo)
        } else {
            txtCover.text = getText(R.string.add_cover_photo)
            imgCover.setImageResource(R.drawable.ic_round_add_to_photos_24)
            val padding = resources.getDimensionPixelSize(R.dimen.margin_10dp)
            imgCover.setPadding(padding, padding, padding, padding)
        }
    }


    fun showCoverSetting(isExpand: Boolean) {
        val behavior = BottomSheetBehavior.from(bottomSettingCover)
        behavior.state = if (isExpand) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (isExpand) {
            linearChangeCover.setOnClickListener {
                selectImage()
                showCoverSetting(false)
            }
            linearRemoveCover.setOnClickListener {
                showImage(null)
                showCoverSetting(false)
            }
            bottomSettingCover.setOnClickListener { showCoverSetting(false) }
        }
    }

    fun whenPublic() {
        isPrivate = false
        radioPublic.isChecked = true
        radioPrivate.isChecked = false
        txtPrivateMore.visibility = View.GONE
        showPrivacySelected()

        isHide = false
        showPrivateSelected()
        radioShow.isChecked = true
        radioHide.isChecked = false
    }

    var isPrivate: Boolean = false
    fun showPrivacySetting(isExpand: Boolean) {
        val behavior = BottomSheetBehavior.from(bottomSettingPrivacy)
        behavior.state = if (isExpand) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (isExpand) {

            relativePublic.setOnClickListener {
                whenPublic()
            }
            radioPublic.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) relativePublic.performClick()
            }


            relativePrivate.setOnClickListener {

                isPrivate = true
                radioPublic.isChecked = false
                radioPrivate.isChecked = true
                txtPrivateMore.visibility = View.VISIBLE
                showPrivacySelected()
            }
            radioPrivate.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) relativePrivate.performClick()
            }


            viewBlankPrivacy.setOnClickListener { showPrivacySetting(false) }
            txtDonePrivacy.setOnClickListener {
                showPrivacySetting(false)
            }
            imgClosePrivacy.setOnClickListener { showPrivacySetting(false) }
        }
    }

    fun whenHide() {
        isHide = true
        showPrivateSetting(false)
        showPrivateSelected()
        radioShow.isChecked = false
        radioHide.isChecked = true
    }
    fun whenShow(){
        isHide = false
        showPrivateSetting(false)
        showPrivateSelected()
        radioShow.isChecked = true
        radioHide.isChecked = false
    }

    var isHide: Boolean = false
    fun showPrivateSetting(isExpand: Boolean) {
        val behavior = BottomSheetBehavior.from(bottomSettingPrivate)
        behavior.state = if (isExpand) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (isExpand) {
            relativeShow.setOnClickListener {
                whenShow()
            }

            relativeHide.setOnClickListener {
                whenHide()
            }

            radioShow.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    relativeShow.performClick()
                }
            }
            radioHide.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    relativeHide.performClick()
                }
            }

            viewBlankPrivate.setOnClickListener { showPrivateSetting(false) }
        }
        imgClosePrivate.setOnClickListener { showPrivateSetting(false) }

    }

    fun showPrivateSelected() {
        /*imgPrivateShow.visibility = View.VISIBLE
        txtPrivateShow.text = getText(R.string.group_hide)
        val params = txtPrivateShow.layoutParams as RelativeLayout.LayoutParams
        params.removeRule(RelativeLayout.CENTER_VERTICAL)
        txtPrivateShowBottom.visibility = View.VISIBLE*/

        if (isHide) {
            imgPrivateShow.setImageResource(R.drawable.ic_round_visibility_off_24)
            txtPrivateShowBottom.text = getText(R.string.hide)
        } else {
            imgPrivateShow.setImageResource(R.drawable.ic_round_visibility_24)
            txtPrivateShowBottom.text = getText(R.string.show)
        }

    }

    fun showPrivacySelected() {
        imgPrivacy.visibility = View.VISIBLE
        txtPrivacy.text = getText(R.string.privacy)
        val params = txtPrivacy.layoutParams as RelativeLayout.LayoutParams
        params.removeRule(RelativeLayout.CENTER_VERTICAL)
        txtPrivacyBottom.visibility = View.VISIBLE

        if (isPrivate) {
            imgPrivacy.setImageResource(R.drawable.ic_round_lock_24)
            txtPrivacyBottom.text = getText(R.string.privacy_private)
            linearShowHideGroup.visibility = View.VISIBLE

        } else {
            imgPrivacy.setImageResource(R.drawable.ic_round_public_24)
            txtPrivacyBottom.text = getText(R.string.privacy_public)
            linearShowHideGroup.visibility = View.GONE
        }

        //hien thi nut Done
        txtDonePrivacy.isEnabled = true
        txtDonePrivacy.setTextColor(ContextCompat.getColor(context, R.color.light_blue_500))
    }

    private var postUtils: GroupCoverUtils? = null
    fun createGroup() {
        val name = txtName.text.toString()
        val description = txtDescription.text.toString()

        if (name.isNotBlankAndNotEmpty()) {
            if (uriImage != null) {
                uploadImage(uriImage!!, name, description, isPrivate, isHide)
            } else {
                val guid: String = UUID.randomUUID().toString()
                createGroup(guid, name, description, isPrivate, isHide, "", "")
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    fun showDialogLoading() {
        llProgressBar.visibility = View.VISIBLE
        pbText.text = getText(R.string.group_creating)
    }

    fun hideDialogLoading() {
        llProgressBar.visibility = View.GONE
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    fun uploadImage(uri: Uri, name: String, description: String, isPrivate: Boolean, isHide: Boolean) {
        if (MyUtils.checkInternetConnection(context)) {
            showDialogLoading()
            val list = java.util.ArrayList<Uri>()
            list.add(uri)

            val guid: String = UUID.randomUUID().toString()
            postUtils?.uploadImageToServer(guid, uri, object : UploadImageListener {
                override fun onUploadImageSuccess(GUID: String?, itemContent: java.util.ArrayList<Item>?) {
                    val item = itemContent?.get(0)
                    val cover = item?.large?.link
                    val small = item?.small?.link
                    if (cover != null) {
                        if (small != null) {
                            createGroup(guid, name, description, isPrivate, isHide, cover, small)
                        }
                    }
                }

            })
        } else {
            MyUtils.showThongBao(context)
        }
    }

    fun createGroup(guid: String, name: String, description: String, isPrivate: Boolean, isHide: Boolean, coverUrl: String, coverSmall: String) {
        MyApplication.apiManager.groupCreate(guid, name, description, isPrivate, isHide, coverUrl, coverSmall, object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val obj = response.body()
                parseJson(obj.toString())
                hideDialogLoading()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                hideDialogLoading()
            }

        })
    }

    private fun parseJson(response: String) {
        if (!TextUtils.isEmpty(response)) {
            val result = Webservices.parseJson(response, GroupInfo::class.java, false)
            if (result != null) {
                if (result.errorCode == ReturnResult.SUCCESS) {
                    //refresh lai list home
                    //load lai danh sach group
                    sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))

                    //add vao top man hinh all group
                    var group = result.data as GroupInfo
                    if (group != null) {
                        //group do minh tao ra thi xem nhu da join
                        group.isJoin = GroupInfo.JOINNED

                        val intent = Intent(AllGroupActivity.ACTION_CREATE_NEW_GROUP)
                        intent.putExtra(GroupInfo.GROUP_INFO, group)
                        sendBroadcast(intent)

                        //vao man hinh chi tiet
                        GroupDetailActivity.openActivity(context, group.groupId, true)

                    }

                    finish()

                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    fun initUIEdit(group: GroupInfo) {
        if (isEdit) {
            txtTitle.text = getText(R.string.update)
            linearCover.isEnabled = false
            txtName.setText(group.groupName)
            txtDescription.setText(group.description)
            btnCreateGroup.text = getText(R.string.update)
            btnCreateGroup.setOnClickListener { updateInfo() }

            //set cover
            if (!group.coverSmallUrl.isNullOrEmpty()) {
                val corners = resources.getDimensionPixelSize(R.dimen.margin_8dp)
                val imgSize = resources.getDimensionPixelSize(R.dimen.cover_preview_size)
                Glide.with(this)
                        .load(group.coverSmallUrl)
                        .override(imgSize, imgSize)
                        .transform(CenterCrop(), RoundedCorners(corners))
                        .into(imgCover)
                imgCover.setPadding(0, 0, 0, 0)
                txtCover.text = getText(R.string.cover_photo)
            }


            //PRIVACY
            isPrivate = group.isPrivate
            isHide = group.isHide
            showPrivacySelected()

            if (group.isPrivate) {
                //Neu la private thi khong cho edit privacy isPrivate, show/hide thi cho edit isHide
                relativePrivacy.isEnabled = false
                relativePrivacy.setBackgroundResource(R.drawable.bg_rectangle_rounded_9_fill)
//                relativeGroupShowHide.isEnabled = false
//                relativeGroupShowHide.setBackgroundResource(R.drawable.bg_rectangle_rounded_9_fill)

                if(isHide){
                    whenHide()
                }else{
                    whenShow()
                }

            } else {
                //public cho phep chon ve private
                whenPublic()

            }
        }
    }

    /**
     * Cập nhật title và description và privacy 2 api độc lập
     */
    fun updateInfo() {
        if (group != null) {
            val name = txtName.text.toString()
            val description = txtDescription.text.toString()
            if (name.isNotEmpty()) {

                //#1 udpate info
                if (!group?.groupName.equals(name) || !group?.description.equals(description)) {
                    MyApplication.apiManager.updateInfo(group!!.groupId, name, description, object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            val obj = response.body()
                            hideDialogLoading()
                            val result = Webservices.parseJson(obj.toString(), GroupInfo::class.java, false)
                            if (result != null) {
                                if (result.errorCode == ReturnResult.SUCCESS) {
                                    //refresh lai list home
                                    //load lai danh sach group
                                    sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))

                                    //add vao top man hinh all group
                                    var group = result.data as GroupInfo
                                    if (group != null) {
                                        //group do minh tao ra thi xem nhu da join
                                        group.isJoin = GroupInfo.JOINNED

                                        //update man hinh chi tiet, va allgroup
                                        val intent = Intent(AllGroupActivity.ACTION_REPLACE_GROUP)
                                        intent.putExtra(GroupInfo.GROUP_INFO, group)
                                        sendBroadcast(intent)

                                    }

                                    MyUtils.showToast(context, R.string.update_success)
                                    updatePrivacy(true)
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            hideDialogLoading()
                        }

                    })
                } else {
//                    MyUtils.showToast(context, R.string.info_not_change)
                    //#2 update privacy
                    updatePrivacy(false)
                }


            }
        }

    }

    fun updatePrivacy(isShowSuccessed:Boolean){
        if (group?.isPrivate != isPrivate || group?.isHide != isHide) {
            MyApplication.apiManager.updatePrivacy(group!!.groupId, isPrivate, isHide, object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val obj = response.body()
                    hideDialogLoading()
                    val result = Webservices.parseJson(obj.toString(), GroupInfo::class.java, false)
                    if (result != null) {
                        if (result.errorCode == ReturnResult.SUCCESS) {
                            //refresh lai list home
                            //load lai danh sach group
                            sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))

                            //add vao top man hinh all group
                            var group = result.data as GroupInfo
                            if (group != null) {
                                //group do minh tao ra thi xem nhu da join
                                group.isJoin = GroupInfo.JOINNED

                                //update man hinh chi tiet, va allgroup
                                val intent = Intent(AllGroupActivity.ACTION_REPLACE_GROUP)
                                intent.putExtra(GroupInfo.GROUP_INFO, group)
                                sendBroadcast(intent)

                            }

                            if(!isShowSuccessed){
                                MyUtils.showToast(context, R.string.update_success)
                            }
                            finish()

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    hideDialogLoading()
                }

            })
        }else{
            if(!isShowSuccessed){
                MyUtils.showToast(context, R.string.update_success)
            }
            finish()
        }
    }
}