package com.seru.serujuragan.mvp.contract.toko

import com.seru.serujuragan.data.response.DataDistrict
import com.seru.serujuragan.data.response.DataVillages
import com.seru.serujuragan.data.response.ListAuditTokoRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 27/01/2020
 */
class ListAuditTokoContract {
    interface View : BaseContract.View {
        fun loadAllDistrictSuccess(listDistrict: MutableList<DataDistrict>)
        fun loadAllVilageSuccess(listVillage: MutableList<DataVillages>)
        fun loadListAuditSuccess(listStatusToko: MutableList<ListAuditTokoRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListAuditToko()
    }
}