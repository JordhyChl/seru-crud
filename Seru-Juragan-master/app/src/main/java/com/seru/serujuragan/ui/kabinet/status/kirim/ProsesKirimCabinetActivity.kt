package com.seru.serujuragan.ui.kabinet.status.kirim

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.Picture
import com.seru.serujuragan.data.request.SubmitProsesPemindahan
import com.seru.serujuragan.data.request.SurveyKabinet
import com.seru.serujuragan.data.response.CabinetMandiriRes
import com.seru.serujuragan.data.response.RequestInfoRes
import com.seru.serujuragan.data.response.UploadPicRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.ProsesKirimKabinetContract
import com.seru.serujuragan.permission.PermissionsConfig
import com.seru.serujuragan.ui.base.BaseActivity
import com.seru.serujuragan.ui.kabinet.status.DetailStatusCabinetActvity
import com.seru.serujuragan.ui.kabinet.status.ListStatusCabinetActivity
import com.seru.serujuragan.ui.kabinet.status.UbahJadwalKabinetActivity
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.customview.StaticBottomSheet
import com.seru.serujuragan.view.setLocalImage
import com.seru.serujuragan.view.signaturepad.SignaturePadActivity
import kotlinx.android.synthetic.main.activity_proses_kirim_cabinet.*
import kotlinx.android.synthetic.main.dialog_alert_mocklocation.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class ProsesKirimCabinetActivity : BaseActivity(),
    ProsesKirimKabinetContract.View,
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    companion object{
        val TAG = ProsesKirimCabinetActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val STORAGE_PERMISSION_REQUEST_CODE = 2
        private const val REQ_CABINET_OUTSIDE = 301
        private const val REQ_CABINET_INSIDE = 302
        private const val REQ_KTP = 303
        private const val REQ_KTP_OWNER = 304
        private const val REQ_ADR = 305
        private const val REQ_SIGNATURE = 306
    }

    @Inject
    lateinit var presenter: ProsesKirimKabinetContract.Presenter

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
    private lateinit var dialog: Dialog
    private lateinit var idRequest: String
    private var requestState: Int = 0
    private lateinit var requestStatus: String
    private var submitSuccess:Boolean = false
    //img
    private lateinit var pictureSourceMenu: StaticBottomSheet
    private lateinit var requestPicture: String
    private var pic1 : String = "2"
    private var pic2 : String = "2"
    private var pic3 : String = "2"
    private var pic4 : String = "2"
    private var pic5 : String = "2"
    private var pic6 : String = "2"
    private var mDataPicture = ArrayList<Picture>()

    //Kelengkapan Cabinet
    private var unitCabinet: String = ""
    private var unitCabinetVal : Int = 1
    private var unitHighlighter : String = ""
    private var unitHighlighterVal : Int = 1
    private var unitKeranjang : String = ""
    private var unitKeranjangVal : Int = 1
    private var unitPanduan : String = ""
    private var unitPanduanVal : Int = 1
    private var unitKunci : String = ""
    private var unitKunciVal : Int = 1
    private var unitScrapper : String = ""
    private var unitScrapperVal : Int = 1
    private var unitLainnya : String = ""
    private var unitLainnyaVal : Int = 1
    //Pastikan
    private var barcodeCabinetBol : Boolean = false
    private var penerinaSesuaiBol : Boolean = false
    private var posmTerpasangBol : Boolean = false
    private var kardusBersihBol : Boolean = false
    private var instruksiDiberikanBol : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proses_kirim_cabinet)
        toolbarTitle.text = "DETAIL KIRIM KABINET"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        initMenuPicture()
        initDataPic()
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
        idRequest = intent.getStringExtra(Constants.ID_REQUEST_CABINET)!!
        requestState = intent.getIntExtra(Constants.REQUEST_STATE,0)
        requestStatus = intent.getStringExtra(Constants.REQUEST_STATUS)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadDetailRequest(idRequest)

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

        btnCheckin.setOnClickListener {
            val lat = shopLat.toString()
            val long = shopLong.toString()
            showProgressDialog(true)
            presenter.submitCheckin("process", idRequest, lat, long)
        }

        btnDetailToko.setOnClickListener {
            val intent = Intent(this, DetailStatusCabinetActvity::class.java)
            intent.putExtra(Constants.ID_REQUEST_CABINET, idRequest)
            intent.putExtra(Constants.REQUEST_STATE, requestState)
            intent.putExtra(Constants.REQUEST_STATUS, requestStatus)
            startActivity(intent)
        }

        btnBAP.setOnClickListener {
            downloadToSharePDF("bap", idRequest)
        }

        btnSuratJalan.setOnClickListener {
            downloadToSharePDF("adr", idRequest)
        }

        btnChangeDate.setOnClickListener {
            val intent = Intent(this, UbahJadwalKabinetActivity::class.java)
            intent.putExtra(Constants.INTENT_SOURCE,"kirim")
            intent.putExtra(Constants.ID_REQUEST_CABINET, idRequest)
            intent.putExtra(Constants.REQUEST_STATE, requestState)
            intent.putExtra(Constants.REQUEST_STATUS, requestStatus)
            startActivity(intent)
        }

        imgOutCabinet.setOnClickListener {
            requestPicture = REQ_CABINET_OUTSIDE.toString()
            pictureSourceMenu.show()
        }

        imgInCabinet.setOnClickListener {
            requestPicture = REQ_CABINET_INSIDE.toString()
            pictureSourceMenu.show()
        }

        imgKtp.setOnClickListener {
            requestPicture = REQ_KTP.toString()
            pictureSourceMenu.show()
        }

        imgKtpOwner.setOnClickListener {
            requestPicture = REQ_KTP_OWNER.toString()
            pictureSourceMenu.show()
        }

        imgADR.setOnClickListener {
            requestPicture = REQ_ADR.toString()
            pictureSourceMenu.show()
        }

        imgSignature.setOnClickListener {
            Log.e(TAG,"sign Clicked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionsConfig.STORAGE)
            else openSignaturePad()
        }

        btnSubmitRequest.setOnClickListener {
            getFormValue()
        }

        btnFinish.setOnClickListener {
            val intent = Intent(this, ListStatusCabinetActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun initDataPic(){
        mDataPicture.add(Picture("","luarkabinet"))
        mDataPicture.add(Picture("","dalamkabinet"))
        mDataPicture.add(Picture("","ktp"))
        mDataPicture.add(Picture("","ktpdanpemilik"))
        mDataPicture.add(Picture("","adr"))
        mDataPicture.add(Picture("","signature"))
    }

    fun onCheckboxClicked(view: View){
        if (view is CheckBox){
            val checked = view.isChecked
            when(view.getId()){
                R.id.cbUnitCabinet ->
                    if (checked){
                        edtUnitCabinet.isEnabled = true
                        unitCabinet = "1"
                        cbUnitCabinetV.isChecked = true
                    }else{
                        edtUnitCabinet.isEnabled = false
                        unitCabinet = "2"
                        cbUnitCabinetV.isChecked = false
                    }
                R.id.cbUnitHighliter ->
                    if (checked){
                        edtUnitHighlighter.isEnabled = true
                        unitHighlighter = "1"
                        cbUnitHighliterV.isChecked = true
                    }else{
                        edtUnitHighlighter.isEnabled = false
                        unitHighlighter = "2"
                        cbUnitHighliterV.isChecked = false
                    }
                R.id.cbUnitKeranjang ->
                    if (checked){
                        edtUnitKeranjang.isEnabled = true
                        unitKeranjang = "1"
                        cbUnitKeranjangV.isChecked = true
                    }else{
                        edtUnitKeranjang.isEnabled = false
                        unitKeranjang = "2"
                        cbUnitKeranjangV.isChecked = false
                    }
                R.id.cbUnitPanduan ->
                    if (checked){
                        edtUnitPanduan.isEnabled = true
                        unitPanduan = "1"
                        cbUnitPanduanV.isChecked = true
                    }else{
                        edtUnitPanduan.isEnabled = false
                        unitPanduan = "2"
                        cbUnitPanduanV.isChecked = false
                    }
                R.id.cbUnitKunci ->
                    if (checked){
                        edtUnitKunci.isEnabled = true
                        unitKunci = "1"
                        cbUnitKunciV.isChecked = false
                    }else{
                        edtUnitKunci.isEnabled = false
                        unitKunci = "2"
                        cbUnitKunciV.isChecked = false
                    }
                R.id.cbUnitScrapper ->
                    if (checked){
                        edtUnitScrapper.isEnabled = true
                        unitScrapper = "1"
                        cbUnitScrapperV.isChecked = true
                    }else{
                        edtUnitScrapper.isEnabled = false
                        unitScrapper = "2"
                        cbUnitScrapperV.isChecked = false
                    }
                R.id.cbUnitOther ->
                    if (checked){
                        edtUnitOther.isEnabled = true
                        unitLainnya = "1"
                        cbUnitOtherV.isChecked = true
                    }else{
                        edtUnitOther.isEnabled = false
                        unitLainnya = "2"
                        cbUnitOtherV.isChecked = false
                    }
                //Pastikan
                R.id.cbBarcodeSesuai ->
                    barcodeCabinetBol = checked
                R.id.cbPenerimaSesuai ->
                    penerinaSesuaiBol = checked
                R.id.cbPOSM ->
                    posmTerpasangBol = checked
                R.id.cbKardusBaik ->
                    kardusBersihBol = checked
                R.id.cbInstruksi ->
                    instruksiDiberikanBol = checked
            }
        }
    }

    private fun getFormValue(){
        var listPhoto = false

        if (unitCabinet == "1"){
            unitCabinetVal = edtUnitCabinet.text.toString().toInt()
            tvUnitCabinet.text = edtUnitCabinet.text.toString()
        }else{
            unitCabinetVal = 0
            tvUnitCabinet.text = "0"
        }

        if (unitHighlighter == "1"){
            unitHighlighterVal = edtUnitHighlighter.text.toString().toInt()
            tvUnitHighlighter.text = edtUnitHighlighter.text.toString()
        }else{
            unitHighlighterVal = 0
            tvUnitHighlighter.text = "0"
        }

        if (unitKeranjang == "1"){
            unitKeranjangVal = edtUnitKeranjang.text.toString().toInt()
            tvUnitKeranjang.text = edtUnitKeranjang.text.toString()
        }else{
            unitKeranjangVal = 0
            tvUnitKeranjang.text = "0"
        }

        if (unitPanduan == "1"){
            unitPanduanVal =  edtUnitPanduan.text.toString().toInt()
            tvUnitPanduan.text = edtUnitPanduan.text.toString()
        }else{
            unitPanduanVal = 0
            tvUnitPanduan.text = "0"
        }

        if(unitKunci == "1"){
            unitKunciVal = edtUnitKunci.text.toString().toInt()
            tvUnitKunci.text = edtUnitKunci.text.toString()
        }else{
            unitKunciVal = 0
            tvUnitKunci.text = "0"
        }

        if (unitScrapper == "1"){
            unitScrapperVal = edtUnitScrapper.text.toString().toInt()
            tvUnitScrapper.text = edtUnitScrapper.text.toString()
        }else{
            unitScrapperVal = 0
            tvUnitScrapper.text = "0"
        }

        if (unitLainnya == "1"){
            unitLainnyaVal = edtUnitOther.text.toString().toInt()
            tvUnitOther.text = edtUnitOther.text.toString()
        }else{
            unitLainnyaVal = 0
            tvUnitOther.text = "0"
        }

        var kelengkapanKabinet = SurveyKabinet(unitCabinet, unitCabinetVal, unitHighlighter, unitHighlighterVal,
            unitKeranjang,unitKeranjangVal, unitPanduan, unitPanduanVal, unitKunci, unitKunciVal,
            unitScrapper, unitScrapperVal, unitLainnya, unitLainnyaVal)

        val dataReq = SubmitProsesPemindahan(kelengkapanKabinet, mDataPicture)

        val jsonSubmit = Gson().toJson(dataReq)
        Log.i(TAG,"value submit : $jsonSubmit")

        if (pic1 == "1" && pic2 == "1" && pic3 == "1" && pic4 == "1"){
            listPhoto = true
        }

        if (barcodeCabinetBol && penerinaSesuaiBol && posmTerpasangBol && kardusBersihBol && instruksiDiberikanBol){
            if (listPhoto){
                showProgressDialog(true)
                presenter.submitMovingProcess(idRequest, dataReq)
                Log.d(TAG,"value submit : ok")
            }else{
                Toast.makeText(this,"Anda Harus mengupload semua Foto", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"Anda Harus memastikan semua syarat terpenuhi", Toast.LENGTH_SHORT).show()
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
        imgQrCode2.setImageBitmap(bitmap)
    }

    private fun initMenuPicture(){

        pictureSourceMenu = StaticBottomSheet(this)
            .init(R.layout.bottom_sheet_media_source)
            .setCancelable(true)
            .setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            .setGravity(Gravity.BOTTOM)

        pictureSourceMenu.getView().findViewById<LinearLayout>(R.id.menuCamera).setOnClickListener {
            pictureSourceMenu.dismiss()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermission(this, Manifest.permission.CAMERA, PermissionsConfig.CAMERA)
            else openCamera(requestPicture)
        }
    }

    private fun openCamera(start: String){
        Log.i(TAG, "open camera")
        ImagePicker.with(this)
            .crop(16f, 9f)
            .compress(120)
            .cameraOnly()
            .start(start.toInt())
    }

    private fun openSignaturePad(){
        Log.e(TAG,"sign open")
        val intent = Intent(this, SignaturePadActivity::class.java)
        startActivityForResult(intent,
            REQ_SIGNATURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQ_CABINET_OUTSIDE -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("luarkabinet",localFile)
                    showProgressDialog(true)
                }
                REQ_CABINET_INSIDE -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("dalamkabinet",localFile)
                    showProgressDialog(true)
                }
                REQ_KTP -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("ktp",localFile)
                    showProgressDialog(true)
                }
                REQ_KTP_OWNER -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("ktpdanpemilik",localFile)
                    showProgressDialog(true)
                }
                REQ_ADR -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("adr",localFile)
                    showProgressDialog(true)
                }
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
                val mol = checkMockLocation(loc!!, this@ProsesKirimCabinetActivity)
                Log.e(TAG,"mock check val : $mol")
                if (mol){
                    showInfoMockDialog()
                }
                Location.distanceBetween(
                    locJuaragan.latitude, locJuaragan.longitude,
                    location!!.latitude, location!!.longitude,
                    distance)

                try {
                    if (distance[0] > radiusFinal){
                        Log.i(TAG,"radius tidak masuk : ${distance[0]}")
                        tv_checkinInfo.text = "Maaf, lokasi anda tidak berada dalam radius Toko"
                        btnCheckin.setBackgroundResource(R.color.material_green_200)
                        tv_checkinInfo.setTextColor(Color.RED)
                    }else{
                        Log.i(TAG,"radius masuk : ${distance[0]}")
                        shopLat = location.latitude
                        shopLong = location.longitude
                        AppPreference.tempLocation = location.latitude.toString()+location.longitude.toString()
                        btnCheckin.isEnabled = true
                        btnCheckin.setBackgroundResource(R.color.material_green_500)
                        tv_checkinInfo.visibility = View.GONE
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

    private fun downloadToSharePDF(pathName: String, idRequest: String){
        Log.d(TAG,"call share PDF")
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *permissions)){
            showProgressDialog(true)
            presenter.downloadPdfShare(pathName, idRequest)
        }else{
            EasyPermissions.requestPermissions(this,
                getString(R.string.storage_permission_required),
                STORAGE_PERMISSION_REQUEST_CODE, *permissions)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun hasPermissionGranted(permission: Int, hasGranted: Boolean) {
        when (permission) {
            PermissionsConfig.CAMERA -> {
                if (hasGranted)
                    openCamera(requestPicture)
            }
            PermissionsConfig.STORAGE -> {
                if (hasGranted) openSignaturePad()
            }
        }
    }

    override fun loadRequestInfo(requestInfoRes: RequestInfoRes) {
        tvRTM.text = requestInfoRes.requestId
        tvCabinetCode.text = requestInfoRes.cabinet.cabinetCode
        tvCabinetType.text = requestInfoRes.cabinet.cabinetType
        val qrCode = requestInfoRes.cabinet.cabinetQrCode
        generateQr(qrCode)
        tvQrCode.text = qrCode
        shopLat = requestInfoRes.location.shop_latitude
        shopLong = requestInfoRes.location.shop_longitude
        tvTokoLat.text = "$shopLat"
        tvTokoLong.text = "$shopLong"
        if (!shopLat.isNaN() && !shopLong.isNaN()){
            updateMap(shopLat, shopLong)
            drawRadius(shopLat, shopLong)
            btnCheckin.isEnabled = true
        }
        //halaman konfirmasi
        tvRTMConfirm.text = requestInfoRes.requestId
        tvCabinetCode2.text = requestInfoRes.cabinet.cabinetCode
        tvCabinetType2.text = requestInfoRes.cabinet.cabinetType
        tvQrCode2.text = qrCode
    }

    override fun uploadImgSuccess(name: String, imgFile: File?, res: UploadPicRes) {
        Log.e(TAG,"data : ${res.imgId}")

        for (i in mDataPicture.indices){
            if (mDataPicture[i].picture_name == name) {
                mDataPicture[i].picture_id = res.imgId
            }
        }
        mDataPicture = mDataPicture

        when (name) {
            "luarkabinet" -> {
                pic1 = "1"
                imgOutCabinet.setLocalImage(imgFile!!)
            }
            "dalamkabinet" -> {
                pic2 = "1"
                imgInCabinet.setLocalImage(imgFile!!)
            }
            "ktp" -> {
                pic3 = "1"
                imgKtp.setLocalImage(imgFile!!)
            }
            "ktpdanpemilik" -> {
                pic4 = "1"
                imgKtpOwner.setLocalImage(imgFile!!)
            }
            "adr" -> {
                pic5 = "1"
                imgADR.setLocalImage(imgFile!!)
            }
            "signature" -> {
                pic6 = "1"
                imgSignature.setLocalImage(imgFile!!)
            }
        }
    }

    override fun movingProcessSuccess(cabinetMandiriRes: CabinetMandiriRes) {
        Log.e(TAG," success : ${cabinetMandiriRes.requestId}")
        scrollView.visibility = View.GONE
        svSuccesSubmit.visibility = VISIBLE
        submitSuccess = true
    }

    override fun downloadPDFSuccess(fileName: String) {
        if (fileName.isNotEmpty()){
            try {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS),fileName)
                file.setReadable(true, false)
                /*val uri = FileProvider.getUriForFile(this,
                    "com.seru.serujuragan.provider",file)
                val intent = Intent(Intent.ACTION_SEND)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "application/pdf"
                startActivity(Intent.createChooser(intent,"Bagikan PDF via"))*/
                showProgressDialog(false)
                Toast.makeText(this, "Dokumen $fileName berhasil di Unduh", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Log.e(TAG,"catch : $e")
            }
        }
    }

    override fun checkinSuccess() {
        secKelengkapanCabinet.visibility = VISIBLE
        lyImgKabinet.visibility = VISIBLE
        sec_verifikasi_pemilik.visibility = VISIBLE
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
        Log.e(TAG," error cause : $error")
        if (error == "upimagefail"){
            Toast.makeText(this,"Unggah Foto gagal, harap ulangi kembali",Toast.LENGTH_LONG).show()
        }else{
            scrollView.visibility = View.GONE
            emptyPage.visibility = View.VISIBLE
            Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (submitSuccess){
            Log.d(TAG,"back button disable")
        }else{
            super.onBackPressed()
        }
    }
}
