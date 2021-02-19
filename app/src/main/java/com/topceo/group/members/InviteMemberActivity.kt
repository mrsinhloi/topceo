package com.topceo.group.members

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
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
import kotlinx.android.synthetic.main.activity_group_member_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class InviteMemberActivity : AppCompatActivity() {
    companion object {
        fun openActivity(context: Context, groupId: Long) {
            val intent = Intent(context, InviteMemberActivity::class.java)
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
        setContentView(R.layout.activity_group_member_search)

        setSupportActionBar(toolbar)
//        android:tint
        toolbar.setNavigationOnClickListener {
            finish() }
        setTitle("")

        context = this
        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User

        groupId = intent.getLongExtra(GroupInfo.GROUP_ID, 0)
        if (groupId > 0) {
            initRV()
            initSearchView()
        }

    }


    lateinit var adapter: InviteMemberAdapter
    fun initRV() {
        ////rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager
        rv.itemAnimator = DefaultItemAnimator()
//        rv.addItemDecoration(DividerItemDecoration(rv.context, layoutManager.orientation))
        adapter = InviteMemberAdapter(this@InviteMemberActivity, ArrayList<GroupMember>(), user.userId, groupId)
        rv.adapter = adapter

        //restore list invited
        adapter.restoreListInvited()

        searchUser("")

    }


    fun initSearchView() {
        imgClear.setOnClickListener(View.OnClickListener {
            txtSearch.setText("")
            txtSearch.requestFocus()
            MyUtils.showKeyboard(this)
            searchUser("")
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
    }

    ////////////////////////////////////////////////////////////////////////////////////
    private val TYPING_TIMER_LENGTH : Long = 500
    private var mTyping = false
    private val mTypingHandler = Handler()
    private val onTypingTimeout = Runnable {
        if (!mTyping) return@Runnable
        mTyping = false
        //dang stop thi search
        val keyword = txtSearch.text.toString().trim()
        searchUser(keyword)
    }

    /**
     * Luc nao cung lay 15 ptu dau tien cho cau search, ko load more
     */
    fun searchUser(keyword: String) {
        MyApplication.apiManager.searchToInvite(groupId, keyword, 1, 15,
                object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        val data = response.body()
                        if (data != null) {
                            val collectionType = object : TypeToken<ArrayList<GroupMember>>() {}.type
                            val result = Webservices.parseJson(data.toString(), collectionType, true)
                            if (result != null) {
                                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                    var list = result.data as ArrayList<GroupMember>
                                    if (list.size > 0) {
                                        rv.post {
                                            adapter.update(list)
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


    override fun onDestroy() {
        super.onDestroy()
        adapter.saveListInvited()
        setResult(RESULT_OK)
    }


}