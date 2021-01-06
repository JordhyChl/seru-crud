package com.seru.serujuragan.ui.base

/**
 * Created by Mahendra Dev on 17/12/2019
 */
open class BaseMainFragment : androidx.fragment.app.Fragment() {

//    fun setupToolbar(activity: MainActivity, toolbar: Toolbar, page: Int) {
//        toolbar.setTitleTextAppearance(activity, R.style.ToolbarTitle_Primary)
//        toolbar.setTitle(MainConfig.data[page].title)
//        if (MainConfig.data[page].menu != MainConfig.UNSPECIFIED)
//            toolbar.inflateMenu(MainConfig.data[page].menu)
//        activity.setSupportActionBar(toolbar)
//    }

    open fun onRefreshFragment() {}

    open fun onShowFragment() {}

    open fun onHideFragment() {}

    open fun onNetworkChanged(isConnected: Boolean) {}

}
