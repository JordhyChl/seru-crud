package com.seru.serujuragan.ui.kabinet.mandiri

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListCabinetOutletRes
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_toko_asal.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 08/05/2020.
 */
class ListTokoAsalAdapter (private val activity: ListTokoAsalActivity, private val mListTokoAsal: MutableList<ListCabinetOutletRes>):
    RecyclerView.Adapter<ListTokoAsalAdapter.TokoAsalHolder>() {

    fun addData(mListStatusCabinet: MutableList<ListCabinetOutletRes>){
        this.mListTokoAsal.clear()
        this.mListTokoAsal.addAll(mListStatusCabinet)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListTokoAsal.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListTokoAsalAdapter.TokoAsalHolder {
        return TokoAsalHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_toko_asal, parent, false))
    }

    override fun getItemCount(): Int {
        return mListTokoAsal.size
    }

    override fun onBindViewHolder(
        holder: ListTokoAsalAdapter.TokoAsalHolder,
        position: Int
    ) {
        val tokoAsalModel = mListTokoAsal[position]

        holder.outletId.text = tokoAsalModel.outlet_id
        holder.outletName.text = tokoAsalModel.outlet_name
        val joinDate = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(tokoAsalModel.join_date)
        holder.joinDate.text = joinDate
        holder.phoneNumber.text = tokoAsalModel.outlet_phone
        holder.cabinetCode.text = tokoAsalModel.cabinet_code

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailTokoAsalActivity::class.java)
            intent.putExtra(Constants.ID_TOKO_ASAL, tokoAsalModel.outlet_id)
            activity.startActivity(intent)
        }
    }

    inner class TokoAsalHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val outletId = itemView.tvTokoId!!
        val outletName = itemView.tvTokoName!!
        val phoneNumber = itemView.tvPhone!!
        val joinDate = itemView.tvJoinDate!!
        val cabinetCode = itemView.tvCabinetCode!!
    }
}