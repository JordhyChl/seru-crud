package com.seru.serujuragan.ui.toko.status

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.FilterTokoReq
import com.seru.serujuragan.data.response.DataDistrict
import com.seru.serujuragan.data.response.DataVillages
import com.seru.serujuragan.data.response.ListTokoRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.ListStatusTokoContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.customadapter.CustomKecamatanAdapter
import com.seru.serujuragan.view.customadapter.CustomKelurahanAdapter
import kotlinx.android.synthetic.main.activity_status_toko.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class StatusTokoActivity : AppCompatActivity(), ListStatusTokoContract.View {

    companion object {
        val TAG = StatusTokoActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: ListStatusTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDataStatus = ArrayList<ListTokoRes>()
    private val mStatusTokoAdapter = StatusTokoAdapter(this, mDataStatus)
    private var mDataDistrict = ArrayList<DataDistrict>()
    private var mDataVillage =  ArrayList<DataVillages>()
    private var kecamatanValue : String? = ""
    private var kelurahanValue : String? = ""
    private var tokoName : String = ""
    private var tokoCode : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_toko)

        toolbarTitle.text = "Cek Status Toko"
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
        tvdateToday.text = AppPreference.dayNow
        if (isConnected){
            Log.i("is connect intenter ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadListDistrict()
            presenter.loadListStatusToko()
        }else{
            lyListStatusToko.visibility = GONE
            offlinePage.visibility = VISIBLE
            Log.i("is connect intenter ? :", isConnected.toString())
        }
//        btnKec.setOnClickListener {
//            presenter.loadListDistrict()
//        }
        btnSearchName.setOnClickListener {
            tokoName = edtSearchNamaToko.text.toString()
            tokoCode = edtSearchKodeToko.text.toString()
            val filter = FilterTokoReq(tokoCode,tokoName, kecamatanValue!!, kelurahanValue!!,"")
            Log.i(TAG, "val filter : $filter")
            showProgressDialog(true)
            presenter.filterValidasiList(filter)
        }
        btnSearchKodeToko.setOnClickListener {
            tokoName = edtSearchNamaToko.text.toString()
            tokoCode = edtSearchKodeToko.text.toString()
            val filter = FilterTokoReq(tokoCode,tokoName, kecamatanValue!!, kelurahanValue!!,"")
            Log.i(TAG, "val filter : $filter")
            showProgressDialog(true)
            presenter.filterValidasiList(filter)
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
        mStatusTokoAdapter.clearAllData()
        rvListStatusToko.visibility = GONE
        swipeRefreshLayout.visibility = GONE
        lyListStatusToko.visibility = GONE
        emptyPage.visibility = VISIBLE
        Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "Tidak terdapat Toko pada Area yang Anda Pilih", Toast.LENGTH_LONG).show()
    }

    override fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>) {
        initSpinnerKec(listDistrict)
        spKecamatan.visibility = VISIBLE
//        btnKec.visibility = GONE
    }

    override fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>) {
        initSpinnerKel(listVillage)
    }

    override fun loadListStatusSuccess(listStatusToko: MutableList<ListTokoRes>) {
        if (listStatusToko.size > 0){
            if (!listStatusToko.isNullOrEmpty()){
                mDataStatus.clear()
                mDataStatus.addAll(listStatusToko)
                mStatusTokoAdapter.addData(listStatusToko)
                mStatusTokoAdapter.notifyDataSetChanged()
                rvListStatusToko.visibility = VISIBLE
                swipeRefreshLayout.visibility = VISIBLE
                lyEmptyResult.visibility = GONE
            }else{
                Log.e(TAG,"data empty")
            }
        }else{
            mStatusTokoAdapter.clearAllData()
            rvListStatusToko.visibility = GONE
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
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
        rvListStatusToko.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListStatusToko.setHasFixedSize(true)
        rvListStatusToko.isNestedScrollingEnabled = false
        rvListStatusToko.adapter = mStatusTokoAdapter
    }
}
