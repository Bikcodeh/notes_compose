package com.bikcodeh.notes_compose.util.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(
    @StringRes resId: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, this.getString(resId), duration).show()
}