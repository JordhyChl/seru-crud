package com.seru.serujuragan.ui.kabinet.status

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListCabinetRes
import com.seru.serujuragan.ui.kabinet.status.kirim.ProsesKirimCabinetActivity
import com.seru.serujuragan.ui.kabinet.status.tarik.ProsesTarikCabinetActivity
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_status_cabinet.view.*

/**
 * Created by Mahendra Dev on 05/05/2020.
 */
class ListStatusCabinetAdapter(private val activity: ListStatusCabinetActivity, private val mListStatusCabinet: MutableList<ListCabinetRes>):
    RecyclerView.Adapter<ListStatusCabinetAdapter.StatusCabinetHolder>() {

    fun addData(mListStatusCabinet: MutableList<ListCabinetRes>){
        this.mListStatusCabinet.clear()
        this.mListStatusCabinet.addAll(mListStatusCabinet)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListStatusCabinet.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListStatusCabinetAdapter.StatusCabinetHolder {
        return StatusCabinetHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_status_cabinet, parent, false))
    }

    override fun getItemCount(): Int {
        return mListStatusCabinet.size
    }

    override fun onBindViewHolder(
        holder: ListStatusCabinetAdapter.StatusCabinetHolder,
        position: Int
    ) {
        val statusCabinetModel = mListStatusCabinet[position]

        when(statusCabinetModel.request_status.statusId){
            1 -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_yellow)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, DetailStatusCabinetActvity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
            2 -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_yellow)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, UbahJadwalKabinetActivity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.INTENT_SOURCE,"")
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
            3 -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_orange)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, ProsesTarikCabinetActivity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
            4 -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_orange)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, ProsesKirimCabinetActivity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
            5 -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_green)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, DetailStatusCabinetActvity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
            else -> {
                holder.lyCabinetStatus.setBackgroundResource(R.drawable.background_rectangle_round_red)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, DetailStatusCabinetActvity::class.java)
                    intent.putExtra(Constants.ID_REQUEST_CABINET,statusCabinetModel.request_id)
                    intent.putExtra(Constants.REQUEST_STATE, statusCabinetModel.request_status.statusId)
                    intent.putExtra(Constants.REQUEST_STATUS, statusCabinetModel.request_status.statusName)
                    activity.startActivity(intent)
                }
            }
        }

        holder.requestId.text = statusCabinetModel.request_id
        holder.outletAsalName.text = statusCabinetModel.outlet_asal.outlet_name
        holder.outletTujuanName.text = statusCabinetModel.outlet_tujuan.outlet_name
        holder.cabinetCode.text = statusCabinetModel.cabinet.cabinetCode
        holder.cabinetType.text = statusCabinetModel.cabinet.cabinetType
        holder.requestType.text = statusCabinetModel.request_type.requestName
        holder.requestStatus.text = statusCabinetModel.request_status.statusName
        holder.layoutTujuan.visibility = VISIBLE
    }

    inner class StatusCabinetHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val requestId = itemView.tvRequestId!!
        val outletAsalName = itemView.tvOutletAsal!!
        val layoutTujuan = itemView.lyOutletTujuan!!
        val outletTujuanName = itemView.tvOutletTujuan!!
        val cabinetCode = itemView.tvCabinetCode!!
        val cabinetType = itemView.tvCabinetType!!
        val requestType = itemView.tvReqType!!
        val requestStatus = itemView.tvRequestStatus!!
        val lyCabinetStatus = itemView.lyRequestStatus!!
    }
}