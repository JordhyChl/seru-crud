package com.seru.serujuragan.view.customadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.DataVillages
import kotlinx.android.synthetic.main.custom_spinner_area.view.*

/**
 * Created by Mahendra Dev on 02/01/2020
 */
class CustomKelurahanAdapter(c: Context,
                             area: List<DataVillages>) :
    ArrayAdapter<DataVillages>(c,0,area) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {

        return this.createView(position, recycledView, parent)

    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {

        return this.createView(position, recycledView, parent)

    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val area = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.custom_spinner_area, parent, false
        )

        view.tvSpnCustomArea.text = area?.village_name

//        val view = recycledView ?: LayoutInflater.from(context).inflate(
//
//            R.layout.custom_spinner_area
//
//            parent,
//
//            false
//
//        )



        //view.moodImage.setImageResource(mood.image)

        //view.moodText.text = mood.description

        return view

    }
}