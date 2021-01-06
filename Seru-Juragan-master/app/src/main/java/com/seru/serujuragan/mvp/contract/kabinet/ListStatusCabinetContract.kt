package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.mvp.contract.BaseContract

class ListStatusCabinetContract {
    interface View : BaseContract.View {
        fun loadListStatusCabinet(listStatusCabinet: MutableList<ListCabinetRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListStatusToko(idTokoRequest: String, namaToko:String, noToko:String, qrCode:String, stateId:String)
    }
}