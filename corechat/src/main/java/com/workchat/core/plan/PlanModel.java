package com.workchat.core.plan;

import android.os.Parcel;
import android.os.Parcelable;

import com.workchat.core.models.chat.Member;

import java.util.ArrayList;

public class PlanModel implements Parcelable {
    public static final String PLAN_MODEL = "PLAN_MODEL";

    public static final String ACTIVE = "active";
    public static final String CLOSE = "close";
    public static final String DELETE = "delete";

    private String title;
    private long timeStamp;
    private int duration;
    private Place place;
    private String note;
    private ArrayList<Vote> result;
    private ArrayList<Comment> comments;
    private String status;//active, close, delete
    private ArrayList<Integer> likes;

    //bo sung local
    private Member owner;
    private int memberCount;
    private String roomId;
    private String chatLogId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAddress(){
        String address = "";
        if(place!=null){
            address = place.getAddress();
        }
        return address;
    }
    public String getLat(){
        String address = "";
        if(place!=null){
            address = place.getLat();
        }
        return address;
    }
    public String getLng(){
        String address = "";
        if(place!=null){
            address = place.getLng();
        }
        return address;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Vote> getResult() {
        if(result==null)result = new ArrayList<>();
        return result;
    }

    public void setResult(ArrayList<Vote> result) {
        this.result = result;
    }

    public ArrayList<Comment> getComments() {
        if(comments==null)comments = new ArrayList<>();
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Integer> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Integer> likes) {
        this.likes = likes;
    }

    public int getPositionSelected(String id) {
        int positionSelected = -1;

        if(result!=null && result.size()>0){
            for (int i = 0; i < result.size(); i++) {
                Vote vote = result.get(i);
                if (vote.getUserId().equals(id)){
                    String status = vote.getStatus();
                    switch (status){
                        case Vote.YES:
                            positionSelected=0;
                            break;
                        case Vote.NO:
                            positionSelected=1;
                            break;
                        case Vote.MAYBE:
                            positionSelected=2;
                            break;
                    }
                    break;
                }
            }
        }
        return positionSelected;
    }

    public Member getOwner() {
        return owner;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getNumberVote(String status) {
        int number = 0;
        if(result!=null && result.size()>0){
            for (int i = 0; i < result.size(); i++) {
                Vote vote = result.get(i);
                if(vote.getStatus().equals(status)){
                    number+=1;
                }
            }
        }
        return number;
    }

    public PlanModel changeVote(int positionSelected, String id) {
        //tim xem user nay da vote chua, neu co thi sua, chua co thi them moi
        Vote vote=null;
        int position = -1;
        if(result!=null && result.size()>0){
            for (int i = 0; i < result.size(); i++) {
                Vote item = result.get(i);
                if (item.getUserId().equals(id)) {
                    vote = item;
                    position = i;
                    break;
                }
            }
        }
        if(vote!=null){
            int selected = getPositionSelected(id);
            //neu vi tri chon co thay doi thi moi cap nhat
            if(positionSelected!=selected){
                String status = getStatus(positionSelected);
                result.get(position).setStatus(status);
            }
        }else{
            //them mot vote moi
            Vote myVote = new Vote();
            myVote.setUserId(id);
            String status = getStatus(positionSelected);
            myVote.setStatus(status);
            getResult().add(myVote);
        }

        return this;
    }

    public String getStatus(int positionSelected){
        String result = "";
        switch (positionSelected){
            case 0:
                result = Vote.YES;
                break;
            case 1:
                result = Vote.NO;
                break;
            case 2:
                result = Vote.MAYBE;
                break;
        }
        return result;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }


    public void setChatLogId(String chatLogId) {
        this.chatLogId = chatLogId;
    }

    public String getChatLogId() {
        return chatLogId;
    }

    public PlanModelLocal getPlanModelLocal() {
        PlanModelLocal plan = new PlanModelLocal(title, timeStamp,duration, null, note);
        return plan;
    }


    public PlanModel() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.timeStamp);
        dest.writeInt(this.duration);
        dest.writeParcelable(this.place, flags);
        dest.writeString(this.note);
        dest.writeTypedList(this.result);
        dest.writeTypedList(this.comments);
        dest.writeString(this.status);
        dest.writeList(this.likes);
        dest.writeParcelable(this.owner, flags);
        dest.writeInt(this.memberCount);
        dest.writeString(this.roomId);
        dest.writeString(this.chatLogId);
    }

    protected PlanModel(Parcel in) {
        this.title = in.readString();
        this.timeStamp = in.readLong();
        this.duration = in.readInt();
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.note = in.readString();
        this.result = in.createTypedArrayList(Vote.CREATOR);
        this.comments = in.createTypedArrayList(Comment.CREATOR);
        this.status = in.readString();
        this.likes = new ArrayList<Integer>();
        in.readList(this.likes, Integer.class.getClassLoader());
        this.owner = in.readParcelable(Member.class.getClassLoader());
        this.memberCount = in.readInt();
        this.roomId = in.readString();
        this.chatLogId = in.readString();
    }

    public static final Creator<PlanModel> CREATOR = new Creator<PlanModel>() {
        @Override
        public PlanModel createFromParcel(Parcel source) {
            return new PlanModel(source);
        }

        @Override
        public PlanModel[] newArray(int size) {
            return new PlanModel[size];
        }
    };
}
