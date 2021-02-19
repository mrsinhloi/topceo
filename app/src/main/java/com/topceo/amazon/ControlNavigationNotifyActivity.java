package com.topceo.amazon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by MrPhuong on 2016-09-21.
 */

public class ControlNavigationNotifyActivity extends Activity {
    private Context context=this;
    private NotifyClass notify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Bundle b=getIntent().getExtras();
        if(b!=null){
            notify=b.getParcelable(NotifyClass.NOTIFY_CLASS);
            if(notify!=null){
                //Neu app chua chay, co the chua tao cookie, nen can dam bao app da chay vao main chua
                if(MainActivity.isExist){
                    //goi broadcast va notify sang de MainActivity lam
                    Intent intent1=new Intent(MainActivity.ACTION_GO_TO_NOTIFY);
                    intent1.putExtra(NotifyClass.NOTIFY_CLASS, notify);
                    sendBroadcast(intent1);

                }else{//
                    //dong ung dung

                    //vao man hinh loading -> vao main -> Báo lại anh đang ở main -> em gotoPhotoDetail đi
                    Intent intent1=new Intent(context, LoadingActivity.class);
                    intent1.putExtra(NotifyClass.NOTIFY_CLASS, notify);
                    context.startActivity(intent1);


                }
            }
        }

        finish();*/

    }




}
