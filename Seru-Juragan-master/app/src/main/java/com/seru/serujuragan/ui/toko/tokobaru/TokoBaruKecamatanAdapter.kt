package com.seru.serujuragan.ui.toko.tokobaru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListNewTokoRes
import kotlinx.android.synthetic.main.item_kecamatan.view.*

class TokoBaruKecamatanAdapter(private val listKecamatan: MutableList<ListNewTokoRes>) : RecyclerView.Adapter<TokoBaruKecamatanAdapter.KecamatanHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KecamatanHolder {

        return KecamatanHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_kecamatan, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listKecamatan.size
    }

    override fun onBindViewHolder(holder: KecamatanHolder, position: Int) {

        val kecamatanModel = listKecamatan[position]
        val villageData = kecamatanModel.listaArea
        val c = holder.itemView.rvListVillage.context

        holder.tvDistrictHeader.text = kecamatanModel.district_name

        val childLayoutManager = LinearLayoutManager(c,LinearLayoutManager.HORIZONTAL, false)
        holder.itemView.rvListVillage.apply {
            layoutManager = childLayoutManager
            adapter = TokoBaruKelurahanAdapter(villageData)
        }
    }


    inner class KecamatanHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvDistrictHeader = view.tvDistrictName!!
    }
}