package com.seru.serujuragan.ui.kabinet.mandiri

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListCabinetOutletRes
import com.seru.serujuragan.data.response.ListDbTokoRes
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_database_toko.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 08/05/2020.
 */
class ListTokoTujuanAdapter (private val activity: ListTokoTujuanActivity, private val mListTokoTujuan: MutableList<ListCabinetOutletRes>):
    RecyclerView.Adapter<ListTokoTujuanAdapter.TokoTujuanHolder>() {

    private lateinit var idTokoAsal : String
    private lateinit var alasanTarik: String
    private lateinit var catatanLain: String
    private lateinit var idTTD: String

    fun addData(mListStatusCabinet: MutableList<ListCabinetOutletRes>, idAsal: String, recallReason:String, otherNote:String, signatureId:String){
        this.mListTokoTujuan.clear()
        this.mListTokoTujuan.addAll(mListStatusCabinet)
        idTokoAsal = idAsal
        alasanTarik = recallReason
        catatanLain = otherNote
        idTTD = signatureId
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListTokoTujuan.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListTokoTujuanAdapter.TokoTujuanHolder {
        return TokoTujuanHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_database_toko, parent, false))
    }

    override fun getItemCount(): Int {
        return mListTokoTujuan.size
    }

    override fun onBindViewHolder(
        holder: ListTokoTujuanAdapter.TokoTujuanHolder,
        position: Int
    ) {
        val tokoTujuanModel = mListTokoTujuan[position]

        holder.outletId.text = tokoTujuanModel.outlet_id
        holder.outletName.text = tokoTujuanModel.outlet_name
        holder.outletPhone.text = tokoTujuanModel.outlet_phone
        val joinDate = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(tokoTujuanModel.join_date)
        holder.surveyDate.text = joinDate

        Log.e("Toko Asal Adapter",idTokoAsal)
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailTokoTujuanActivity::class.java)
            intent.putExtra(Constants.ID_TOKO_ASAL, idTokoAsal)
            intent.putExtra(Constants.ID_TOKO_TUJUAN, tokoTujuanModel.outlet_id)
            intent.putExtra(Constants.RECALL_REASON,alasanTarik)
            intent.putExtra(Constants.RECALL_NOTE,catatanLain)
            intent.putExtra(Constants.ID_SIGNATURE,idTTD)
            activity.startActivity(intent)
        }
    }

    inner class TokoTujuanHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val outletId = itemView.tvTokoId!!
        val outletName = itemView.tvTokoName!!
        val outletPhone = itemView.tvTokoOwner!!
        val surveyDate = itemView.tvJoinDate!!
    }
}