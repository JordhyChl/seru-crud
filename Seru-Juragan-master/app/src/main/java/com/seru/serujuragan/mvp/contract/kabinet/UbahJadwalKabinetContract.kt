package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.request.RequestScheduleReq
import com.seru.serujuragan.data.response.CabinetMandiriRes
import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.data.response.RequestScheduleRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class UbahJadwalKabinetContract {
    interface View : BaseContract.View {
        fun loadDetailScheduleSuccess(detailRequestSchedule: RequestScheduleRes)
        fun submitScheduleSuccess(cabinetMandiriRes: CabinetMandiriRes)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailSchedule(idRequestCabinet: String)
        fun submitScheduleProcess(idRequestCabinet: String, req: RequestScheduleReq)
    }
}