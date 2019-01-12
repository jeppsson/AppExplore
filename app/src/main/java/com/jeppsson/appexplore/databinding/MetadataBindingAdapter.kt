package com.jeppsson.appexplore.databinding

import android.os.Bundle
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("metadata")
fun metadata(view: TextView, metaData: Bundle?) {
    if (metaData != null) {
        val sb = StringBuilder()
        for (metaDataName in metaData.keySet()) {
            sb.append(metaDataName)
                    .append(": ")
                    .append(metaData.get(metaDataName))
                    .append('\n')
        }
        view.text = sb.toString().trim()
    }
}
