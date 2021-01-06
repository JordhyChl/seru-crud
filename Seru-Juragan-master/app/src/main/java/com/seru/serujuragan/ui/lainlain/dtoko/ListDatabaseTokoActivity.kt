package com.seru.serujuragan.ui.lainlain.dtoko

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
import com.seru.serujuragan.data.response.ListDbTokoRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.lainlain.DatabaseTokoContract
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_list_database_toko.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class ListDatabaseTokoActivity : AppCompatActivity(),
    DatabaseTokoContract.View {

    companion object {
        val TAG = ListDatabaseTokoActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: DatabaseTokoContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDbTokoRes = ArrayList<ListDbTokoRes>()
    private val mDbHunterAdapter = DatabaseTokoAdapter(this, mDbTokoRes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_database_toko)
        toolbarTitle.text = "List Database Toko"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        isConnected = InternetCheck.isConnected(this)
        injectDependency()
        presenter.attach(this)
        init()
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
            initRview()
            presenter.loadAllDbToko()
        }else{
            swipeLyDbToko.visibility = GONE
            offlinePageLdb.visibility = VISIBLE
            Log.i("is connect intenter ? :", isConnected.toString())
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
        swipeLyDbToko.visibility = GONE
        emptyPageLdb.visibility = VISIBLE
    }

    override fun loadDbTokoSuccess(dataListToko: MutableList<ListDbTokoRes>) {
        if (dataListToko.size > 0){
            if (!dataListToko.isNullOrEmpty()){
                mDbTokoRes.clear()
                mDbTokoRes.addAll(dataListToko)
                mDbHunterAdapter.addData(dataListToko)
                mDbHunterAdapter.notifyDataSetChanged()
                rvListdbToko.visibility = VISIBLE
                swipeLyDbToko.visibility = VISIBLE
                lyEmptyResultToko.visibility = GONE
            }else{
                Log.e(TAG,"data empty")
            }
        }else{
            mDbHunterAdapter.clearAllData()
            rvListdbToko.visibility = GONE
            swipeLyDbToko.visibility = GONE
            lyEmptyResultToko.visibility = VISIBLE
            Toast.makeText(this, "Tidak terdapat toko pada database Anda", Toast.LENGTH_SHORT).show()
        }
    }


    override fun doLogout() {

    }

    private fun initRview(){
        rvListdbToko.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListdbToko.setHasFixedSize(true)
        rvListdbToko.isNestedScrollingEnabled = false
        rvListdbToko.adapter = mDbHunterAdapter
    }
}
