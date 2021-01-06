package com.seru.serujuragan.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.seru.serujuragan.App
import com.seru.serujuragan.BuildConfig
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.UserLoginRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.LoginContract
import com.seru.serujuragan.ui.home.HomeActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), LoginContract.View {

    @Inject lateinit var presenter: LoginContract.Presenter
    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private lateinit var phoneNumber: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        isConnected = InternetCheck.isConnected(this)

        injectDependency()
        presenter.attach(this)

        tvAppVersion.text = "Versi ${BuildConfig.VERSION_NAME}"

        btnLogin.setOnClickListener {
            if (isConnected)
            onLogin()
            else Toast.makeText(this,"Mohon perikasi kembali koneksi internet Anda", Toast.LENGTH_LONG).show()
        }


    }

    @Suppress("DEPRECATION")
    private fun injectDependency(){
        val activityComponent = DaggerActivityComponent
            .builder()
            .activityModule(ActivityModule(this))
            .appModule(AppModule(App.instance))
            .dataModule(DataModule())
            .build()

        activityComponent.inject(this)
    }

    private fun onLogin(){
        //var pn = edtPhoneNumber.text.toString()
        val mailFormat = handleEmail(edtEmail.text.toString())
        val email = edtEmail.text.toString()
        var pin = edtPin.text.toString()
        if (email.isEmpty() || pin.isEmpty()){
            Toast.makeText(this, "Tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }else if (!mailFormat){
            Toast.makeText(this, "Mohon masukan email anda dengan benar", Toast.LENGTH_SHORT).show()
        }else{
            showProgressDialog(true)
            Log.i("on login : ","email : $email ,pin : $pin ")
            presenter.loginUser(email, pin)
        }
//        if (pn.startsWith("0"))
//            pn = pn.substring(1)
//        phoneNumber = "62$pn"
//        if (pn.startsWith("62"))
//            phoneNumber = pn
//        if (pn.isEmpty() || pin.isEmpty()){
//            Toast.makeText(this, "Tidak boleh kosong", Toast.LENGTH_SHORT).show()
//        }else if (pn.length < 9){
//            Toast.makeText(this, "Mohon masukan nomor anda dengan benar", Toast.LENGTH_SHORT).show()
//        }else{
//            showProgressDialog(true)
//            Log.i("on login : ","phone : $phoneNumber ,pin : $pin ")
//            presenter.loginUser(phoneNumber, pin)
//        }
    }

    private fun handleEmail(email: String): Boolean{
        return (!TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e(TAG, "error cause : $error")
        if (errorCode == 401)
            Toast.makeText(this, "Nomor atau Password Anda salah, Mohon Periksa kembali",Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, "Terjadi kesalahan sistem, Mohon hubungi Custumer Service",Toast.LENGTH_LONG).show()
    }

    override fun loginSuccess(userLoginRes: UserLoginRes) {
        var token = userLoginRes.token
        Log.i(TAG,"login token result : $token")

        AppPreference.isLoggedIn = true
        AppPreference.token = "Bearer ${userLoginRes.token}"
        AppPreference.idJuragan = userLoginRes.idJuragan
        AppPreference.nameJuragan = userLoginRes.nameJuragan
        AppPreference.latitudeJuragan = userLoginRes.location.latitude
        AppPreference.longitudeJuragan = userLoginRes.location.longitude
        AppPreference.radiusJuragan = userLoginRes.scope.radius
        AppPreference.thresholdJuragan = userLoginRes.scope.threshold


        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun doLogout() {
        Log.e(TAG,"")
        Toast.makeText(this,"Anda tidak memilik akses", Toast.LENGTH_LONG).show()
    }
}
