package com.seru.serujuragan.ui.toko.tambah

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.seru.serujuragan.view.customview.StaticBottomSheet
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.*
import com.seru.serujuragan.data.request.EsBrand
import com.seru.serujuragan.data.request.Kulkas
import com.seru.serujuragan.data.request.Picture
import com.seru.serujuragan.data.request.Selling
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.TambahTokoContract
import com.seru.serujuragan.permission.PermissionsConfig
import com.seru.serujuragan.service.workmanager.AddTokoWorker
import com.seru.serujuragan.ui.base.BaseActivity
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.ui.home.HomeActivity
import com.seru.serujuragan.ui.toko.status.DetailTokoActivity
import com.seru.serujuragan.ui.toko.validasi.ValidasiTokoActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.customadapter.CustomKecamatanAdapter
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.customadapter.CustomKelurahanAdapter
import com.seru.serujuragan.view.setLocalImage
import com.seru.serujuragan.view.signaturepad.SignaturePadActivity
import com.seru.serujuragan.view.timelineview.TimelineAdapter
import kotlinx.android.synthetic.main.activity_tambah_toko.*
import kotlinx.android.synthetic.main.dialog_alert_device_offline.*
import kotlinx.android.synthetic.main.dialog_alert_rooted.*
import kotlinx.android.synthetic.main.dialog_alert_validasi.*
import kotlinx.android.synthetic.main.dialog_alert_validasi.tvAlertID
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class TambahTokoActivity :
    BaseActivity(),
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks,
    SlideDatePickerDialogCallback,
    TambahTokoContract.View{

    companion object {
        private val TAG = TambahTokoActivity::class.java.simpleName
        private const val REQ_OUTSIDE = 301
        private const val REQ_INSIDE = 302
        private const val REQ_KTP = 303
        private const val REQ_SIGNATURE = 304
    }

    @Inject lateinit var presenter: TambahTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private lateinit var mAdapterTimeline: TimelineAdapter
    private val mDataList = ArrayList<History>()
    private lateinit var mLayoutManager: LinearLayoutManager
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private val locJuaragan = LatLng(
        AppPreference.latitudeJuragan.toDouble(),
        AppPreference.longitudeJuragan.toDouble()
    )
    private val radiusJuragan = AppPreference.radiusJuragan.toDouble()
    private val thresholdRadius = AppPreference.thresholdJuragan.toDouble()
    private val radiusJuraganFinal = radiusJuragan + thresholdRadius
    private var fillColorArgb : Int = 0
    private var strokeColorArgb: Int = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private lateinit var pictureSourceMenu: StaticBottomSheet
    private lateinit var requestPicture: String

    private var kecamatanValue : String = "1"
    private var kelurahanValue : String = "1"
    //Keterangan toko
    private var jenisTokoBol : String = "1"
    private var jenisToko : String = ""
    private var statusToko : String = ""
    private var tipeJalan : String = ""
    private var radiusSekitarBol : String = "1"
    private var radiusSekitarVal : String = "lainnya"
    private var jualEsBol : String = "2"
    private var jualEsOther : String = "2"
    private var kulkasBol : String = "2"
    private var kulkasVal : String = ""
    private var beliPaketBol : String = ""
    private var tempatBol : String = ""
    private var kapasitasListrik : String = ""
    private var freqPemadaman : String = ""
    private var kirimKabinetMandiri : Int = 2
    private var surveyTim: Boolean = false
    private var statusValidasi : String = "Tunda"
    private var validateState : String = "1"
    private var pnCheck: Boolean = false
    private var pic1 : String = "2"
    private var pic2 : String = "2"
    private var pic3 : String = "2"
    private var pic4 : String = "2"
    private var mDataPicture = ArrayList<Picture>()
    private val mDataEsBrand = ArrayList<EsBrand>()
    var datePickerValidate : Long = 0
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_toko)

        toolbarTitle.text = "Tambah Toko"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        setDataListItems()
        initRecyclerView()
        initMenuPicture()
        initDataPic()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            presenter.loadListDistrict()
            //presenter.loadListVillage(contentType, token)
            showProgressDialog(true)
            //presenter.loadTimeline()
            Log.e(TAG,"zone time : ${AppPreference.dateTimestamp}")

        }else{
            scrollView.visibility = GONE
            offlinePage.visibility = VISIBLE
            Log.i("is connect intenter ? :", isConnected.toString())
        }
    }

    private fun initSpinnerKec(listKec: MutableList<DataDistrict>){
        val aa = CustomKecamatanAdapter(
            this,
            listKec
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
                val kec = parent?.getItemAtPosition(position) as DataDistrict
                kecamatanValue = kec.district_id
                presenter.loadListVillage(kecamatanValue)
                Log.i(TAG, "Kecamatan ID :  $kecamatanValue")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    private fun initSpinnerKel(listKel: MutableList<DataVillages>){
        val aa = CustomKelurahanAdapter(
            this,
            listKel
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
                val kel = parent?.getItemAtPosition(position) as DataVillages
                kelurahanValue = kel.village_id
                //kelurahanValue = parent?.selectedItem.toString()
                Log.i(TAG, "Kelurahan value : $kelurahanValue")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }


    private fun setDataListItems() {
        mDataList.add(History(AppPreference.dateTimestamp/1000,1,"juragan","Juragan",""))
    }

    private fun initDataPic(){
        mDataPicture.add(Picture("","luartoko"))
        mDataPicture.add(Picture("","dalamtoko"))
        mDataPicture.add(Picture("","ktp"))
        mDataPicture.add(Picture("","signature"))
    }

    private fun initView(){

        tvdateToday.text = AppPreference.dayNow
        btnCheckin.setOnClickListener {
            sec_data_toko_form.visibility = VISIBLE
            sec_ket_toko_form.visibility = VISIBLE
            sec_foto_form.visibility = VISIBLE
            sec_validasi_syarat.visibility = VISIBLE
            sec_footer.visibility = VISIBLE
        }

        edtPhone1.isLongClickable = false
        edtPhone2.isLongClickable = false

        edtPhone1.doAfterTextChanged {
            pnCheck = false
            phone1Check.visibility = GONE
        }

        btnValidatePN.setOnClickListener {
            val num = handlePhoneNumber(edtPhone1.text.toString())
            if (num.isNotEmpty()){
                Log.i(TAG, "phone number check : $num")
                if (num.length <9){
                    Toast.makeText(this, "Nomor telepon tidak boleh kurang dari 9 digit", Toast.LENGTH_LONG)
                        .show()
                }else {
                    progressDialog.show(this@TambahTokoActivity)
                    val filter = FilterTokoReq("", "", "", "", num)
                    presenter.filterToko(filter)
                }
            }
            else{
                Log.i(TAG, "phone cannot be null ; $num")
                Toast.makeText(this,"Nomor tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        /*Foto*/
        imgOutshop.setOnClickListener {
            requestPicture = REQ_OUTSIDE.toString()
            pictureSourceMenu.show()
        }
        imgInshop.setOnClickListener {
            requestPicture = REQ_INSIDE.toString()
            pictureSourceMenu.show() }
        imgKtp.setOnClickListener {
            requestPicture = REQ_KTP.toString()
            pictureSourceMenu.show() }
        imgSignature.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionsConfig.STORAGE)
            else openSignaturePad()
        }

        tvDay.setOnClickListener {
            when(statusValidasi){
                "Tunda" ->{
                    initCalendar(1)
                }
                else ->{
                    initCalendar(5)
                }
            }
        }

        spStatusValidasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG,"do nothing")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val nilaiValidasi = parent?.getItemAtPosition(position)
                statusValidasi = nilaiValidasi.toString()
                if (statusValidasi == "Tunda") {
                    desDay.text = "Jadwal Usul Validasi"
                    initDefaultDate(1)
                }
                else {
                    desDay.text = "Jadwal Usul Pengiriman Kabinet"
                    initDefaultDate(5)
                }
                Log.i(TAG, "nilai :  $statusValidasi")
            }

        }

        btnSubmitSurvey.setOnClickListener {
            getFormValue()
        }

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                //ket toko
                R.id.rbTokoGt ->
                    if (checked){
                        jenisTokoBol = "1"
                        jenisToko = "Toko GT"
                        edtJenisTokoOther.visibility = GONE
                    }
                R.id.rbWarung ->
                    if (checked){
                        jenisTokoBol = "1"
                        jenisToko = "Warung"
                        edtJenisTokoOther.visibility = GONE
                    }
                R.id.rbKiosPulsa ->
                    if (checked){
                        jenisTokoBol = "1"
                        jenisToko = "Kios Pulsa"
                        edtJenisTokoOther.visibility = GONE
                    }
                R.id.rbJenisSekolah ->
                    if (checked){
                        jenisTokoBol = "1"
                        jenisToko = "Sekolah"
                        edtJenisTokoOther.visibility = GONE
                    }
                R.id.rbJenisLainnya ->
                    if (checked){
                        jenisTokoBol = "2"
                        jenisToko = "Lainnya"
                        edtJenisTokoOther.visibility = VISIBLE
                    }
                R.id.rbHakMilik ->
                    if (checked){
                        statusToko = "Hak Milik"
                    }
                R.id.rbSewa ->
                    if (checked){
                        statusToko = "Sewa"
                    }
                R.id.rb1mobil ->
                    if (checked){
                        tipeJalan = "1 Mobil"
                    }
                R.id.rb2mobil ->
                    if (checked){
                        tipeJalan = "2 Mobil"
                    }
                R.id.rb2motor ->
                    if (checked){
                        tipeJalan = "2 Motor"
                    }
                R.id.rb1motor ->
                    if (checked){
                        tipeJalan = "1 Motor"
                    }
                R.id.rbSekolah ->
                    if (checked){
                        radiusSekitarBol = "1"
                        radiusSekitarVal = "Sekolah"
                        edtRadiusOther.visibility = GONE
                    }
                R.id.rbMasjid ->
                    if (checked){
                        radiusSekitarBol = "1"
                        radiusSekitarVal = "Masjid"
                        edtRadiusOther.visibility = GONE
                    }
                R.id.rbLainnya ->
                    if (checked){
                        edtRadiusOther.visibility = VISIBLE
                        radiusSekitarBol = "2"
                    }
                R.id.rbYaJual ->
                    if (checked){
                        jualEsBol = "1"
                        lyCheckboxEs.visibility = VISIBLE
                    }
                R.id.rbTidakJual ->
                    if (checked){
                        jualEsBol = "2"
                        lyCheckboxEs.visibility = GONE
                    }
                R.id.rbYaAdaKulkas ->
                    if (checked){
                        kulkasBol = "1"
                        lyRbTriggerKulkas.visibility = VISIBLE
                    }
                R.id.rbTidakAdaKulkas ->
                    if (checked){
                        kulkasBol = "2"
                        lyRbTriggerKulkas.visibility = GONE
                    }
                R.id.rb1Pintu ->
                    if (checked){
                        kulkasVal = "1"
                    }
                R.id.rb2Pintu ->
                    if (checked){
                        kulkasVal = "2"
                    }
                R.id.rbYaBersediaPerdana ->
                    if (checked){
                        beliPaketBol = "1"
                    }
                R.id.rbTidakBersediaPerdana ->
                    if (checked){
                        beliPaketBol = "2"
                    }
                R.id.rbYaTersediaTempat ->
                    if (checked){
                        tempatBol = "1"
                    }
                R.id.rbTidakTersediaTempat ->
                    if (checked){
                        tempatBol = "2"
                    }
                R.id.rb450va ->
                    if (checked){
                        kapasitasListrik = "450 VA"
                    }
                R.id.rb900va ->
                    if (checked){
                        kapasitasListrik = "900 VA"
                    }
                R.id.rb1300va ->
                    if (checked){
                        kapasitasListrik = "1300 VA"
                    }
                R.id.rb2200va ->
                    if (checked){
                        kapasitasListrik = "2200 VA"
                    }
                R.id.rbMore2200va ->
                    if (checked){
                        kapasitasListrik = "> 2200 VA"
                    }
                R.id.rbJarangSekali ->
                    if (checked){
                        freqPemadaman = "Jarang Sekali"
                    }
                R.id.rbJarang ->
                    if (checked){
                        freqPemadaman = "Jarang"
                    }
                R.id.rbSering ->
                    if (checked){
                        freqPemadaman = "Sering"
                    }
                R.id.rbSeringSekali ->
                    if (checked){
                        freqPemadaman = "Sering Sekali"
                    }
                R.id.rbYaKirimMandiri ->
                    if (checked){
                        kirimKabinetMandiri = 1
                    }
                R.id.rbTidakKirimMandiri ->
                    if (checked){
                        kirimKabinetMandiri = 2
                    }
            }
        }
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.cbEsWalls -> {
                    if (checked) {
                        mDataEsBrand.add(EsBrand("Walls"))
                    } else {
                        // do nothing
                    }
                }
                R.id.cbEsAice -> {
                    if (checked) {
                        mDataEsBrand.add(EsBrand("Aice"))
                    } else {
                        // do nothing
                    }
                }
                R.id.cbEsCampina -> {
                    if (checked) {
                        mDataEsBrand.add(EsBrand("Campina"))
                    } else {
                        // do nothing
                    }
                }
                R.id.cbEsGlico -> {
                    if (checked) {
                        mDataEsBrand.add(EsBrand("Glico"))
                    } else {
                        // do nothing
                    }
                }
                R.id.cbEsOther -> {
                    if (checked) {
                        edtCbesOther.visibility = VISIBLE
                        jualEsOther = "1"
                    } else {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun handlePhoneNumber(pn:String):String{

        var phoneNumber = ""

        when {
            pn.startsWith("0") -> {
                val phone = pn.substring(1)
                phoneNumber = "62$phone"
            }
            pn.startsWith("8") -> {
                phoneNumber = "62$pn"
            }
            pn.startsWith("62") -> {
                phoneNumber = pn
            }
            else -> {
                Toast.makeText(this, "Format nomor telepon Anda salah.", Toast.LENGTH_LONG).show()
            }
        }
        return phoneNumber
    }

    private fun getFormValue(){
        isConnected = InternetCheck.isConnected(this)
        //Data toko
        var phone2 = ""
        var namaToko = edtNamatoko.text.toString()
        var namaPemilik = edtNamaPemilik.text.toString()
        var alamat = edtAlamat.text.toString()
        var phone1 = handlePhoneNumber(edtPhone1.text.toString())
        if (edtPhone2.text.isNotEmpty()){
            phone2 = handlePhoneNumber(edtPhone2.text.toString())}
        var note = edtNoteTambah.text.toString()
        var listPhoto = false

        if (jenisTokoBol == "2"){
            jenisToko = edtJenisTokoOther.text.toString()
        }

        if (radiusSekitarBol == "2"){
            radiusSekitarVal = edtRadiusOther.text.toString()
        }

        if (jualEsBol == "2"){
            mDataEsBrand.clear()
        }

        if (jualEsOther == "1"){
            val esOther = edtCbesOther.text.toString()
            mDataEsBrand.add(EsBrand(esOther))
        }

        if (pic1 == "1" && pic2 == "1" && pic3 == "1" && pic4 == "1"){
            listPhoto = true
        }

        when (statusValidasi) {
            "Approve" -> {
                validateState = "3"
            }
            "Tunda" -> {
                validateState = "2"
            }
        }

        val locationSurvey = ShopLocation(currentLatitude,currentLongitude)
        val outlet = Outlet(namaToko, namaPemilik, alamat, phone1,
            phone2, kecamatanValue,kelurahanValue)
        val listEsBrand: List<EsBrand> = mDataEsBrand
        val selling = Selling(jualEsBol, listEsBrand)
        val kulkas = Kulkas(kulkasBol,kulkasVal)
        val listPicture: List<Picture> = mDataPicture
        val survey = Survey(jenisToko,statusToko,tipeJalan,radiusSekitarVal,
            selling, kulkas, beliPaketBol,tempatBol, kapasitasListrik,freqPemadaman,
            listPicture)
        val statusValidasi = State(validateState,datePickerValidate,note, kirimKabinetMandiri)
        val reqAddToko = AddTokoReq(shop_location = locationSurvey, outlet = outlet, survey = survey, state = statusValidasi)

        val reqJSON = Gson().toJson(reqAddToko)

        if (pnCheck) {
            if (!currentLatitude.isNaN() && !currentLongitude.isNaN() && namaToko.isNotEmpty() && namaPemilik.isNotEmpty()
                && alamat.isNotEmpty() && jualEsBol.isNotEmpty() && kulkasBol.isNotEmpty() && mDataPicture.isNotEmpty()
                && jenisToko.isNotEmpty() && statusToko.isNotEmpty() && tipeJalan.isNotEmpty() && radiusSekitarVal.isNotEmpty()
                && beliPaketBol.isNotEmpty() && tempatBol.isNotEmpty() && kapasitasListrik.isNotEmpty() && freqPemadaman.isNotEmpty()
                && listPhoto && validateState.isNotEmpty()
            ) {

                if (phone1.length <9) {
                    Toast.makeText(this, "Nomor telepon tidak boleh kurang dari 9 digit", Toast.LENGTH_LONG)
                        .show()
                }else {
                    if (jualEsBol == "1") {
                        if (listEsBrand.isNotEmpty()) {
                            Log.i(TAG, "submit form : $reqJSON")
                            if (isConnected) {
                                showProgressDialog(true)
                                presenter.submitForm(reqAddToko)
                            } else {
                                showOfflineDialogInfo(this, reqAddToko)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.alert_subform_uncomplete),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Log.i(TAG, "submit form : $reqJSON")
                        if (isConnected) {
                            showProgressDialog(true)
                            presenter.submitForm(reqAddToko)
                        } else {
                            showOfflineDialogInfo(this, reqAddToko)
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.alert_form_uncomplete), Toast.LENGTH_SHORT)
                    .show()
            }
        }else{
            Toast.makeText(this, "Anda belum melakukan validasi nomor telepon",Toast.LENGTH_LONG).show()
        }
    }

    private fun handleOffline(req: AddTokoReq){

        val reqJSON = Gson().toJson(req)

        val tokoData = Data.Builder()
            .putString("data", reqJSON)
            .build()

        val constraints =  Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequest.Builder(AddTokoWorker::class.java)
            .setConstraints(constraints)
            .setInputData(tokoData)
            .build()

        WorkManager.getInstance().enqueue(request)
    }

    private fun initRecyclerView(){
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

//        pictureSourceMenu.getView().findViewById<LinearLayout>(R.id.menuGallery).setOnClickListener {
//            pictureSourceMenu.dismiss()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                checkPermission(
//                    this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    PermissionsConfig.STORAGE
//                )
//                return@setOnClickListener
//            }
//            openGallery(requestPicture)
//        }
    }

    private fun openCamera(start: String){
        Log.i(TAG, "open camera")
        ImagePicker.with(this)
            .crop(16f, 9f)
            .compress(120)
            .cameraOnly()
            .start(start.toInt())
    }

    private fun openGallery(start: String){
        Log.i(TAG, "open gallery kudune")
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .galleryOnly()
            .start(start.toInt())
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
        checkRadius()
        map.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun hasLocationPermissions():Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun enableMyLocation() {
        // Enable the location layer. Request the location permission if needed.
        if (hasLocationPermissions()){
            Log.i(TAG,"has permission loc")
            map.isMyLocationEnabled = true
            checkRadius()
        }else{
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                PermissionsConfig.LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
            if (EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.i(TAG,"My Loc : ${mLastLocation.latitude},${mLastLocation.longitude}")
        }
    }

    private fun checkRadius(){
        if (hasLocationPermissions()){
            if (isLocationEnabled()){
                var distance = FloatArray(2)
                fillColorArgb = Color.argb(100, 230, 230, 149)

                Log.i(TAG, "loc juragan : $locJuaragan")
                with(map){
                    moveCamera(CameraUpdateFactory.newLatLngZoom(locJuaragan, 12.0f))
                    addMarker(MarkerOptions().apply {
                        position(locJuaragan)
                    })
                    addCircle(CircleOptions().apply {
                        center(locJuaragan)
                        radius(radiusJuragan)
                        fillColor(fillColorArgb)
                        strokeColor(Color.rgb( 230, 230, 70))
                    })

                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        val loc : Location? = location
                        val mol = checkMockLocation(loc!!, this@TambahTokoActivity)
                        Log.e(TAG,"mock check val : $mol")
                        if (mol){
                            showInfoMockDialog()
                        }
                        if (loc == null){
                            requestNewLocationData()
                        }

                        Location.distanceBetween(
                            locJuaragan.latitude, locJuaragan.longitude,
                            location!!.latitude, location.longitude,
                            distance)

                        try {
                            if (distance[0] > radiusJuraganFinal){
                                Log.i("radius","tidak masukk")
                                tv_checkinInfo.text = "Maaf, lokasi anda tidak berada dalam radius Juragan"
                                btnCheckin.setBackgroundResource(R.color.material_green_200)
                                tv_checkinInfo.setTextColor(Color.RED)
                            }else{
                                Log.i("radius","masukk ${distance[0]}")
                                tvCurLat.text = location.latitude.toString()
                                tvCurLong.text = location.longitude.toString()
                                currentLatitude = location.latitude
                                currentLongitude = location.longitude
                                AppPreference.tempLocation = location.latitude.toString()+location.longitude.toString()
                                btnCheckin.isEnabled = true
                                btnCheckin.setBackgroundResource(R.color.material_green_800)
                                tv_checkinInfo.isVisible = false
                            }
                        }catch (e:Error){
                            Log.e("err","$e")
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            enableMyLocation()
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
            scrollView.visibility = GONE
            emptyPage.visibility = VISIBLE
            Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>) {
        initSpinnerKec(listDistrict)
    }

    override fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>) {
        initSpinnerKel(listVillage)
    }

    override fun uploadImgSuccess(name:String, imgFile: File?, res: UploadPicRes) {
        Log.e(TAG,"data : ${res.imgId}")

        for (i in mDataPicture.indices){
            if (mDataPicture[i].picture_name == name) {
                mDataPicture[i].picture_id = res.imgId
            }
        }
        mDataPicture = mDataPicture

        when (name) {
            "luartoko" -> {
                pic1 = "1"
                imgOutshop.setLocalImage(imgFile!!)
            }
            "dalamtoko" -> {
                pic2 = "1"
                imgInshop.setLocalImage(imgFile!!)
            }
            "ktp" -> {
                pic3 = "1"
                imgKtp.setLocalImage(imgFile!!)
            }
            "signature" -> {
                pic4 = "1"
                imgSignature.setLocalImage(imgFile!!)
            }
        }
    }

    override fun filterTokoSucces(filterRes: List<ListTokoRes>) {
        if (filterRes.isNotEmpty()) {
            val idToko = filterRes[0].outlet_id
            val statusToko = filterRes[0].status_survey.statusId
            val section = filterRes[0].status_survey.section
            Log.i(TAG, "filter toko id : $idToko")
            Log.i(TAG, "filter status id : $statusToko")
            Log.i(TAG, "filter toko section : $section")
            if (section == "hunter"){
                if (statusToko == "1"){
                    showDialogValidasiInfo(this, idToko)
                }else{
                    showDialogStatusInfoHunter(this, idToko)
                }
            }else if (section == "juragan"){
                if (statusToko == "2"){
                    showDialogValidasiInfo(this, idToko)
                }else{
                    showDialogStatusInfo(this, idToko)
                }
            }else{
                showDialogStatusInfo(this, idToko)
            }
        }else{
            pnCheck = true
            phone1Check.visibility = VISIBLE
            progressDialog.dismissProg()
        }
    }

    override fun submitFormSuccess(submitRes: AddTokoRes) {

        Log.i(TAG, submitRes.outlet_id)
        Toast.makeText(this,"Submit Form Anda berhasil",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun submitFormError(error: String, errorCode: Int) {
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


    private fun showDialogValidasiInfo(context: Context, idToko: String) : Dialog {
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
            finish()
        }

        dialog.btnAlertValidasi.setOnClickListener {
            val intent = Intent(context, ValidasiTokoActivity::class.java)
            intent.putExtra(Constants.ID_TOKO,idToko)
            dialog.context.startActivity(intent)
            finish()
        }

        dialog.show()

        return dialog
    }

    private fun showDialogStatusInfo(context: Context, idToko: String) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_validasi, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.tvAlertID.text = " $idToko "
        dialog.btnAlertValidasi.text = "Lihat Detail"
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnAlertExit.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("id",idToko)
            dialog.context.startActivity(intent)
            finish()
        }

        dialog.btnAlertValidasi.setOnClickListener {
            val intent = Intent(context, DetailTokoActivity::class.java)
            intent.putExtra(Constants.ID_TOKO,idToko)
            dialog.context.startActivity(intent)
            finish()
        }

        dialog.show()

        return dialog
    }

    private fun showDialogStatusInfoHunter(context: Context, idToko: String) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_validasi, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.tvAlertID.text = " $idToko "
        dialog.btnAlertValidasi.visibility = GONE
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnAlertExit.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("id",idToko)
            dialog.context.startActivity(intent)
            finish()
        }

        dialog.show()

        return dialog
    }

    private fun showOfflineDialogInfo(context: Context, dataToko: AddTokoReq) : Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewGroup =  inflator.inflate(R.layout.dialog_alert_device_offline, null)

        dialog = Dialog(context)
        dialog.setContentView(viewGroup)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.btnRetry.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnSubmitOffline.setOnClickListener {
            handleOffline(dataToko)
            val intent = Intent(context, HomeActivity::class.java)
            dialog.context.startActivity(intent)
            dialog.dismiss()
            finish()
        }

        dialog.show()

        return dialog
    }


    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        val date = SimpleDateFormat("dd / MMMM / yyyy",Locale("in")).format(calendar.time)
        val timestamp = calendar.timeInMillis

        datePickerValidate = timestamp
        tvDay.text = date
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:$requestCode")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleACC:$requestCode")
        checkRadius()
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
}
