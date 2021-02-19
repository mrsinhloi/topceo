package com.topceo.socialspost.facebook

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import org.json.JSONObject
import java.io.*

class FacebookApi() {

    lateinit var userId: String
    lateinit var accessToken: AccessToken

    ///////////////////////////////////////////////////////////////////////////////////////////////
    fun parseListPage(json: JSONObject): ArrayList<Page> {
        var list = ArrayList<Page>()
        val data = json.optJSONArray("data")
                ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
                ?.map {
                    val id = it.getString("id")
                    val name = it.getString("name")
                    val access_token = it.getString("access_token")
                    val page = Page(id, name, access_token)
                    list.add(page)
                }


//        val strings = arrayListOf("1", "2", "3")
//        val ints = strings.map { it.toInt() }

        return list
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    fun readByteArray(path: String): ByteArray? {
        var byteArray: ByteArray? = null
        if (path.isNotEmpty()) {
            val file = File(path)
            try {
                val bis = BufferedInputStream(FileInputStream(file))
                val dis = DataInputStream(bis)
                byteArray = ByteArray(file.length().toInt())
                dis.readFully(byteArray)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return byteArray
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    open fun getPages(callback: GraphRequest.Callback) {
        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/$userId/accounts",
                null,
                HttpMethod.GET,
                callback
        ).executeAsync()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  MyApplication.facebookApi.postMessage(message, pageId, access_token, callbackShowToastOK);
     */
    fun postMessage(message: String, pageId: String, access_token: String, callback: GraphRequest.Callback) {
        val params = Bundle()
        params.putString("message", message)
        params.putString("access_token", access_token)

        /* make the API call */
        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/$pageId/feed",
                params,
                HttpMethod.POST,
                callback
                /*GraphRequest.Callback { response -> *//* handle the result *//*
                    if (response.error == null) {
                        com.ehubstar.utils.MyUtils.showToast(context, "Post OK")
                    }
                }*/
        ).executeAsync()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * MyApplication.facebookApi.postPhotoUrl(message, url, pageId, access_token, callbackShowToastOK);
     */
    fun postPhotoUrl(message: String, url: String, pageId: String, access_token: String, published:Boolean, callback: GraphRequest.Callback) {
        val params = Bundle()
        params.putString("message", message)
        params.putString("url", url)
        params.putString("access_token", access_token)
        params.putBoolean("published", published)

        /* make the API call */
        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/$pageId/photos",
                params,
                HttpMethod.POST,
                callback
        ).executeAsync()
    }

    fun postAlbumToPage(message: String, ids:ArrayList<String>, pageId: String, access_token: String, callback: GraphRequest.Callback) {
        val params = Bundle()
        params.putString("message", message)
        params.putString("access_token", access_token)
        for(i in ids.indices){
            val id = ids[i]
            params.putString("attached_media[$i]", "{\"media_fbid\":\"$id\"}")
        }

        /* make the API call */
        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/$pageId/feed",
                params,
                HttpMethod.POST,
                callback
        ).executeAsync()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * MyApplication.facebookApi.postPhotoPath(message, path, pageId, access_token, callbackShowToastOK);
     */
    fun postPhotoPath(message: String?, path: String?, pageId: String, access_token: String?, callback: GraphRequest.Callback) {
        val byteArray: ByteArray? = path?.let { readByteArray(it) }
        if (byteArray != null) {
            val params = Bundle()
            params.putString("message", message)
            params.putByteArray("object_attachment", byteArray)
            params.putString("access_token", access_token)
            params.putBoolean("published", true)

            /* make the API call */
            GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/$pageId/photos",
                    params,
                    HttpMethod.POST,
                    callback
            ).executeAsync()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    fun postVideoUrl(message: String?, url: String?, pageId: String, access_token: String?, callback: GraphRequest.Callback) {
//        val byteArray: ByteArray? = path?.let { readByteArray(it) }
//        if (byteArray != null) {
        val params = Bundle()
        params.putString("description", message)
        params.putString("file_url", url)
        params.putString("access_token", access_token)
        params.putBoolean("published", true)

        /* make the API call */
        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/$pageId/videos",
                params,
                HttpMethod.POST,
                callback
        ).executeAsync()
//        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


}