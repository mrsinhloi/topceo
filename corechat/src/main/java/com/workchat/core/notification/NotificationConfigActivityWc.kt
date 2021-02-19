package com.workchat.core.notification

import android.content.Intent
import android.os.Bundle
import androidx.core.content.edit
import com.workchat.core.activities.base.BaseActivityWc
import com.workchat.corechat.R
import kotlinx.android.synthetic.main.activity_notify_config.*

class NotificationConfigActivityWc : BaseActivityWc() {

    override val fragContainer: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_config)

        setSupportActionBar(ntfConfigActivity_toolbar)
        ntfConfigActivity_toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp)
            setNavigationOnClickListener { finish() }
        }

        ntfConfigActivity_viewType.apply {
            when (prefs.getInt(NOTIFY_VIEW, NOTIFY_VIEW_ALL)) {
                NOTIFY_VIEW_ALL -> check(R.id.rd1)
                NOTIFY_VIEW_READ -> check(R.id.rd2)
                NOTIFY_VIEW_UNREAD -> check(R.id.rd3)
            }
            setOnCheckedChangeListener { _, i ->
                when (i) {
                    R.id.rd1 -> prefs.edit { putInt(NOTIFY_VIEW, NOTIFY_VIEW_ALL) }
                    R.id.rd2 -> prefs.edit { putInt(NOTIFY_VIEW, NOTIFY_VIEW_READ) }
                    R.id.rd3 -> prefs.edit { putInt(NOTIFY_VIEW, NOTIFY_VIEW_UNREAD) }
                }
                sendBroadcast(Intent(NotificationNavFrag.ACTION_RELOAD_DATA))
            }
        }

        ntfConfigActivity_receiveNtfCheck.apply {
            isChecked = prefs.getBoolean(IS_RECEIVE_NOTIFY, true)
            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit {
                    putBoolean(IS_RECEIVE_NOTIFY, isChecked)
                }
                val intent = Intent(NotificationNavFrag.ACTION_ENABLE_NOTIFY).apply { putExtra(IS_RECEIVE_NOTIFY, isChecked) }
                sendBroadcast(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

}