package com.seru.serujuragan.view.customview

/**
 * Created by Aditya Augusta on 10/15/17.
 */
abstract class InputBarListener {

    interface KeyboardListener {
        fun onFocusChanged(keyboardEditText: KeyboardEditText, hasFocus: Boolean)
        fun onSelectedContextMenu(action: Int)
        fun onShowPanel(mode: Int)
        fun onHidePanel(mode: Int)
    }

    interface PanelListener {
        fun onKeyboardMode()
        fun onStickerMode()
        fun onAttachmentMode()
    }

}
