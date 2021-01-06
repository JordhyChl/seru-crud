package com.seru.serujuragan.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.seru.serujuragan.R
import com.seru.serujuragan.ui.home.HomeActivity
import kotlinx.android.synthetic.main.dialog_alert_process.*
import kotlinx.android.synthetic.main.dialog_alert_validasi.*


/**
 * Created by Mahendra Dev on 30/12/2019
 */
class CustomProgressDialog {

    lateinit var dialog: Dialog

    fun showDialogInfo(context: Context, idToko: String) : Dialog{
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_validasi, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.tvAlertID.text = " $idToko "
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnAlertExit.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("id",idToko)
            dialog.context.startActivity(intent)
        }

        dialog.show()

        return dialog
    }

    fun show(context: Context) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_process, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.tvLoadingDialog.text = "Cek Nomor ..."
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        return dialog
    }

    fun showProg(context: Context) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_loading, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

//        val gifImageView: ImageView = dialog.findViewById(R.id.custom_loading_imageView)
//
//        val imageViewTarget = DrawableImageViewTarget(gifImageView)
//
//        Glide.with(context)
//            .load(R.drawable.loading_icon)
//            .placeholder(R.drawable.loading_icon)
//            .centerCrop()
//            .into(imageViewTarget)

        return dialog
    }

    fun dismissProg() {
        return dialog.dismiss()
    }
}