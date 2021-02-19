package com.topceo.crop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;

public class AppAnalyze {

    private Bitmap imageUpload;
    private static AppAnalyze _instance;

    private AppAnalyze() {
    };

    private Context mcontext;
    private String likeword;
    private String commentword;
    private String winkword;
    private String globalrank;
    private String localrank;
    private int TopMode;
    private String UserIdSearch;
    private String UserIdFollow;
    private Uri imageUri;

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    private Uri videoUri;
    private String cameramoment;
    private Boolean isCameraFont;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    private int IntentActivityId;
    private int positionjoinwinkHomeActivity;
    private int positionjoinwinkHashtagActivity;
    private int positionjoinwinkUserImageActivity;
    private int positionjoinwinkUserImageInfoFromUserName;
    private int positionuserfollow;
    private int positioncheckfacebook;

    private Handler mhandler_UserImageInfo;
    private Handler mhandler_UserImageInfoFromUserName;
    private Handler mhandler_Home;
    private Handler mhandler_UserImageList;
    private Handler mhandler_Hashtag;
    private Handler mhandler_TopImage;
    
    private String imageid;

    private ArrayList<String> contactlist = new ArrayList<String>();

    public int CurrentActivity;

    public ArrayList<String> getContactlist() {
        return this.contactlist;
    }

    public void setContactlist(ArrayList<String> contactlist) {
        this.contactlist = contactlist;
    }


    public synchronized static AppAnalyze getInstance() {
        if (_instance == null) {
            _instance = new AppAnalyze();
        }

        return _instance;
    }



    public Bitmap getImageUpload() {
        return imageUpload;
    }

    public void setImageUpload(Bitmap imageUpload) {
        this.imageUpload = imageUpload;
    }

    public Context getMcontext() {
        return this.mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public String getGlobalrank() {
        return this.globalrank;
    }

    public void setGlobalrank(String globalrank) {
        this.globalrank = globalrank;
    }

    public String getLocalrank() {
        return this.localrank;
    }

    public void setLocalrank(String localrank) {
        this.localrank = localrank;
    }

    public String getLikeword() {
        return this.likeword;
    }

    public void setLikeword(String likeword) {
        this.likeword = likeword;
    }

    public String getCommentword() {
        return this.commentword;
    }

    public void setCommentword(String commentword) {
        this.commentword = commentword;
    }

    public String getWinkword() {
        return this.winkword;
    }

    public void setWinkword(String winkword) {
        this.winkword = winkword;
    }

    public String encodepassword(String password) {

        char[] passtmp = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < passtmp.length; i++) {
            char c = passtmp[i];
            int j = (int) c + i;
            char[] cc = Character.toChars(j);
            pass.append(cc);
        }

        return pass.toString();
    }

    public String decodepassword(String password) {
        char[] passtmp = password.toCharArray();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < passtmp.length; i++) {
            char c = passtmp[i];
            int j = (int) c - i;
            char[] cc = Character.toChars(j);
            pass.append(cc);
        }

        return pass.toString();
    }

    public int getTopMode() {
        return this.TopMode;
    }

    public void setTopMode(int topMode) {
        this.TopMode = topMode;
    }

    public int getIntentActivityId() {
        return this.IntentActivityId;
    }

    public void setIntentActivityId(int intentActivityId) {
        this.IntentActivityId = intentActivityId;
    }

    public Handler getMhandler_UserImageInfo() {
        return this.mhandler_UserImageInfo;
    }

    public void setMhandler_UserImageInfo(Handler mhandler_UserImageInfo) {
        this.mhandler_UserImageInfo = mhandler_UserImageInfo;
    }

    public int getPositionjoinwinkHomeActivity() {
        return this.positionjoinwinkHomeActivity;
    }

    public void setPositionjoinwinkHomeActivity(int positionjoinwinkHomeActivity) {
        this.positionjoinwinkHomeActivity = positionjoinwinkHomeActivity;
    }

    public int getPositionjoinwinkUserImageActivity() {
        return this.positionjoinwinkUserImageActivity;
    }

    public void setPositionjoinwinkUserImageActivity(int positionjoinwinkUserImageActivity) {
        this.positionjoinwinkUserImageActivity = positionjoinwinkUserImageActivity;
    }

    public int getPositionjoinwinkUserImageInfoFromUserName() {
        return this.positionjoinwinkUserImageInfoFromUserName;
    }

    public void setPositionjoinwinkUserImageInfoFromUserName(int positionjoinwinkUserImageInfoFromUserName) {
        this.positionjoinwinkUserImageInfoFromUserName = positionjoinwinkUserImageInfoFromUserName;
    }

    public Handler getMhandler_UserImageInfoFromUserName() {
        return this.mhandler_UserImageInfoFromUserName;
    }

    public void setMhandler_UserImageInfoFromUserName(Handler mhandler_UserImageInfoFromUserName) {
        this.mhandler_UserImageInfoFromUserName = mhandler_UserImageInfoFromUserName;
    }

    public String getUserIdSearch() {
        return this.UserIdSearch;
    }

    public void setUserIdSearch(String userIdSearch) {
        this.UserIdSearch = userIdSearch;
    }

    public int getPositionuserfollow() {
        return this.positionuserfollow;
    }

    public void setPositionuserfollow(int positionuserfollow) {
        this.positionuserfollow = positionuserfollow;
    }

    public String getUserIdFollow() {
        return this.UserIdFollow;
    }

    public void setUserIdFollow(String userIdFollow) {
        this.UserIdFollow = userIdFollow;
    }



    // In showimageItemActivity
    public String imageItemId;

    public String getImageItemId() {
        return this.imageItemId;
    }

    public void setImageItemId(String imageItemId) {
        this.imageItemId = imageItemId;
    }

    private boolean isupload = false;

    public boolean isIsupload() {
        return this.isupload;
    }

    public void setIsupload(boolean isupload) {
        this.isupload = isupload;
    }



    public boolean isCommentUsername = false;
    public boolean isComment = false;
    public boolean isCommentHome = false;
    public boolean isCommentNotify = false;
    public boolean isCommentHashtag = false;
    public boolean isCommentYou = false;
    public boolean isCommentPopular = false;
    public boolean isCommentFollower = false;
    public boolean isCommentFollowing = false;
    public boolean isCommentContact = false;
    public int positionComment = 0;
    public int positionCommentUsername = 0;
    public int positionCommentHome = 0;
    public int positionCommentNotify = 0;
    public int positionCommentYou = 0;
    public int positionCommentPopular = 0;
    public int positionCommentFollower = 0;
    public int positionCommentFollowing = 0;
    public int positionCommentContact = 0;
    public int positionCommentHashtag = 0;

    public boolean isComment() {
        return this.isComment;
    }

    public void setComment(boolean isComment) {
        this.isComment = isComment;
    }



    public Handler getMhandler_Home() {
		return mhandler_Home;
	}

	public void setMhandler_Home(Handler mhandler_Home) {
		this.mhandler_Home = mhandler_Home;
	}
	
	public Handler getMhandler_TopImage() {
		return mhandler_TopImage;
	}

	public void setMhandler_TopImage(Handler mhandler_TopImage) {
		this.mhandler_TopImage = mhandler_TopImage;
	}

	public Handler getMhandler_UserImageList() {
		return mhandler_UserImageList;
	}

	public void setMhandler_UserImageList(Handler mhandler_UserImageList) {
		this.mhandler_UserImageList = mhandler_UserImageList;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public int getPositioncheckfacebook() {
		return positioncheckfacebook;
	}

	public void setPositioncheckfacebook(int positioncheckfacebook) {
		this.positioncheckfacebook = positioncheckfacebook;
	}

	public boolean isCommentHome() {
		return isCommentHome;
	}

	public void setCommentHome(boolean isCommentHome) {
		this.isCommentHome = isCommentHome;
	}


	public boolean isCommentNotify() {
		return isCommentNotify;
	}

	public void setCommentNotify(boolean isCommentNotify) {
		this.isCommentNotify = isCommentNotify;
	}

	public int getPositionCommentHome() {
		return positionCommentHome;
	}

	public void setPositionCommentHome(int positionCommentHome) {
		this.positionCommentHome = positionCommentHome;
	}

	public int getPositionCommentNotify() {
		return positionCommentNotify;
	}

	public void setPositionCommentNotify(int positionCommentNotify) {
		this.positionCommentNotify = positionCommentNotify;
	}


	public String getCameramoment() {
		return cameramoment;
	}

	public void setCameramoment(String cameramoment) {
		this.cameramoment = cameramoment;
	}


	public Handler getMhandler_Hashtag() {
		return mhandler_Hashtag;
	}

	public void setMhandler_Hashtag(Handler mhandler_Hashtag) {
		this.mhandler_Hashtag = mhandler_Hashtag;
	}

	public int getPositionjoinwinkHashtagActivity() {
		return positionjoinwinkHashtagActivity;
	}

	public void setPositionjoinwinkHashtagActivity(
			int positionjoinwinkHashtagActivity) {
		this.positionjoinwinkHashtagActivity = positionjoinwinkHashtagActivity;
	}

	public Boolean getIsCameraFont() {
		return isCameraFont;
	}

	public void setIsCameraFont(Boolean isCameraFont) {
		this.isCameraFont = isCameraFont;
	}

	public boolean isCommentUsername() {
		return isCommentUsername;
	}

	public void setCommentUsername(boolean isCommentUsername) {
		this.isCommentUsername = isCommentUsername;
	}

	public int getPositionCommentUsername() {
		return positionCommentUsername;
	}

	public void setPositionCommentUsername(int positionCommentUsername) {
		this.positionCommentUsername = positionCommentUsername;
	}

	public int topDate = -1;
    public int topCountry = -1;

}
