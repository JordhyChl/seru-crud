package com.seru.serujuragan.ui.toko.audit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.DataDistrict
import com.seru.serujuragan.data.response.DataVillages
import com.seru.serujuragan.data.response.ListAuditTokoRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.ListAuditTokoContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_list_audit.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class ListAuditTokoActivity : AppCompatActivity(),
    ListAuditTokoContract.View {

    companion object {
        val TAG = ListAuditTokoActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: ListAuditTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDataListAudit = ArrayList<ListAuditTokoRes>()
    private val mAuditTokoAdapter = ListAuditTokoAdapter(this, mDataListAudit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_audit)
        toolbarTitle.text = "AUDIT TOKO"
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
            Log.i("is connect internet ? :", isConnected.toString())
            showProgressDialog(true)
            //presenter.loadListDistrict()
            presenter.loadListAuditToko()
        }else{
            lyListAuditToko.visibility = GONE
            offlinePage.visibility = VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
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
        lyListAuditToko.visibility = GONE
        swipeRefreshLayout.visibility = GONE
        emptyPage.visibility = VISIBLE
        Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "Tidak terdapat Toko pada Area yang Anda Pilih", Toast.LENGTH_LONG).show()
    }

    override fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadListAuditSuccess(listStatusToko: MutableList<ListAuditTokoRes>) {
        if (listStatusToko.size > 0){
            if (!listStatusToko.isNullOrEmpty()){
                mDataListAudit.clear()
                mDataListAudit.addAll(listStatusToko)
                mAuditTokoAdapter.addData(listStatusToko)
                mAuditTokoAdapter.notifyDataSetChanged()
                rvListAuditToko.visibility = VISIBLE
                swipeRefreshLayout.visibility = VISIBLE
                lyEmptyResult.visibility = GONE
            }else{
                Log.e(TAG,"data empty")
            }
        }else{
            mAuditTokoAdapter.clearAllData()
            rvListAuditToko.visibility = GONE
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
        rvListAuditToko.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListAuditToko.setHasFixedSize(true)
        rvListAuditToko.isNestedScrollingEnabled = false
        rvListAuditToko.adapter = mAuditTokoAdapter
    }
}
