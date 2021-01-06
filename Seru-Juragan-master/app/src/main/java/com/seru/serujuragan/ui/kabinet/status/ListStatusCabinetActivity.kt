package com.seru.serujuragan.ui.kabinet.status

import android.app.Activity
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
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.ListStatusCabinetContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import com.seru.serujuragan.view.customadapter.CustomRequestStateAdapter
import com.seru.serujuragan.view.qrcode.QrCodeActivity
import kotlinx.android.synthetic.main.activity_list_status_cabinet.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class ListStatusCabinetActivity : AppCompatActivity(), ListStatusCabinetContract.View {

    companion object {
        val TAG = ListStatusCabinetActivity::class.java.simpleName
        private const val REQ_QRCODE = 305
    }

    @Inject
    lateinit var presenter: ListStatusCabinetContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDataListCabinet = ArrayList<ListCabinetRes>()
    private val mStatusCabinetAdapter = ListStatusCabinetAdapter(this, mDataListCabinet)
    private var mDataRequestState = ArrayList<RequestState>()
    private var menuVisible : Boolean = true
    private var namaToko : String = ""
    private var kodeToko : String = ""
    private var noToko : String = ""
    private var qrToko : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_status_cabinet)
        toolbarTitle.text = "Status Kabinet"
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
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            if (!menuVisible){
                showProgressDialog(true)
                presenter.loadListStatusToko("","","","","")
            }
            initSpinnerState()
            //Dev Data
            /*mDataListCabinet.add(ListCabinetRes("RTM - 11111111","11111111","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(1,"Waiting")))
            mDataListCabinet.add(ListCabinetRes("RTM - 2222222","2222222","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(2,"Disetujui")))
            mDataListCabinet.add(ListCabinetRes("RTM - 3333333","3333333","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(3,"Siap Tarik")))
            mDataListCabinet.add(ListCabinetRes("RTM - 444444","444444","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(4,"Siap Kirim")))
            mDataListCabinet.add(ListCabinetRes("RTM - 5555555","5555555","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(5,"Selesai")))
            mDataListCabinet.add(ListCabinetRes("RTM - 6666666","6666666","Toko Dummy",
                Cabinet("CC12233","Cab_open009","1"),11111111, RequestType(1,""),CabinetStatus(6,"Ditolak")))
            mStatusCabinetAdapter.notifyDataSetChanged()*/
        }else{
            lyListStatusCabinet.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){
        tvdateToday.text = AppPreference.dayNow

        btnStatusTarik.setOnClickListener {  }
        btnStatusTukar.setOnClickListener {  }
        btnStatusTarikMandiri.setOnClickListener {
            lyListStatusCabinet.visibility = VISIBLE
            lyMenuStatus.visibility = GONE
            menuVisible = false
            init()
        }

        btnSearchName.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            qrToko = ""
            showProgressDialog(true)
            presenter.loadListStatusToko(kodeToko,namaToko,noToko,qrToko,"")
        }
        btnSearchCode.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            qrToko = ""
            showProgressDialog(true)
            presenter.loadListStatusToko(kodeToko,namaToko,noToko,qrToko,"")
        }
        btnSearchtelp.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            qrToko = ""
            showProgressDialog(true)
            presenter.loadListStatusToko(kodeToko,namaToko,noToko,qrToko,"")
        }
        btnScanQr.setOnClickListener {
            val intent = Intent(this, QrCodeActivity::class.java)
            startActivityForResult(intent, REQ_QRCODE)
        }

        rvListStatusCabinet.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListStatusCabinet.setHasFixedSize(true)
        rvListStatusCabinet.isNestedScrollingEnabled = false
        rvListStatusCabinet.adapter = mStatusCabinetAdapter
    }

    private fun initSpinnerState(){
        val initialState = listOf(
            RequestState("","Pilih Status"),
            RequestState("1","Waiting"),
            RequestState("2","Disetujui"),
            RequestState("3","Siap Tarik"),
            RequestState("4","Siap Kirim"),
            RequestState("5","Selesai"),
            RequestState("6","Ditolak")
        )
        mDataRequestState.clear()
        mDataRequestState.addAll(initialState)
        val aa = CustomRequestStateAdapter(
            this,
            mDataRequestState
        )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spApprovalStatus.adapter = aa

        spApprovalStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0){
                    Log.e(TAG, "do nothing in index 0")
                }else {
                    val state = parent?.getItemAtPosition(position) as RequestState
                    val stateId = state.stateId
                    showProgressDialog(true)
                    presenter.loadListStatusToko("","","","",stateId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQ_QRCODE -> {
                    if (data != null){
                        val qrCodeValue = data.extras?.get("qrValue").toString()
                        Log.i(TAG,"QRcode value : $qrCodeValue")
                        showProgressDialog(true)
                        presenter.loadListStatusToko(kodeToko,namaToko,noToko,qrToko,"")
                    }
                }
            }
        }
    }

    override fun loadListStatusCabinet(listStatusCabinet: MutableList<ListCabinetRes>) {
        Log.i(TAG, "list size ${listStatusCabinet.size}")
        if (listStatusCabinet.size > 0){
            if (!listStatusCabinet.isNullOrEmpty()){
                mDataListCabinet.clear()
                mDataListCabinet.addAll(listStatusCabinet)
                mStatusCabinetAdapter.addData(listStatusCabinet)
                mStatusCabinetAdapter.notifyDataSetChanged()
                rvListStatusCabinet.visibility = View.VISIBLE
                swipeRefreshLayout.visibility = View.VISIBLE
                lyEmptyResult.visibility = View.GONE
            }
        }else{
            mStatusCabinetAdapter.clearAllData()
            rvListStatusCabinet.visibility = View.GONE
            swipeRefreshLayout.visibility = View.GONE
            lyEmptyResult.visibility = View.VISIBLE
            Toast.makeText(this, "Tidak terdapat Toko pada Area yang Anda Pilih", Toast.LENGTH_LONG).show()
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
        Log.e("Error load cause", error)
        mStatusCabinetAdapter.clearAllData()
        lyListStatusCabinet.visibility = View.GONE
        rvListStatusCabinet.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
        emptyPage.visibility = View.VISIBLE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (menuVisible){
            super.onBackPressed()
        }else{
            lyListStatusCabinet.visibility = GONE
            lyMenuStatus.visibility = VISIBLE
            menuVisible = true
            mStatusCabinetAdapter.clearAllData()
        }
    }
}
