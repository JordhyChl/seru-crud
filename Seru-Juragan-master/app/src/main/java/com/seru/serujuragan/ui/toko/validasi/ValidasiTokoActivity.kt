package com.seru.serujuragan.ui.toko.validasi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.seru.serujuragan.view.customview.StaticBottomSheet
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.*
import com.seru.serujuragan.data.request.EsBrand
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.data.response.Kulkas
import com.seru.serujuragan.data.response.Picture
import com.seru.serujuragan.data.response.Selling
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.ValidasiTokoContract
import com.seru.serujuragan.permission.PermissionsConfig
import com.seru.serujuragan.ui.base.BaseActivity
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.ImagePreviewActivity
import com.seru.serujuragan.view.customadapter.CustomKecamatanAdapter
import com.seru.serujuragan.view.customadapter.CustomKelurahanAdapter
import com.seru.serujuragan.view.setLocalImage
import com.seru.serujuragan.view.setServerImage
import com.seru.serujuragan.view.timelineview.TimelineAdapter
import kotlinx.android.synthetic.main.activity_validasi_toko.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ValidasiTokoActivity : BaseActivity(),
    ValidasiTokoContract.View,
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks,
    SlideDatePickerDialogCallback{

    companion object {
        val TAG = ValidasiTokoActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQ_OUTSIDE = 301
        private const val REQ_INSIDE = 302
        private const val REQ_KTP = 303
        private const val REQ_SIGNATURE = 304
    }

    @Inject
    lateinit var presenter: ValidasiTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private lateinit var mAdapterTimeline: TimelineAdapter
    private val mDataList = ArrayList<History>()
    private lateinit var mLayoutManager: LinearLayoutManager
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val defaultLoc = LatLng(-6.1788367,106.8246641)
    private var shopLatitude: Double = 0.0
    private var shopLongitude: Double = 0.0

    private lateinit var pictureSourceMenu: StaticBottomSheet
    private lateinit var requestPicture: String

    lateinit var idToko : String
    private var kecamatanValue : String = "1"
    private var kelurahanValue : String = "1"
    private var statusValidasi : String = "Tunda"
    private var validateState : String = "1"
    lateinit var mDataSelling: Selling
    lateinit var mDataKulkas: Kulkas
    //Keterangan toko
    private lateinit var jenisToko : String
    private lateinit var statusToko : String
    private lateinit var tipeJalan : String
    private lateinit var radiusSekitarVal : String
    private lateinit var beliPaketBol : String
    private lateinit var tempatBol : String
    private lateinit var kapasitasListrik : String
    private lateinit var freqPemadaman : String
    private lateinit var mDataPicture: List<Picture>
    private val mDataDistrict = ArrayList<DataDistrict>()
    private val mDataVillages = ArrayList<DataVillages>()
    var datePickerValidate : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validasi_toko)
        toolbarTitle.text = "INPUT VALIDASI TOKO"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        //initRecyclerView()
        initMenuPicture()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnRenewLocation.isEnabled = true
        btnRenewLocation.setOnClickListener { updateLocation() }
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

        initView()
        val idToko = intent.getStringExtra(Constants.ID_TOKO)!!
        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadDataValidasiToko(idToko)
            presenter.loadListDistrict()
            //presenter.loadListVillage(contentType, token)
        }else{
            scrollView.visibility = GONE
            offlinePage.visibility = VISIBLE
            Log.i("is connect intenter ? :", isConnected.toString())
        }
    }

    private fun initSpinnerKec(listKec: MutableList<DataDistrict>){
        val initialKec = DataDistrict("","","","Pilih Kecamatan")
        mDataDistrict.clear()
        mDataDistrict.add(initialKec)
        mDataDistrict.addAll(listKec)
        val aa = CustomKecamatanAdapter(
            this,
            mDataDistrict
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spKecamatan.adapter = aa

        spKecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                val kec = parent?.getItemAtPosition(position) as DataDistrict
//                kecamatanValue = kec.district_id
//                presenter.loadListVillage(kecamatanValue)
//                Log.i(TAG, "Kecamatan ID :  $kecamatanValue")

                if (position == 0){
                    Log.e(TAG, "do nothing in index 0")
                }else {
                    val kec = parent?.getItemAtPosition(position) as DataDistrict
                    kecamatanValue = kec.district_id
                    showProgressDialog(true)
                    presenter.loadListVillage(kecamatanValue)
                    Log.i(TAG, "Kecamatan ID :  $kecamatanValue")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

    }

    private fun initSpinnerKel(listKel: MutableList<DataVillages>){
        val initialKel = DataVillages("","","","Pilih Kelurahan")
        mDataVillages.clear()
        mDataVillages.add(initialKel)
        mDataVillages.addAll(listKel)
        val aa = CustomKelurahanAdapter(
            this,
            mDataVillages
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spKelurahan.adapter = aa

        spKelurahan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                val kel = parent?.getItemAtPosition(position) as DataVillages
//                kelurahanValue = kel.village_id
//                Log.i(TAG, "Kelurahan value : $kelurahanValue")

                if (position == 0) {
                    Log.e(TAG, "do nothing in index 0")
                }else{
                    val kel = parent?.getItemAtPosition(position) as DataVillages
                    kelurahanValue = kel.village_id
                    Log.i(TAG, "Kelurahan value : $kelurahanValue")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    private fun initView(){

        tvdateToday.text = AppPreference.dayNow
        tvKecamatan.setOnClickListener {
            tvKecamatan.visibility = GONE
            //tvKelurahan.visibility = GONE
            spKecamatan.visibility = VISIBLE
            //spKelurahan.visibility = VISIBLE
        }
        btnNavigasi.setOnClickListener { navigateTo() }
        /*Foto*/
        btnEditOutShop.setOnClickListener {
            requestPicture = REQ_OUTSIDE.toString()
            pictureSourceMenu.show()
        }
        btnEditInShop.setOnClickListener {
            requestPicture = REQ_INSIDE.toString()
            pictureSourceMenu.show() }
        btnEditKtp.setOnClickListener {
            requestPicture = REQ_KTP.toString()
            pictureSourceMenu.show() }

        spStatusValidasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i(TAG,"do nothing")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val nilaiValidasi = parent?.getItemAtPosition(position)
                statusValidasi = nilaiValidasi.toString()
                when (statusValidasi) {
                    "Tunda" -> {
                        descDay.text = "Jadwal Usul Validasi"
                        lyJadwalPengiriman.visibility = VISIBLE
                        initDefaultDate(1)
                    }
                    "Approve" -> {
                        descDay.text = "Jadwal Usul Pengiriman Kabinet"
                        lyJadwalPengiriman.visibility = VISIBLE
                        initDefaultDate(5)
                    }
                    else -> {
                        lyJadwalPengiriman.visibility = GONE
                    }
                }
                Log.i(TAG, "nilai :  $statusValidasi")
            }

        }

        btnSubmitSurvey.setOnClickListener {
            getFormValue()
        }
    }

    private fun showViewData(res: DetailTokoRes){

        tvKodeToko.text = res.outlet_id
        idToko = res.outlet_id
        edtNamaToko.setText(res.shop_name)
        edtNamaPemilik.setText(res.owner_name)
        edtAlamat.setText(res.address)
        edtPhone1.setText(res.shop_telp1)
        edtPhone2.setText(res.shop_telp2)
        //kecamatan
        tvKecamatan.text = res.district.districtName
        kecamatanValue = res.district.districtID
        //kelurahan
        tvKelurahan.text = res.village.villagesName
        kelurahanValue = res.village.villagesID

        tvCurLat.text = res.location.shop_latitude.toString()
        tvCurLong.text = res.location.shop_longitude.toString()
        shopLatitude = res.location.shop_latitude
        shopLongitude = res.location.shop_longitude
        if (!shopLatitude.isNaN() && !shopLongitude.isNaN()){
            updateMarker(shopLatitude, shopLongitude)
            btnNavigasi.isEnabled = true
        }
        //imgOutshop
        if (res.outlet_info != null) {
            val imageUrl = res.outlet_info.picture
            mDataPicture = imageUrl
            for (i in imageUrl.indices) {
                when (val imageName = imageUrl[i].picture_name) {
                    "luartoko" -> {
                        Log.e(TAG, "luar : $imageName")
                        Log.i(TAG, "url luar: ${imageUrl[i].picture_id}")
                        val imgUrlOut = imageUrl[i].picture_id
                        imgOutshop.setServerImage(imgUrlOut, this)
                        imgOutshop.setOnClickListener {
                            val intent = Intent(this, ImagePreviewActivity::class.java)
                            intent.putExtra("imgUrl", imgUrlOut)
                            intent.putExtra("imgTitle", "Foto Luar Toko")
                            startActivity(intent)
                        }
                    }
                    "dalamtoko" -> {
                        val imgUrlIn = imageUrl[i].picture_id
                        imgInshop.setServerImage(imgUrlIn, this)
                        imgInshop.setOnClickListener {
                            val intent = Intent(this, ImagePreviewActivity::class.java)
                            intent.putExtra("imgUrl", imgUrlIn)
                            intent.putExtra("imgTitle", "Foto Dalam Toko")
                            startActivity(intent)
                        }
                    }
                    "ktp" -> {
                        val imgUrlKtp = imageUrl[i].picture_id
                        imgKtp.setServerImage(imgUrlKtp, this)
                        imgKtp.setOnClickListener {
                            val intent = Intent(this, ImagePreviewActivity::class.java)
                            intent.putExtra("imgUrl", imgUrlKtp)
                            intent.putExtra("imgTitle", "Foto KTP")
                            startActivity(intent)
                        }
                    }
                    "signature" -> {
                        val imgUrlSign = imageUrl[i].picture_id
                        imgSignature.setServerImage(imgUrlSign, this)
                        imgSignature.setOnClickListener {
                            val intent = Intent(this, ImagePreviewActivity::class.java)
                            intent.putExtra("imgUrl", imgUrlSign)
                            intent.putExtra("imgTitle", "Foto Tanda Tangan")
                            startActivity(intent)
                        }
                    }
                }
            }
            //jenis toko
            tvJenisToko.text = res.outlet_info.id_outlet_type
            jenisToko = res.outlet_info.id_outlet_type
            //status toko
            tvStatusKepemilikan.text = res.outlet_info.id_ownership_status
            statusToko = res.outlet_info.id_ownership_status
            //tipe jalan
            tvTipeJalan.text = res.outlet_info.id_street_type
            tipeJalan = res.outlet_info.id_street_type
            //radius
            tvRadiusTempat.text = res.outlet_info.area_radius
            radiusSekitarVal = res.outlet_info.area_radius
            //sudah jual
            if (res.outlet_info.selling.selling == "1") {
                var aice = ""
                var walls = ""
                var campina = ""
                var glico = ""
                var lainnya = ""
                val brand = res.outlet_info.selling.name
                for (a in brand.indices) {
                    val bname = brand[a].brandName
                    Log.e(TAG, "list brand $bname")
                    when (bname) {
                        "Aice" -> {
                            Log.i(TAG, "aice true")
                            aice = "Aice"
                        }
                        "Walls" -> {
                            Log.i(TAG, "walls true")
                            walls = "Walls"
                        }
                        "Campina" -> {
                            Log.i(TAG, "campina true")
                            campina = "Campina"
                        }
                        "Glico" -> {
                            Log.i(TAG, "glico true")
                            glico = "Glico"
                        }
                        else -> {
                            Log.i(TAG, "lainnya true")
                            lainnya = brand.last().brandName
                        }
                    }
                }
                tvJualEs.text = "Ya, $aice,$walls,$campina,$glico,$lainnya"
            } else {
                tvJualEs.text = "Tidak"
            }
            mDataSelling = res.outlet_info.selling
            if (res.outlet_info.kulkas.exist == "1") {
                var kulkasType = ""
                when (res.outlet_info.kulkas.type) {
                    "1" -> {
                        kulkasType = "1 Pintu"
                    }
                    "2" -> {
                        kulkasType = "2 Pintu"
                    }
                }
                tvKulkas.text = "Ya, $kulkasType"
            } else {
                tvKulkas.text = "Tidak"
            }
            mDataKulkas = res.outlet_info.kulkas
            tvPaketPerdana.text = if (res.outlet_info.perdana == "1") "Ya" else "Tidak"
            beliPaketBol = res.outlet_info.perdana
            tvFreezer.text = if (res.outlet_info.freezer == "1") "Ya" else "Tidak"
            tempatBol = res.outlet_info.freezer
            tvKapasitasListrik.text = res.outlet_info.listrikCapacity
            kapasitasListrik = res.outlet_info.listrikCapacity
            tvFreqBlackout.text = res.outlet_info.listrikPadam
            freqPemadaman = res.outlet_info.listrikPadam
        }else{
            Log.d(TAG, "outlet info null")
        }

        if (res.outlet_status != null) {
            val listTimeline = res.outlet_status.timelineHistory
            mDataList.addAll(listTimeline)
            initTimelineView()

            if (res.outlet_status.status.id_status == "1") {
                lyValidateStatus.visibility = VISIBLE
            }
            val statusState = res.outlet_status.status.id_status
            Log.e(TAG, "status_id:  $statusState")
            spStatusValidasi.setSelection(statusState.toInt() - 2)

            val surveyDate = res.outlet_status.dateRecommendation
            val tanggal = surveyDate.toString()
            if (tanggal == "0") {
                defaultDate()
            } else {
                datePickerValidate = res.outlet_status.dateRecommendation
                val localDate = Date(surveyDate * 1000)
                Log.d(TAG, "localdate $localDate")
                Log.d(TAG, "date $surveyDate")
                val date = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(localDate)
                tvDay.text = date
            }

            tvDay.setOnClickListener {
                //initCalendar()
                when(statusValidasi){
                    "Tunda" ->{
                        initCalendar(1)
                    }
                    else ->{
                        initCalendar(5)
                    }
                }
            }
            edtNoteValidasi.setText(res.outlet_status.status.note_outlet)
        }else{
            Log.d(TAG, "outlet status null")
        }
    }

    private fun initTimelineView(){
        mLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTimeline.layoutManager = mLayoutManager
        mAdapterTimeline = TimelineAdapter(mDataList)
        rvTimeline.adapter = mAdapterTimeline
    }

    private fun initDefaultDate(startDate: Int){
        val timestamp = Calendar.getInstance(Locale("in"))
        timestamp.add(Calendar.DAY_OF_YEAR,+startDate)
        val date = SimpleDateFormat("dd / MMMM / yyyy",Locale("in")).format(timestamp.time)
        datePickerValidate = timestamp.timeInMillis
        tvDay.text = date
        Log.e(TAG, "date timestamp : $datePickerValidate")
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
        Log.i(TAG, "open camera kudune")
        ImagePicker.with(this)
            .crop(16f, 9f)
            .compress(300)
            .cameraOnly()
            .start(start.toInt())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQ_OUTSIDE -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("luartoko",localFile)
                    showProgressDialog(true)
                }
                REQ_INSIDE -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("dalamtoko",localFile)
                    showProgressDialog(true)
                }
                REQ_KTP -> {
                    val localFile = ImagePicker.getFile(data)
                    Log.e(TAG, "img Path : $localFile")
                    presenter.uploadFoto("ktp",localFile)
                    showProgressDialog(true)
                }
            }

        }


    }

    private fun getFormValue(){

        //Data toko
        var namaToko = edtNamaToko.text.toString()
        var nama_pemilik = edtNamaPemilik.text.toString()
        var alamat = edtAlamat.text.toString()
        var phone1 = edtPhone1.text.toString()
        var phone2 = edtPhone2.text.toString()
        var note = edtNoteValidasi.text.toString()
        when (statusValidasi) {
            "Tunda" -> {
                validateState = "2"
            }
            "Approve" -> {
                validateState = "3"
            }
            "Batal" -> {
                validateState = "4"
            }
        }

        val locationSurvey = ShopLocationUpdate(shopLatitude,shopLongitude)
        val outlet = OutletUpdate(idToko,namaToko, nama_pemilik, alamat, phone1,
            phone2, kecamatanValue,kelurahanValue)
        @Suppress("UNCHECKED_CAST")
        val listPicture: List<PictureUpdate> = mDataPicture as List<PictureUpdate>
        //iceSelling
        @Suppress("UNCHECKED_CAST")
        val listSelName = mDataSelling.name as List<EsBrand>
        val sel = mDataSelling.selling
        val sellingUpdate = SellingUpdate(sel,listSelName)
        //icebox
        val haveIcebox = mDataKulkas.exist
        val typeIcebox = mDataKulkas.type
        val kulkasUpdate = KulkasUpdate(haveIcebox,typeIcebox)
        val survey = SurveyUpdate(jenisToko,statusToko,tipeJalan,radiusSekitarVal,
            sellingUpdate, kulkasUpdate, beliPaketBol,tempatBol, kapasitasListrik,freqPemadaman,listPicture)
        val statusValidasi = StateUpdate(validateState,datePickerValidate,note)
        val reqUpdateToko = UpdateTokoReq(shop_location = locationSurvey, outlet = outlet, survey = survey, state = statusValidasi)

        val jsonUpdateData = Gson().toJson(reqUpdateToko)

        Log.e(TAG,"value form survey: $jsonUpdateData")
        if (!shopLatitude.isNaN() && !shopLongitude.isNaN() && namaToko.isNotEmpty()
            && nama_pemilik.isNotEmpty() && alamat.isNotEmpty() && mDataPicture.isNotEmpty()
            && listPicture.size >=4){
            Log.i(TAG,"submit form : $reqUpdateToko")
            showProgressDialog(true)
            presenter.submitValidasiToko(reqUpdateToko)
        } else {
            Toast.makeText(this,"Mohon isi semua form yang tersedia dan upload semua Foto.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        enableMyLocation()
        map.uiSettings.isMyLocationButtonEnabled = false

        with(map){
            moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 14.0f))
        }
    }

    private fun updateMarker(newLat: Double, newLong:Double){
        val location = LatLng(newLat, newLong)

        Log.e(TAG,"loc: $location ")

        with(map){
            map.clear()
            moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
            addMarker(MarkerOptions().apply {
                position(location)
            })
        }
    }

    private fun updateLocation(){
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val lat = it.latitude
            val long = it.longitude
            Log.i(TAG,"lati : $lat , long : $long")
            shopLatitude = lat
            shopLongitude = long
            tvCurLat.text = shopLatitude.toString()
            tvCurLong.text = shopLongitude.toString()

            val myLocation = LatLng(shopLatitude, shopLongitude)
            Log.i(TAG,"myLoc : $myLocation")
            with(map){
                map.clear()
                moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f))
                addMarker(MarkerOptions().apply {
                    position(myLocation)
                })
            }
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
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${shopLatitude},${shopLongitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        Log.e(TAG,"uriuri :  $gmmIntentUri")
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

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
        }else {
            scrollView.visibility = GONE
            emptyPage.visibility = VISIBLE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadDataVildasitokoSuccess(detailTokoRes: DetailTokoRes) {
        Log.i(TAG, "load data value : ${detailTokoRes.outlet_id}")
        showViewData(detailTokoRes)
    }

    override fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>) {
        initSpinnerKec(listDistrict)
        mDataDistrict.addAll(listDistrict)
    }

    override fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>) {
        initSpinnerKel(listVillage)
        mDataVillages.addAll(listVillage)
        tvKelurahan.visibility = GONE
        spKelurahan.visibility = VISIBLE
    }

    override fun uploadImgSuccess(name:String, imgFile: File?, res: UploadPicRes) {
        Log.e(TAG,"data : ${res.imgId}")
        Log.e(TAG,"data typeFile : $name")
        Log.e(TAG,"mDataPicture before after update value : $mDataPicture")

        for (i in mDataPicture.indices){
            if (mDataPicture[i].picture_name == name) {
                Log.e(TAG,"picture name and typeFile has same name $name")
                Log.e(TAG,"picture id first value ${mDataPicture[i].picture_id}")
                mDataPicture[i].picture_id = res.imgId
                Log.e(TAG,"picture id has been updated become ${mDataPicture[i].picture_id}")
            }
        }
        Log.e(TAG,"mDataPicture value after update value : $mDataPicture")
        mDataPicture = mDataPicture
        when (name) {
            "luartoko" -> {
                imgOutshop.setLocalImage(imgFile!!)
            }
            "dalamtoko" -> {
                imgInshop.setLocalImage(imgFile!!)
            }
            "ktp" -> {
                imgKtp.setLocalImage(imgFile!!)
            }
        }
    }

    override fun submitValidasiTokoSuccess(updateRes: AddTokoRes) {
        Log.i(TAG, updateRes.outlet_id)
        Toast.makeText(this,"Submit Validasi Toko Anda berhasil",Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ListValidasiTokoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun submitValidasiError(error: String, errorCode: Int) {
        Log.e(TAG," error cause : $error")
        if (errorCode == 0){
            Toast.makeText(this, "Terjadi kesalahan, mohon ulangi kembali", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "Terjadi kesalahan server, mohon hubungi Customer Service", Toast.LENGTH_LONG).show()
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasPermissionGranted(permission: Int, hasGranted: Boolean) {
        when (permission) {
            PermissionsConfig.CAMERA -> {
                if (hasGranted)
                    openCamera(requestPicture)
            }
        }
    }

    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        val date = SimpleDateFormat("dd / MMMM / yyyy",Locale("in")).format(calendar.time)
        val timestamp = calendar.timeInMillis

        datePickerValidate = timestamp
        tvDay.text = date
    }

    private fun defaultDate(){
        val localDate = localDateTime()
        val date = localDate.format(DateTimeFormatter.ofPattern("dd - MMM - yyyy", Locale("in")))
        val timestamp = Calendar.getInstance(Locale("in"))
        timestamp.add(Calendar.DAY_OF_YEAR,+5)
        datePickerValidate = timestamp.timeInMillis
        tvDay.text = date
        Log.e(TAG, "date timestamp : $datePickerValidate")
    }

    private fun localDateTime(): LocalDateTime {
        return LocalDateTime.now().plusDays(5)
    }
}
