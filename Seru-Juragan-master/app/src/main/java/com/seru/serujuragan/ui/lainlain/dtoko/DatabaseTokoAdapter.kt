package com.seru.serujuragan.ui.lainlain.dtoko

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListDbTokoRes
import com.seru.serujuragan.util.Constants
import kotlinx.android.synthetic.main.item_database_toko.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DatabaseTokoAdapter(private val activity: ListDatabaseTokoActivity, private val mListDbToko: MutableList<ListDbTokoRes>) :
    RecyclerView.Adapter<DatabaseTokoAdapter.DbTokoHolder>() {

    fun addData(mListStatusToko: MutableList<ListDbTokoRes>){
        this.mListDbToko.clear()
        this.mListDbToko.addAll(mListStatusToko)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListDbToko.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DbTokoHolder {
        return DbTokoHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_database_toko, parent, false))
    }

    override fun getItemCount(): Int {
        return mListDbToko.size
    }

    override fun onBindViewHolder(holder: DbTokoHolder, position: Int) {
        val dbTokoModel = mListDbToko[position]

        holder.TokoId.text = dbTokoModel.id_uli
        holder.TokoName.text = dbTokoModel.nama_toko
        holder.TokoOwner.text = dbTokoModel.owner_toko
        val joinDate = dbTokoModel.tanggal_bergabung
        val localDate = Date(joinDate*1000)
        val date = SimpleDateFormat("dd - MMM - yyyy", Locale("in")).format(localDate)
        holder.tokoJoinDate.text = date

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailTokoDbActivity::class.java)
            intent.putExtra(Constants.ID_TOKO, dbTokoModel.id_seru)
            activity.startActivity(intent)
        }

    }

    inner class DbTokoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TokoId = itemView.tvTokoId!!
        val TokoName = itemView.tvTokoName!!
        val TokoOwner = itemView.tvTokoOwner!!
        val tokoJoinDate = itemView.tvJoinDate!!
    }
}