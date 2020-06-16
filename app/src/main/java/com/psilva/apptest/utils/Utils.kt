package com.psilva.apptest.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class Utils {


    fun hideSoftKeyboardBottomSheet(ctx: Context, view: View) {
        val inputManager = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}