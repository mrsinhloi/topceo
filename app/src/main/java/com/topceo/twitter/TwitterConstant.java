package com.topceo.twitter;

/**
 * Created by MrPhuong on 2016-08-23.
 */
public class TwitterConstant {
    public static String TWITTER_CONSUMER_KEY = "a85SNQoCRpPuvyIxnvdwaJ2ku";
    public static String TWITTER_CONSUMER_SECRET = "FT4NiMaHHJMGDffEGhEPhDGh5LscmDkg7OWUkebnTo9ZVdg8tb";
    public static String TWITTER_TOKEN="767931506392043520-YFHDBV8pLeX02AoFEzxNe2PFK53wNeB";//for dev account
    public static String TWITTER_TOKEN_SECRET="gxioFxy1OjNGmtTnYHzdgM7IMU6NE6XZnMnmCOc6mwL8e";//for dev account

    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    final public static String  CALLBACK_SCHEME = "x-latify-oauth-twitter";
    final public static String  CALLBACK_URL = CALLBACK_SCHEME + "://callback";
}
