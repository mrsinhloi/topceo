package com.topceo.group

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.db.TinyDB
import com.topceo.fragments.Fragment_1_Home_SonTung
import com.topceo.group.models.GroupInfo
import com.topceo.group.models.NotifySettings
import com.topceo.objects.other.User
import com.topceo.services.ReturnResult
import com.topceo.services.Webservices
import com.topceo.utils.MyUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_group_member.toolbar
import kotlinx.android.synthetic.main.activity_group_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingGroupActivity : AppCompatActivity() {
    companion object {
        fun openActivity(context: Context, info: GroupInfo) {
            val intent = Intent(context, SettingGroupActivity::class.java)
            intent.putExtra(GroupInfo.GROUP_INFO, info)
            context.startActivity(intent)
        }
    }

    lateinit var user: User
    lateinit var db: TinyDB
    var groupInfo: GroupInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting)

        setSupportActionBar(toolbar)
//        android:tint
        toolbar.setNavigationOnClickListener { finish() }
        setTitle("")

        db = TinyDB(this)
        user = db.getObject(User.USER, User::class.java) as User

        settings = NotifySettings(-1, 1, true)
        groupInfo = intent.getParcelableExtra(GroupInfo.GROUP_INFO)
        if (groupInfo != null) {
            getSetting(groupInfo!!.groupId)

            //neu la owner hoac la admin sys thi dc edit config
            initAdminConfig(groupInfo!!)
        }

        linear1.setOnClickListener {
            val items = resources.getStringArray(R.array.arr_group_setting_feed)
            var checkedItem = if (settings.PostPush == 1) 0 else 1

            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.feed)
                    .setSingleChoiceItems(items, checkedItem, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            checkedItem = which
                        }

                    })
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            if (checkedItem == 0) {
                                settings.PostPush = 1
                            } else {
                                settings.PostPush = 0
                            }
                            loadSettingUI(settings)

                            //call update setting
                            setSetting()

                        }
                    })
                    .show()
        }
        linear2.setOnClickListener {
            switch1.isChecked = !switch1.isChecked
        }
        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            settings.MemberRequest = isChecked
            //call update setting
            setSetting()
        }

    }

    lateinit var settings: NotifySettings
    fun getSetting(groupId: Long) {
        if (MyUtils.checkInternetConnection(this)) {
            MyApplication.apiManager.groupSettingGet(groupId,
                    object : Callback<JsonObject?> {
                        override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                            val data = response.body()
                            if (data != null) {
                                val result = Webservices.parseJson(data.toString(), NotifySettings::class.java, false)
                                if (result != null) {
                                    if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                        try {
                                            settings = result.data as NotifySettings
                                            loadSettingUI(settings)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                            MyUtils.log("error")
                        }
                    })
        } else {
            MyUtils.showThongBao(this)
        }
    }

    private fun loadSettingUI(settings: NotifySettings) {
        ////
        if (settings.PostPush == 1) {
            txt1.text = getText(R.string.group_setting_feed_1)
        } else {
            txt1.text = getText(R.string.group_setting_feed_2)
        }

        ////
        switch1.isChecked = settings.MemberRequest
    }

    fun setSetting() {
        if (groupInfo != null) {
            val groupId: Long = groupInfo!!.groupId
            if (MyUtils.checkInternetConnection(this)) {
                MyApplication.apiManager.groupSettingSet(groupId, settings,
                        object : Callback<JsonObject?> {
                            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                                val data = response.body()
                                if (data != null) {
                                    val result = Webservices.parseJson(data.toString(), Boolean::class.java, false)
                                    if (result != null) {
                                        if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
//                                            MyUtils.showToastDebug(this@SettingGroupActivity, "OK")
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                                MyUtils.log("error")
                            }
                        })
            } else {
                MyUtils.showThongBao(this)
            }
        }
    }


    var isChanged: Boolean = false

    //admin setting group
    fun initAdminConfig(group: GroupInfo) {
        val isOwner = group.createUserId == user.userId
        if (group.isAdmin || isOwner) {
            linearAdminConfig.visibility = View.VISIBLE

            //1
            val memberApprove = group.memberApprove
            when (memberApprove) {
                GroupInfo.MEMBER_APPROVE_AUTO -> radio1.isChecked = true
                GroupInfo.MEMBER_APPROVE_EVERYONE -> radio2.isChecked = true
                GroupInfo.MEMBER_APPROVE_ADMIN_MOD -> radio3.isChecked = true
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                isChanged = true
                when (checkedId) {
                    R.id.radio1 -> {
                        group.memberApprove = GroupInfo.MEMBER_APPROVE_AUTO
                    }
                    R.id.radio2 -> {
                        group.memberApprove = GroupInfo.MEMBER_APPROVE_EVERYONE
                    }
                    R.id.radio3 -> {
                        group.memberApprove = GroupInfo.MEMBER_APPROVE_ADMIN_MOD
                    }

                }
            }

            //2
            switch2.isChecked = group.isMemberCanPost
            switch2.setOnCheckedChangeListener { buttonView, isChecked ->
                group.isMemberCanPost = isChecked
                isChanged = true
            }
            linear3.setOnClickListener {
                switch2.isChecked = !switch2.isChecked
                group.isMemberCanPost = switch2.isChecked
                isChanged = true
            }

            //3
            switch3.isChecked = group.isPostNeedApprove
            switch3.setOnCheckedChangeListener { buttonView, isChecked ->
                group.isPostNeedApprove = isChecked
                isChanged = true
            }
            linear4.setOnClickListener {
                switch3.isChecked = !switch3.isChecked
                group.isPostNeedApprove = switch3.isChecked
                isChanged = true
            }

        }

    }

    fun setConfig() {
        if (groupInfo != null) {
            val groupId: Long = groupInfo!!.groupId
            if (MyUtils.checkInternetConnection(this)) {
                MyApplication.apiManager.groupUpdateSettings(
                        groupId,
                        groupInfo!!.memberApprove,
                        groupInfo!!.isMemberCanPost,
                        groupInfo!!.isPostNeedApprove,
                        object : Callback<JsonObject?> {
                            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                                val data = response.body()
                                if (data != null) {
                                    val result = Webservices.parseJson(data.toString(), GroupInfo::class.java, false)
                                    if (result != null) {
                                        if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
//                                            MyUtils.showToastDebug(this@SettingGroupActivity, "OK")
                                            val intent = Intent(AllGroupActivity.ACTION_REPLACE_GROUP)
                                            intent.putExtra(GroupInfo.GROUP_INFO, groupInfo)
                                            sendBroadcast(intent)

                                            //bao refresh list home
                                            //load lai danh sach group
                                            sendBroadcast(Intent(Fragment_1_Home_SonTung.ACTION_REFRESH_GROUP))
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                                MyUtils.log("error")
                            }
                        })
            } else {
                MyUtils.showThongBao(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isChanged) {
            setConfig()
        }
    }

}