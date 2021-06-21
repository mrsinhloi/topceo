package com.topceo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.imagezoom.ImageZoom;
import com.topceo.objects.other.User;
import com.topceo.utils.MyUtils;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH07_MyCardActivity extends AppCompatActivity {
    private Activity context = this;


    private User user;

    @BindView(R.id.txt2)
    TextView txt2;

    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageZoom img2;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.imgLevel)
    ImageView imgLevel;
    @BindView(R.id.btnShare)
    Button imgShare;


    @BindView(R.id.img3)
    ImageZoom imgQrcode;

    @BindView(R.id.linearWidth)
    LinearLayout linearWidth;
    @BindView(R.id.rotateLayout)
    RotateLayout rotateLayout;

//    @BindView(R.id.img1)ImageView img1;

    private int screenWidth = 100;

    private int[] levels = new int[]{
            R.drawable.ic_svg_29_card_member,
            R.drawable.ic_svg_30_card_vip,
            R.drawable.ic_svg_31_card_vvip
    };

    private int position = 0;

    private void changeLevel() {
        imgLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position += 1;
                imgLevel.setImageResource(levels[position % levels.length]);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //android O fix bug orientation
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.my_card_activity);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        screenWidth = MyUtils.getScreenWidth(context);

        /*FrameLayout.LayoutParams layout = (FrameLayout.LayoutParams)linearRootAll.getLayoutParams();
        int w = layout.width;
        int h = layout.height;
        layout.width = h;
        layout.height = w;

        linearRootAll.setLayoutParams(layout);
        linearRootAll.setRotation(90f);*/

//        title.setText(getText(R.string.user_shared));
        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }


        rotateLayout.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (user != null) {
            final String data = user.getQRLink();

            if(!TextUtils.isEmpty(data)){
                try {

                    imgShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyUtils.share(context, data);
                        }
                    });

                    //barcode
                    int size = getResources().getDimensionPixelSize(R.dimen.barcode_height) * 2;
                    Bitmap b = createImage(data, "Barcode", size * 4, size);
                    if (b != null) {
                        img2.setImageBitmap(b);
                    }

                    //qrcode
                    size = getResources().getDimensionPixelSize(R.dimen.qrcode_height) * 2;
                    b = createImage(data, "QR Code", size, size);
                    if (b != null) {
                        imgQrcode.setImageBitmap(b);
                    }

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }


            switch (user.getVipLevelId()) {
                case User.VIP_LEVEL_SKY:
//                    txt2.setText(getString(R.string.fan_mobile, user.getUserName()));
                    txt2.setText(user.getUserName());

                    imgLevel.setImageResource(R.drawable.ic_svg_29_card_member);
                    break;
                case User.VIP_LEVEL_VIP:
//                    txt2.setText(getString(R.string.vip_mobile, user.getUserName()));
                    txt2.setText(user.getUserName());

                    imgLevel.setImageResource(R.drawable.ic_svg_30_card_vip);
                    break;
                case User.VIP_LEVEL_VVIP:
//                    txt2.setText(getString(R.string.vvip_mobile, user.getUserName()));
                    txt2.setText(user.getUserName());

                    imgLevel.setImageResource(R.drawable.ic_svg_31_card_vvip);
                    break;
            }


            /*if (user.isVip()) {
//                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.black));
//                txtVip.setVisibility(View.VISIBLE);
//                txtFan.setVisibility(View.GONE);

            } else {
//                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey_800));
//                txtVip.setVisibility(View.GONE);
//                txtFan.setVisibility(View.VISIBLE);

            }*/
        }


        ViewTreeObserver viewTreeObserver = linearWidth.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    //set chieu dai card view theo ratio 86/54 = 1.6
                    int w = linearWidth.getMeasuredWidth();
                    int h = linearWidth.getMeasuredHeight();
                    MyUtils.log("card size " + w + ", " + h + " ratio = " + (w * 1.0f / h));


                    if (isFirst) {
                        isFirst = false;
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearWidth.getLayoutParams();
                        params.height = h;

                        float ratio = 1.25f;
                        params.width = (int) (h * ratio);
                        linearWidth.setLayoutParams(params);
                    }

                }
            });
        }

        changeLevel();
        img1.setClipToOutline(true);

    }

    boolean isFirst = true;

    private int size = 660;
    private int size_width = 660;
    private int size_height = 264;

    public Bitmap createImage(String message, String type, int sWidth, int sHeight) throws WriterException {
        size_height = sHeight;
        size_width = sWidth;
        size = sWidth;

        BitMatrix bitMatrix = null;
        // BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
        switch (type) {
            case "QR Code":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                break;
            case "Barcode":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, size_width, size_height);
                break;
            case "Data Matrix":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.DATA_MATRIX, size, size);
                break;
            case "PDF 417":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.PDF_417, size_width, size_height);
                break;
            case "Barcode-39":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_39, size_width, size_height);
                break;
            case "Barcode-93":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_93, size_width, size_height);
                break;
            case "AZTEC":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.AZTEC, size, size);
                break;
            default:
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                break;
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (bitMatrix.get(j, i)) {
                    pixels[i * width + j] = 0xff000000;


                } else {
                    pixels[i * width + j] = 0x00ffffff;
//                    pixels[i * width + j] = 0xffe01b22;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


}
