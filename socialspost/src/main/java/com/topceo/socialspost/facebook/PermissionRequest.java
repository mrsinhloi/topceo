/*
 * Copyright (c) 2017-present, Facebook, Inc. All rights reserved.
 * <p>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 * <p>
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.topceo.socialspost.facebook;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

public class PermissionRequest {
    public interface CallbackRevokePermission {
        public void completed();
    }

    private static final String PERMISSIONS_ENDPOINT = "/me/permissions";
    public static final String APP = "app";//THU HOI QUYEN APP THI PHAI DANG NHAP LAI
    public static final String[] PERMISSIONS_PAGE = {
            //post feed in page
            "pages_read_engagement",
            "pages_manage_posts"

            //post photo in page //https://developers.facebook.com/docs/graph-api/photo-uploads/ (xem quyen)
            /*"pages_manage_ads",
            "pages_manage_metadata",
            "pages_read_user_content",
            "pages_manage_engagement"*/
    };

    public static final String[] PERMISSION_PAGE_AND_USER_PROFILE = {
            //user
            "public_profile",

            //post feed in page
            "pages_read_engagement",
            "pages_manage_posts"
    };


    /**
     * Thu lai cac quyen tren page (2 quyen)
     * @param callback
     */
    public static void makeRevokePermissionPage(CallbackRevokePermission callback) {
        removePermission(0, callback);
    }

    private static void removePermission(final int index, final CallbackRevokePermission callback) {
        if (index < PERMISSIONS_PAGE.length) {
            String permission = PERMISSIONS_PAGE[index];
            makeRevokePermRequest(permission, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    int next = index + 1;
                    removePermission(next, callback);
                }
            });
        } else {
            callback.completed();
        }
    }

    /**
     * Thu lai 1 quyen
     * @param permission
     * @param callback
     */
    public static void makeRevokePermRequest(String permission, GraphRequest.Callback callback) {
        String graphPath;
        if (permission.equals(APP)) {
            graphPath = PERMISSIONS_ENDPOINT;
        } else {
            graphPath = PERMISSIONS_ENDPOINT + "/" + permission;
        }

        GraphRequest request =
                GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), graphPath, callback);
        request.setHttpMethod(HttpMethod.DELETE);
        request.executeAsync();
    }
}
