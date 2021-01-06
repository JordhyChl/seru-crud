package com.seru.serujuragan.ui.lainlain.dhunter

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
import com.seru.serujuragan.data.response.ListDbHunterRes
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.lainlain.DatabaseHunterContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_list_database_hunter.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import javax.inject.Inject

class ListDatabaseHunterActivity : AppCompatActivity(),
    DatabaseHunterContract.View{

    companion object {
        val TAG = ListDatabaseHunterActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: DatabaseHunterContract.Presenter

    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private val mDbHunterRes = ArrayList<ListDbHunterRes>()
    private val mDbHunterAdapter = DatabaseHunterAdapter(this, mDbHunterRes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_database_hunter)

        toolbarTitle.text = "List Database Hunter"
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
            presenter.loadAllDbHunter()
        }else{
            swipeRefreshLayout.visibility = GONE
            offlinePage.visibility = VISIBLE
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
        rvListdbHunter.visibility = GONE
        swipeRefreshLayout.visibility = GONE
        emptyPage.visibility = VISIBLE
        Toast.makeText(this, error,Toast.LENGTH_SHORT).show()
    }

    override fun loadDbHunterSuccess(dataListHunter: MutableList<ListDbHunterRes>) {
        if (dataListHunter.size > 0){
            if (!dataListHunter.isNullOrEmpty()){
                mDbHunterRes.clear()
                mDbHunterRes.addAll(dataListHunter)
                mDbHunterAdapter.addData(dataListHunter)
                mDbHunterAdapter.notifyDataSetChanged()
                rvListdbHunter.visibility = VISIBLE
                swipeRefreshLayout.visibility = VISIBLE
                lyEmptyResult.visibility = GONE
            }else{
                Log.e(TAG,"data empty")
            }
        }else{
            mDbHunterAdapter.clearAllData()
            rvListdbHunter.visibility = GONE
            swipeRefreshLayout.visibility = GONE
            lyEmptyResult.visibility = VISIBLE
            Toast.makeText(this, "Tidak terdapat data yang Anda minta.",Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadDbHunterNull() {
        rvListdbHunter.visibility = GONE
        Toast.makeText(this, "Tidak terdapat data yang Anda minta.",Toast.LENGTH_LONG).show()
    }

    override fun doLogout() {
        this.getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        Log.i(TAG, "app pref :  ${AppPreference.isLoggedIn}")
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun initRview(){
        rvListdbHunter.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        rvListdbHunter.setHasFixedSize(true)
        rvListdbHunter.isNestedScrollingEnabled = false
        rvListdbHunter.adapter = mDbHunterAdapter
    }
}

