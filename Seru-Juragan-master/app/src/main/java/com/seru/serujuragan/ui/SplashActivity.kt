package com.seru.serujuragan.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.ui.home.HomeActivity
import com.seru.serujuragan.util.CheckRootTask
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.dialog_alert_rooted.*

/**
 * Created by Mahendra Dev on 16/12/2019
 */
class SplashActivity: AppCompatActivity(), CheckRootTask.OnCheckRootFinishedListener {

    companion object{
        val TAG = SplashActivity::class.java.simpleName
    }
    private val progressDialog = CustomProgressDialog()
    lateinit var dialog: Dialog

    private fun initConfig() {

    }

    private fun startActivity() {
        val intent = when {
            AppPreference.isLoggedIn -> Intent(this, HomeActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in_d200, R.anim.no_animation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkingRoot()
        //dev mode
        //startActivity()
        //initFcm()
    }

    private fun checkingRoot(){
        progressDialog.showProg(this)
        val checkRootTask = CheckRootTask(this,this)
        checkRootTask.execute(true)
    }

    private fun showInfoDialog(){
        val inflator = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_rooted, null)

        dialog = Dialog(this)
        dialog.setContentView(viewGroup)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnAlertRooted.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onCheckRootFinished(isRooted: Boolean) {
        if (isRooted){
            progressDialog.dismissProg()
            showInfoDialog()
        }else{
            progressDialog.dismissProg()
            startActivity()
            initFcm()
        }
    }

    private fun initFcm(){

        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("notif")
            .addOnCompleteListener { task ->
                var msg = "sub"
                if (!task.isSuccessful) {
                    msg = "sub fail"
                }
                Log.d(TAG, msg)
            }
        // [END subscribe_topics]

        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                AppPreference.fcmToken = token.toString()
                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
              //  Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
        // [END retrieve_current_token]
    }
}