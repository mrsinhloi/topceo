package com.topceo.viewholders;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.topceo.objects.db.ImageItemDB;
import com.topceo.socialview.core.widget.SocialView;
import com.topceo.txtexpand.HashtagClickableSpan;
import com.topceo.txtexpand.MentionClickableSpan;
import com.topceo.txtexpand.UrlClickableSpan;
import com.topceo.utils.MyUtils;
import com.topceo.views.ExpandableTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class HolderUtils {
    public static final int MAX_LINES = 5;

    public static void setReadMore(ExpandableTextView txt, TextView txtMoreTop) {
        txt.post(new Runnable() {
            @Override
            public void run() {
                int lines = txt.getLineCount();
                String text = txt.getText().toString().trim();
                if (!TextUtils.isEmpty(text) && lines > MAX_LINES) {
                    txtMoreTop.setVisibility(View.VISIBLE);
                    txt.setMaxLines(MAX_LINES);
                    txt.setEllipsize(TextUtils.TruncateAt.END);
                    txtMoreTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            txt.setMaxLines(Integer.MAX_VALUE);
                            txtMoreTop.setVisibility(View.GONE);
                        }
                    });
                } else {
                    txtMoreTop.setVisibility(View.GONE);
                    txtMoreTop.setLines(0);
                    txt.setMaxLines(Integer.MAX_VALUE);
                }
            }
        });
    }

    public static void setDescription(String description, AppCompatTextView txtDescription, Context context) {
        if (!TextUtils.isEmpty(description)) {
            txtDescription.setVisibility(View.VISIBLE);
            txtDescription.setText(MyUtils.fromHtml(description));
            showMentionHashtagUrl(txtDescription, context);

        } else {
            txtDescription.setVisibility(View.GONE);
        }
    }

    public static void showMentionHashtagUrl(AppCompatTextView txtDescription, Context context) {
        String description = txtDescription.getText().toString();

        SpannableString builder = new SpannableString(description);

        //MENTION
        try {
            Pattern pattern1 = SocialView.PATTERN_MENTION;
            Matcher matcher1 = pattern1.matcher(description);
            while (matcher1.find()) {
                MentionClickableSpan click = new MentionClickableSpan(matcher1.group(), context);
                builder.setSpan(click, matcher1.start(), matcher1.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //HASHTAG
        try {
            Pattern pattern2 = SocialView.PATTERN_HASHTAG;
            Matcher matcher2 = pattern2.matcher(description);
            while (matcher2.find()) {
                HashtagClickableSpan click = new HashtagClickableSpan(matcher2.group(), context);
                builder.setSpan(click, matcher2.start(), matcher2.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //URL
        try {
            Pattern pattern3 = SocialView.PATTERN_HYPERLINK;
            Matcher matcher3 = pattern3.matcher(description);
            while (matcher3.find()) {
                UrlClickableSpan click = new UrlClickableSpan(matcher3.group(), context);
                builder.setSpan(click, matcher3.start(), matcher3.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtDescription.setText(builder);
        txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

    }
    /*public static void setDescription(String description, SocialTextViewExpand txtDescription, Context context) {
        if (!TextUtils.isEmpty(description)) {
            txtDescription.setVisibility(View.VISIBLE);
            txtDescription.setText(MyUtils.fromHtml(description));
            txtDescription.setOnHashtagClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    MyUtils.gotoHashtag(text.toString(), context);
                }
            });
            txtDescription.setOnMentionClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    MyUtils.gotoProfile(text.toString(), context);
                }
            });
            txtDescription.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    MyUtils.openWebPage(text.toString(), context);
                }
            });
        } else {
            txtDescription.setVisibility(View.GONE);
        }
    }*/

    public static void updateLiked(long imageItemId, boolean isLiked, int likeCount) {
        Realm realm = Realm.getDefaultInstance();
        if (realm != null && !realm.isClosed()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ImageItemDB item = realm.where(ImageItemDB.class)
                            .equalTo("ImageItemId", imageItemId).findFirst();
                    if (item != null) {
                        item.setLiked(isLiked);
                        item.setLikeCount(likeCount);
                    }
                    realm.close();
                }
            });
        }
    }
}
