package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.response.ListCabinetOutletRes
import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 10/05/2020.
 */
class ListTokoAsalContract {
    interface View : BaseContract.View {
        fun loadListStatusCabinet(listStatusCabinet: MutableList<ListCabinetOutletRes>)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadListTokoAsal(idToko:String, namaToko:String, noToko:String, noKabinet:String)
    }
}