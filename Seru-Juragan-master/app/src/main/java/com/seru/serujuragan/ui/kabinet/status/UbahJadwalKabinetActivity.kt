package com.seru.serujuragan.ui.kabinet.status

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.RequestScheduleReq
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.di.component.DaggerActivityComponent
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.mvp.contract.kabinet.UbahJadwalKabinetContract
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.util.Constants
import com.seru.serujuragan.util.InternetCheck
import com.seru.serujuragan.view.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_ubah_jadwal_kabinet.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UbahJadwalKabinetActivity : AppCompatActivity(), SlideDatePickerDialogCallback,
    UbahJadwalKabinetContract.View {

    companion object{
        val TAG = UbahJadwalKabinetActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenter: UbahJadwalKabinetContract.Presenter
    private var isConnected = false
    private val progressDialog = CustomProgressDialog()
    private var callDate: Boolean = false
    private var datePickerCall : Long = 0
    private var sentDate: Boolean = false
    private var datePickerSent : Long = 0
    private var dateCallPicked: Boolean = false
    private var dateSentPicked: Boolean = false
    private lateinit var idRequest: String
    private lateinit var intentSource: String
    private var requestState: Int = 0
    lateinit var requestStatus: String
    private var isReschedule: Boolean = false
    lateinit var recallDriver: String
    lateinit var recallCar: String
    lateinit var shippingDriver: String
    lateinit var shippingCar:String
    private lateinit var infoTarik: RecallCabinet
    private var submitSuccess:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_jadwal_kabinet)
        toolbarTitle.text = "INPUT JADWAL PEMINDAHAN"
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
        idRequest = intent.getStringExtra(Constants.ID_REQUEST_CABINET)!!
        intentSource = intent.getStringExtra(Constants.INTENT_SOURCE)!!
        requestState = intent.getIntExtra(Constants.REQUEST_STATE,0)
        requestStatus = intent.getStringExtra(Constants.REQUEST_STATUS)!!
        if (isConnected){
            Log.i("is connect internet ? :", isConnected.toString())
            showProgressDialog(true)
            presenter.loadDetailSchedule(idRequest)
            when (intentSource) {
                "kirim" -> {
                    tvSelectDateRecall.isEnabled = false
                    edtNoDriverTarik.isEnabled = false
                    edtNoMobilTarik.isEnabled = false
                    isReschedule = true
                }
                "tarik" -> {
                    isReschedule = true
                }
                else -> {
                    Log.d(TAG, "normal")
                    isReschedule = false
                }
            }
        }else{
            scrollView.visibility = View.GONE
            offlinePage.visibility = View.VISIBLE
            Log.i("is connect internet ? :", isConnected.toString())
        }
    }

    private fun initView(){

        btnDetailRequest.setOnClickListener {
            val intent = Intent(this, DetailStatusCabinetActvity::class.java)
            intent.putExtra(Constants.ID_REQUEST_CABINET, idRequest)
            intent.putExtra(Constants.REQUEST_STATE, requestState)
            intent.putExtra(Constants.REQUEST_STATUS, requestStatus)
            startActivity(intent)
        }

        tvSelectDateRecall.setOnClickListener {
            initCalendar(1)
            callDate = true
            sentDate = false
        }

        tvSelectDateSent.setOnClickListener {
            initCalendar(1)
            callDate = false
            sentDate = true
        }

        btnSubmitRequest.setOnClickListener {
            getFormValue()
        }
    }

    private fun generateQr(code: String){
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 150, 150)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        imgQrCode.setImageBitmap(bitmap)
        imgQrCode2.setImageBitmap(bitmap)
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

    private fun getFormValue(){

        var recallDriverInput = edtNoDriverTarik.text.toString()
        var recallCarInput = edtNoMobilTarik.text.toString()
        var sentDriverInput = edtNoDriverKirim.text.toString()
        var sentCarInput = edtNoMobilKirim.text.toString()
        //var note =

        infoTarik = if (isReschedule && intentSource == "kirim"){
            RecallCabinet(datePickerCall,recallCar,recallDriver)
        }else{
            RecallCabinet(datePickerCall,recallCarInput,recallDriverInput)
        }
        val infoKirim = SentCabinet(datePickerSent,sentCarInput,sentDriverInput)
        val req = RequestScheduleReq(isReschedule,infoTarik,infoKirim,"")

        val jsonSubmit = Gson().toJson(req)
        Log.i(TAG,"value submit : $jsonSubmit")
        if (recallDriverInput.isNotEmpty() && recallCarInput.isNotEmpty() && sentDriverInput.isNotEmpty() && sentCarInput.isNotEmpty()){
            showProgressDialog(true)
            presenter.submitScheduleProcess(idRequest, req)
        }else{
            Toast.makeText(this,"Anda Harus memastikan semua form terisi", Toast.LENGTH_LONG).show()
        }

    }

    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        val date = SimpleDateFormat("dd / MMMM / yyyy",Locale("in")).format(calendar.time)
        val timestamp = calendar.timeInMillis

        if (callDate){
            if (dateSentPicked){
                if (timestamp < datePickerSent){
                    datePickerCall = timestamp
                    tvSelectDateRecall.text = date
                    dateCallPicked = true
                }else{
                    Toast.makeText(this,"Tanggal tarik harus lebih kecil dari tanggal kirim", Toast.LENGTH_SHORT).show()
                }
            }else{
                datePickerCall = timestamp
                tvSelectDateRecall.text = date
                dateCallPicked = true
            }
        }else{
            if (dateCallPicked){
                if (datePickerCall < timestamp){
                    datePickerSent = timestamp
                    tvSelectDateSent.text = date
                    dateSentPicked = true
                }else{
                    Toast.makeText(this,"Tanggal tarik harus lebih kecil dari tanggal kirim", Toast.LENGTH_SHORT).show()
                }
            }else{
                datePickerSent = timestamp
                tvSelectDateSent.text = date
                dateSentPicked = true
            }
        }
    }

    override fun loadDetailScheduleSuccess(detailRequestSchedule: RequestScheduleRes) {

        tvRTM.text = detailRequestSchedule.request_id
        tvCabinetCode.text = detailRequestSchedule.cabinet.cabinetCode
        tvCabinetType.text = detailRequestSchedule.cabinet.cabinetType
        val qrCode = detailRequestSchedule.cabinet.cabinetQrCode
        generateQr(qrCode)
        tvQrCode.text = qrCode
        //info jadwal tarik
        datePickerCall = detailRequestSchedule.recall_info.schedule
        recallCar = detailRequestSchedule.recall_info.vehicle_number
        recallDriver = detailRequestSchedule.recall_info.driver_number
        edtNoMobilTarik.setText(recallCar)
        edtNoDriverTarik.setText(recallDriver)
        val recallDate = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(datePickerCall)
        tvDateRecallInfo.text = recallDate
        tvDriverNumberRecall.text = recallDriver
        tvCarNumberRecall.text = recallCar
        //info jadwal kirim
        datePickerSent = detailRequestSchedule.sent_info.schedule
        shippingCar = detailRequestSchedule.sent_info.vehicle_number
        shippingDriver = detailRequestSchedule.sent_info.driver_number
        edtNoMobilKirim.setText(shippingCar)
        edtNoDriverKirim.setText(shippingDriver)
        val shippingDate = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(datePickerSent)
        tvDateShippingInfo.text = shippingDate
        tvDriverNumberShipping.text = shippingDriver
        tvCarNumberShipping.text = shippingCar
    }

    override fun submitScheduleSuccess(cabinetMandiriRes: CabinetMandiriRes) {
        Log.d(TAG, "ID success ${cabinetMandiriRes.requestId}")
        tvRTMConfirm.text = "Permintaan ${cabinetMandiriRes.requestId}"
        tvCabinetCode2.text = cabinetMandiriRes.cabinet.cabinetCode
        tvCabinetType2.text = cabinetMandiriRes.cabinet.cabinetType
        val qrCode = cabinetMandiriRes.cabinet.cabinetQrCode
        tvQrCode2.text = qrCode

        scrollView.visibility = View.GONE
        svSuccesSubmit.visibility = VISIBLE
        submitSuccess = true
        btnFinish.setOnClickListener {
            val intent = Intent(this, ListStatusCabinetActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
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
        scrollView.visibility = View.GONE
        emptyPage.visibility = View.VISIBLE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (submitSuccess){
            Log.d(TAG,"back button disable")
        }else{
            super.onBackPressed()
        }
    }
}
