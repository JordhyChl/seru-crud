package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.request.FilterTokoReq
import com.seru.serujuragan.data.response.DataDistrict
import com.seru.serujuragan.data.response.DataVillages
import com.seru.serujuragan.data.response.ListTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 29/12/2019
 */
class ListStatusTokoContract {
    interface View : BaseContract.View {
        fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>)
        fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>)
        fun loadListStatusSuccess(listStatusToko: MutableList<ListTokoRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListDistrict()
        fun loadListVillage(kecId:String)
        fun loadListStatusToko()
        fun filterValidasiList(filterToko: FilterTokoReq)
    }
}