package com.workchat.core.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.Project;
import com.workchat.core.utils.MyUtils;

import java.util.ArrayList;

public class DeeplinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtils.log("DeeplinkActivity...");
        /*//deeplink kích hoạt 3 lần màn hình này, nên hạn chế bằng cách trong vòng 5s ko dc chạy liên tục (xử lý 2 bước)
        //1
        long past = db.getLong(TinyDB.LAST_TIME_DEEP_LINK, 0);
        long current = SystemClock.elapsedRealtime();
        long diff = current - past;
        boolean isCanRun = diff > 5000;
        //2
        db.putLong(TinyDB.LAST_TIME_DEEP_LINK, current);
        if(!isCanRun){
            MyUtils.log("deeplink die...");
            finish();
            return;
        }else{
            MyUtils.log("deeplink run...");
        }*/

        //lấy thông tin deeplink
        runDeepLinking();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Project project;
    private String roomId = "";
    private String roomLogId = "";
    private String userId = "";
    ArrayList<String> params = new ArrayList<>();

    private void runDeepLinking() {

        Intent intent = getIntent();
        if (intent != null) {

            Uri data = intent.getData();
            if (data != null && data.isHierarchical()) {

                //neu da login thi moi vao chat
                UserChatCore user = ChatApplication.Companion.getUser();
                if (user != null) {

                    roomId = data.getQueryParameter("roomId");
                    if (!TextUtils.isEmpty(roomId)) {
                        roomLogId = data.getQueryParameter("roomLogId");
                        String userIdString = data.getQueryParameter("userId");
                        if (!TextUtils.isEmpty(userIdString)) {
                            userId = userIdString;
                            if (!userId.equals(user.get_id())) {
                                //neu khac userId thi ko cho vao
                                finish();
                                return;
                            }
                        }
                    } else {
                        String type = data.getQueryParameter("type");
                        if (!TextUtils.isEmpty(type)) {
                            //luu ds params
                            if (type.equalsIgnoreCase("private")) {

                                String userid = data.getQueryParameter("userid");

                                //1 params
                                params.add(userid);

                            } else if (type.equalsIgnoreCase("item")) {

                                String useridowner = data.getQueryParameter("useridowner");
                                String useridsguest = data.getQueryParameter("useridsguest");//owner
                                if (TextUtils.isEmpty(useridsguest)) {
                                    useridsguest = user.get_id() + "";
                                }
                                String itemid = data.getQueryParameter("itemid");
                                String itemname = data.getQueryParameter("itemname");
                                String itemimage = data.getQueryParameter("itemimage");
                                String itemlink = data.getQueryParameter("itemlink");
                                String itemprice = data.getQueryParameter("itemprice");
                                //todo bổ sung giá gốc
                                String itemoriginprice = data.getQueryParameter("itemoriginprice");

                                //8 params
                                params.add(useridowner);
                                params.add(useridsguest);
                                params.add(itemid);
                                params.add(itemname);
                                params.add(itemimage);
                                params.add(itemlink);
                                params.add(itemprice);
                                params.add(itemoriginprice);

                            } else if (type.equalsIgnoreCase("page")) {

                                String useridguest = data.getQueryParameter("useridguest");
                                String pageid = data.getQueryParameter("pageid");
                                String pagename = data.getQueryParameter("pagename");
                                String pagelink = data.getQueryParameter("pagelink");
                                String pageimage = data.getQueryParameter("pageimage");

                                //5 params
                                params.add(useridguest);
                                params.add(pageid);
                                params.add(pagename);
                                params.add(pagelink);
                                params.add(pageimage);
                            } else if (type.equalsIgnoreCase("join")) {//join to channel
//                        mbnapp://chat/room?type=join&link=sdsds
                                //add 2 cai giong nhau de phan biet voi 1 params
                                String link = data.getQueryParameter("link");
                                params.add(link);
                                params.add(link);

                            }
                        } else {
                            String id = data.getQueryParameter("projectId");
                            String name = data.getQueryParameter("projectName");
                            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {

                                String userIdString = data.getQueryParameter("userId");
                                if (!TextUtils.isEmpty(userIdString)) {
                                    userId = userIdString;
                                    if (!userId.equals(user.get_id())) {
                                        //neu khac userId thi ko cho vao
                                        finish();
                                        return;
                                    }
                                }

                                try {
                                    long projectId = Long.parseLong(id);
                                    project = new Project();
                                    project.setProjectId(projectId);
                                    project.setProjectName(name);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }


                }

                //neu co data thi chay theo deeplink, nguoc lai thi chi mo app resume trạng thái đang có
                ChatApplication.Companion.openDeeplink(roomId, roomLogId, project, params);

            }else{
                //neu chua mo app thi mo app
                ChatApplication.Companion.reopenMainActivity();
            }
        }



        finish();


    }


}
