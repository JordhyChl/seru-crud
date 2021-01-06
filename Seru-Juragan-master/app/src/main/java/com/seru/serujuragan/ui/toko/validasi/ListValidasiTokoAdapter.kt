package com.seru.serujuragan.ui.toko.validasi

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListTokoRes
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_toko_validasi.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 30/12/2019
 */
class ListValidasiTokoAdapter(private val activityList: ListValidasiTokoActivity, private val mListValidasi: MutableList<ListTokoRes>) :
    RecyclerView.Adapter<ListValidasiTokoAdapter.ValidasiHolder>() {

    private val TAG = ListValidasiTokoAdapter::class.java.simpleName

    fun addData(mListValidasi: MutableList<ListTokoRes>){
        this.mListValidasi.clear()
        this.mListValidasi.addAll(mListValidasi)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListValidasi.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValidasiHolder {
        return ValidasiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toko_validasi, parent, false))
    }

    override fun getItemCount(): Int {
        return mListValidasi.size
    }

    override fun onBindViewHolder(holder: ValidasiHolder, position: Int) {

        val validasiModel = mListValidasi[position]
        Log.e(TAG,"toko id : ${validasiModel.outlet_id}")
        Log.i(TAG,"toko status : ${validasiModel.status_survey.statusName}")

        when (validasiModel.status_survey.statusName) {
            "Tunda" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_orange)
                holder.tvValidateStatus.text = validasiModel.status_survey.statusName
            }
            "Approve Juragan" -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_green)
                holder.tvValidateStatus.text = validasiModel.status_survey.statusName
            }
            else -> {
                holder.btnValidate.setBackgroundResource(R.drawable.background_rectangle_round_yellow)
                holder.tvValidateStatus.text = validasiModel.status_survey.statusName
            }
        }

        holder.tokoId.text = validasiModel.outlet_id
        holder.tokoName.text = validasiModel.outlet_name
        val surveyDate = validasiModel.date_survey.toLong()
        val localDate = Date(surveyDate*1000)
        val date = SimpleDateFormat("dd - MMM - yyyy",Locale("in")).format(localDate)
        holder.surveyDate.text = date
        val dis = validasiModel.shop_distance.toString()
        Log.i(TAG,"distance val : $dis")
        if (dis=="0.0") {
            holder.lyShopDistance.visibility = GONE
        }else{
            holder.lyShopDistance.visibility = VISIBLE
            holder.shopDistance.text = dis.take(4)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activityList, ValidasiTokoActivity::class.java)
            intent.putExtra(Constants.ID_TOKO, validasiModel.outlet_id)
            activityList.startActivity(intent)
        }
    }

    inner class ValidasiHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tokoId = itemView.tvTokoId!!
        val tokoName = itemView.tvTokoName!!
        val surveyDate = itemView.tvSurveyDate!!
        var lyShopDistance = itemView.lyDistance!!
        val shopDistance = itemView.tvShopDistance!!
        val btnValidate = itemView.lyValidateStatus!!
        val tvValidateStatus = itemView.tvValidateStatus!!

    }
}