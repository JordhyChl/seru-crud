package com.seru.serujuragan.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.model.BaseModelData
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.HomeContract
import com.seru.serujuragan.ui.kabinet.mandiri.ListTokoAsalActivity
import com.seru.serujuragan.ui.kabinet.status.ListStatusCabinetActivity
import com.seru.serujuragan.ui.lainlain.ProfileActivity
import com.seru.serujuragan.ui.lainlain.dhunter.ListDatabaseHunterActivity
import com.seru.serujuragan.ui.lainlain.dtoko.ListDatabaseTokoActivity
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.ui.toko.audit.ListAuditTokoActivity
import com.seru.serujuragan.ui.toko.status.StatusTokoActivity
import com.seru.serujuragan.ui.toko.tambah.TambahTokoActivity
import com.seru.serujuragan.ui.home.crd.CRUDActivity
import com.seru.serujuragan.ui.toko.validasi.ListValidasiTokoActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_alert_logout.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

/**
 * Created by Mahendra Dev on 23/12/2019
 */
class HomeActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    HomeContract.View{

    companion object {
        val TAG = HomeActivity::class.java.simpleName
        const val RC_LOCATION_CAMERA_STORAGE_PERM = 123
    }

    @Inject
    lateinit var presenter: HomeContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val LOCATION_CAMERA_STORAGE = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var showPermissionDeniedDialog = false
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        injectDependency()
        presenter.attach(this)
        Log.e(TAG,"value token : ${AppPreference.token}")
        initCheckPermission()
        init()
        Log.e(TAG,"FCM token : ${AppPreference.fcmToken}")
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
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

    private fun showLogoutDialog(context: Context) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_logout, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnNoLogout.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnYesLogout.setOnClickListener {
            logout()
        }

        dialog.show()

        return dialog
    }

    private fun logout(){
        this.getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        Log.i(TAG, "app pref :  ${AppPreference.isLoggedIn}")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun initCheckPermission(){
        if (EasyPermissions.hasPermissions(this, *LOCATION_CAMERA_STORAGE)){
            Log.d(TAG,"has permission")
        }else{
            EasyPermissions.requestPermissions(this,
                getString(R.string.permission_required_toast),
                RC_LOCATION_CAMERA_STORAGE_PERM,
                *LOCATION_CAMERA_STORAGE)
        }
    }

    private fun init(){
        isConnected = InternetCheck.isConnected(this)

        if (isConnected){
//            showProgressDialog(true)
//            presenter.getTaskCount()
            initIntent()
            initView()
            lyTaskInfo.visibility = VISIBLE
            lyOfflineInfo.visibility = GONE
        }else{
            initIntent()
            initView()
            lyTaskInfo.visibility = GONE
            lyOfflineInfo.visibility = VISIBLE
            showOfflineSnack()
        }

    }

    private fun initIntent(){

//        ly_addToko.setOnClickListener {
////            startActivity(Intent(this, FormSurveyActivity::class.java))
////        }

        //TOKO
        ly_auditToko.setOnClickListener {
            startActivity(Intent(this,
                ListAuditTokoActivity::class.java))
        }

        ly_validateToko.setOnClickListener {
            startActivity(Intent(this,
                ListValidasiTokoActivity::class.java))
        }

        ly_addToko.setOnClickListener {
            startActivity(Intent(this,
                TambahTokoActivity::class.java))
        }

        //CRUD
        crudTest.setOnClickListener {
            startActivity(Intent(this,
            CRUDActivity::class.java))
        }

        ly_statusToko.setOnClickListener {
            startActivity(Intent(this,
                StatusTokoActivity::class.java))
        }

        //Kabinet
        ly_tarikMandiri.setOnClickListener {
            startActivity(Intent(this, ListTokoAsalActivity::class.java))
        }

        ly_tukarKabinet.setOnClickListener {
            //startActivity(Intent(this, PenjadwalanTarikKirimActivity::class.java))
        }

        ly_tarikKabinet.setOnClickListener {
            //startActivity(Intent(this, ProsesTarikCabinetActivity::class.java))
        }

        ly_cekStatusKabinet.setOnClickListener {
            startActivity(Intent(this, ListStatusCabinetActivity::class.java))
        }

        //LAIN LAIN
        lyDatabaseToko.setOnClickListener {
            startActivity(Intent(this, ListDatabaseTokoActivity::class.java))
        }

        lyDatabaseHunter.setOnClickListener {
            startActivity(Intent(this, ListDatabaseHunterActivity::class.java))
        }

        ly_profil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        ly_logout.setOnClickListener {
            showLogoutDialog(this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(){

        val zoned = zoneDate().toEpochSecond()*1000
        val localDate = localDateTime()
        val date = localDate.format(DateTimeFormatter.ofPattern("EEEE, dd - MMMM - yyyy", Locale("in")))
        val time = localDate.format(DateTimeFormatter.ofPattern("H", Locale("in"))).toInt()
        val idJuragan = AppPreference.idJuragan
        AppPreference.dayNow = date
        AppPreference.dateTimestamp = zoned
        Log.d(TAG,"jam : $time")
        when {
            time < 12 -> tvGreetings.text = "Selamat Pagi"
            time in 11..15 -> tvGreetings.text = "Selamat Siang"
            time in 15..18 -> tvGreetings.text = "Selamat Sore"
            else -> tvGreetings.text = "Selamat Malam"
        }
        tv_juragan_name.text = AppPreference.nameJuragan
        tv_juragan_id.text = "($idJuragan)"
        tvdateToday.text = date
    }

    private fun showOfflineSnack(){
        // Show a snack bar for undo option
        Snackbar.make(
            root_layout, // Parent view
            "Anda sedang offline", // Message to show
            Snackbar.LENGTH_INDEFINITE
        ).setAction( // Set an action for snack bar
            "Muat Ulang" // Action button text
        ) { // Action button click listener
            init()
            root_layout.setBackgroundColor(Color.parseColor("#f2f2f2"))
        }.show()
    }

    private fun localDateTime(): LocalDateTime{
        return LocalDateTime.now()
    }

    private fun zoneDate(): ZonedDateTime{
        return ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
    }

    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e(TAG,"Error load cause : $error")
    }

    override fun loadSuccess(countData: BaseModelData) {
        if (!countData.data.isNullOrEmpty()){
            tv_total_message.text = countData.data
            imgTaskExist.visibility = VISIBLE
            if (countData.data == "0")
                imgTaskExist.visibility = GONE
        }else{
            tv_total_message.text = " - "
        }
    }

    override fun doLogout() {
        this.getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        Log.i(TAG, "app pref :  ${AppPreference.isLoggedIn}")
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "On Start")

        isConnected = InternetCheck.isConnected(this)
        if (isConnected){
            showProgressDialog(true)
            presenter.getTaskCount()
            initIntent()
            initView()
            lyTaskInfo.visibility = VISIBLE
            lyOfflineInfo.visibility = GONE
        }else{
            initIntent()
            initView()
            lyTaskInfo.visibility = GONE
            lyOfflineInfo.visibility = VISIBLE
            showOfflineSnack()
        }
    }

    override fun onResume() {
        super.onResume()

        isConnected = InternetCheck.isConnected(this)

        if (isConnected) {
            lyTaskInfo.visibility = VISIBLE
            lyOfflineInfo.visibility = GONE
        }else {
            initView()
            initIntent()
            lyTaskInfo.visibility = GONE
            lyOfflineInfo.visibility = VISIBLE
            showOfflineSnack()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}