package com.smartapp.loadmore

import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.smartapp.post_like_facebook.R
import com.smartapp.timeago.TimeAgoMessages
import java.util.*


abstract class BaseAdapterLoadMore<T>(var context: Context, var list: ArrayList<T?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var inflater: LayoutInflater = LayoutInflater.from(context)
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var corners: Int = 8
    lateinit var messages: TimeAgoMessages

    init {
        // get device dimensions
        val displayMetrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(displayMetrics)

        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        corners = context.resources.getDimensionPixelOffset(R.dimen.margin_8dp)

        val LocaleBylanguageTag: Locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
        messages = TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build()
    }


    override fun getItemCount(): Int = list.count()
    abstract fun configure(item: T, holder: RecyclerView.ViewHolder)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position] == null) {

        }else{
            list[position]?.let {
                configure(it, holder)
                /*when(getItemViewType(position)){
                    R.layout.header_simple_1 -> configure(it, holder as HeaderViewHolder)
                    else -> configure(it, holder as ItemViewHolder)
                }*/

            }
        }

    }

    fun update(items: ArrayList<T?>) {
        this.list = items
        notifyDataSetChanged()
    }

    fun add(t: T) {
        list.add(t)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(items: ArrayList<T?>) {
        val from = list.size
        list.addAll(items)
        notifyItemRangeInserted(from, list.size - 1)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addPosition(t: T, position: Int) {
        list.add(position, t)
        notifyItemInserted(position)
    }

    fun removerItem(t: T) {
        val postion = list.indexOf(t)
        removerPosition(postion)
    }

    fun removerPosition(position: Int) {
        if (list.size > 0) {
            if (position < list.size) {
                list.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun updatePosition(t: T, position: Int) {
        if (position < list.size) {
            list[position] = t
            notifyItemChanged(position)
        }
    }


    val data: ArrayList<T>
        get() = list as ArrayList<T>

    fun addLoadingView() {
        //Add loading item
        Handler().post {
            list.add(null)
            notifyItemInserted(list.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (list.size != 0) {
            list.removeAt(list.size - 1)
            notifyItemRemoved(list.size)
        }
    }

}

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
