package com.topceo.profile;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.topceo.R;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

class ProfileUtils {

    public static final String INSTAGRAM_PREFIX = "https://www.instagram.com/";
    public static final String TWITTER_PREFIX = "https://twitter.com/";

    static int[] imgs = new int[]{
            R.drawable.ic_social_web,
            R.drawable.ic_social_facebook,
            R.drawable.ic_social_instagram,
            R.drawable.ic_social_twitter,
            R.drawable.ic_social_youtube,
            R.drawable.ic_social_linkedin
    };

    public static void setSocialText(Context context, LayoutInflater inflater, LinearLayout linearSocial, ArrayList<SocialItem> list) {
        if (context != null && list != null) {

            linearSocial.setVisibility(View.VISIBLE);
            //xoa cai cu truoc khi add cai moi
            if (linearSocial.getChildCount() > 0) {
                linearSocial.removeAllViews();
            }

            String[] tags = context.getResources().getStringArray(R.array.arr_socials);

            for (int i = 0; i < list.size(); i++) {
                SocialItem item = list.get(i);
                String link = item.getLink();
                if (!TextUtils.isEmpty(link)) {

                    View v = inflater.inflate(R.layout.social_row, null);
                    ImageView img = v.findViewById(R.id.img);
                    TextView txt = v.findViewById(R.id.txt);
                    txt.setText(link);

                    if (item.getNameCode().equalsIgnoreCase(tags[0])) {
                        img.setImageResource(imgs[0]);
                    } else if (item.getNameCode().equalsIgnoreCase(tags[1])) {
                        img.setImageResource(imgs[1]);
                    } else if (item.getNameCode().equalsIgnoreCase(tags[2])) {
                        img.setImageResource(imgs[2]);//instagram
                        String username = MyUtils.getUsername(link);
                        if (!TextUtils.isEmpty(username)) {
                            String url = INSTAGRAM_PREFIX + username;
                            txt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyUtils.openWebPage(url, context);
                                }
                            });
                        }
                    } else if (item.getNameCode().equalsIgnoreCase(tags[3])) {
                        img.setImageResource(imgs[3]);//twitter
                        String username = MyUtils.getUsername(link);
                        if (!TextUtils.isEmpty(username)) {
                            String url = TWITTER_PREFIX + username;
                            txt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyUtils.openWebPage(url, context);
                                }
                            });
                        }
                    } else if (item.getNameCode().equalsIgnoreCase(tags[4])) {
                        img.setImageResource(imgs[4]);
                    } else if (item.getNameCode().equalsIgnoreCase(tags[5])) {
                        img.setImageResource(imgs[5]);
                    }
                    linearSocial.addView(v);

                }

            }
        }
    }
}
