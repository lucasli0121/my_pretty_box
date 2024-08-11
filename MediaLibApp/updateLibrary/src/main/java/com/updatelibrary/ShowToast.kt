package com.updatelibrary

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class ShowToast {

    companion object {
        private const val THIS_FILE = "ShowToast"
        fun displayToast(ctx: Context, msgid: Int) {
            val toast: Toast = Toast.makeText(ctx, ctx.resources.getString(msgid), Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

        fun displayToast(ctx: Context?, msg: String?) {
            val toast: Toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

}