package com.seru.serujuragan.ui.lainlain

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.UserProfileRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.lainlain.ProfileContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.view_toolbar_primary_logo.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class ProfileActivity : AppCompatActivity(),
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks,
    ProfileContract.View {

    companion object {
        val TAG = ProfileActivity::class.java.simpleName
    }

    @Inject lateinit var presenter: ProfileContract.Presenter
    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private var userLat: Double = 0.0
    private var userLong: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    private fun init(){
        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadProfile()
        }else{
            offlinePage.visibility = VISIBLE
            nestedScrollView.visibility = GONE
            Log.i("is connect intenter ? :", isConnected.toString())
        }
    }

    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e("Error load cause","$error")
    }

    override fun loadProfileSuccess(userProfile: UserProfileRes) {
        Log.e("success cause","${userProfile.id}")
        if (userProfile != null) {
            Log.e("success cause","${userProfile.areaData.village.villagesName}")
            tvIdJuragan.text = userProfile.id
            tvIdUnilever.text = userProfile.idUnilever
            tvNama.text = userProfile.name
            tvNomorTlp.text = userProfile.phone
            tvEmail.text = userProfile.email
            tvAlamat.text = userProfile.address
            tvNamaNegara.text = userProfile.areaData.country.countryName
            tvNamaProvinsi.text = userProfile.areaData.province.provinceName
            tvNamaDistrik.text = userProfile.areaData.district.districtName
            tvNamaDesa.text = userProfile.areaData.village.villagesName

            userLat = userProfile.latitude
            userLong = userProfile.longitude
            if (!userLat.isNaN() && !userLong.isNaN()){
                updateMap(userLat, userLong)
            }

        }
    }

    private fun updateMap(newLat: Double, newLong:Double){
        //map = googleMap ?: return
        val newLoc = LatLng(newLat, newLong)
        Log.e(TAG, "maps : $newLoc")
        with(map){
            moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 13.0f))
            addMarker(MarkerOptions().apply {
                position(newLoc)
            })
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

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map.uiSettings.isMyLocationButtonEnabled = false
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
