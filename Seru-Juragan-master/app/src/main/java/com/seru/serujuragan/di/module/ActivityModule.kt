package com.seru.serujuragan.di.module

import android.app.Activity
import com.seru.serujuragan.mvp.contract.*
import com.seru.serujuragan.mvp.contract.kabinet.*
import com.seru.serujuragan.mvp.contract.lainlain.DatabaseHunterContract
import com.seru.serujuragan.mvp.contract.lainlain.DatabaseTokoContract
import com.seru.serujuragan.mvp.contract.lainlain.DetailTokoDbContract
import com.seru.serujuragan.mvp.contract.lainlain.ProfileContract
import com.seru.serujuragan.mvp.contract.toko.*
import dagger.Module
import dagger.Provides
//import com.seru.serujuragan.data.repository.AppRepoInterface
//import com.seru.serujuragan.mvp.contract.FormSurveyContract
import com.seru.serujuragan.mvp.presenter.*
import com.seru.serujuragan.mvp.presenter.kabinet.*
import com.seru.serujuragan.mvp.presenter.lainlain.DatabaseHunterPresenter
import com.seru.serujuragan.mvp.presenter.lainlain.DatabaseTokoPresenter
import com.seru.serujuragan.mvp.presenter.lainlain.DetailTokoDbPresenter
import com.seru.serujuragan.mvp.presenter.lainlain.ProfilePresenter
import com.seru.serujuragan.mvp.presenter.toko.*

//import com.seru.serujuragan.mvp.presenter.FormSurveyPresenter

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity():Activity{
        return activity
    }

//    @Provides
//    fun provideFormSurveyPresenter(): FormSurveyContract.Presenter{
//        return FormSurveyPresenter()
//    }

    @Provides
    fun providesLoginPresenter(): LoginContract.Presenter{
        return LoginPresenter()
    }

    @Provides
    fun providesHomePresenter(): HomeContract.Presenter{
        return HomePresenter()
    }

    @Provides
    fun providesTokoBaruPresenter(): TokoBaruContract.Presenter{
        return TokoBaruPresenter()
    }

    @Provides
    fun providesListAuditTokoPresenter(): ListAuditTokoContract.Presenter {
        return ListAuditTokoPresenter()
    }

    @Provides
    fun providesListValidasiTokoPresenter(): ListValidasiTokoContract.Presenter {
        return ListValidasiTokoPresenter()
    }

    @Provides
    fun providesValidasiTokoPresenter(): ValidasiTokoContract.Presenter {
        return ValidasiTokoPresenter()
    }

    @Provides
    fun providesTambahTokoPresenter(): TambahTokoContract.Presenter {
        return TambahTokoPresenter()
    }

    @Provides
    fun providesListStatusTokoPresenter(): ListStatusTokoContract.Presenter {
        return ListStatusTokoPresenter()
    }

    @Provides
    fun providesDetailTokoPresenter(): DetailTokoContract.Presenter {
        return DetailTokoPresenter()
    }

    @Provides
    fun providesListTokoAsalActvity(): ListTokoAsalContract.Presenter{
        return ListTokoAsalPresenter()
    }

    @Provides
    fun providesDetailTokoAsalActvity(): DetailTokoAsalContract.Presenter{
        return DetailTokoAsalPresenter()
    }

    @Provides
    fun providesListTokoTujuanActvity(): ListTokoTujuanContract.Presenter{
        return ListTokoTujuanPresenter()
    }

    @Provides
    fun providesDetailTokoTujuanActvity(): DetailTokoTujuanContract.Presenter{
        return DetailTokoTujuanPresenter()
    }

    @Provides
    fun providesListStatusKabinetActivity(): ListStatusCabinetContract.Presenter{
        return ListStatusCabinetPresenter()
    }

    @Provides
    fun providesDetailStatusKabinetActivity(): DetailStatusKabinetContract.Presenter{
        return DetailStatusKabinetPresenter()
    }

    @Provides
    fun providesUbahJadwalKabinetActivity(): UbahJadwalKabinetContract.Presenter{
        return UbahJadwalKabinetPresenter()
    }

    @Provides
    fun providesProsesTarikKabinetActivity(): ProsesTarikKabinetContract.Presenter{
        return ProsesTarikKabinetPresenter()
    }

    @Provides
    fun providesProsesKirimKabinetActivity(): ProsesKirimKabinetContract.Presenter{
        return ProsesKirimKabinetPresenter()
    }

    @Provides
    fun providesListDatabaseHunterPresenter(): DatabaseHunterContract.Presenter {
        return DatabaseHunterPresenter()
    }

    @Provides
    fun providesListDatabaseTokoPresenter(): DatabaseTokoContract.Presenter{
        return DatabaseTokoPresenter()
    }

    @Provides
    fun providesDetailTokoDbPresenter(): DetailTokoDbContract.Presenter {
        return DetailTokoDbPresenter()
    }

    @Provides
    fun providesProfilePresenter(): ProfileContract.Presenter{
        return ProfilePresenter()
    }


}