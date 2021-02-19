package com.workchat.core.models.realm;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.models.chat.Item;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.Page;
import com.workchat.core.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Room extends RealmObject implements Parcelable, Cloneable, Comparable {
    public static final int NOT_INT_ROOM_CODE = 100;

    public static final String ROOM = "ROOM";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String ROOM_NAME = "ROOM_NAME";
    public static final java.lang.String ROOM_AVATAR = "ROOM_AVATAR";


    public static final String ROOM_TYPE_PRIVATE = "private";
    public static final String ROOM_TYPE_ITEM = "item";
    public static final String ROOM_TYPE_PAGE = "page";
    public static final String ROOM_TYPE_CUSTOM = "custom";//room tu tao va channel thi moi cho phep member roi khoi
    public static final String ROOM_TYPE_CHANNEL = "channel";//room tu tao va channel thi moi cho phep member roi khoi
    public static final String ROOM_TYPE_CHANNEL_ADMIN = "channelAdmin";
    public static final String ROOM_TYPE_SUPPORT = "support";
    public static final String ROOM_TYPE_PROJECT = "project";


    public static final String CHAT_TYPE = "CHAT_TYPE";//dung trong chat recent adapter
    public static final String ROOM_POSITION = "ROOM_POSITION";
    public static final String ROOM_LIST_SAVE = "ROOM_LIST_SAVE";
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String ROOM_SUPPORT_CACHE = "ROOM_SUPPORT_CACHE";


    @PrimaryKey
    private String _id;
    private String type;
    private String roomName;
    private String roomAvatar;
    private String userId1;
    private String userId2;
    private String userIdGuest;
    private String userIdOwner;
    private UserChat lastLogAuthor;
    private boolean isViewed;
    private long createDate;
    private long lastLogDate;
    //local
    private long lastLogDateLong;

    @Ignore
    private Page page;
    @Ignore
    private Item item;
    @Ignore
    private ArrayList<Member> members = new ArrayList<>();
    private LastLog lastLog;////json se bi lỗi khi truyền giữa các activity, set null trươc khi truyen

    //bo sung 7/3/2018 cho channel
    private String description;
    private boolean isPrivate;
    private String joinLink;
    private boolean onlyAdminAddUser;
    private boolean signMessage;//co hieenj ten admin trong message hay ko?

    public boolean isEnableChatWithAdmin() {
        return enableChatWithAdmin;
    }

    public void setEnableChatWithAdmin(boolean enableChatWithAdmin) {
        this.enableChatWithAdmin = enableChatWithAdmin;
    }

    private boolean enableChatWithAdmin;//cho phep chat voi admin hay khong?
    //BO SUNG 18/09/2018
    private String channelId;
    private String channelName;
    private String channelAvatar;

    public String getProjectLink() {
        if(project!=null){
            projectLink = project.getProjectLink();
        }
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    private String projectLink;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Ignore
    private Project project;

    //bo sung local
    private boolean isPin;
    private boolean isMember;//user co phai la member cua room nay khong?
    private int memberCount;//so member trong group/channel

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public String getUserIdGuest() {
        return userIdGuest;
    }

    public void setUserIdGuest(String userIdGuest) {
        this.userIdGuest = userIdGuest;
    }

    public String getUserIdOwner() {
        return userIdOwner;
    }

    public void setUserIdOwner(String userIdOwner) {
        this.userIdOwner = userIdOwner;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getLastLogDate() {
        return lastLogDate;
    }

    public void setLastLogDate(long lastLogDate) {
        this.lastLogDate = lastLogDate;

        //parse sang kieu long ho tro cho viec sap xep nhanh hon
        setLastLogDateLong(lastLogDate * 1000);
    }

    public UserChat getLastLogAuthor() {
        return lastLogAuthor;
    }

    public void setLastLogAuthor(UserChat lastLogAuthor) {
        this.lastLogAuthor = lastLogAuthor;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }


    public ArrayList<Member> getMembers() {
        if (members == null) members = new ArrayList<Member>();
        //bo minh ra
        /*try {
            UserMBN user = ChatApplication.getUser();
            if (user != null) {
                String id = user.get_id();
                if (!TextUtils.isEmpty(id)) {
                    for (int i = 0; i < members.size(); i++) {
                        if (members.get(i).getUserId() == Long.parseLong(id)) {
                            members.remove(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public LastLog getLastLog() {
        return lastLog;
    }

    public void setLastLog(LastLog lastLog) {
        this.lastLog = lastLog;
    }

    public static Room parseRoom(Context context, Object... args) {
        Room room = null;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        room = new Gson().fromJson(data, Room.class);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseRoom" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return room;
    }

    public static ArrayList<Room> parseListRoom(Context context, Object... args) {
        ArrayList<Room> list = null;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        java.lang.reflect.Type type = new TypeToken<ArrayList<Room>>() {
                        }.getType();
                        list = new Gson().fromJson(data, type);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseListRoom" + errMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isSuccess(Context context, Object... args) {
        boolean isSuccess = false;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    isSuccess = true;
                } else {
                    String errMessage = json.getString("error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getErrorMessage(Context context, Object... args) {
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {

                } else {
                    String errMessage = json.getString("error");
                    return errMessage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getErrorCode(Context context, Object... args) {
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                return code;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getRoomAvatar() {
        if (roomAvatar == null) roomAvatar = "";
        return roomAvatar;
    }

    public void setRoomAvatar(String roomAvatar) {
        this.roomAvatar = roomAvatar;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getJoinLink() {
        return joinLink;
    }

    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }

    public boolean isOnlyAdminAddUser() {
        return onlyAdminAddUser;
    }

    public void setOnlyAdminAddUser(boolean onlyAdminAddUser) {
        this.onlyAdminAddUser = onlyAdminAddUser;
    }

    public boolean isSignMessage() {
        return signMessage;
    }

    public void setSignMessage(boolean signMessage) {
        this.signMessage = signMessage;
    }

    public boolean isPin() {
        return isPin;
    }

    public void setPin(boolean pin) {
        isPin = pin;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public int getMemberCount() {
        members = getMembers();
        return memberCount = members.size() + 1;//do da remove minh ra khoi nen +1
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    /*@Override
    public int compareTo(@NonNull Object o) {
        Room room = (Room) o;
        if (room.get_id().equals(this._id) && room.getRoomName().equals(this.roomName)) {
            return 0;
        }
        return 1;
    }*/

    @Override
    public boolean equals(Object obj) {
        Room room = (Room) obj;
        return room.get_id().equals(this._id);
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelAvatar() {
        return channelAvatar;
    }

    public void setChannelAvatar(String channelAvatar) {
        this.channelAvatar = channelAvatar;
    }


    public Room duplicate() {
        Room r = null;

        try {
            r = new Room();
            r.setPin(isPin());
            r.set_id(get_id());
            r.setChannelAvatar(getChannelAvatar());
            r.setChannelId(getChannelId());
            r.setChannelName(getChannelName());
            r.setCreateDate(getCreateDate());
            r.setDescription(getDescription());
            r.setItem(getItem());
            r.setJoinLink(getJoinLink());
            r.setLastLog(getLastLog());
            r.setLastLogAuthor(getLastLogAuthor());
            r.setLastLogDate(getLastLogDate());
            r.setMember(isMember());
            r.setMemberCount(getMemberCount());
            r.setMembers(getMembers());
            r.setOnlyAdminAddUser(isOnlyAdminAddUser());
            r.setPage(getPage());
            r.setPrivate(isPrivate());
            r.setRoomAvatar(getRoomAvatar());
            r.setRoomName(getRoomName());
            r.setSignMessage(isSignMessage());
            r.setEnableChatWithAdmin(enableChatWithAdmin);
            r.setType(getType());
            r.setUserId1(getUserId1());
            r.setUserId2(getUserId2());
            r.setUserIdGuest(getUserIdGuest());
            r.setUserIdOwner(getUserIdOwner());
            r.setViewed(isViewed());
            r.setProjectLink(getProjectLink());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public long getLastLogDateLong() {
        if (lastLogDateLong <= 0) {
            //parse sang kieu long ho tro cho viec sap xep nhanh hon
            setLastLogDateLong(lastLogDate * 1000);
        }
        return lastLogDateLong;
    }

    public void setLastLogDateLong(long lastLogDateLong) {
        this.lastLogDateLong = lastLogDateLong;
    }


    public Room() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.type);
        dest.writeString(this.projectLink);
        dest.writeString(this.roomName);
        dest.writeString(this.roomAvatar);
        dest.writeString(this.userId1);
        dest.writeString(this.userId2);
        dest.writeString(this.userIdGuest);
        dest.writeString(this.userIdOwner);
        dest.writeParcelable(this.lastLogAuthor, flags);
        dest.writeByte(this.isViewed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createDate);
        dest.writeLong(this.lastLogDate);
        dest.writeLong(this.lastLogDateLong);
        dest.writeParcelable(this.page, flags);
        dest.writeParcelable(this.item, flags);
        dest.writeTypedList(this.members);
        dest.writeParcelable(this.lastLog, flags);
        dest.writeString(this.description);
        dest.writeByte(this.isPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.joinLink);
        dest.writeByte(this.onlyAdminAddUser ? (byte) 1 : (byte) 0);
        dest.writeByte(this.signMessage ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableChatWithAdmin ? (byte) 1 : (byte) 0);

        dest.writeString(this.channelId);
        dest.writeString(this.channelName);
        dest.writeString(this.channelAvatar);
        dest.writeByte(this.isPin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMember ? (byte) 1 : (byte) 0);
        dest.writeInt(this.memberCount);
        /*if (project != null) {
            dest.writeParcelable(this.project, flags);
        }*/
    }

    protected Room(Parcel in) {
        this._id = in.readString();
        this.type = in.readString();
        this.projectLink = in.readString();
        this.roomName = in.readString();
        this.roomAvatar = in.readString();
        this.userId1 = in.readString();
        this.userId2 = in.readString();
        this.userIdGuest = in.readString();
        this.userIdOwner = in.readString();
        this.lastLogAuthor = in.readParcelable(UserChat.class.getClassLoader());
        this.isViewed = in.readByte() != 0;
        this.createDate = in.readLong();
        this.lastLogDate = in.readLong();
        this.lastLogDateLong = in.readLong();
        this.page = in.readParcelable(Page.class.getClassLoader());
        this.item = in.readParcelable(Item.class.getClassLoader());
        this.members = in.createTypedArrayList(Member.CREATOR);
        this.lastLog = in.readParcelable(LastLog.class.getClassLoader());
        this.description = in.readString();
        this.isPrivate = in.readByte() != 0;
        this.joinLink = in.readString();
        this.onlyAdminAddUser = in.readByte() != 0;
        this.signMessage = in.readByte() != 0;
        this.enableChatWithAdmin = in.readByte() != 0;

        this.channelId = in.readString();
        this.channelName = in.readString();
        this.channelAvatar = in.readString();

        this.isPin = in.readByte() != 0;
        this.isMember = in.readByte() != 0;
        this.memberCount = in.readInt();

        /*try {
            this.project = in.readParcelable(Project.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    protected Room clone() {
        Room room = null;
        try {
            room = (Room) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return room;
    }

    @Override
    public int compareTo(Object o) {
        Room compare = (Room) o;
        if (compare.get_id().equals(this.get_id()) &&
                compare.getLastLog().getChatLogId().equals(this.getLastLog().getChatLogId())) {
            return 0;
        }
        return 1;
    }
}
