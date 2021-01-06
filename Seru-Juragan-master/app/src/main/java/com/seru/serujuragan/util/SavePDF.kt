package com.seru.serujuragan.util

import android.os.Environment
import android.util.Log
import okhttp3.ResponseBody
import java.io.*
import java.lang.Exception

/**
 * Created by Mahendra Dev on 15/05/2020.
 */

object SavePDF{
    const val TAG = "Save PDF proses"

     fun saveFile(bodyFile: ResponseBody, fileName: String): Boolean{
        var result: Boolean

        try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val pdf = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ),fileName)

                Log.e(TAG, "simpan : $pdf")
                inputStream = bodyFile.byteStream()
                outputStream = FileOutputStream(pdf)
                while (true){
                    val read = inputStream.read(fileReader)
                    if (read == -1){
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
                result = true
            }catch (e: Exception){
                result = false
            }finally {
                inputStream?.close()
                outputStream?.close()
            }
            //result = true
        }catch (e: IOException){
            result = false
        }

        return result
    }
}