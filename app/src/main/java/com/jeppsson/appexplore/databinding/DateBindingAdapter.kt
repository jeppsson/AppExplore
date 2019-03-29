package com.jeppsson.appexplore.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("date")
fun getDate(view: TextView, date: Long) {
    view.text = SimpleDateFormat.getDateTimeInstance().format(Date(date))
}
