package com.seru.serujuragan.di.component

import dagger.Component
import com.seru.serujuragan.di.module.ActivityModule
import com.seru.serujuragan.di.module.AppModule
import com.seru.serujuragan.di.module.DataModule
import com.seru.serujuragan.ui.login.LoginActivity
import com.seru.serujuragan.ui.home.HomeActivity
import com.seru.serujuragan.ui.kabinet.mandiri.DetailTokoAsalActivity
import com.seru.serujuragan.ui.kabinet.mandiri.DetailTokoTujuanActivity
import com.seru.serujuragan.ui.kabinet.mandiri.ListTokoAsalActivity
import com.seru.serujuragan.ui.kabinet.mandiri.ListTokoTujuanActivity
import com.seru.serujuragan.ui.kabinet.status.DetailStatusCabinetActvity
import com.seru.serujuragan.ui.kabinet.status.ListStatusCabinetActivity
import com.seru.serujuragan.ui.kabinet.status.UbahJadwalKabinetActivity
import com.seru.serujuragan.ui.kabinet.status.kirim.ProsesKirimCabinetActivity
import com.seru.serujuragan.ui.kabinet.status.tarik.ProsesTarikCabinetActivity
import com.seru.serujuragan.ui.lainlain.ProfileActivity
import com.seru.serujuragan.ui.lainlain.dhunter.ListDatabaseHunterActivity
import com.seru.serujuragan.ui.lainlain.dtoko.DetailTokoDbActivity
import com.seru.serujuragan.ui.lainlain.dtoko.ListDatabaseTokoActivity
import com.seru.serujuragan.ui.toko.audit.ListAuditTokoActivity
import com.seru.serujuragan.ui.toko.status.DetailTokoActivity
import com.seru.serujuragan.ui.toko.status.StatusTokoActivity
import com.seru.serujuragan.ui.toko.tambah.TambahTokoActivity
import com.seru.serujuragan.ui.toko.tokobaru.TokoBaruActivity
import com.seru.serujuragan.ui.toko.validasi.ListValidasiTokoActivity
import com.seru.serujuragan.ui.toko.validasi.ValidasiTokoActivity
import javax.inject.Singleton

/**
 * Created by Arya Yudha Mahendra on 29/06/2019.
 */
@Singleton
@Component(modules = [ActivityModule::class, AppModule::class, DataModule::class])
interface ActivityComponent {

    //fun inject(formSurveyActivity: FormSurveyActivity)

    fun inject(loginActivity: LoginActivity)

    fun inject(homeActivity: HomeActivity)

    fun inject(tokoBaruActivity: TokoBaruActivity)

    fun inject(listAuditTokoActivity: ListAuditTokoActivity)

    fun inject(listValidasiTokoActivity: ListValidasiTokoActivity)

    fun inject(validasiTokoActivity: ValidasiTokoActivity)

    fun inject(tambahTokoActivity: TambahTokoActivity)

    fun inject(statusTokoActivity: StatusTokoActivity)

    fun inject(detailTokoActivity: DetailTokoActivity)

    fun inject(listTokoAsalActivity: ListTokoAsalActivity)

    fun inject(detailTokoAsalActivity: DetailTokoAsalActivity)

    fun inject(listTokoTujuanActivity: ListTokoTujuanActivity)

    fun inject(detailTokoTujuanActivity: DetailTokoTujuanActivity)

    fun inject(listStatusKabinetActivity: ListStatusCabinetActivity)

    fun inject(detailStatusCabinetActvity: DetailStatusCabinetActvity)

    fun inject(ubahJadwalKabinetActivity: UbahJadwalKabinetActivity)

    fun inject(prosesTarikCabinetActivity: ProsesTarikCabinetActivity)

    fun inject(prosesKirimCabinetActivity: ProsesKirimCabinetActivity)

    fun inject(listDatabaseHunterActivity: ListDatabaseHunterActivity)

    fun inject(listDatabaseTokoActivity: ListDatabaseTokoActivity)

    fun inject(detailTokoDbActivity: DetailTokoDbActivity)

    fun inject(profileActivity: ProfileActivity)

}