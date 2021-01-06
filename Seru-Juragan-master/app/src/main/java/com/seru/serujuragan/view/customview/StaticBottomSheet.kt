package com.seru.serujuragan.view.customview

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.seru.serujuragan.R

/**
 * Created by adityaaugusta on 5/4/17.
 */
class StaticBottomSheet(private val activity: Activity) {

    private var view: View? = null
    private var dialog: Dialog? = null

    fun init(view: Int): StaticBottomSheet {
        this.view = activity.layoutInflater.inflate(view, null)
        dialog = Dialog(activity, R.style.MaterialDialogSheet)
        dialog!!.setContentView(this.view!!)
        return this
    }

    fun setCancelable(isCancelable: Boolean): StaticBottomSheet {
        this.dialog!!.setCancelable(isCancelable)
        return this
    }

    fun setGravity(gravity: Int): StaticBottomSheet {
        this.dialog!!.window!!.setGravity(gravity)
        return this
    }

    fun setLayout(width: Int, height: Int): StaticBottomSheet {
        this.dialog!!.window!!.setLayout(width, height)
        return this
    }

    fun getView(): View {
        return this.view!!
    }

    fun show() { this.dialog!!.show() }

    fun dismiss() { this.dialog!!.dismiss() }

}
