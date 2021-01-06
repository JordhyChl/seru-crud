package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.request.SubmitProsesPemindahan
import com.seru.serujuragan.data.response.CabinetMandiriRes
import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.data.response.RequestInfoRes
import com.seru.serujuragan.data.response.UploadPicRes
import com.seru.serujuragan.mvp.contract.BaseContract
import java.io.File

/**
 * Created by Mahendra Dev on 16/05/2020.
 */
class ProsesTarikKabinetContract {
    interface View : BaseContract.View {
        fun loadRequestInfo(requestInfoRes: RequestInfoRes)
        fun uploadImgSuccess(name: String, imgFile: File?, res: UploadPicRes)
        fun movingProcessSuccess(cabinetMandiriRes: CabinetMandiriRes)
        //fun downloadFormA1Success(fileName:String)
        fun downloadPDFSuccess(fileName:String)
        fun checkinSuccess()
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailRequest(idRequestCabinet: String)
        fun uploadFoto(pathName:String, imgFile: File?)
        fun submitMovingProcess(idRequestCabinet: String, req: SubmitProsesPemindahan)
        fun downloadPdfShare(idRequest:String, typeRequest:String)
        //fun downloadPDF(pathName: String, idRequest: String)
        fun submitCheckin(path:String, idRequest: String, latitude: String, longitude: String)
    }
}