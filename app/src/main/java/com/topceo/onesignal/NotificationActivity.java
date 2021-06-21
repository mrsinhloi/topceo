package com.topceo.onesignal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.google.gson.JsonObject;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Mr.Phuong on 10/22/2015.
 * Lam trung gian de chuyen den activity phu hop
 */
public class NotificationActivity extends AppCompatActivity {

    public static int FLAG_INTENT = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP;
    private Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //giam notify
        MyApplication.getInstance().setNumberForIcon(MyApplication.numberNotify--);

        final Bundle b = getIntent().getExtras();
        if (b != null) {

            //MTP
            NotifyObject obj = b.getParcelable(NotifyObject.NOTIFY_OBJECT);
            if (obj != null) {
                //neu co notifyId thi set la da xem
                if (obj.getNotifyId() > 0) {
                    updateUserNotifyIsView(obj);
                } else {
                    goMain(obj);
                    finish();
                }

            } else { //CHAT

                String roomId = b.getString(Room.ROOM_ID, "");
//                String roomLogId = b.getString(RoomLog.ROOM_LOG_ID, "");
                if (!TextUtils.isEmpty(roomId)) {
                    //neu chua mo man hinh chinh thi mo len
                    if (MH01_MainActivity.isExist) {
                        MyUtils.openChatRoom(getApplicationContext(), roomId, "");
                    } else {
                        Intent intent = new Intent(context, MH01_MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Room.ROOM_ID, roomId);
                        startActivity(intent);

                    }

                    //collapse widget
//                    EventBus.getDefault().post(new MenuEvent(true));

                }

                finish();

            }


        } else {
            goMain(null);
            finish();
        }
    }

    private void updateUserNotifyIsView(final NotifyObject obj) {
        if (com.topceo.utils.MyUtils.checkInternetConnection(context)) {
            if (obj.getNotifyId() > 0) {
                MyApplication.apiManager.updateUserNotifyIsView(
                        obj.getNotifyId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            /*JsonObject data = response.body();
                            if (data != null) {
                                ReturnResult result = Webservices.parseJson(data.toString(), String.class, false);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {

                                    } else {
                                        String message = result.getErrorMessage();
                                        if (!TextUtils.isEmpty(message)) {
                                            MyUtils.showAlertDialog(context, message);
                                        }
                                    }
                                }


                            }*/

                                goMain(obj);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                com.topceo.utils.MyUtils.log("error");
                                goMain(obj);
                                finish();
                            }
                        });

            }
        } else {
            finish();
        }
    }

    private void goMain(NotifyObject obj) {
        //Neu app chua chay, co the chua tao cookie, nen can dam bao app da chay vao main chua
        if (MH01_MainActivity.isExist) {
            //goi broadcast va notify sang de MainActivity lam
            Intent intent = new Intent(MH01_MainActivity.ACTION_GO_TO_NOTIFY);
            if(obj!=null){
                intent.putExtra(NotifyObject.NOTIFY_OBJECT, obj);
            }
            sendBroadcast(intent);

        } else {//

            //vao man hinh loading -> vao main -> Báo lại anh đang ở main -> em gotoPhotoDetail đi
            Intent intent = new Intent(context, MH01_MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(obj!=null){
                intent.putExtra(NotifyObject.NOTIFY_OBJECT, obj);
            }
            startActivity(intent);


        }
    }
}
