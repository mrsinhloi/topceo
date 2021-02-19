package com.workchat.core.notification

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDragCallback
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import com.orhanobut.logger.Logger
import com.sonhvp.utilities.content.Receiver
import com.sonhvp.utilities.content.getColorCompat
import com.sonhvp.utilities.content.getDrawableCompat
import com.sonhvp.utilities.json.jsonArray
import com.sonhvp.utilities.json.toJSON
import com.sonhvp.utilities.standard.isBlankOrEmpty
import com.sonhvp.utilities.standard.then
import com.workchat.core.api.emit
import com.workchat.core.config.ChatApplication
import com.workchat.core.event.SocketConnectEvent
import com.workchat.core.fragments.base.BaseFrag
import com.workchat.core.notification.events.NotificationEvent
import com.workchat.core.notification.items.*
import com.workchat.core.notification.model.*
import com.workchat.core.utils.MyUtils
import com.workchat.corechat.R
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_notify.*
import kotlinx.android.synthetic.main.fragment_notify.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject

class NotificationNavFrag : BaseFrag(), SharedPreferences.OnSharedPreferenceChangeListener {

    override val fragLayout: Int = R.layout.fragment_notify
    private lateinit var socket: Socket

    private var isLoading = false

    fun getMyContext(): Context? {
        var context: Context? = null
        try {
            context = requireContext()
        } catch (e: Exception) {
        }
        return context
    }

    private val items = ComparableItemListImpl(Comparator<BaseNotificationItem> { o1, o2 -> o2.notify.createDate.compareTo(o1.notify.createDate) })
    private val itemAdapter = ModelAdapter(items) { element: BaseNotification ->

        var context: Context? = getMyContext()
        if (context != null) {
            when (element) {
                is AddContactNotification -> AddContactItem(context, element)
                is AddToRoomNotification -> AddToRoomItem(context, element)
                is RemoveFromRoomNotification -> RemoveFromRoomItem(context, element)
                is NewToChatNotification -> NewToChatItem(context, element)
                is LoginToChatNotification -> LoginToChatItem(context, element)
                is PollActionNotification -> PollActionItem(context, element)
                is PlanReminderActionNotification -> PlanReminderActionItem(context, element)


                else -> {
                    Logger.d("$element")
                    null
                }
            }
        } else {
            null
        }
    }
    private val footerAdapter = ItemAdapter<com.mikepenz.fastadapter.ui.items.ProgressItem>()
    private val ntfAdapter: FastAdapter<IItem<out RecyclerView.ViewHolder>> = FastAdapter.with(mutableListOf(itemAdapter, footerAdapter))


    private var endScroll: EndlessRecyclerOnScrollListener? = null
    override fun onCreateViewBlock(): View.() -> Unit = {
        socket = ChatApplication.getSocketInitIfNull()!!
        prefs.registerOnSharedPreferenceChangeListener(this@NotificationNavFrag)
        itemAdapter.apply {
            itemFilter.filterPredicate = { item, constraint ->
                when (constraint.toString().toInt()) {
                    1 -> item.notify.isView
                    2 -> !item.notify.isView
                    else -> true
                }
            }
            filter(prefs.getInt(NOTIFY_VIEW, NOTIFY_VIEW_ALL).toString())
        }
        ntfNavFrag_ntfList.apply {
            var context: Context? = getMyContext()
            if (context != null) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
                adapter = ntfAdapter


                endScroll = object : com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener(footerAdapter) {
                    override fun onLoadMore(currentPage: Int) {
                        if (!isLoading) {
                            if (itemAdapter.adapterItems != null && itemAdapter.adapterItems.size > 10) launch {
                                //                            delay(200)
//                            Logger.d(itemAdapter.adapterItems)
                                try {
                                    getNotifications(itemAdapter.adapterItems.last().notify._id)
                                } catch (e: Exception) {
                                }
                            }

                            //Logger.d("${itemAdapter.itemList.items.last().ntf.createDate.toLong().toDateStr()}")
                        }
                    }
                }
                addOnScrollListener(endScroll!!)


                val touchCallback = SimpleSwipeDragCallback(
                        object : ItemTouchCallback {
                            override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {}
                            override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
                                return false
                            }
                        },
                        object : SimpleSwipeCallback.ItemSwipeCallback {
                            override fun itemSwiped(position: Int, direction: Int) {
                                deleteNotification(itemAdapter.getAdapterItem(position).notify._id)
                                itemAdapter.remove(position)
                            }
                        },
                        context getDrawableCompat R.drawable.ic_delete_bin,
                        ItemTouchHelper.LEFT,
                        context getColorCompat R.color.no
                )
                ItemTouchHelper(touchCallback).run { attachToRecyclerView(this@apply) }
            }
        }

        ntfAdapter.onClickListener = { _, _, item, position ->
            if (item is BaseNotificationItem) {
                //Logger.d("${item.ntf}")
                updateNotifyListIsView(mutableListOf(item.notify._id), true)
                item.notify.isView = true
                ntfAdapter.notifyAdapterItemChanged(position)
                this@NotificationNavFrag.context?.let { currentContext ->
                    item navigationFrom currentContext
                }

                //Goi lay lai danh sach so chua doc
                ChatApplication.getUnreadNotifyCount()
            }
            true
        }

        ntfNavFrag_settings.setOnClickListener {
            startActivity(Intent(context, NotificationConfigActivityWc::class.java))
        }

        ntfNavFrag_checkAll.setOnClickListener {
            AlertDialog.Builder(context!!).apply {
                setMessage(R.string.confirm_read_all_notify)
                setTitle(R.string.notification)
                setPositiveButton(R.string.ok) { dialog, _ ->
                    updateAllNotifyIsView()
                    dialog.dismiss()
                }
                setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            }.run {
                show()
            }
        }
        registerReceiver()
        getNotifications("")

        swipeRefreshLayout.setOnRefreshListener {
            getNotifications("")
        }

    }

    override fun onSocketConnected(socketStatus: SocketConnectEvent) {
        isLoading = false
        socket = ChatApplication.socket!!
        launch {
            //delay(200)
            withContext(Dispatchers.Default) {
                if (itemAdapter.itemList.items.isEmpty()) getNotifications("")
            }
        }
    }

    override fun onSocketDisconnected(socketStatus: SocketConnectEvent) {
        super.onSocketDisconnected(socketStatus)
        isLoading = false
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun onUpdateNotification(event: NotificationEvent) {
        //Logger.d("${event.notification}")
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onReceiveNotification(event: NotificationEvent) {
        //Logger.d("${event.notification}")
        if (itemAdapter.itemList.items.none { it.notify._id == event.notification._id }) {
            itemAdapter.add(0, event.notification)
            restoreScrollPositionAfterAdAdded()
        }
    }

    private val receiver = Receiver(
            onTrigger = { _, intent ->
                //            val b = intent.extras
                intent.action?.run {
                    when {
                        equals(ACTION_RELOAD_DATA, ignoreCase = true) -> {
                            getNotifications("")
                        }
                        /*equals(ACTION_DELETE_NOTIFY_IN_ADAPTER, ignoreCase = true) -> {
                            val listIds = b!!.getStringArrayList(BaseNotification.LIST_STRING_IDS)
                            if (listIds != null && listIds.size > 0) {
                                ntfAdapter.delete(listIds)
                            }
                        }
                        equals(ACTION_MARK_VIEW_NOTIFY_IN_ADAPTER, ignoreCase = true) -> {
                            val listIds = b!!.getStringArrayList(BaseNotification.LIST_STRING_IDS)
                            val isView = b.getBoolean(BaseNotification.IS_VIEWED, false)
                            if (listIds != null) {
                                ntfAdapter.update(listIds, isView)
                            }
                        }
                        equals(ACTION_ENABLE_NOTIFY, ignoreCase = true) -> {
                            val isEnable = b!!.getBoolean(IS_RECEIVE_NOTIFY, true)
                            configPushInNotify(isEnable)
                        }*/
                    }
                }
            }
    )

    private fun registerReceiver() {
        context?.run {
            val intentFilter = IntentFilter().apply {
                addAction(ACTION_DELETE_NOTIFY_IN_ADAPTER)
                addAction(ACTION_MARK_VIEW_NOTIFY_IN_ADAPTER)
                addAction(ACTION_ENABLE_NOTIFY)
                addAction(ACTION_RELOAD_DATA)
            }
            registerReceiver(receiver, intentFilter)
        }
    }


    val pageCount = 20
    private fun getNotifications(lastItemId: String) {
        if (context != null && MyUtils.checkInternetConnection(context)) {
            val isView: Boolean? = when (prefs.getInt(NOTIFY_VIEW, NOTIFY_VIEW_ALL)) {
                NOTIFY_VIEW_ALL -> null
                NOTIFY_VIEW_READ -> true
                NOTIFY_VIEW_UNREAD -> false
                else -> null
            }
            if (socket != null && socket.connected()) {
                if (lastItemId.isBlankOrEmpty()) {
                    //lay cau hinh ntf page dau tien
                    getUserSettings()
                }

                isLoading = true
                MyUtils.log("getNotifications")

                socket.emit<JSONArray>(
                        "getNotifyList",
                        jsonObj {
                            put("lastItemId", lastItemId)
                            put("isView", isView)
                            put("itemCount", pageCount)
                        },
                        onResult = { data ->
                            //Logger.d("length: ${data.length()}")
//                    Logger.d("data: $data")
                            runOnUiThread {
                                if (activity != null && !activity!!.isFinishing) {
                                    if (lastItemId.isBlankOrEmpty()) {
                                        itemAdapter.clear()
                                    }
                                    for (i in 0 until data.length()) {
                                        data[i].toJSON().toNotification()?.let { loadedNtf ->
                                            //                                Logger.d(loadedNtf)
                                            itemAdapter.adapterItems.none { ntfItem -> ntfItem.notify._id == loadedNtf._id }.then {

                                                itemAdapter.add(loadedNtf)

                                                //an empty view
                                                if (emptyView != null && emptyView.visibility == View.VISIBLE) {
                                                    emptyView.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }

                                    if (TextUtils.isEmpty(lastItemId)) {
                                        endScroll!!.resetPageCount()
                                    }

                                    isLoading = false
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
                                        swipeRefreshLayout.isRefreshing = false
                                    }
                                }


                            }
                        }
                        , onFailure = { _, errorString ->
                    runOnUiThread {

                        if (TextUtils.isEmpty(lastItemId) && endScroll != null) {
                            endScroll!!.resetPageCount()
                        }

                        isLoading = false
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        }

//                    MyUtils.showAlertDialog(context, errorString)
                    }
                }
                )
            }
        } else {
            isLoading = false
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun deleteNotification(notifyId: String) {
        if (socketConnected) {
            socket.emit<JSONObject>(
                    "removeNotifyList",
                    jsonObj {
                        put("notifyIds", jsonArray { put(notifyId) })
                    },
                    onSuccess = {
                        runOnUiThread { MyUtils.showToast(context, R.string.delete_success) }
                    }
            )
        }
    }

    private fun updateNotifyListIsView(ids: MutableList<String>, isViewed: Boolean) {
        if (socketConnected) {
            if (ids.isNotEmpty()) {
                socket.emit<JSONObject>(
                        "updateNotifyListIsView",
                        jsonObj {
                            put("notifyIds", JSONArray(ids))
                            put("isView", isViewed)
                        },
                        onSuccess = {
                            Logger.d("updateNotifyListIsView")
                        },
                        onFailure = { errorCode, error ->
                            Logger.d("errorCode: $errorCode \nerror: $error")
                        }
                )
            }
        }
    }

    private fun updateAllNotifyIsView() {
        if (socketConnected) {
            socket.emit<JSONObject>(
                    "updateAllNotifyIsView",
                    onSuccess = {
                        runOnUiThread {
                            MyUtils.showToast(context, R.string.update_success)
                            itemAdapter.itemList.items.onEach { it.notify.isView = true }
                            ntfAdapter.notifyAdapterDataSetChanged()
                        }
                    }
            )
        }
    }

    private fun configPushInNotify(isEnable: Boolean) {
        if (socketConnected) {
            socket.emit<JSONObject>(
                    "configPushInNotify",
                    jsonObj {
                        put("enable", isEnable)
                    },
                    onSuccess = {
                        //runOnUiThread { MyUtils.showToast(context, R.string.update_success) }
                    }
            )
        }
    }

    private fun getUserSettings() {
        if (socketConnected) {
            socket.emit<JSONObject>(
                    "getUserSettings",
                    onResult = { data ->
                        data.run {
                            prefs.edit { putBoolean(IS_RECEIVE_NOTIFY, getBoolean("isPushInNotify")) }
                            //runOnUiThread { MyUtils.showToast(context, R.string.update_success) }
                        }
                    }
            )
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Logger.d("onSharedPreferenceChanged")
        when (key) {
            NOTIFY_VIEW -> itemAdapter.filter(prefs.getInt(NOTIFY_VIEW, NOTIFY_VIEW_ALL).toString())
            IS_RECEIVE_NOTIFY -> configPushInNotify(prefs.getBoolean(IS_RECEIVE_NOTIFY, true))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    //scroll to top
    private fun restoreScrollPositionAfterAdAdded() {
        ntfNavFrag_ntfList.run {
            (layoutManager as LinearLayoutManager).run {
                if (findFirstVisibleItemPosition() == 0) {
                    scrollToPosition(0)
                }
            }
        }
    }

    companion object {
        const val ACTION_DELETE_NOTIFY_IN_ADAPTER = "ACTION_DELETE_NOTIFY_IN_ADAPTER"
        const val ACTION_MARK_VIEW_NOTIFY_IN_ADAPTER = "ACTION_MARK_VIEW_NOTIFY_IN_ADAPTER"
        const val ACTION_ENABLE_NOTIFY = "ACTION_ENABLE_NOTIFY"
        const val ACTION_RELOAD_DATA = "ACTION_RELOAD_DATA"
    }

}

fun jsonObj(block: JSONObject.() -> Unit): JSONObject = JSONObject().apply(block)