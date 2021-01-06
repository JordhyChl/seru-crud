package com.seru.serujuragan.ui.toko.audit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListAuditTokoRes
import kotlinx.android.synthetic.main.item_toko.view.*

/**
 * Created by Mahendra Dev on 27/01/2020
 */
class ListAuditTokoAdapter(private val activity: ListAuditTokoActivity,
                           private val mListAuditToko: MutableList<ListAuditTokoRes>):
    RecyclerView.Adapter<ListAuditTokoAdapter.AuditHolder>() {

    fun addData(mListAuditToko: MutableList<ListAuditTokoRes>){
        this.mListAuditToko.clear()
        this.mListAuditToko.addAll(mListAuditToko)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListAuditToko.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AuditHolder {
        return AuditHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toko, parent, false))
    }

    override fun getItemCount(): Int {
        return mListAuditToko.size
    }

    override fun onBindViewHolder(holder: AuditHolder, position: Int) {
        val auditModel = mListAuditToko[position]

        when (auditModel.status_survey.statusName) {
            "belum audit" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_orange)
                holder.tvValidateAudit.text = auditModel.status_survey.statusName
            }
            else -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_green)
                holder.tvValidateAudit.text = auditModel.status_survey.statusName
            }
        }

        holder.tokoId.text = auditModel.outlet_id
        holder.tokoName.text = auditModel.outlet_name
        //holder.tokoPhone.text = AuditModel.outlet_phone
//        val surveyDate = auditModel.date_survey
//        val deliveryDate = auditModel.date_delivery
//        val localDate = Date(surveyDate*1000)
//        val dateSurvey = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(surveyDate*1000)
//        val dateDelivery = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(deliveryDate*1000)
//        holder.surveyDate.text = dateSurvey

//        holder.itemView.setOnClickListener {
//            val intent = Intent(activity, DetailTokoActivity::class.java)
//            intent.putExtra(Constants.ID_TOKO, AuditModel.outlet_id)
//            activity.startActivity(intent)
//        }
    }

    inner class AuditHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tokoId = itemView.tvTokoId!!
        val tokoName = itemView.tvTokoName!!
        //val tokoPhone = itemView.tvTokoPhone!!
        //val surveyDate = itemView.tvSurveyDate!!
        val btnValidate = itemView.lyValidateStatus!!
        val tvValidateAudit = itemView.tvValidateStatus!!
        //val tvDeliveryPlan = itemView.tvDelivPlan!!
    }
}