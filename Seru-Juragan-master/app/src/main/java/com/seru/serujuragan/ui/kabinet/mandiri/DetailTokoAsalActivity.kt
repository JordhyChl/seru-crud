package com.seru.serujuragan.ui.kabinet.mandiri

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.DetailTokoAsalRes
import com.seru.serujuragan.data.response.UploadPicRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.DetailTokoAsalContract
import com.seru.serujuragan.permission.PermissionsConfig
import com.seru.serujuragan.ui.base.BaseActivity
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.setLocalImage
import com.seru.serujuragan.view.signaturepad.SignaturePadActivity
import kotlinx.android.synthetic.main.activity_detail_toko_asal.*
import kotlinx.android.synthetic.main.dialog_alert_mocklocation.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class DetailTokoAsalActivity : BaseActivity(),
    DetailTokoAsalContract.View, OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    companion object{
        val TAG = DetailTokoAsalActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQ_SIGNATURE = 304
    }

    @Inject
    lateinit var presenter: DetailTokoAsalContract.Presenter
    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private var fillColorArgb : Int = 0
    private var strokeColorArgb : Int = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val defaultLoc = LatLng(-6.1788367,106.8246641)
    private var shopLat: Double = 0.0
    private var shopLong: Double = 0.0
    private var cabinetSelected: Boolean = false
    private var recallReason: String = ""
    private var otherNote: String = ""
    private var signatureId: String = ""
    private lateinit var idToko: String
    lateinit var dialog: Dialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_toko_asal)
        toolbarTitle.text = "DETAIL TOKO ASAL"
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

        idToko = intent.getStringExtra(Constants.ID_TOKO_ASAL)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            Log.e(TAG, idToko)
            showProgressDialog(true)
            presenter.loadDetailToko(idToko)
        }else{
            scrollView.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnNavigasi.setOnClickListener { navigateTo() }

        btnCheckin.setOnClickListener {
            val lat = shopLat.toString()
            val long = shopLong.toString()
            showProgressDialog(true)
            presenter.submitCheckin("request", idToko, lat, long)
        }

        imgSignature.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionsConfig.STORAGE)
            else openSignaturePad()
        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }

    }

    fun onCheckboxClicked(view: View){
        if (view is CheckBox){
            val checked = view.isChecked
            when(view.getId()){
                R.id.cbSelectOutlet ->
                    cabinetSelected = checked
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbTutup ->
                    if (checked){
                        recallReason = "Tutup"
                    }
                R.id.rbPindah ->
                    if (checked){
                        recallReason = "Pindah"
                    }
                R.id.rbTarget ->
                    if (checked){
                        recallReason = "Di bawah target"
                    }
                R.id.rbLainnya ->
                    if (checked){
                        recallReason = "Lainnya"
                    }
            }
        }
    }

    private fun openSignaturePad(){
        val intent = Intent(this, SignaturePadActivity::class.java)
        startActivityForResult(intent,
            REQ_SIGNATURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQ_SIGNATURE -> {
                    if (resultCode == Activity.RESULT_OK){
                        if (data != null){
                            val signatureFile = data.extras?.get("dataFile")
                            Log.i(TAG,"nilai sigURI : $signatureFile")
                            presenter.uploadFoto("signature", signatureFile as File?)
                            showProgressDialog(true)
                        }
                    }
                }
            }
        }
    }

    private fun showViewData(res: DetailTokoAsalRes){

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
        shopLat = res.location.shop_latitude
        shopLong = res.location.shop_longitude
        if (!shopLat.isNaN() && !shopLong.isNaN()){
            updateMap(shopLat, shopLong)
            drawRadius(shopLat,shopLong)
            btnNavigasi.isEnabled = true
        }
        tvCabinetCode.text = res.cabinetInfo.cabinetCode
        tvCabinetType.text = res.cabinetInfo.cabinetType
        tvQrCode.text = res.cabinetInfo.cabinetQrCode

        if (res.cabinetInfo.cabinetQrCode.isEmpty()){
            generateQr("EMPTY")
        }else{
            generateQr(res.cabinetInfo.cabinetQrCode)
        }

        btnSubmitRequest.setOnClickListener {

            otherNote = edtNoteTambah.text.toString()

            if (validateForm()){
                val intent = Intent(this, ListTokoTujuanActivity::class.java)
                intent.putExtra(Constants.RECALL_REASON, recallReason)
                intent.putExtra(Constants.RECALL_NOTE, otherNote)
                intent.putExtra(Constants.ID_SIGNATURE, signatureId)
                intent.putExtra(Constants.ID_TOKO_ASAL, res.outlet_id)
                startActivity(intent)
            }
        }
    }

    private fun validateForm():Boolean{
        return if (cabinetSelected && recallReason.isNotEmpty() && signatureId.isNotEmpty()){
            true
        } else {
            Toast.makeText(this, "Harap pilih kabinet dan isi alasan penarikan serta tanda tangan", Toast.LENGTH_LONG).show()
            false
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
        //map.uiSettings.isZoomControlsEnabled = true

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

    @SuppressLint("MissingPermission")
    private fun drawRadius(latitude: Double, longitude: Double){

        val locJuaragan = LatLng(latitude, longitude)
        val radiusJur = 15.0
        val thresholdJur = 5f
        val radiusFinal = radiusJur + thresholdJur
        var distance = FloatArray(2)
        fillColorArgb = Color.argb(100, 230, 230, 149)
        strokeColorArgb = Color.argb(130, 230, 230, 70)

        Log.d(TAG, "loc juragan : $locJuaragan")
        with(map){
            moveCamera(CameraUpdateFactory.newLatLngZoom(locJuaragan, 19.0f))
            addMarker(MarkerOptions().apply {
                position(locJuaragan)
            })
            addCircle(CircleOptions().apply {
                center(locJuaragan)
                radius(radiusJur)
                fillColor(fillColorArgb)
                strokeWidth(thresholdJur)
                strokeColor(strokeColorArgb)
            })

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val loc : Location? = location
                val mol = checkMockLocation(loc!!, this@DetailTokoAsalActivity)
                Log.e(TAG,"mock check val : $mol")
                if (mol){
                    showInfoMockDialog()
                }
                Location.distanceBetween(
                    locJuaragan.latitude, locJuaragan.longitude,
                    location!!.latitude, location!!.longitude,
                    //lat,long,
                    distance)

                try {
                    if (distance[0] > radiusFinal){
                        Log.i(TAG,"radius tidak masuk : ${distance[0]}")
                        //tv_checkinInfo.text = "Maaf, lokasi anda tidak berada dalam radius Toko"
                        //btnCheckin.setBackgroundResource(R.color.material_green_200)
                        //tv_checkinInfo.setTextColor(Color.RED)
                    }else{
                        Log.i(TAG,"radius masuk : ${distance[0]}")
                        shopLat = location.latitude
                        shopLong = location.longitude
                        AppPreference.tempLocation = location.latitude.toString()+location.longitude.toString()
                        //btnCheckin.isEnabled = true
                        //btnCheckin.setBackgroundResource(R.color.material_green_500)
                        //tv_checkinInfo.visibility = View.GONE
                    }
                }catch (e:Error){
                    Log.e(TAG,"error catch : $e")
                }

            }
        }
    }

    private fun checkMockLocation(location: Location, context: Context) : Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            return location.isFromMockProvider
        }else{
            var mockLoc = "0"
            try {
                mockLoc = Settings.Secure.getString(context.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION)
            }catch (e: Exception){
                Log.e(TAG,"catch : $e")
            }
            return mockLoc != "0"
        }
    }

    private fun showInfoMockDialog(){
        val inflator = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_mocklocation, null)

        dialog = Dialog(this)
        dialog.setContentView(viewGroup)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnAlertRooted.setOnClickListener {
            dialog.dismiss()
            doLogout()
            finish()
        }

        dialog.show()
    }

    private fun navigateTo(){
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${shopLat},${shopLong}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        Log.e(TAG,"uriuri :  $gmmIntentUri")
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun hasPermissionGranted(permission: Int, hasGranted: Boolean) {
        when (permission) {
            PermissionsConfig.STORAGE -> {
                if (hasGranted) openSignaturePad()
            }
        }
    }

    override fun loadDetailTokoSuccess(detailTokoRes: DetailTokoAsalRes) {
        Log.i(TAG, "load data value : ${detailTokoRes.outlet_id}")
        showViewData(detailTokoRes)
    }

    override fun uploadImgSuccess(name:String, imgFile: File?, res: UploadPicRes) {
        Log.e(TAG,"data : ${res.imgId}")
        signatureId = res.imgId
        imgSignature.setLocalImage(imgFile!!)
    }

    override fun checkinSuccess() {
        Log.d(TAG, "success checkin")
        sec_ket_kabinet.visibility = View.VISIBLE
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
//        Log.e("Error load cause","$error")
//        scrollView.visibility = View.GONE
//        emptyPage.visibility = View.VISIBLE
//        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        Log.e(TAG," error cause : $error")
        if (error == "upimagefail"){
            Toast.makeText(this,"Unggah Foto gagal, harap ulangi kembali",Toast.LENGTH_LONG).show()
        }else{
            scrollView.visibility = View.GONE
            emptyPage.visibility = View.VISIBLE
            Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        }
    }
}
