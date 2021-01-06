package com.seru.serujuragan.mvp.contract.kabinet

import com.seru.serujuragan.data.response.DetailTokoAsalRes
import com.seru.serujuragan.data.response.UploadPicRes
import com.seru.serujuragan.mvp.contract.BaseContract
import java.io.File

/**
 * Created by Mahendra Dev on 13/05/2020.
 */
class DetailTokoAsalContract {
    interface View : BaseContract.View {
        fun loadDetailTokoSuccess(detailTokoRes: DetailTokoAsalRes)
        fun uploadImgSuccess(name: String, imgFile: File?, res: UploadPicRes)
        fun checkinSuccess()
    }

    interface Presenter:
        BaseContract.Presenter<View> {
        fun loadDetailToko(idToko: String)
        fun submitCheckin(path:String, idRequest: String, latitude: String, longitude: String)
        fun uploadFoto(pathName:String, imgFile: File?)
    }
}