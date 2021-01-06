package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.request.AddTokoReq
import com.seru.serujuragan.data.request.FilterTokoReq
import com.seru.serujuragan.data.response.*
import com.seru.serujuragan.mvp.contract.BaseContract
import java.io.File

/**
 * Created by Mahendra Dev on 29/12/2019
 */
class TambahTokoContract {
    interface View : BaseContract.View {
        fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>)
        fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>)
        fun uploadImgSuccess(name: String, imgFile: File?, res: UploadPicRes)
        fun filterTokoSucces(filterRes: List<ListTokoRes>)
        fun submitFormSuccess(submitRes: AddTokoRes)
        fun submitFormError(error: String, errorCode: Int)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadTimeline()
        fun loadListDistrict()
        fun loadListVillage(kecId:String)
        fun filterToko(filterToko: FilterTokoReq)
        fun uploadFoto(pathName:String, imgFile: File?)
        fun submitForm(dataToko: AddTokoReq)
    }
}