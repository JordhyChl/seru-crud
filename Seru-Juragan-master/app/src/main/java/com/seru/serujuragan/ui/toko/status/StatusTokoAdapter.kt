package com.seru.serujuragan.ui.toko.status

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListTokoRes
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_toko_status.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 30/12/2019
 */
class StatusTokoAdapter(private val activity: StatusTokoActivity, private val mListStatusToko: MutableList<ListTokoRes>) :
    RecyclerView.Adapter<StatusTokoAdapter.StatusHolder>() {

    fun addData(mListStatusToko: MutableList<ListTokoRes>){
        this.mListStatusToko.clear()
        this.mListStatusToko.addAll(mListStatusToko)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListStatusToko.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusHolder {
        return StatusHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toko_status, parent, false))
    }

    override fun getItemCount(): Int {
        return mListStatusToko.size
    }

    override fun onBindViewHolder(holder: StatusHolder, position: Int) {
        val statusModel = mListStatusToko[position]

        when (statusModel.status_survey.statusId) {
            "3" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_green)
                holder.tvValidateStatus.text = statusModel.status_survey.statusName
            }
            "5" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_green)
                holder.tvValidateStatus.text = statusModel.status_survey.statusName
            }
            "2" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_orange)
                holder.tvValidateStatus.text = statusModel.status_survey.statusName
            }
            "7" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_yellow)
                holder.tvValidateStatus.text = statusModel.status_survey.statusName
            }
            else -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_red)
                holder.tvValidateStatus.text = statusModel.status_survey.statusName
            }
        }

        holder.tokoId.text = statusModel.outlet_id
        holder.tokoName.text = statusModel.outlet_name
        //holder.tokoPhone.text = statusModel.outlet_phone
        val surveyDate = statusModel.date_survey
        val deliveryDate = statusModel.date_delivery
        val localDate = Date(surveyDate*1000)
        val dateSurvey = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(surveyDate*1000)
        val dateDelivery = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(deliveryDate*1000)
        holder.surveyDate.text = dateSurvey
        holder.tvDeliveryPlan.text = dateDelivery

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailTokoActivity::class.java)
            intent.putExtra(Constants.ID_TOKO, statusModel.outlet_id)
            activity.startActivity(intent)
        }
    }

    inner class StatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tokoId = itemView.tvTokoId!!
        val tokoName = itemView.tvTokoName!!
        //val tokoPhone = itemView.tvTokoPhone!!
        val surveyDate = itemView.tvSurveyDate!!
        val btnValidate = itemView.lyValidateStatus!!
        val tvValidateStatus = itemView.tvValidateStatus!!
        val tvDeliveryPlan = itemView.tvDelivPlan!!
    }
}