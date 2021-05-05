package com.topceo.config

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.getCurrentPostion(): Int {
    return (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
}