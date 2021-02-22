package com.topceo.group

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.db.TinyDB
import com.topceo.group.models.GroupInfo
import com.topceo.objects.other.User
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.smartapp.loadmore.OnLoadMoreListener
import com.smartapp.loadmore.RecyclerViewLoadMoreScroll
import kotlinx.android.synthetic.main.activity_all_group.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AllGroupActivity : AppCompatActivity() {
    companion object {
        val ACTION_JOIN_GROUP: String = "ACTION_JOIN_GROUP"
        val ACTION_UPDATE_BANNER: String = "ACTION_UPDATE_BANNER"
        val ACTION_CREATE_NEW_GROUP: String = "ACTION_CREATE_NEW_GROUP"
        val ACTION_REPLACE_GROUP: String = "ACTION_REPLACE_GROUP"
        val ACTION_DELETE_GROUP: String = "ACTION_DELETE_GROUP"
        val ACTION_REMOVE_MEMBER: String = "ACTION_REMOVE_MEMBER"


        fun openActivity(context: Context, userId: Long) {
            val intent = Intent(context, AllGroupActivity::class.java)
            if (userId > 0) {
                intent.putExtra(User.USER_ID, userId)
            }
            context.startActivity(intent)
        }
    }

    var isOwner = true
    lateinit var context: Context
    lateinit var user: User
    lateinit var db: TinyDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_group)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp)
        toolbar.setNavigationOnClickListener { finish() }
        setTitle("")

        context = this
        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User


        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(User.USER_ID)) {
                val userId = bundle.getLong(User.USER_ID)
                if (userId != user.userId) {
                    isOwner = false
                }
            }
        }


        initRV()
        initSearchView()
        initReceiver()

        //tao nhom
        if (user.roleId == User.ADMIN_ROLE_ID) {
            btnCreateGroup.visibility = View.VISIBLE
            btnCreateGroup.setOnClickListener {
                val keyword = txtSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    imgClear.performClick()
                }
                CreateGroupActivity.openActivity(this)
            }
        } else {
            btnCreateGroup.visibility = View.GONE
        }


        //kiem tra dang vao user/owner
        if (!isOwner) {
            titleGroup.text = getText(R.string.common_group)
            btnCreateGroup.visibility = View.GONE
            relativeSearch.visibility = View.GONE
            rvSearch.visibility = View.GONE
        }

    }


    var pageIndex = 1
    val pageSize = 20
    lateinit var scrollListener: RecyclerViewLoadMoreScroll

    lateinit var adapter: AllGroupAdapter
    fun initRV() {
        ////rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(DividerItemDecoration(rv.context, layoutManager.orientation))
        adapter = AllGroupAdapter(this@AllGroupActivity, ArrayList<GroupInfo?>())
        rv.adapter = adapter


        ////init load more
        scrollListener = RecyclerViewLoadMoreScroll(layoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
//                getMyGroups("", pageIndex++)
            }

        })
        rv.addOnScrollListener(scrollListener)

        //page first
        getMyGroups("", pageIndex)


    }

    lateinit var adapterSearch: AllGroupAdapter
    fun initRvSearch() {
        ////rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSearch.layoutManager = layoutManager
        rvSearch.itemAnimator = DefaultItemAnimator()
        rvSearch.addItemDecoration(DividerItemDecoration(rv.context, layoutManager.orientation))
        adapterSearch = AllGroupAdapter(this@AllGroupActivity, ArrayList<GroupInfo?>())
        rvSearch.adapter = adapterSearch

        adapterSearch.setGroupListener(object : GroupListener {
            override fun onJoinGroup(group: GroupInfo?) {
                if (group != null) {
                    if (adapter != null) {
                        adapter.updateGroupJoin(group.groupId)
                    }
                }
            }
        })
    }


    fun showLoadMore() {
        if (pageIndex > 1) {//load more
            adapter.addLoadingView()
        }
    }

    fun hideLoadMore() {
        if (pageIndex > 1) {//load more
            adapter.removeLoadingView()
            scrollListener.setLoaded()////Change the boolean isLoading to false
        }
    }


    private fun getMyGroups(keyword: String, page: Int) {
        if (MyUtils.checkInternetConnection(this)) {
            showLoadMore()
            MyApplication.apiManager.groupList(
                    keyword, page, pageSize,
                    object : Callback<JsonObject?> {
                        override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                            hideLoadMore()

                            val data = response.body()
                            if (data != null) {
                                val collectionType = object : TypeToken<ArrayList<GroupInfo?>?>() {}.type
                                val result = Webservices.parseJson(data.toString(), collectionType, true)
                                if (result != null) {
                                    if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                        var list = result.data as ArrayList<GroupInfo?>
                                        if (list.size > 0) {
//                                            list = increaseList(list)
                                            if (pageIndex == 1) {
                                                //tao section admin
                                                val header = GroupInfo()
                                                header.isHeader = true
                                                header.groupName = getString(R.string.joinned_groups)
                                                list.add(0, header)

                                                adapter = AllGroupAdapter(context, list)
                                                rv.adapter = adapter

                                                //neu so luong group van con thi load more
                                                if (list.size < pageSize) {
                                                    //lay danh sach nhom khac
                                                    searchGroup("")
                                                } else {
                                                    //load more
                                                    getMyGroups("", ++pageIndex)
                                                }
                                            } else {
                                                //load more
                                                rv.post { adapter.addAll(list) }
                                            }
                                        } else {
                                            //lay danh sach nhom khac
                                            searchGroup("")
                                        }
                                    }
                                }
                            }


                        }

                        override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                            MyUtils.log("error")
                            hideLoadMore()
                        }
                    })
        }
    }

    fun initSearchView() {
        imgClear.setOnClickListener(View.OnClickListener {
            txtSearch.setText("")
            txtSearch.requestFocus()
            MyUtils.showKeyboard(this)
            showRvWhenSearch(false)
        })
        txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text = s.toString().trim()
                if (!TextUtils.isEmpty(text)) {
//                    txtSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null)
                    imgClear.visibility = View.VISIBLE
                } else {
//                    txtSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
                    imgClear.visibility = View.GONE
                }
                if (!mTyping) {
                    mTyping = true
                }
                mTypingHandler.removeCallbacks(onTypingTimeout)
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH)
            }
        })

        //init search
        initRvSearch()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    private val TYPING_TIMER_LENGTH: Long = 500
    private var mTyping = false
    private val mTypingHandler = Handler()
    private val onTypingTimeout = Runnable {
        if (!mTyping) return@Runnable
        mTyping = false
        //dang stop thi search
        val keyword = txtSearch.text.toString().trim()
        if (keyword.isNotEmpty()) {
            searchGroup(keyword)
        } else {
            showRvWhenSearch(false)
            MyUtils.hideKeyboard(this)
        }

    }

    /**
     * Chi can load 1 page suggest, ko can phai load all group
     */
    private fun searchGroup(keyword: String) {
        //chi la owner moi co danh sach search
        if (isOwner) {
            val isSearching = keyword.trim().isNotEmpty()
            showRvWhenSearch(isSearching)
            if (MyUtils.checkInternetConnection(this)) {
                showLoadMore()
                MyApplication.apiManager.searchToJoin(
                        keyword, 1, pageSize,
                        object : Callback<JsonObject?> {
                            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                                hideLoadMore()

                                val data = response.body()
                                if (data != null) {
                                    val collectionType = object : TypeToken<ArrayList<GroupInfo?>?>() {}.type
                                    val result = Webservices.parseJson(data.toString(), collectionType, true)
                                    if (result != null) {
                                        if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                            var list = result.data as ArrayList<GroupInfo?>
                                            if (list.size > 0) {

                                                //adapter dua vao isSearching de bind adapter
                                                if (isSearching) {
                                                    rvSearch.post { adapterSearch.update(list) }
                                                } else {
                                                    //tao section admin
                                                    val header = GroupInfo()
                                                    header.isHeader = true
                                                    header.groupName = getString(R.string.other_groups)
                                                    list.add(0, header)

                                                    //load more
                                                    rv.post { adapter?.addAll(list) }
                                                }

                                            } else {
                                                if (isSearching) {
                                                    rvSearch.post { adapterSearch.update(list) }
                                                }
                                            }
                                        }
                                    }
                                }


                            }

                            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                                MyUtils.log("error")
                                hideLoadMore()
                            }
                        })
            }
        }
    }


    fun showRvWhenSearch(isSearching: Boolean) {
        if (isSearching) {
            rv.visibility = View.GONE
            rvSearch.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            rvSearch.visibility = View.GONE
        }
    }


    lateinit var broadcastReceiver: BroadcastReceiver
    fun initReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_JOIN_GROUP -> {
                        val groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
                        if (groupId > 0) {
                            adapter.updateGroupJoin(groupId)
                            adapterSearch.updateGroupJoin(groupId)
                        }
                    }
                    ACTION_UPDATE_BANNER -> {
                        val group = intent.getParcelableExtra<GroupInfo>(GroupInfo.GROUP_INFO)
                        if (group != null) {
                            adapter.updateGroupBanner(group)
                            adapterSearch.updateGroupBanner(group)
                        }
                    }
                    ACTION_CREATE_NEW_GROUP -> {
                        val group = intent.getParcelableExtra<GroupInfo>(GroupInfo.GROUP_INFO)
                        if (group != null) {
                            //vi tri 0 la header
                            adapter.addPosition(group, 1)
                        }
                    }
                    ACTION_REPLACE_GROUP -> {
                        val group = intent.getParcelableExtra<GroupInfo>(GroupInfo.GROUP_INFO)
                        if (group != null) {
                            //vi tri 0 la header
                            adapter.replace(group)
                            adapterSearch.replace(group)
                        }
                    }
                    ACTION_DELETE_GROUP -> {
                        val groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
                        if (groupId > 0) {
                            adapter.deleteGroup(groupId)
                            adapterSearch.deleteGroup(groupId)
                        }
                    }
                    ACTION_REMOVE_MEMBER -> {
                        val groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
                        if (groupId > 0) {
                            adapter.decreaseMember(groupId)
                            adapterSearch.decreaseMember(groupId)
                        }
                    }

                }

            }
        }

        var filter = IntentFilter()
        filter.addAction(ACTION_JOIN_GROUP)
        filter.addAction(ACTION_UPDATE_BANNER)
        filter.addAction(ACTION_CREATE_NEW_GROUP)
        filter.addAction(ACTION_REPLACE_GROUP)
        filter.addAction(ACTION_DELETE_GROUP)
        filter.addAction(ACTION_REMOVE_MEMBER)



        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}