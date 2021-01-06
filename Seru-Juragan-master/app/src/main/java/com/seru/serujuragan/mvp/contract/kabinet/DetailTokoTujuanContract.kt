package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.request.CabinetMandiriReq
import com.seru.serujuragan.data.response.CabinetMandiriRes
import com.seru.serujuragan.data.response.DetailTokoAsalRes
import com.seru.serujuragan.mvp.contract.BaseContract

/**
 * Created by Mahendra Dev on 13/05/2020.
 */
class DetailTokoTujuanContract {
    interface View : BaseContract.View {
        fun loadDetailTokoSuccess(detailTokoRes: DetailTokoAsalRes)
        fun submitRequestSuccess(cabinetMandiriRes: CabinetMandiriRes)
        fun submitRequestError(error: String, errorCode: Int)
        //fun downloadFormA1Success(fileName:String)
        fun downloadPDFSuccess(fileName:String)
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailToko(idTokoAsal: String,idTokoTujuan: String)
        fun submitRequestCabinet(idTokoAsal: String,idTokoTujuan: String, cabinetMandiriReq: CabinetMandiriReq)
        fun downloadPdfShare(idRequest:String, typeRequest:String)
        //fun downloadPDF(pathName: String, idRequest: String)
    }
}