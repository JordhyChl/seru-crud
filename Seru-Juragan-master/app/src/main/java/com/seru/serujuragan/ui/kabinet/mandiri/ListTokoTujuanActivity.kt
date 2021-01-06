package com.seru.serujuragan.ui.kabinet.mandiri

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.seru.serujuragan.mvp.contract.kabinet.ListTokoTujuanContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_list_toko_asal.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class ListTokoTujuanActivity : AppCompatActivity(), ListTokoTujuanContract.View {

    companion object{
        val TAG = ListTokoTujuanActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: ListTokoTujuanContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDataListToko = ArrayList<ListCabinetOutletRes>()
    private val mTokoTujuanAdapter = ListTokoTujuanAdapter(this, mDataListToko)
    private lateinit var recallReason: String
    private lateinit var otherNote: String
    private lateinit var signatureId: String
    private lateinit var idTokoAsal: String
    private var namaToko : String = ""
    private var kodeToko : String = ""
    private var noToko : String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_toko_tujuan)
        toolbarTitle.text = "Toko Tujuan"
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
        idTokoAsal = intent.getStringExtra(Constants.ID_TOKO_ASAL)!!
        recallReason = intent.getStringExtra(Constants.RECALL_REASON)!!
        otherNote = intent.getStringExtra(Constants.RECALL_NOTE)!!
        signatureId = intent.getStringExtra(Constants.ID_SIGNATURE)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadListTokoTujuan(idTokoAsal,"","","")
            //Dev Test
            /*mDataListToko.add(ListCabinetOutletRes("OUT - 11111111","Toko Dummy 1","6281111111111",
                    1589100995, "1589100995"))
            mDataListToko.add(ListCabinetOutletRes("OUT - 22222222","Toko Dummy 2","6281111111111",
                1589100995, "1589100995"))
            mTokoTujuanAdapter.notifyDataSetChanged()*/
        }else{
            lyListStatusCabinet.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){
        btnSearchName.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            showProgressDialog(true)
            presenter.loadListTokoTujuan(idTokoAsal,kodeToko,namaToko,noToko)
        }
        btnSearchCode.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            showProgressDialog(true)
            presenter.loadListTokoTujuan(idTokoAsal,kodeToko,namaToko,noToko)
        }
        btnSearchtelp.setOnClickListener {
            namaToko = edtSearchNamaToko.text.toString()
            kodeToko = edtSearchKodeToko.text.toString()
            noToko = edtSearchTelpToko.text.toString()
            showProgressDialog(true)
            presenter.loadListTokoTujuan(idTokoAsal,kodeToko,namaToko,noToko)
        }

        rvListStatusCabinet.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListStatusCabinet.setHasFixedSize(true)
        rvListStatusCabinet.isNestedScrollingEnabled = false
        rvListStatusCabinet.adapter = mTokoTujuanAdapter
    }

    override fun loadListStatusCabinet(listStatusCabinet: MutableList<ListCabinetOutletRes>) {
        Log.i(TAG, "list size ${listStatusCabinet.size}")
        if (listStatusCabinet.size > 0){
            if (!listStatusCabinet.isNullOrEmpty()){
                Log.d(TAG,"testets : $idTokoAsal $recallReason - $otherNote - $signatureId")
                mDataListToko.clear()
                mDataListToko.addAll(listStatusCabinet)
                mTokoTujuanAdapter.addData(listStatusCabinet,idTokoAsal,recallReason,otherNote,signatureId)
                mTokoTujuanAdapter.notifyDataSetChanged()
                rvListStatusCabinet.visibility = View.VISIBLE
                swipeRefreshLayout.visibility = View.VISIBLE
                lyEmptyResult.visibility = View.GONE
            }
        }else{
            mTokoTujuanAdapter.clearAllData()
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
        Log.e("Error load cause",error)
        mTokoTujuanAdapter.clearAllData()
        lyListStatusCabinet.visibility = View.GONE
        rvListStatusCabinet.visibility = View.GONE
        swipeRefreshLayout.visibility = View.GONE
        emptyPage.visibility = View.VISIBLE
        Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
    }
}
