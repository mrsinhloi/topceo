package com.workchat.core.models.chat;

/**
 * Created by MrPhuong on 2017-08-30.
 * Tham chieu ContentTypeLayout.java
 */

public class ContentType {

    //local
    public static final String DATE     = "date";//1

    public static final String TEXT     = "text";//2
    public static final String IMAGE    = "image";//3
    public static final String FILE     = "file";//4
    public static final String LINK     = "link";//5

    public static final String ACTION   = "action";//6
    public static final String LOCATION = "location";//7
    public static final String NEW_PAYMENT = "newPaymentReceive";//8
    public static final String PAYMENT_STATUS = "paymentStatusChanged";//9

    //bo sung 2 dang item, video
    public static final String ITEM     = "item"; //10 dung de pin tren top khi chat voi san pham cua nguoi ban
    public static final String ALBUM    = "album";//11
    public static final String VIDEO    = "video";//12
    public static final String VOICE    = "voice";//3
    public static final String CONTACT  = "contact";//14
    public static final String PLAN     = "plan";//15

    //bo sung meeting
    public static final String MEETING  = "zoomMeeting";



}