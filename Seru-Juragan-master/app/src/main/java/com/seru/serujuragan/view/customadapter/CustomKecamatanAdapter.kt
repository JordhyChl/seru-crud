package com.seru.serujuragan.view.customadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.seru.serujuragan.R
import com.seru.serujuragan.data.response.DataDistrict
import kotlinx.android.synthetic.main.custom_spinner_area.view.*

/**
 * Created by Mahendra Dev on 02/01/2020
 */
class CustomKecamatanAdapter(c: Context, area: List<DataDistrict>) :
    ArrayAdapter<DataDistrict>(c,0,area) {

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

        view.tvSpnCustomArea.text = area?.district_name

        return view

    }
}