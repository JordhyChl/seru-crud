package com.seru.serujuragan.ui.toko.tokobaru

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.AreaDetail
import com.seru.serujuragan.ui.toko.validasi.ListValidasiTokoActivity
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_kelurahan.view.*

class TokoBaruKelurahanAdapter(private val listKelurahan: MutableList<AreaDetail>) : RecyclerView.Adapter<TokoBaruKelurahanAdapter.KelurahanHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelurahanHolder {

        return KelurahanHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_kelurahan, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listKelurahan.size
    }

    override fun onBindViewHolder(holder: KelurahanHolder, position: Int) {

        val kelurahanModel = listKelurahan[position]
        holder.tvVillageName.text = kelurahanModel.villageName
        holder.tvDealTotal.text = kelurahanModel.deal.toString()
        holder.tvTundaTotal.text = kelurahanModel.tunda.toString()

        Log.e("TAG kelurahan","${kelurahanModel.villageName}")

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ListValidasiTokoActivity::class.java)
            intent.putExtra(Constants.ID_AREA_FILTER, kelurahanModel.id_village)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class KelurahanHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val tvVillageName = view.tvVillageName!!
        val tvDealTotal = view.tvDealTotal!!
        val tvTundaTotal = view.tvTundaTotal!!
    }
}