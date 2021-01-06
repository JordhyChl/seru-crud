package com.seru.serujuragan.ui.toko.tokobaru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.response.ListNewTokoRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.toko.TokoBaruContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_toko_baru.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class TokoBaruActivity : AppCompatActivity(), TokoBaruContract.View {

    companion object {
        val TAG = TokoBaruActivity::class.java.simpleName
    }

    @Inject lateinit var presenter: TokoBaruContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toko_baru)
        toolbarTitle.text = "Toko Baru"
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
            presenter.loadListNewToko()
        }else{
            Log.i("is connect intenter ? :", isConnected.toString())
        }
    }

    private fun showDistrict(kecamatanList: MutableList<ListNewTokoRes>) {
        rv_list_kecamatan.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_list_kecamatan.setHasFixedSize(true)
        rv_list_kecamatan.isNestedScrollingEnabled = false
        rv_list_kecamatan.adapter =
            TokoBaruKecamatanAdapter(
                kecamatanList
            )
    }


    override fun showProgressDialog(show: Boolean) {
        if (show){
            progressDialog.showProg(this)
        }else{
            progressDialog.dismissProg()
        }
    }

    override fun showErrorMessage(error: String, errorCode: Int) {
        Log.e(TAG,"Error load cause,$error")
    }

    override fun loadListNewTokoSucces(listNewToko: MutableList<ListNewTokoRes>) {
        val listNewShop = listNewToko[0].listaArea[0].villageName
        Log.e(TAG,"dis name : $listNewShop")
        if (listNewShop.isNotEmpty()){
            showDistrict(listNewToko)
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
}
