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
import com.topceo.group.models.PendingPost
import com.topceo.objects.image.ImageItem
import com.topceo.objects.other.User
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_group_member_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApprovePostActivity : AppCompatActivity() {
    companion object {
        fun openActivity(context: Context, groupId: Long) {
            val intent = Intent(context, ApprovePostActivity::class.java)
            intent.putExtra(GroupInfo.GROUP_ID, groupId)
            context.startActivity(intent)
        }
    }

    lateinit var context: Context
    lateinit var user: User
    lateinit var db: TinyDB
    var groupId: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_post_approve)

        setSupportActionBar(toolbar)
//        android:tint
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setTitle("")

        context = this
        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User

        groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
        if (groupId > 0) {
            initRV()
        }

    }


    lateinit var adapter: ApprovePostAdapter
    fun initRV() {
        ////rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager
        rv.itemAnimator = DefaultItemAnimator()
//        rv.addItemDecoration(DividerItemDecoration(rv.context, layoutManager.orientation))
        adapter = ApprovePostAdapter(this@ApprovePostActivity, ArrayList<ImageItem>())
        rv.adapter = adapter

        groupPendingPost()

    }


    /**
     * Luc nao cung lay 15 ptu dau tien cho cau search, ko load more
     */
    fun groupPendingPost() {
        MyApplication.apiManager.groupPendingPost(groupId, 1, 100,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        val data = response.body()
                        if (data != null) {
                            val collectionType = object : TypeToken<ArrayList<PendingPost>>() {}.type
                            val result = Webservices.parseJson(data.toString(), collectionType, true)
                            if (result != null) {
                                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                    result.data?.let {
                                        var list = it as ArrayList<PendingPost>
                                        if (list.size > 0) {
                                            val posts: ArrayList<ImageItem> = list.map {
                                                it.convertToImageItem()
                                            } as ArrayList<ImageItem>

                                            rv.post {
                                                adapter.update(posts)
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        MyUtils.log("error")
                    }
                })
    }

}