package com.topceo.group.members

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.db.TinyDB
import com.topceo.group.models.GroupInfo
import com.topceo.group.models.GroupMember
import com.topceo.objects.other.User
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.smartapp.loadmore.OnLoadMoreListener
import com.smartapp.loadmore.RecyclerViewLoadMoreScroll
import kotlinx.android.synthetic.main.activity_group_member.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MemberGroupActivity : AppCompatActivity() {
    companion object {
        fun openActivity(context: Context, info: GroupInfo) {
            val intent = Intent(context, MemberGroupActivity::class.java)
            intent.putExtra(GroupInfo.GROUP_INFO, info)
            context.startActivity(intent)
        }
    }

    lateinit var user: User
    lateinit var db: TinyDB
    var groupInfo: GroupInfo? = null
    var canDeleteUser: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_member)

        setSupportActionBar(toolbar)
//        android:tint
        toolbar.setNavigationOnClickListener { finish() }
        setTitle("")

        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User

        groupInfo = intent.getParcelableExtra(GroupInfo.GROUP_INFO)
        if (groupInfo != null) {

            //dc xoa member
//            val isOwner = groupInfo!!.createUserId == user.userId
            if (groupInfo!!.isAdmin || groupInfo!!.isMod) {
                canDeleteUser = true
            }

            initRV()
            initRefreshUI()
        }

        btnInvite.setOnClickListener {
            if (groupInfo != null) {
                InviteMemberActivity.openActivity(this, groupInfo!!.groupId)
            }
        }

    }


    var pageIndex = 1
    val pageSize = 15
    lateinit var scrollListener: RecyclerViewLoadMoreScroll

    lateinit var adapter: MemberGroupAdapter
    fun initRV() {
        ////rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager
        rv.itemAnimator = DefaultItemAnimator()
//        rv.addItemDecoration(DividerItemDecoration(rv.context, layoutManager.orientation))
        adapter = MemberGroupAdapter(this@MemberGroupActivity, ArrayList<GroupMember?>(), user.userId, canDeleteUser)
        rv.adapter = adapter


        ////init load more
        scrollListener = RecyclerViewLoadMoreScroll(layoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                getMembers(pageIndex++)
            }

        })
        rv.addOnScrollListener(scrollListener)

        getAdmin()

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


    /** 1 page tra ve tat ca admins */
    private fun getAdmin() {
        if (MyUtils.checkInternetConnection(this)) {
            if (groupInfo != null) {
                adapter.clear()
                MyApplication.apiManager.getAdminsGroup(groupInfo!!.groupId,
                        object : Callback<JsonObject?> {
                            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                                val data = response.body()
                                if (data != null) {
                                    val collectionType = object : TypeToken<ArrayList<GroupMember>>() {}.type
                                    val result = Webservices.parseJson(data.toString(), collectionType, true)
                                    if (result != null) {
                                        if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                            var list = result.data as ArrayList<GroupMember?>
                                            if (list.size > 0) {
                                                //tao section admin
                                                val admin = GroupMember()
                                                admin.isHeader = true
                                                admin.userName = getString(R.string.admins)
                                                list.add(0, admin)


                                                rv.post {
                                                    adapter.update(list)
                                                    getMembers(pageIndex = 1)
                                                }
                                            } else {
                                                getMembers(pageIndex = 1)
                                            }
                                        }
                                    }
                                }
                                setRefresh(false)
                            }

                            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                                MyUtils.log("error")
                                setRefresh(false)
                            }
                        })
            }
        }
    }


    /**
     * Co load more
     */
    fun getMembers(pageIndex: Int) {
        showLoadMore()
        MyApplication.apiManager.getMembersGroup(groupInfo!!.groupId, pageIndex, pageSize,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        hideLoadMore()
                        val data = response.body()
                        if (data != null) {
                            val collectionType = object : TypeToken<ArrayList<GroupMember>>() {}.type
                            val result = Webservices.parseJson(data.toString(), collectionType, true)
                            if (result != null) {
                                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                    var list = result.data as ArrayList<GroupMember?>
                                    if (list.size > 0) {

                                        if (pageIndex == 1) {//page dau tien
                                            //tao section admin
                                            val member = GroupMember()
                                            member.isHeader = true
                                            member.userName = getString(R.string.members)
                                            list.add(0, member)
                                        }

                                        rv.post {
                                            adapter.addAll(list)
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

    fun initRefreshUI() {
        // Configure the refreshing colors

        //////////////////////////////////////////////////////////////////////////////////
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipeContainer.setOnRefreshListener {
            if (MyUtils.checkInternetConnection(this)) {
                //lay page 0
                setRefresh(true)
                getAdmin()
            } else {
                setRefresh(false)
                MyUtils.showThongBao(this)
            }
        }
    }

    private fun setRefresh(isRefresh: Boolean) {
        if (isRefresh) { //on
            if (swipeContainer != null && !swipeContainer.isRefreshing) swipeContainer.isRefreshing = isRefresh
        } else { //off
            if (swipeContainer != null && swipeContainer.isRefreshing) swipeContainer.isRefreshing = isRefresh
        }
    }
}