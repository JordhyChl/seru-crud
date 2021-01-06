package com.seru.serujuragan.ui.kabinet.status

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.DetailStatusKabinetContract
import com.seru.serujuragan.mvp.presenter.kabinet.DetailStatusKabinetPresenter
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_detail_status_cabinet_actvity.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class DetailStatusCabinetActvity : AppCompatActivity(),
    DetailStatusKabinetContract.View, OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    companion object{
        val TAG = DetailStatusCabinetActvity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    @Inject
    lateinit var presenter: DetailStatusKabinetContract.Presenter
    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private val defaultLoc = LatLng(-6.1788367,106.8246641)
    private var outletLatAsal: Double = 0.0
    private var outletLongAsal: Double = 0.0
    private var outletLatTujuan: Double = 0.0
    private var outletLongTujuan: Double = 0.0
    private var requestState: Int = 0
    lateinit var requestStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_status_cabinet_actvity)
        toolbarTitle.text = "DETAIL TARIK MANDIRI"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        initView()
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

        val idRequest = intent.getStringExtra(Constants.ID_REQUEST_CABINET)!!
        requestState = intent.getIntExtra(Constants.REQUEST_STATE,0)
        requestStatus = intent.getStringExtra(Constants.REQUEST_STATUS)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadDetailRequest(idRequest)
            //Dev Data
            /*showDataTokoAsal(DetailTokoAsalRes("31710700041589352108914027008", "DEV Mitra Test Tukar Mandiri", "DEV Team",
                "62833399201","62822993022","Jl. Mampang Prapatan",
                District("3171070","MAMPANG PRAPATAN"), Village("3171070004","MAMPANG PRAPATAN"),
                Location(-6.2419278,106.8232298), Cabinet("S291200000264","","S291200000264")
            ))

            showDataTokoTujuan(DetailTokoAsalRes("31710700041589352108914027008", "DEV Mitra Test Tukar Mandiri", "DEV Team",
                "62833399201","62822993022","Jl. Mampang Prapatan",
                District("3171070","MAMPANG PRAPATAN"), Village("3171070004","MAMPANG PRAPATAN"),
                Location(-6.2419278,106.8232298), Cabinet("S291200000264","","S291200000264")
            ))*/
        }else{
            //lyListStatusCabinet.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){

        //showViewData(DetailTokoAsalRes())

        val mapFragmentAsal = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragmentAsal.getMapAsync(this)

        val mapFragmentTujuan = supportFragmentManager.findFragmentById(R.id.mapTujuan) as SupportMapFragment
        mapFragmentTujuan.getMapAsync(this)

    }

    private fun showDataTokoAsal(res: DetailTokoKabinetRes){


        tvStatusState.text = requestStatus
        tvKodeToko.text = res.outlet_id
        tvNamaToko.text = res.shop_name
        tvNamaPemilikToko.text = res.owner_name
        tvAlamat.text = res.address
        tvPhone1.text = res.shop_telp1
        btnPhone1.setOnClickListener {
            callIntent(res.shop_telp1)
        }
        tvPhone2.text = res.shop_telp2
        btnPhone2.setOnClickListener {
            if (res.shop_telp2.isNotEmpty())
                callIntent(res.shop_telp2)
            else Log.i(TAG,"phone 2 empty")
        }
        tvKecamatan.text = res.district.districtName
        tvKelurahan.text = res.village.villagesName
        tvTokoLat.text = res.location.shop_latitude.toString()
        tvTokoLong.text = res.location.shop_longitude.toString()
        outletLatAsal = res.location.shop_latitude
        outletLongAsal = res.location.shop_longitude
        if (!outletLatAsal.isNaN() && !outletLongAsal.isNaN()){
            updateMap(outletLatAsal, outletLongAsal)
            btnNavigasiAsal.isEnabled = true
        }
        btnNavigasiAsal.setOnClickListener {
            navigateTo(outletLatAsal, outletLongAsal)
        }

    }

    private fun showDataTokoTujuan(res: DetailTokoKabinetRes){

        Log.e(TAG,"toko tujuan = ${res.shop_name}")

        tvKodeTokoTujuan.text = res.outlet_id
        tvNamaTokoTujuan.text = res.shop_name
        tvNamaPemilikTokoTujuan.text = res.owner_name
        tvAlamatTujuan.text = res.address
        tvPhone1Tujuan.text = res.shop_telp1
        btnPhone1Tujuan.setOnClickListener {
            callIntent(res.shop_telp1)
        }
        tvPhone2Tujuan.text = res.shop_telp2
        btnPhone2Tujuan.setOnClickListener {
            if (res.shop_telp2.isNotEmpty())
                callIntent(res.shop_telp2)
            else Log.i(TAG,"phone 2 empty")
        }
        tvKecamatanTujuan.text = res.district.districtName
        tvKelurahanTujuan.text = res.village.villagesName
        tvTokoLatTujuan.text = res.location.shop_latitude.toString()
        tvTokoLongTujuan.text = res.location.shop_longitude.toString()
        outletLatTujuan = res.location.shop_latitude
        outletLongTujuan = res.location.shop_longitude
        if (!outletLatTujuan.isNaN() && !outletLongTujuan.isNaN()){
            updateMap(outletLatTujuan, outletLongTujuan)
            btnNavigasiTujuan.isEnabled = true
        }
        btnNavigasiTujuan.setOnClickListener {
            navigateTo(outletLatTujuan, outletLongTujuan)
        }

    }

    private fun generateQr(code: String){
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 150, 150)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        imgQrCode.setImageBitmap(bitmap)
    }

    @SuppressLint("MissingPermission")
    private fun callIntent(phoneNumber: String){
        val phoneUri: Uri = Uri.parse("tel:$phoneNumber")
        val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
        startActivity(callIntent)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        enableMyLocation()
        map.uiSettings.isMyLocationButtonEnabled = false

        with(map){
            moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 14.0f))
//            addMarker(MarkerOptions().apply {
//                position(kost)
//            })
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

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private fun enableMyLocation() {
        // Enable the location layer. Request the location permission if needed.
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            map.isMyLocationEnabled = true

        } else {
            // if permissions are not currently granted, request permissions
            EasyPermissions.requestPermissions(this,
                getString(R.string.permission_rationale_location),
                LOCATION_PERMISSION_REQUEST_CODE, *permissions)
        }
    }

    private fun navigateTo(latitude:Double, longitude:Double){
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${latitude},${longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        Log.e(TAG,"uriuri :  $gmmIntentUri")
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun loadDetailStatusCabinet(detailRequestMandiriRes: DetailRequestMandiriRes) {
        when(requestState){
            3 -> {
                lyRequestStatus.setBackgroundColor(Color.parseColor("#F08E30"))
                lyKabinet.setBackgroundResource(R.drawable.bg_rect_border_orange)
            }
            4 -> {
                lyRequestStatus.setBackgroundColor(Color.parseColor("#F08E30"))
                lyKabinet.setBackgroundResource(R.drawable.bg_rect_border_orange)
            }
            5 -> {
                lyRequestStatus.setBackgroundColor(Color.parseColor("#009688"))
                lyKabinet.setBackgroundResource(R.drawable.bg_rect_border_green)
            }
            6 -> {
                lyRequestStatus.setBackgroundResource(R.color.material_red_700)
                lyKabinet.setBackgroundResource(R.drawable.bg_rect_border_red)
                lyRejectReason.visibility = VISIBLE
            }
            else -> {
                lyRequestStatus.setBackgroundColor(Color.parseColor("#FFEB3B"))
                lyKabinet.setBackgroundResource(R.drawable.bg_rect_border_yellow)
            }
        }
        tvStatusType.text = "Tarik Mandiri"
        tvStatusState.text = ""
        tvRTM.text = detailRequestMandiriRes.requestId
        tvCabinetCode.text = detailRequestMandiriRes.cabinet.cabinetCode
        tvCabinetType.text = detailRequestMandiriRes.cabinet.cabinetType
        val qrCode = detailRequestMandiriRes.cabinet.cabinetQrCode
        generateQr(qrCode)
        tvQrCode.text = qrCode
        showDataTokoAsal(detailRequestMandiriRes.detailTokoAsal)
        showDataTokoTujuan(detailRequestMandiriRes.detailTokoTujuan)
        when(detailRequestMandiriRes.recallReason){
            "Tutup"-> {
                rbTutup.isChecked = true
            }
            "Pindah"-> {
                rbPindah.isChecked = true
            }
            "Di bawah target"-> {
                rbTarget.isChecked = true
            }
            else ->{
                rbLainnya.isChecked = true
            }
        }
        tvRejectReason.text = detailRequestMandiriRes.rejectReason
    }

    override fun doLogout() {
        this.getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        Log.i(TAG, "app pref :  ${AppPreference.isLoggedIn}")
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e("Error load cause", error)
        scrollView.visibility = GONE
        emptyPage.visibility = View.VISIBLE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
