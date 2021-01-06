package com.seru.serujuragan.ui.toko.validasi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.FilterTokoReq
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.ListValidasiTokoContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.customadapter.CustomKecamatanAdapter
import com.seru.serujuragan.view.customadapter.CustomKelurahanAdapter
import kotlinx.android.synthetic.main.activity_list_validasi_toko.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class ListValidasiTokoActivity :
    AppCompatActivity(),
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks,
    ListValidasiTokoContract.View {

    companion object {
        val TAG = ListValidasiTokoActivity::class.java.simpleName
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @Inject
    lateinit var presenter: ListValidasiTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDataList = ArrayList<ListTokoRes>()
    private val mListValidasiTokoAdapter = ListValidasiTokoAdapter(this,mDataList)
    private var mDataDistrict = ArrayList<DataDistrict>()
    private var mDataVillage =  ArrayList<DataVillages>()
    private var kecamatanValue : String? = null
    private var kelurahanValue : String? = null
    //maps
    private lateinit var map: GoogleMap
    private var showPermissionDeniedDialog = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_validasi_toko)

        toolbarTitle.text = "VALIDASI TOKO"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
        initView()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
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
        tvdateToday.text = AppPreference.dayNow
        val mapView = findViewById<ViewGroup>(R.id.maps)
        val idArea = intent.getStringExtra(Constants.ID_AREA_FILTER)
        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadListDistrict()
//            if (!idArea.isNullOrEmpty()){
//                val filterArea = FilterTokoReq("","","",idArea,"")
//                Log.e(TAG,"filter : $filterArea")
//                presenter.filterValidasiList(filterArea)
//            }
//            else
            presenter.loadValidasiList()
        }else{
            lyListValidasi.visibility = GONE
            offlinePage.visibility = VISIBLE
            Log.i("is connect intenter ? :", isConnected.toString())
        }

        btnOpenMapsFilter.setOnClickListener {
            layoutKecamatan.visibility = GONE
            layoutKelurahan.visibility = GONE
            lyMapFilter.visibility = GONE
            btnFilterByArea.visibility = VISIBLE
            lyMapsMarker.visibility = VISIBLE
            filterByLocation()
        }
        btnFilterByArea.setOnClickListener {
            layoutKecamatan.visibility = VISIBLE
            layoutKelurahan.visibility = VISIBLE
            lyMapFilter.visibility = VISIBLE
            btnFilterByArea.visibility = GONE
            lyMapsMarker.visibility = GONE
            mapView.updateLayoutParams {
                width = 0
                height = 400
            }
            showProgressDialog(true)
            presenter.loadValidasiList()
        }
        btnNavigasi.setOnClickListener {
            filterByLocation()
        }
        btnExtMaps.setOnClickListener {
            mapView.updateLayoutParams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    private fun filterByLocation(){

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val lat = it.latitude
            val long = it.longitude
            Log.i(TAG,"lati : $lat , long : $long")
            currentLatitude = lat
            currentLongitude = long

            presenter.filterByLocation(lat, long)
            showProgressDialog(true)

            val myLocation = LatLng(currentLatitude, currentLongitude)
            Log.i(TAG,"myLoc : $myLocation")
            with(map){
                //map.clear()
                moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f))
                addMarker(MarkerOptions().apply {
                    position(myLocation)
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_user_location))
                })
            }
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
                if (position == 0){
                    Log.e(TAG, "do nothing in index 0")
                }else {
                    val kec = parent?.getItemAtPosition(position) as DataDistrict
                    kecamatanValue = kec.district_id
                    val filter = FilterTokoReq("", "", kecamatanValue!!, "", "")
                    showProgressDialog(true)
                    presenter.filterValidasiList(filter)
                    presenter.loadListVillage(kecamatanValue!!)
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
        mDataVillage.clear()
        mDataVillage.add(initialKel)
        mDataVillage.addAll(listKel)

        val aa = CustomKelurahanAdapter(
            this,
            mDataVillage
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
                if (position == 0) {
                    Log.e(TAG, "do nothing in index 0")
                }else{
                    val kel = parent?.getItemAtPosition(position) as DataVillages
                    kecamatanValue = kel.district_id
                    kelurahanValue = kel.village_id
                    val filter = FilterTokoReq("", "", kecamatanValue!!, kelurahanValue!!, "")
                    showProgressDialog(true)
                    presenter.filterValidasiList(filter)
                    Log.i(TAG, "Kelurahan value : $kelurahanValue")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        Log.e("Error load cause","$error")
        mListValidasiTokoAdapter.clearAllData()
        lyListValidasi.visibility = GONE
        rvListValidasiToko.visibility = GONE
        swipeRefreshLayout.visibility = GONE
        emptyPage.visibility = VISIBLE
        Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "Tidak terdapat Toko pada Area yang Anda Pilih",Toast.LENGTH_LONG).show()
    }

    override fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>) {
        Log.i(TAG, "list district size ${listDistrict.size}")
        initSpinnerKec(listDistrict)
    }

    override fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>) {
        Log.i(TAG, "list village size ${listVillage.size}")
        initSpinnerKel(listVillage)
    }

    override fun loadListTokoSuccess(listToko: MutableList<ListTokoRes>) {

        Log.i(TAG, "list size ${listToko.size}")
        if (listToko.size > 0){
            if (!listToko.isNullOrEmpty()){
                mDataList.clear()
                mDataList.addAll(listToko)
                mListValidasiTokoAdapter.addData(listToko)
                mListValidasiTokoAdapter.notifyDataSetChanged()
                rvListValidasiToko.visibility = VISIBLE
                swipeRefreshLayout.visibility = VISIBLE
                lyEmptyResult.visibility = GONE
                addMarkersToMap()
            }
        }else{
            mListValidasiTokoAdapter.clearAllData()
            rvListValidasiToko.visibility = GONE
            swipeRefreshLayout.visibility = GONE
            lyEmptyResult.visibility = VISIBLE
            Toast.makeText(this, "Tidak terdapat Toko pada Area yang Anda Pilih",Toast.LENGTH_LONG).show()
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

    private fun initView(){
        swipeRefreshLayout.isEnabled = false
        rvListValidasiToko.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListValidasiToko.setHasFixedSize(true)
        rvListValidasiToko.isNestedScrollingEnabled = false
        rvListValidasiToko.adapter = mListValidasiTokoAdapter
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map.uiSettings.isMyLocationButtonEnabled = false
        enableMyLocation()

        with(map){
            setOnMarkerClickListener(this@ListValidasiTokoActivity)
            setOnInfoWindowClickListener(this@ListValidasiTokoActivity)
        }
        addMarkersToMap()
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d(TAG,"marker klik, ${marker.title}")
        return false
    }

    override fun onInfoWindowClick(klikMarker: Marker) {
        Log.e(TAG,"window marker klik: ${klikMarker.title}")
        val intent = Intent(this, ValidasiTokoActivity::class.java)
        intent.putExtra(Constants.ID_TOKO, klikMarker.title)
        startActivity(intent)
    }

    private fun addMarkersToMap(){

        for (t in mDataList.indices){
            val location = LatLng(mDataList[t].shop_location.shop_latitude,
                mDataList[t].shop_location.shop_longitude)
            val title = mDataList[t].outlet_id
            val snippet = mDataList[t].outlet_name

            with(map){
                //map.clear()
                addMarker(MarkerOptions().apply {
                    position(location)
                    title(title)
                    snippet(snippet)
                })
            }
        }
    }
}
