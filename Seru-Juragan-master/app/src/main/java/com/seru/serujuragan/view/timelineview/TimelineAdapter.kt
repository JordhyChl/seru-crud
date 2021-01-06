package com.seru.serujuragan.view.timelineview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.History
import com.seru.serujuragan.data.response.Status
import com.seru.serujuragan.view.timelineview.extentions.formatDateTime
import com.seru.serujuragan.view.timelineview.extentions.getColorCompat
import com.seru.serujuragan.view.timelineview.extentions.setGone
import com.seru.serujuragan.view.timelineview.extentions.setVisible
import com.seru.serujuragan.view.timelineview.utils.VectorDrawableUtils
import kotlinx.android.synthetic.main.item_timeline_horizontal.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mahendra Dev on 26/12/2019
 */
class TimelineAdapter(private val mTimelineList: List<History>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater
    val TAG = TimelineAdapter::class.java.simpleName

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return ViewHolder(mLayoutInflater.inflate(R.layout.item_timeline_horizontal, parent, false))
    }

    override fun getItemCount(): Int {
        return mTimelineList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeLineModel = mTimelineList[position]
        Log.e(TAG,"timeline : ${timeLineModel.id_status}")
        when (timeLineModel.status_active) {
//            2 -> {
//                setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_grey_500)
//                setLine(holder, R.color.material_grey_500, R.color.material_grey_500)
//                holder.message.setTextColor(getColorCompat(R.color.material_grey_500))
//            }
            1 -> {
                setMarker(holder, R.drawable.ic_marker_active, R.color.colorPrimary)
                setLine(holder, R.color.colorPrimary, R.color.material_grey_500)
            }
            else -> {
                setMarker(holder, R.drawable.ic_marker, R.color.colorPrimary)
                setLine(holder, R.color.colorPrimary, R.color.colorPrimary)
            }
        }

        val timelineDate = timeLineModel.date_status*1000
        //val localDate = Date(timelineDate*1000)
        Log.e("List","localdate $timelineDate")
        val date = SimpleDateFormat("dd/MM/yy", Locale("in")).format(timelineDate)
        if (date.isNotEmpty()) {
            holder.date.setVisible()
            holder.date.text = date
        } else
            holder.date.setGone()

        holder.message.text = timeLineModel.status_value
    }

    private fun setMarker(holder: ViewHolder, drawableResId: Int, colorFilter: Int) {
        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, colorFilter))
    }

    private fun setLine(holder: ViewHolder, startColor: Int, endColor:Int){
        holder.timeline.setStartLineColor(ContextCompat.getColor(holder.itemView.context, startColor),0)
        holder.timeline.setEndLineColor(ContextCompat.getColor(holder.itemView.context, endColor),0)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val date = itemView.text_timeline_date!!
        val message = itemView.text_timeline_title!!
        val timeline = itemView.timeline!!

        init {
            timeline.initLine(0)
        }
    }
}