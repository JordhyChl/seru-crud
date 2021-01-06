package com.seru.serujuragan.ui.kabinet.mandiri

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.CabinetMandiriReq
import com.seru.serujuragan.data.request.MoveEstimate
import com.seru.serujuragan.data.request.MoveNotes
import com.seru.serujuragan.data.response.CabinetMandiriRes
import com.seru.serujuragan.data.response.DetailTokoAsalRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.DetailTokoTujuanContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_detail_toko_tujuan.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DetailTokoTujuanActivity : AppCompatActivity(), SlideDatePickerDialogCallback,
    DetailTokoTujuanContract.View, OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks{

    companion object{
        val TAG = DetailTokoTujuanActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val STORAGE_PERMISSION_REQUEST_CODE = 2
    }

    @Inject
    lateinit var presenter: DetailTokoTujuanContract.Presenter
    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private val defaultLoc = LatLng(-6.1788367,106.8246641)
    private var shopLat: Double = 0.0
    private var shopLong: Double = 0.0
    private lateinit var idTokoAsal: String
    private lateinit var idTokoTujuan: String
    private lateinit var recallReason: String
    private lateinit var otherNote: String
    private lateinit var signatureId: String
    private var tnc: Boolean = false
    private var callDate: Boolean = false
    private var datePickerCall : Long = 0
    private var sentDate: Boolean = false
    private var datePickerSent : Long = 0
    private var dateCallPicked: Boolean = false
    private var dateSentPicked: Boolean = false
    private var submitSuccess:Boolean = false
    lateinit var tokoAsalName: String
    private lateinit var idRequest : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_toko_tujuan)
        toolbarTitle.text = "DETAIL TOKO TUJUAN"
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

        idTokoAsal = intent.getStringExtra(Constants.ID_TOKO_ASAL)!!
        idTokoTujuan = intent.getStringExtra(Constants.ID_TOKO_TUJUAN)!!
        recallReason = intent.getStringExtra(Constants.RECALL_REASON)!!
        otherNote = intent.getStringExtra(Constants.RECALL_NOTE)!!
        signatureId = intent.getStringExtra(Constants.ID_SIGNATURE)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            Log.e(TAG, idTokoAsal)
            showProgressDialog(true)
            presenter.loadDetailToko(idTokoAsal,idTokoTujuan)
        }else{
            svDetailToko.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnNavigasi.setOnClickListener { navigateTo() }

        tvWithdrawDate.setOnClickListener {
            initCalendar(1)
            callDate = true
            sentDate = false
        }

        tvSentDate.setOnClickListener {
            initCalendar(2)
            callDate = false
            sentDate = true
        }

        btnShareA1.setOnClickListener {
            downloadToSharePDF("withdraw", idRequest)
        }

        btnFinish.setOnClickListener {
            val intent = Intent(this, ListTokoAsalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

    }

    fun onCheckboxClicked(view: View){
        if (view is CheckBox){
            val checked = view.isChecked
            when(view.getId()){
                R.id.cbTNC ->
                    tnc = checked
            }
        }
    }

    private fun showViewData(res: DetailTokoAsalRes){

        tvKodeToko.text = res.outlet_id
        tvNamaToko.text = res.shop_name
        tokoAsalName = res.shop_name
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
            btnNavigasi.isEnabled = true
        }
        tvCabinetCode.text = res.cabinetInfo.cabinetCode
        tvCabinetType.text = res.cabinetInfo.cabinetType
        tvQrCode.text = res.cabinetInfo.cabinetQrCode

        if (res.cabinetInfo.cabinetQrCode.isEmpty()){
            generateQr("EMPTY",1)
        }else{
            generateQr(res.cabinetInfo.cabinetQrCode,1)
        }

        Log.e(ListTokoTujuanActivity.TAG,"testets : $recallReason - $otherNote - $signatureId")

        btnSubmitRequest.setOnClickListener {
            if (validateForm()) {
                Log.e(TAG, "$datePickerCall - $datePickerSent - $recallReason - $otherNote - $signatureId")
                val dateRecomendation = MoveEstimate(datePickerCall, datePickerSent)
                val notes = MoveNotes(otherNote,otherNote)
                val reqCabinet = CabinetMandiriReq(moveEstimate = dateRecomendation, moveNotes = notes, reasonWithdrawal = recallReason, signatureId = signatureId)
                showProgressDialog(true)
                presenter.submitRequestCabinet(idTokoAsal, idTokoTujuan, reqCabinet)
                //submitRequestSuccess()
            }
        }
    }

    private fun validateForm():Boolean{
        val stringCallDate = datePickerCall.toString()
        val stringSentDate = datePickerSent.toString()
        return if (tnc && stringCallDate != "0" && stringSentDate != "0"){
            true
        } else {
            Toast.makeText(this, "Harap isi perkiraan tanggal dan check setuju", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun generateQr(code: String, layoutId:Int){
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

        if (layoutId == 1) {
            imgQrCode.setImageBitmap(bitmap)
        }else{
            imgQrCode2.setImageBitmap(bitmap)
        }
    }

    private fun initCalendar(startDate: Int){
        SlideDatePickerDialog.Builder()
            .setLocale(Locale("in"))
            .setHeaderDateFormat("EEEE dd MMMM")
            .setShowYear(true)
            .setCancelText("batal")
            .setConfirmText("pilih")
            .setEndDate(Calendar.getInstance().apply {
                add(Calendar.DATE, 90)
            })
            .setStartDate(Calendar.getInstance().apply {
                set(Calendar.DATE, this.get(Calendar.DATE) + startDate)
            })
            .setPreselectedDate(Calendar.getInstance().apply {
                set(Calendar.DATE, this.get(Calendar.DATE) + startDate)
            })
            .build()
            .show(supportFragmentManager, "TAG")
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

    private fun navigateTo(){
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${shopLat},${shopLong}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        Log.e(TAG,"uriuri :  $gmmIntentUri")
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

/*    private fun downloadToShareA1(){
        Log.d(TAG,"call share PDF")
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *permissions)){
            showProgressDialog(true)
            //presenter.downloadFormA1("", signatureId)
        }else{
            EasyPermissions.requestPermissions(this,
                getString(R.string.storage_permission_required),
                STORAGE_PERMISSION_REQUEST_CODE, *permissions)
        }
    }*/

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

    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        val date = SimpleDateFormat("dd / MMMM / yyyy",Locale("in")).format(calendar.time)
        val timestamp = calendar.timeInMillis

        /*if (callDate){
            datePickerCall = timestamp
            tvWithdrawDate.text = date
        }else{
            datePickerSent = timestamp
            tvSentDate.text = date
        }*/

        if (callDate){
            if (dateSentPicked){
                Log.e(TAG, "Pilih tanggal tarik : $dateSentPicked")
                if (timestamp < datePickerSent){
                    Log.e(TAG, "Pilih tanggal tarik : $datePickerSent")
                    Log.e(TAG, "Pilih tanggal tarik : $timestamp")
                    datePickerCall = timestamp
                    tvWithdrawDate.text = date
                    dateCallPicked = true
                }else{
                    Toast.makeText(this,"Tanggal tarik harus lebih kecil dari tanggal kirim", Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.e(TAG, "Pilih tanggal tarik : $dateSentPicked")
                datePickerCall = timestamp
                tvWithdrawDate.text = date
                dateCallPicked = true
            }

        }else{
            if (dateCallPicked){
                Log.e(TAG, "Pilih tanggal kirim : $dateCallPicked")
                if (datePickerCall < timestamp){
                    Log.e(TAG, "Pilih tanggal kirim : $datePickerCall")
                    Log.e(TAG, "Pilih tanggal kirim : $timestamp")
                    datePickerSent = timestamp
                    tvSentDate.text = date
                    dateSentPicked = true
                }else{
                    Toast.makeText(this,"Tanggal tarik harus lebih kecil dari tanggal kirim", Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.e(TAG, "Pilih tanggal kirim : $dateCallPicked")
                datePickerSent = timestamp
                tvSentDate.text = date
                dateSentPicked = true
            }

        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun loadDetailTokoSuccess(detailTokoRes: DetailTokoAsalRes) {
        Log.i(TAG, "load data value : ${detailTokoRes.outlet_id}")
        showViewData(detailTokoRes)
    }

    @SuppressLint("SetTextI18n")
    override fun submitRequestSuccess(cabinetMandiriRes: CabinetMandiriRes) {
        svDetailToko.visibility = GONE
        svSuccesSubmit.visibility = VISIBLE
        submitSuccess = true

        tvRequestId.text = "Permintaan ${cabinetMandiriRes.requestId}"
        idRequest = cabinetMandiriRes.requestId
        tvOutletAsal.text = tokoAsalName
        tvCabinetCode2.text = cabinetMandiriRes.cabinet.cabinetCode
        tvCabinetType2.text = cabinetMandiriRes.cabinet.cabinetType
        tvQrCode2.text = cabinetMandiriRes.cabinet.cabinetQrCode
        val qrCode = cabinetMandiriRes.cabinet.cabinetQrCode
        generateQr(qrCode,2)
    }

    override fun submitRequestError(error: String, errorCode: Int) {
        Log.e(TAG," error cause : $error")
        if (errorCode == 0){
            Toast.makeText(this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "Terjadi kesalahan server, mohon hubungi Customer Service", Toast.LENGTH_LONG).show()
        }
    }

/*    override fun downloadFormA1Success(fileName:String) {
        if (fileName.isNotEmpty()){
            try {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS),fileName)
                file.setReadable(true, false)
                val uri = FileProvider.getUriForFile(this,
                    "com.seru.serujuragan.provider",file)
                val intent = Intent(Intent.ACTION_SEND)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "application/pdf"
                startActivity(Intent.createChooser(intent,"Bagikan PDF via"))
                showProgressDialog(false)
            }catch (e: Exception){
                Log.e(TAG,"catch : $e")
            }
        }
    }*/

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
                Toast.makeText(this, "Bukti penarikan berhasil di Unduh", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Log.e(TAG,"catch : $e")
            }
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

    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e("Error load cause",error)
        if (errorCode == 2){
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }else{
            svDetailToko.visibility = View.GONE
            emptyPage.visibility = View.VISIBLE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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
