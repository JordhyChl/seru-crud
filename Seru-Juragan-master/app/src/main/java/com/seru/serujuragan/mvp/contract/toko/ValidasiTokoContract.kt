package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.request.UpdateTokoReq
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.mvp.contract.BaseContract
import java.io.File

/**
 * Created by Mahendra Dev on 31/12/2019
 */
class ValidasiTokoContract {
    interface View : BaseContract.View {
        fun loadDataVildasitokoSuccess(detailTokoRes: DetailTokoRes)
        fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>)
        fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>)
        fun uploadImgSuccess(name: String, imgFile: File?, res: UploadPicRes)
        fun submitValidasiTokoSuccess(submitRes: AddTokoRes)
        fun submitValidasiError(error: String, errorCode: Int)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListDistrict()
        fun loadListVillage(kecId:String)
        fun loadDataValidasiToko(idToko: String)
        fun uploadFoto(pathName:String, imgFile: File?)
        fun submitValidasiToko(dataUpdate: UpdateTokoReq)
    }
}