package com.workchat.core.config

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.logger.Logger
import com.workchat.core.api.emit
import com.workchat.core.api.getData
import com.workchat.core.chat.ChatActivity
import com.workchat.core.chat.socketio.SocketState
import com.workchat.core.contacts.Contact_Fragment_2_Online
import com.workchat.core.database.TinyDB
import com.workchat.core.event.RoomLogEvent_Chat
import com.workchat.core.event.RoomLogEvent_Have_Message
import com.workchat.core.event.RoomLogEvent_IsViewed
import com.workchat.core.event.SocketConnectEvent
import com.workchat.core.mbn.models.UserChatCore
import com.workchat.core.models.chat.LocaleHelper
import com.workchat.core.models.chat.RoomLog
import com.workchat.core.models.realm.Project
import com.workchat.core.models.realm.UserChat
import com.workchat.core.notification.NotificationNavFrag
import com.workchat.core.notification.toNotification
import com.workchat.core.retrofit.api.ApiManagerChatCore
import com.workchat.core.ssl.UnsafeOkHttpClient
import com.workchat.core.utils.MyUtils
import com.workchat.corechat.R
import io.realm.Realm
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.collections.ArrayList


open class ChatApplication : App() {


    override fun onCreate() {
        appContext = applicationContext
        super.onCreate()

        // Initialize Realm (just once per application)
        Realm.init(this)
        var config = ChatRealmConfig()
        realmChat = config.open()


        db = TinyDB(this)
        AndroidThreeTen.init(this)

    }


    companion object {
        var realmChat: Realm? = null
        var iconBackCustom: Drawable? = null
        //retrofit
        var apiManagerChat: ApiManagerChatCore? = null

        //WORKCHAT
        var URL_SERVER_CHAT = ""
        private var user: UserChatCore? = null
        private var oneSignalUserId = ""
        private var oneSignalAppId = ""
        var isUseZoom: Boolean = true
        var isShowFullChatFeature: Boolean = true



        fun setOneSignalUserId(userId: String) {
            oneSignalUserId = userId
        }

        fun setOneSignalAppId(appId: String) {
            oneSignalAppId = appId
        }

        fun init(
                googleMapApiKey: String,
                listener: EventControlListener,
                packageName: String,
                urlServerChat: String,
                iconBackDrawable: Drawable,
                useZoom: Boolean,
                isShowFullChatFeature: Boolean
        ) {

            GOOGLE_MAPS_ANDROID_API_KEY = googleMapApiKey
            eventControlListener = listener
            App.eventControlListener = listener
            applicaitonId = packageName
            URL_SERVER_CHAT = urlServerChat
            isUseZoom = useZoom
            this.isShowFullChatFeature = isShowFullChatFeature

            //retrofit
            apiManagerChat = ApiManagerChatCore.getIntance()
            iconBackCustom = iconBackDrawable


        }

        fun whenHaveUser(userChat: UserChatCore) {
            if (db == null) {
                db = TinyDB(appContext)
            }
            //khoi tao socket
            //luu user
            db?.putObject(UserChatCore.USER_MODEL, userChat)

            if (!TextUtils.isEmpty(userChat.token)) {
                db?.putString(TinyDB.TOKEN_USER, userChat.token)
                tokenUser = userChat.token
            } else {
                tokenUser = db!!.getString(TinyDB.TOKEN_USER)
            }


            //read user
            val user = getUser()

            //setup socket
            setupSocket(user)

            //setup api
            //retrofit
            apiManagerChat = ApiManagerChatCore.getIntanceForce()

        }

        ///////////////////
        lateinit var appContext: Context
        var db: TinyDB? = null
        private var TokenUser: String = ""
        var GOOGLE_MAPS_ANDROID_API_KEY: String = ""
        var eventControlListener: EventControlListener? = null
        var applicaitonId: String = ""


        var isConnecting: Boolean = false
        var isExists: Boolean = false

        //Temp forward, share
        var logForward: RoomLog? = null//forward log
        var sharedText: String? = null
        var sharedImageOrVideo: Uri? = null
        var sharedMultiImages: ArrayList<Uri>? = null
        var sharedDocument: Uri? = null
        fun resetLogForwardAndDataShared() {
            logForward = null
            sharedText = null
            sharedImageOrVideo = null
            sharedMultiImages = null
            sharedDocument = null
        }


        fun getLanguageTag(): String {
            var languageTag = LocaleHelper.LANGUAGE_TIENG_VIET
            if (db != null) {
                languageTag = db!!.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.LANGUAGE_TIENG_VIET)
            }
            return languageTag
        }

        //MYXTEAM - chỉ xài cho hàm /app/GetAppVersion
        var OS_MYXTEAM = "AndroidWorkChat"


        const val OS = "Android"
        lateinit var instance: ChatApplication


        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        //OneSignal
        /////////////////////////////////////////////////////////////////////////////////////////////////
        private var isRegistered = false//khi clear memory thi se reset lai
        private fun updateOneSignalUserId() {
            if (!isRegistered) {
                if (isSocketConnected()) {
                    socket?.takeIf { it.connected() }?.apply {
                        if (!TextUtils.isEmpty(oneSignalUserId)) {
                            val obj = JSONObject()
                            try {
                                obj.put("oneSignalUserId", oneSignalUserId)
                                obj.put("oneSignalAppId", oneSignalAppId)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            emit("updateOneSignalUserId", obj, Ack { isRegistered = true })
                        }
                    }
                }
            }
        }

        fun removeOneSignalUserId() {
            socket?.takeIf { it.connected() }?.apply {
                val obj = JSONObject()
                try {
                    obj.put("oneSignalUserId", oneSignalUserId)
                    obj.put("oneSignalAppId", oneSignalAppId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                emit("removeOneSignalUserId", obj, Ack { args ->
                    val isSuccess = RoomLog.isSuccess(appContext, *args)
                    if (isSuccess) {
                        //co xoa dc hay ko thi cung phai cho dang xuat
                    }
                })


            }
            isRegistered = false
            //
            closeSocket()

            //reset local
            setUser(null)
            isSetupSocket = false

            //clear notification
            val notificationManager = appContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()


            //co xoa dc hay ko thi cung phai cho dang xuat
            //clear cache
            //xoa cache
            if (db != null) {
                db?.clear()
            }

            EventBus.getDefault().removeAllStickyEvents()
            EventBus.clearCaches()

            realmChat?.run {
                executeTransaction {
                    it.deleteAll()
                }
            }

        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////


        //lan 1
        //lan 2
        //lan 3, tao lai token
        //sau khi set token thi tao luon socket
        var tokenUser: String
            get() {
                if (TextUtils.isEmpty(TokenUser)) {
                    TokenUser = db!!.getString(TinyDB.TOKEN_USER)
                    if (TextUtils.isEmpty(TokenUser) && getUser() != null) {
                        TokenUser = getUser()!!.token
                        if (TextUtils.isEmpty(TokenUser)) {

                        } else {
                            db!!.putString(TinyDB.TOKEN_USER, TokenUser)
                        }
                    }
                }
                return TokenUser
            }
            set(token) {
                TokenUser = token
                db!!.putString(TinyDB.TOKEN_USER, token)
                if (user != null) {
                    setupSocket(user)
                }
            }


        fun getUser(): UserChatCore? {

            if (db == null) {
                db = TinyDB(appContext!!)
            }

            try {
                if (user == null) {

                    val obj = db!!.getObject(UserChatCore.USER_MODEL, UserChatCore::class.java)
                    if (obj != null) {
                        user = obj as UserChatCore


                        //lay lai token
                        if (TextUtils.isEmpty(user!!.token)) {
                            val token = tokenUser
                            if (!token.isEmpty()) {
                                user!!.token = token
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return user
        }

        fun getHeader(): String {
            var header = ""
            if (user == null) {
                getUser()
            }
            if (user != null) {
                header = "bearer ${user!!.token}"
            }
            return header
        }


        fun setUser(user: UserChatCore?) {
            ChatApplication.user = user
            db!!.putObject(UserChatCore.USER_MODEL, user)

            //neu user == null =>logout
            if (user == null) {
                setUserPassword("")
            }
        }

        fun setUserPassword(password: String) {
            db!!.putString(UserChatCore.USER_PASSWORD, password)
        }

        var followers = arrayListOf<UserChatCore>()



        var ownerMention: String? = ""
        var replaceOwnerMention: String? = ""
        var banOrYou: String? = ""

        var ownerMentionNotify: String? = ""
        var replaceOwnerMentionNotify: String? = ""


        fun initSocket(user: UserChatCore?) {
            if (user != null) {

                if (socket == null) {//tranh tao nhieu ket noi socket
                    try {
                        val token = tokenUser//user.token
//                        val tokenMXT = user.mxtAccessToken
                        val params = "?token=" + token /*+ "&mxt=" + tokenMXT*/ + "&os=" + OS + "&deviceId=" + MyUtils.getIMEI(appContext)
                        var url = URL_SERVER_CHAT + params


                        //option
                        val options = IO.Options()
                        options.forceNew = true//co nen su dung lai ket noi hien co hay khong
                        options.reconnection = true
                        options.timeout = 10000
                        options.reconnectionDelay = 2000//tăng 1000 -> 2000 ms
                        options.reconnectionDelayMax = 4000
                        options.reconnectionAttempts = Integer.MAX_VALUE
                        options.transports = arrayOf(io.socket.engineio.client.transports.WebSocket.NAME)

                        //pass trust certification CA not trust
                        val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
                        options.callFactory = okHttpClient
                        options.webSocketFactory = okHttpClient

                        socket = IO.socket(url, options)
//                        Logger.d("Socket created")


                    } catch (e: URISyntaxException) {
                        throw RuntimeException(e)
                    }

                }

                //@[Hà Phan](userid:2681)
                try {
                    ownerMention = "@[${user.name}](userid:${user._id})"
                    banOrYou = appContext!!.getString(R.string.you)
                    replaceOwnerMention = "@[$banOrYou](userid:${user._id})"

                    ownerMentionNotify = "@${user.name}"
                    replaceOwnerMentionNotify = "@$banOrYou"
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }


        fun getSocketInitIfNull(): Socket? {
            if (socket == null) {
                val user = getUser()
                if (user != null) {
                    initSocket(user)
                    openSocket()
                }
            }

            //neu da setup socket xong roi
//            if (isSetupSocket) {
            if (socket != null && !socket!!.connected()) {
                socket!!.connect()
            }
//            }
            return socket
        }

        fun isSocketConnected(): Boolean {
            return socket != null && socket!!.connected()
        }

        var socket: Socket? = null
            get() = when (field) {
                null -> null
                else -> {
                    if (!field!!.connected()) {
                        //neu chua ket noi va ko co dang ket noi thi goi ket noi lai
                        if (!isConnecting) {
                            field!!.connect()
                        }
                    }
                    field
                }
            }


        /**
         * userId = 158852
         * token =  28df776c06a2531374799ab4f7d8e3bf
         * http://52.187.112.191/?userId=158852&token=28df776c06a2531374799ab4f7d8e3bf&phone=0938936128&os=Android
         */
        fun setupSocket(user: UserChatCore?) {
            if (user != null && !TextUtils.isEmpty(user._id)) {
                try {
                    initSocket(user)
                    socket = getSocketInitIfNull()
                    openSocket()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        var isSetupSocket = false
        private fun openSocket() {
            //        MyUtils.log("openSocket socket null = " + (socket == null));
            if (!isSetupSocket) {
                socket = getSocketInitIfNull()
                socket?.takeUnless {
                    it.connected()
                }?.apply {
                    on(Socket.EVENT_CONNECT, onConnect)
                    on(Socket.EVENT_CONNECTING, onConnecting)
                    on(Socket.EVENT_RECONNECTING, onConnecting)
                    on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout)
                    on(Socket.EVENT_RECONNECT, onReconnect)
                    on(Socket.EVENT_DISCONNECT, onDisconnect)
                    on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                    on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
                    on(Socket.EVENT_ERROR, onError)

                    on("newMessage", onNewMessage)
                    on("updateMessage", onUpdateMessage)
                    on("removeMessage", onRemoveMessage)
                    on("logIsViewed", logIsViewed)

                    on("newNotify", newNotify)
                    on("notifyViewUpdate", notifyViewUpdate)
                    on("removeNotify", removeNotify)

                    //user
                    on("onlineUsers", onlineUsers)//chi tra ve 1 lan khi ket noi
                    on("userConnected", userConnected)
                    on("userDisconnected", userDisconnected)
                    Logger.d("Socket connect() new")

                    connect()
                    isSetupSocket = true
                }
            }
        }

        fun closeSocket() {
            MyUtils.log("closeSocket socket null = " + (socket == null))
//            socket = getSocketInitIfNull()
            if (socket != null) {
                socket?.apply {
                    disconnect()
                    off(Socket.EVENT_CONNECT, onConnect)
                    off(Socket.EVENT_CONNECTING, onConnecting)
                    off(Socket.EVENT_RECONNECTING, onConnecting)
                    off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout)
                    off(Socket.EVENT_RECONNECT, onReconnect)
                    off(Socket.EVENT_DISCONNECT, onDisconnect)
                    off(Socket.EVENT_CONNECT_ERROR, onConnectError)
                    off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
                    off(Socket.EVENT_ERROR, onError)

                    off("newMessage", onNewMessage)
                    off("updateMessage", onUpdateMessage)
                    off("removeMessage", onRemoveMessage)
                    off("logIsViewed", logIsViewed)

                    off("newNotify", newNotify)
                    off("notifyViewUpdate", notifyViewUpdate)
                    off("removeNotify", removeNotify)


                    off("onlineUsers", onlineUsers)
                    off("userConnected", userConnected)
                    off("userDisconnected", userDisconnected)

                }
            }

            socket = null
            isSetupSocket = false

        }

        fun logout(){
            closeSocket()
            setUser(null)
        }


        fun setSocketState(socketState: String) {
            //log trang thai socket
            val intent = Intent(ChatActivity.ACTION_STATE_SOCKET)
            intent.putExtra(ChatActivity.STRING_STATE_SOCKET, socketState)
            if (appContext != null) {
                appContext?.sendBroadcast(intent)
            }
        }

        private val onConnect = Emitter.Listener {
            isConnecting = false
            //chatactivity
            setSocketState(SocketState.CONNECTED)

            //recent chat room
            EventBus.getDefault().postSticky(SocketConnectEvent(true))

            //context.sendBroadcast(new Intent(ChatActivity.ACTION_ONLINE));
            getUnreadRoomCount()
            //goi updateOneSignalUserId
//            updateOneSignalUserId()
            Logger.d("Socket connected")

        }

        private val onDisconnect = Emitter.Listener {
            isConnecting = false
            setSocketState(SocketState.DISCONNECT)
            Logger.d("Socket disconnected")
            /*if (appContext != null) {
                appContext!!.sendBroadcast(Intent(ChatActivity.ACTION_OFFLINE))
            }*/

        }

        private val onConnectError = Emitter.Listener { args ->
            val cause = if (args == null) "no args" else args[0].toString()
//            Logger.d("Socket Error connecting $cause")
            /*if (appContext != null) {
                appContext!!.sendBroadcast(Intent(ChatActivity.ACTION_OFFLINE))
            }*/

            setSocketState(SocketState.ERROR + " " + cause)
            isConnecting = false
        }


        private val onConnecting = Emitter.Listener {
            isConnecting = true
            setSocketState(SocketState.CONNECTING)
        }

        private val onConnectTimeout = Emitter.Listener {
            isConnecting = false
            setSocketState(SocketState.CONNECT_TIMEOUT)
        }

        private val onReconnect = Emitter.Listener {
            isConnecting = false
            setSocketState(SocketState.RECONNECT)
        }


        /////////////////////////////////////////////////////////////////////////////////////////////

        var lastLogId = ""
        private val onNewMessage = Emitter.Listener { args ->

            MyUtils.showToastDebugInThread(appContext, "onNewMessage")
            val log = RoomLog.parseRoomLog(appContext, *args)

            if (log != null) {

                if (log._id.equals(lastLogId)) {
                    //trung tin thi ko lam gi
//                    MyUtils.log("duplicate message " + log.content.toString())
                } else {

                    lastLogId = log._id
                    //xu ly khi nhan dc 1 tin moi, goi bus cho man hinh chat
                    val event = RoomLogEvent_Chat(log)
                    EventBus.getDefault().postSticky(event)

                    //post man hinh recent, doi trang thai => co tin nhan moi
                    val view = RoomLogEvent_Have_Message(log)
                    EventBus.getDefault().post(view)

                    //lay lai so count
                    getUnreadRoomCount()

                    reloadNotifyWorkchat()

                    //test
//                    BubbleHoverMenuService.showFloatingMenu(appContext, AddRoomEvent(log.roomId, log.roomInfo.roomAvatar, false))

                }

            }
        }

        private val onRemoveMessage = Emitter.Listener { args ->
            val log = RoomLog.parseRoomLog(appContext, *args)

            if (log != null) {
                //xu ly khi nhan dc 1 tin moi, goi bus cho man hinh chat
                log.isDeleted = true

                //man hinh chat
                val event = RoomLogEvent_Chat(log)
                EventBus.getDefault().postSticky(event)

                //recent
                //post man hinh recent, doi trang thai => co tin nhan moi
                val view = RoomLogEvent_Have_Message(log)
                EventBus.getDefault().post(view)

                //lay lai so count
                getUnreadRoomCount()

            }
        }

        private val onUpdateMessage = Emitter.Listener { args ->
            val log = RoomLog.parseRoomLog(appContext, *args)

            if (log != null) {
                //xu ly khi nhan dc 1 tin moi, goi bus cho man hinh chat
                log.isUpdated = true

                //man hinh chat
                val event = RoomLogEvent_Chat(log)
                EventBus.getDefault().postSticky(event)

                //recent - thi van hien binh thuong
                //post man hinh recent, doi trang thai => co tin nhan moi
                val view = RoomLogEvent_Have_Message(log)
                EventBus.getDefault().post(view)

                //lay lai so count
                getUnreadRoomCount()
            }
        }

        private val logIsViewed = Emitter.Listener { args ->
            val log = RoomLog.parseRoomLog(appContext, *args)

            if (log != null) {
                //man hinh chat gan day thay the room log dang
                /*Intent intent=new Intent(Fragment_1_Recent_Chat_Room.ACTION_UPDATE_CHAT_ROOM);
                intent.putExtra(Room.ROOM_ID,log.getRoomId());
                context.sendBroadcast(intent);*/
                val event = RoomLogEvent_IsViewed(log)
                EventBus.getDefault().post(event)

                //lay lai so count
                getUnreadRoomCount()

            }

            reloadNotifyWorkchat()
        }

        private val onlineUsers = Emitter.Listener { args ->
            val s = args[0].toString()
            if (!TextUtils.isEmpty(s)) {
                db!!.putString(UserChat.LIST_USER_ONLINE, s)
            }
        }

        private val userConnected = Emitter.Listener { args ->
            val s = args[0].toString()
            if (!TextUtils.isEmpty(s)) {
                //{"errorCode":0,"error":null,"data":{"userId":"5d09b51fc3f5e71bf4d6924c"}}
                try {
                    val obj = JSONObject(s)
                    val data = obj.getJSONObject("data")
                    val userId: Long = data.getLong("userId")

                    val intent: Intent = Intent(Contact_Fragment_2_Online.ACTION_WHEN_USER_ONLINE)
                    intent.putExtra(UserChatCore.USER_ID, userId)
                    appContext!!.sendBroadcast(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private val userDisconnected = Emitter.Listener { args ->
            try {
                val s = args[0].toString()
                if (!TextUtils.isEmpty(s)) {
                    //{"errorCode":0,"error":null,"data":{"userId":"5d09b51fc3f5e71bf4d6924c"}}
                    val obj = JSONObject(s)
                    val data = obj.getJSONObject("data")
                    val userId: Long = data.getLong("userId")

                    val intent: Intent = Intent(Contact_Fragment_2_Online.ACTION_WHEN_USER_OFFLINE)
                    intent.putExtra(UserChatCore.USER_ID, userId)
                    appContext!!.sendBroadcast(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun reloadNotifyWorkchat() {
            //bao cho myxteam lay lai thong tin so notify tu workchat
            this.appContext!!.sendBroadcast(Intent("ACTION_GET_NUMBER_NOTIFY_FROM_WORKCHAT"))
        }


        //END SOCKET//////////////////////////////////////////////////////////////////////////////////////
        /**
         * Khi vua ket noi, khi resume lai man hinh main thi goi lai ham nay de cap nhat so moi nhat
         */
        fun getUnreadRoomCount() {
            if (isSocketConnected()) {
                socket?.takeIf { it.connected() }?.apply {
                    emit<JSONObject>(
                            "getUnreadRoomCount",
                            onResult = { data ->
                                //{"errorCode":0,"error":null,"data":{"total":2,"group":2,"channel":0,"support":0,"private":0,"rooms":[{"_id":"5d031ef6ae532d269c25dbc6","type":"custom"},{"_id":"5d35946e6bdd092f401bfe5d","type":"custom"}]}}
                                if (data.has("total")) {
                                    val count = data.getInt("total")
                                    MyUtils.setNumberChatUnread(count)
//                                Logger.d("getUnreadRoomCount: $count")
                                }
                            },
                            onFailure = { errorCode, error ->
                                Logger.d("errorCode: $errorCode \nerror: $error")
                            }
                    )
                }
            }
            //lay luon so ntf chua doc
            getUnreadNotifyCount()
        }

        //NOTIFY FRAGMENT////////////////////////////////////////////////////////////////////////////////
        fun getUnreadNotifyCount() {
            if (isSocketConnected()) {
                socket?.takeIf { it.connected() }?.apply {

                    emit<JSONObject>(
                            "getUnreadNotifyCount",
                            onResult = { data ->
                                if (data.has("count")) {
                                    val count = data.getInt("count")
                                    MyUtils.setNumberNotifyUnread(count)
                                    //Logger.d("getUnreadNotifyCount: $count")

                                    //neu co so notify thi bao lay lai de dong bo
//                                    if (count > 0) {
                                    appContext!!.sendBroadcast(Intent(NotificationNavFrag.ACTION_RELOAD_DATA))
//                                    }
                                }
                            },
                            onFailure = { errorCode, error ->
                                Logger.d("errorCode: $errorCode \nerror: $error")
                            }
                    )
                }
            }
        }

        private val newNotify = Emitter.Listener { args ->
            args?.getData<JSONObject>()?.toNotification()?.let {
                getUnreadNotifyCount()
                //Logger.d("base ntf: $it \n${it._id}, ${it.type}")
            }
        }

        private val notifyViewUpdate = Emitter.Listener { args ->
//            Logger.d("${args?.getData()<JSONObject>()}")

            args?.getData<JSONObject>()?.let { data ->
                val ids: MutableList<String> = mutableListOf()
                val tag = "notifyIds"
                if (!data.isNull(tag)) {
                    val array = data.getJSONArray(tag)
                    for (i in 0 until array.length()) {
                        ids.add(array[i].toString())
                    }
                }

//                val isViewed = data.optBoolean("isView", false)
                getUnreadNotifyCount()
            }

        }

        private val removeNotify = Emitter.Listener { _ ->
            //lay luon so ntf chua doc
            getUnreadNotifyCount()

        }
        //NOTIFY FRAGMENT////////////////////////////////////////////////////////////////////////////////

        private val onError = Emitter.Listener { args ->
            isConnecting = false
            if (args != null) {
                MyUtils.log("Socket Error connecting " + args[0].toString())
                //{"message":"jwt malformed","code":"invalid_token","type":"UnauthorizedError"}
                val json = args[0].toString()
                try {
                    Logger.d(json)
                    if (json.contains("code")) {
                        val obj = JSONObject(json)
                        if (obj.has("code") && !obj.isNull("code")) {
                            val code = obj.getString("code")
                            if (code == "invalid_token") {
                                if (obj.has("type") && !obj.isNull("type")) {
                                    val type = obj.getString("type")
                                    if (type == "UnauthorizedError") {

                                        closeSocket()

                                        //todo chuyen thanh event
//                                        appContext!!.sendBroadcast(Intent(MainActivity.ACTION_WHEN_TOKEN_INVALID))
                                        whenTokenInvalid()

                                    }
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        //Events
        fun reopenMainActivity(): Unit {
            eventControlListener?.reOpenMainActivity()
        }

        fun openAddContactActivity(): Unit {
            eventControlListener?.openAddContactActivity()
        }

        fun setNumberChatUnread(count: Int): Unit {
            eventControlListener?.setNumberChatUnread(count)
        }

        fun setNumberNotifyUnread(count: Int): Unit {
            eventControlListener?.setNumberNotifyUnread(count)
        }

        fun whenTokenInvalid() {
            eventControlListener?.whenTokenInvalid()
        }

        fun openMainActivityAndSearchRoom() {
            eventControlListener?.openMainActivityAndSearchRoom()
        }

        fun openDeeplink(roomId: String, roomLogId: String, project: Project, params: ArrayList<String>) {
            eventControlListener?.openDeeplink(roomId, roomLogId, project, params)
        }

        fun openProfile(username: String) {
            if(!TextUtils.isEmpty(username)) {
                eventControlListener?.openProfile(username)
            }
        }


        fun whenAppDestroy() {
            /*if (realmChat != null && !realmChat!!.isClosed) {
                realmChat!!.close()
            }*/
            closeSocket()
            isSetupSocket = false
            socket = null
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


}