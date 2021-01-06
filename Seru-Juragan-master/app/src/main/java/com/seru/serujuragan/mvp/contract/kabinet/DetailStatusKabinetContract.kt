package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.response.DetailRequestMandiriRes
import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class DetailStatusKabinetContract {
    interface View : BaseContract.View {
        fun loadDetailStatusCabinet(detailRequestMandiriRes: DetailRequestMandiriRes)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailRequest(idRequestCabinet: String)
    }
}