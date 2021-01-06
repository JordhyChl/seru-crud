package com.seru.serujuragan.view.qrcode

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.seru.serujuragan.R
import com.seru.serujuragan.permission.PermissionsConfig
import com.seru.serujuragan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_qr_code.*
import kotlinx.android.synthetic.main.view_toolbar_primary_right.*

class QrCodeActivity : BaseActivity() {

    lateinit var captureManager: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        toolbarTitle.text = "SCAN QR CODE CABINET"
        toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)

        checkPermission(this, Manifest.permission.CAMERA, PermissionsConfig.CAMERA)
        scanQR()
    }

    private fun scanQR(){
        barcodeView.decodeSingle(object: BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    Log.e("TAG","val : ${it.text}")
                    val scanValue = it.text
                    qrCodeResult(scanValue)

                    val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    if(vib.hasVibrator()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            // void vibrate (VibrationEffect vibe)
                            vib.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    // The default vibration strength of the device.
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }else{
                            // This method was deprecated in API level 26
                            vib.vibrate(100)
                        }
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })
    }

    private fun qrCodeResult(qrValue : String){
        val i = Intent()
        i.putExtra("qrValue", qrValue)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }
}
