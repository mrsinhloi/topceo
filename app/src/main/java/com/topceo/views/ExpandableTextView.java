package com.topceo.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ExpandableTextView extends AppCompatTextView {
    public ExpandableTextView(@NonNull Context context) {
        super(context);
    }

    public ExpandableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*private Integer textMaxLength = 250;
    private Integer seeMoreTextColor = R.color.grey_color;

    private String collapsedTextWithSeeMoreButton;
    private String expandedTextWithSeeMoreButton;
    private String orignalContent;

    private SpannableString collapsedTextSpannable;
    private SpannableString expandedTextSpannable;

    private Boolean isExpanded = false;

    private String seeMore = " ... More", seeLess = " ... Less";

    public ExpandableTextView(Context context)
    {
        super(context);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //set max length of the string text
    public void setTextMaxLength(Integer maxLength)
    {
        textMaxLength = maxLength;
    }

    public void setSeeMoreTextColor(Integer color)
    {
        seeMoreTextColor = color;
    }

    public void expandText(Boolean expand)
    {
        if (expand)
        {
            isExpanded = true;
            setText(expandedTextSpannable);
        }
        else
        {
            isExpanded = false;
            setText(collapsedTextSpannable);
        }

    }

    public void setSeeMoreText(String seeMoreText,String seeLessText)
    {
        seeMore = seeMoreText;
        seeLess = seeLessText;
    }

    public Boolean isExpanded()
    {
        return isExpanded;
    }

    //toggle the state
    public void toggle()
    {
        if (isExpanded)
        {
            isExpanded = false;
            setText(collapsedTextSpannable);
        }
        else
        {
            isExpanded = true;
            setText(expandedTextSpannable);
        }
    }


    public void setContent(String text)
    {
        orignalContent = text;
        this.setMovementMethod(LinkMovementMethod.getInstance());
        //show see more
        if (orignalContent.length() >=textMaxLength)
        {
            collapsedTextWithSeeMoreButton = orignalContent.substring(0,textMaxLength) +"... "+seeMore;
            expandedTextWithSeeMoreButton = orignalContent+" "+seeLess;

            //creating spannable strings
            collapsedTextSpannable = new SpannableString(collapsedTextWithSeeMoreButton);
            expandedTextSpannable = new SpannableString(expandedTextWithSeeMoreButton);


            collapsedTextSpannable.setSpan(clickableSpan,textMaxLength+4,collapsedTextWithSeeMoreButton.length(),0);
            collapsedTextSpannable.setSpan(new StyleSpan(Typeface.ITALIC),textMaxLength+4,collapsedTextWithSeeMoreButton.length(),0);
            collapsedTextSpannable.setSpan(new RelativeSizeSpan(.9f),textMaxLength+4,collapsedTextWithSeeMoreButton.length(),0);
            expandedTextSpannable.setSpan(clickableSpan,orignalContent.length()+1,expandedTextWithSeeMoreButton.length(),0);
            expandedTextSpannable.setSpan(new StyleSpan(Typeface.ITALIC),orignalContent.length()+1,expandedTextWithSeeMoreButton.length(),0);
            expandedTextSpannable.setSpan(new RelativeSizeSpan(.9f),orignalContent.length()+1,expandedTextWithSeeMoreButton.length(),0);

            if (isExpanded)
                setText(expandedTextSpannable);
            else
                setText(collapsedTextSpannable);
        }
        else
        {
            //to do: don't show see more
            setText(orignalContent);
        }
    }


    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget)
        {
            toggle();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(seeMoreTextColor));
        }
    };*/
}
