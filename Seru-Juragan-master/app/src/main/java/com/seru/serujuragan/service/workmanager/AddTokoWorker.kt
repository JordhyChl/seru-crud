package com.seru.serujuragan.service.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.seru.serujuragan.api.ApiService
import com.seru.serujuragan.config.AppPreference
import com.seru.serujuragan.data.request.AddTokoReq
import com.seru.serujuragan.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

/**
 * Created by Mahendra Dev on 26/01/2020
 */
class AddTokoWorker(context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters){

    private val TAG = AddTokoWorker::class.java.simpleName
    private val apiService: ApiService = ApiService.create()
    private val contentType = Constants.CONTENT_TYPE
    private val token = AppPreference.token

    override fun doWork(): Result {

        val datanya = inputData.getString("data")
        val reqToko = Gson().fromJson(datanya, AddTokoReq::class.java)
        Log.e("TAG","worker : $datanya")
        CompositeDisposable()
            .add(apiService.addToko(contentType, token, reqToko)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.code == 200){
                    Log.i(TAG,"background submit : ${it.code}")
                }
            })

        return Result.success()


//        return try {
//            val response = coba(reqToko)
//            Log.e(TAG, "val resp : $response")
////            if (response == 0){
////                coba(reqToko)
////            }else {
//                return when (response) {
//                    200 -> {
//                        Log.i(TAG, "worker result success : ${Result.success()}")
//                        Result.success()
//                    }
//                    401 -> {
//                        Log.i(TAG, "worker result fail : ${Result.failure()}")
//                        Result.failure()
//                    }
//                    else -> {
//                        Log.i(TAG, "worker result retry : ${Result.retry()}")
//                        //Result.retry()
//                        Result.failure()
//                    }
//                }
////            }
//        }catch (e: IOException){
//            Result.retry()
//        }
    }
}