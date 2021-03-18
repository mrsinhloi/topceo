package com.topceo.views

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import com.topceo.viewholders.HolderUtils

class ShowMoreTextView @kotlin.jvm.JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    companion object {
        val SHOWING_LINE: Int = 4
    }

    private var showingLine = SHOWING_LINE

    private var showMore = " more"
    private var showLess = "" //" ...less"
    private val threeDot = "…"

    private var showMoreTextColor = Color.GRAY
    private var showLessTextColor = Color.GRAY

    private var mainText: String? = null
    private var isAlreadySet = false
    private var isCollapse = true
    public var isClickMoreLess = false

    init {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                resetRowLine()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    public fun resetRowLine() {
        if (showingLine >= lineCount) return
        if (isCollapse) {
            showMoreButton()
        } else {
            showLessButton()
        }
    }

    public fun reset() {
        mainText = ""
        isClickMoreLess = false

        showingLine = SHOWING_LINE
        isCollapse = true
        isAlreadySet = false
        text = ""
        invalidate()
        if (showingLine >= lineCount) return
        showMoreButton()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mainText = text.toString()
    }

    private fun showMoreButton() {
        val text = text.toString()
        if (!isAlreadySet) {
            mainText = text
            isAlreadySet = true
        }
        var showingText = ""
        var start = 0
        var end: Int
        for (i in 0 until showingLine) {
            end = layout.getLineEnd(i)
            showingText += text.substring(start, end)
            start = end
        }
        var specialSpace = 0
        var newText: String
        do {
            newText = showingText.substring(
                0, showingText.length - (specialSpace)
            )
            newText += "$threeDot $showMore"
            setText(newText)
            specialSpace++

        } while (lineCount > showingLine)
        isCollapse = true
        setShowMoreColoringAndClickable()
    }

    private fun setShowMoreColoringAndClickable() {
        val spannableString = HolderUtils.getSpannableString(context, text.toString()) //SpannableString(text)
        spannableString.setSpan(
            object : ClickableSpan() {
                override fun updateDrawState(paint: TextPaint) {
                    paint.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    isClickMoreLess = true
                    maxLines = Int.MAX_VALUE
                    text = mainText
                    isCollapse = false
                    showLessButton()
                }
            }, text.length - showMore.length, text.length, 0
        )
        spannableString.setSpan(
            ForegroundColorSpan(showMoreTextColor),
            text.length - showMore.length,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setText(spannableString, BufferType.SPANNABLE)
        movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showLessButton() {
        /*val spannableString = HolderUtils.getSpannableString(context, text.toString())
        movementMethod = LinkMovementMethod.getInstance()
        setText(spannableString, BufferType.SPANNABLE)*/

        val text = "${text.trim()} $showLess"
        val spannableString = HolderUtils.getSpannableString(context, text) //SpannableString(text)
        /*spannableString.setSpan(
            object : ClickableSpan() {
                override fun updateDrawState(pain: TextPaint) {
                    pain.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    isClickMoreLess = true
                    maxLines = showingLine
                    showMoreButton()
                }
            }, text.length - showLess.length, text.length, 0
        )*/
        spannableString.setSpan(
            ForegroundColorSpan(showLessTextColor),
            text.length - (threeDot.length + showLess.length),
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setText(spannableString, BufferType.SPANNABLE)
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun setShowingLine(lineNumber: Int) {
//        if (lineNumber == 0) return
//        showingLine = lineNumber
//        maxLines = showingLine
    }

    fun addShowMoreText(text: String) {
        showMore = text
    }

    fun addShowLessText(text: String) {
        showLess = text
    }

    fun setShowMoreTextColor(color: Int) {
        showMoreTextColor = color
    }

    fun setShowLessTextColor(color: Int) {
        showLessTextColor = color
    }
}