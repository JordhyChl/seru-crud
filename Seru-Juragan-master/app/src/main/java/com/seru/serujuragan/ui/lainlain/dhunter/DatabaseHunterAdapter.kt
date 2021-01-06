package com.seru.serujuragan.ui.lainlain.dhunter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.ListDbHunterRes
import kotlinx.android.synthetic.main.item_database_hunter.view.*

/**
 * Created by Mahendra Dev on 01/02/2020
 */
class DatabaseHunterAdapter(private val activity: ListDatabaseHunterActivity, private val mListDbHunter: MutableList<ListDbHunterRes>) :
    RecyclerView.Adapter<DatabaseHunterAdapter.DbHunterHolder>() {

    fun addData(mListStatusToko: MutableList<ListDbHunterRes>){
        this.mListDbHunter.clear()
        this.mListDbHunter.addAll(mListStatusToko)
        notifyDataSetChanged()
    }

    fun clearAllData(){
        this.mListDbHunter.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DbHunterHolder {
        return DbHunterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_database_hunter, parent, false))
    }

    override fun getItemCount(): Int {
        return mListDbHunter.size
    }

    override fun onBindViewHolder(holder: DbHunterHolder, position: Int) {
        val dbHunterModel = mListDbHunter[position]

        Log.e("TAG presenter","${dbHunterModel.task_summary.approve_task}")

        holder.hunterId.text = dbHunterModel.id_hunter
        holder.hunterName.text = dbHunterModel.hunter_name
        holder.itemDeal.text = dbHunterModel.task_summary.approve_task.toString()
        holder.itemPending.text = dbHunterModel.task_summary.pending_task.toString()
        holder.itemCancel.text = dbHunterModel.task_summary.cancel_task.toString()
        holder.itemTotal.text = dbHunterModel.task_summary.total_task.toString()

    }

    inner class DbHunterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hunterId = itemView.tvHunterId!!
        val hunterName = itemView.tvHunterName!!
        val itemDeal = itemView.tvItemDeal!!
        val itemPending = itemView.tvItemTunda!!
        val itemCancel = itemView.tvItemBatal!!
        val itemTotal = itemView.tvItemVisit!!
    }
}