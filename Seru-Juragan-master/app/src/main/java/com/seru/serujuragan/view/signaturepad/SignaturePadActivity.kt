package com.seru.serujuragan.view.signaturepad

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.github.gcacace.signaturepad.views.SignaturePad
import com.seru.serujuragan.R
import com.seru.serujuragan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_signature_pad.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Path
import java.nio.file.Paths


class SignaturePadActivity : BaseActivity() {

    companion object {
        val TAG = SignaturePadActivity::class.java.simpleName
        private const val DATA_INTENT_FILE = "dataFile"
        private const val DATA_INTENT_PATH = "dataPath"
        private const val DATA_INTENT_URI = "dataUri"
    }

    private lateinit var signatureBitmap: Bitmap
    lateinit var signatureUri: Uri
    lateinit var signaturePath: String
    lateinit var signatureFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_pad)

        signature_pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Toast.makeText(this@SignaturePadActivity, "OnStartSigning", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"OnStartSigning")
            }

            override fun onSigned() {
                save_button.isEnabled = true
                clear_button.isEnabled = true
            }

            override fun onClear() {
                save_button.isEnabled = false
                clear_button.isEnabled = false
            }
        })

        clear_button.setOnClickListener {
            signature_pad.clear()
        }

        save_button.setOnClickListener { saveSignature() }
    }

    private fun saveSignature(){
        Log.i(TAG, "save signature proses")

        signatureBitmap = signature_pad.signatureBitmap
        if (addJpgSignatureToGallery(signatureBitmap)){
            Log.d(TAG,"saved : $signatureUri")
            Log.d(TAG,"saved file : $signaturePath")
            imageResult(signatureFile)
            //imageResult(signatureUri)
            //imageResultPath(signaturePath)
        }else{
            Log.e(TAG,"gagal save")
        }
    }

    private fun imageResult(imageFile: File){
        val i = Intent()
        i.putExtra(DATA_INTENT_FILE, imageFile)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    private fun imageResultUri(imageURI: Uri){
        val i = Intent()
        i.putExtra(DATA_INTENT_URI, imageURI)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    private fun imageResultPath(imagePath: String){
        val i = Intent()
        i.putExtra(DATA_INTENT_PATH, imagePath)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    private fun addJpgSignatureToGallery(signature: Bitmap?): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature!!, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0.0f,0.0f,null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    @Suppress("DEPRECATION")
    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(photo)
        val contentPath: String = photo.absolutePath
        signatureUri = contentUri
        signaturePath = contentPath
        signatureFile = photo
        mediaScanIntent.data = contentUri
        this@SignaturePadActivity.sendBroadcast(mediaScanIntent)
    }

    @Suppress("DEPRECATION")
    fun getAlbumStorageDir(albumName: String?): File? { // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }
}
