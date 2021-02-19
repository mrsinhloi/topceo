package com.workchat.core.notification.items

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.ISwipeable
import com.workchat.core.notification.model.BaseNotification
import com.workchat.corechat.R
import java.util.*

abstract class BaseNotificationItem(val context: Context, val notify: BaseNotification) : AbstractItem<BaseNotificationItem.ViewHolder>(), ISwipeable, IDraggable {

    override val layoutRes: Int = R.layout.ntf_transfer_item
    override val type: Int = 0
    override val isSwipeable: Boolean = true
    override val isDraggable: Boolean = false

    override var identifier: Long = notify._id.hashCode().toLong()

    private val imgColor by lazy {
        val rand = Random()
        Color.rgb(rand.nextInt(180), rand.nextInt(180), rand.nextInt(180))
    }

    private val textRect = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.resources.displayMetrics)
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    abstract infix fun navigationFrom(context: Context)

    fun textBitmap(text: String): Bitmap {
        val imgBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        Canvas(imgBitmap).apply {
            textPaint.getTextBounds(text, 0, 1, textRect)
            drawColor(imgColor)
            drawText(text, 50f, 50f - textRect.exactCenterY() - (textPaint.descent() / 4), textPaint)
        }
        return imgBitmap
    }

}