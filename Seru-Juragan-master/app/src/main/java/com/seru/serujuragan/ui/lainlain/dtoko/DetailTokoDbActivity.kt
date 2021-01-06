package com.seru.serujuragan.ui.lainlain.dtoko

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.DetailTokoRes
import com.seru.serujuragan.data.response.History
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.lainlain.DetailTokoDbContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.ImagePreviewActivity
import com.seru.serujuragan.view.setServerImage
import com.seru.serujuragan.view.timelineview.TimelineAdapter
import kotlinx.android.synthetic.main.activity_detail_toko_db.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class DetailTokoDbActivity : AppCompatActivity(),
    DetailTokoDbContract.View,
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks{

    companion object {
        val TAG = DetailTokoDbActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @Inject
    lateinit var presenter: DetailTokoDbContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private lateinit var mAdapterTimeline: TimelineAdapter
    private val mDataList = ArrayList<History>()
    private lateinit var mLayoutManager: LinearLayoutManager
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private lateinit var urlLocation: String
    private val kost = LatLng(-6.2563281, 106.8360477)
    private var shopLat: Double = 0.0
    private var shopLong: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_toko_db)
        toolbarTitle.text = "DETAIL TOKO"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }

        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        //setDataListItems()
        //initRecyclerView()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnNavigasi.isEnabled = true
        btnNavigasi.setOnClickListener { navigateTo() }

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
        tvdateToday.text = AppPreference.dayNow
        val idToko = intent.getStringExtra(Constants.ID_TOKO)!!
        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadDetailToko(idToko)
        }else{
            offlinePage.visibility = VISIBLE
            scrollView.visibility = GONE
            Log.i("is connect intenter ? :", isConnected.toString())
        }
    }

    private fun showViewData(res: DetailTokoRes){

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
        }
        if (res.outlet_info != null) {

            if (res.outlet_status != null) {
                val listTimeline = res.outlet_status.timelineHistory
                mDataList.addAll(listTimeline)
                initTimelineView()
            }

            tvJenisToko.text = res.outlet_info.id_outlet_type
            tvStatusKepemilikan.text = res.outlet_info.id_ownership_status
            tvTipeJalan.text = res.outlet_info.id_street_type
            tvRadiusTempat.text = res.outlet_info.area_radius
            if (res.outlet_info.selling.selling == "1"){
                var aice = ""
                var walls = ""
                var campina = ""
                var glico = ""
                var lainnya = ""
                val brand = res.outlet_info.selling.name
                for (a in brand.indices){
                    val bname = brand[a].brandName
                    Log.e(TAG, "list brand $bname")
                    when(bname){
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
            }else{
                tvJualEs.text = "Tidak"
            }
            if (res.outlet_info.kulkas.exist == "1"){
                var kulkasType = ""
                when(res.outlet_info.kulkas.type){
                    "1" -> {
                        kulkasType = "1 Pintu"
                    }
                    "2" -> {
                        kulkasType = "2 Pintu"
                    }
                }
                tvKulkas.text = "Ya, $kulkasType"
            }else{
                tvKulkas.text = "Tidak"
            }
            tvPaketPerdana.text = if (res.outlet_info.perdana == "1") "Ya" else "Tidak"
            tvFreezer.text = if (res.outlet_info.freezer == "1") "Ya" else "Tidak"
            tvKapasitasListrik.text = res.outlet_info.listrikCapacity
            tvFrekMatiListrik.text = res.outlet_info.listrikPadam

            val imageUrl = res.outlet_info.picture
            for (i in imageUrl.indices){
                when(val imageName = imageUrl[i].picture_name){
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

        }


        //val jadwalKirim = res.outlet_status.dateRecomendation
        //val localDate = Date(jadwalKirim*1000)
        //val date = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(jadwalKirim)
        //tvJadwalKirim.text = date
    }

    private fun initTimelineView(){
        mLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTimeline.layoutManager = mLayoutManager
        mAdapterTimeline = TimelineAdapter(mDataList)
        rvTimeline.adapter = mAdapterTimeline
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
            moveCamera(CameraUpdateFactory.newLatLngZoom(kost, 13.0f))
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        scrollView.visibility = GONE
        emptyPage.visibility = VISIBLE
    }

    override fun loadDetailtokoSuccess(detailTokoRes: DetailTokoRes) {
        Log.i(TAG, "load data value : ${detailTokoRes.outlet_id}")
        showViewData(detailTokoRes)
    }

    override fun doLogout() {
        this.getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        Log.i(TAG, "app pref :  ${AppPreference.isLoggedIn}")
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
