package com.seru.serujuragan.view.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View


/**
 * Created by adityaaugusta on 15/10/17.
 */
class KeyboardEditText : AppCompatEditText, View.OnTouchListener, InputBarListener.PanelListener {

    companion object {

        const val MODE_KEYBOARD = 1
        const val MODE_STICKER = 2
        const val MODE_ATTACHMENT = 3

        const val ACTION_CUT = 1
        const val ACTION_COPY = 2
        const val ACTION_PASTE = 3

    }

    private var keyboardListener: InputBarListener.KeyboardListener? = null
    private var isContextMenuShown: Boolean = false
    private var hasLongPressed: Boolean = false
    var mode  = 0
    private var action: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("ClickableViewAccessibility")
    fun setOnKeyboardListener(keyboardListener: InputBarListener.KeyboardListener) {
        this.keyboardListener = keyboardListener
        this.setOnTouchListener(this)
    }

    fun onKeyBack() {
        if (isContextMenuShown) {
            isContextMenuShown = false
        } else {
            if (keyboardListener != null)
                keyboardListener!!.onHidePanel(mode)
            mode = 0
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (keyboardListener != null)
            keyboardListener!!.onFocusChanged(this, focused)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            onKeyBack()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> if (hasLongPressed) {
                hasLongPressed = false
                isContextMenuShown = true
            } else {
                isContextMenuShown = false
                onKeyboardMode()
            }
        }
        return false
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)
        isContextMenuShown = false
        when (id) {
            android.R.id.cut -> action = ACTION_CUT
            android.R.id.copy -> action = ACTION_COPY
            android.R.id.paste -> action = ACTION_PASTE
        }
        if (keyboardListener != null)
            keyboardListener!!.onSelectedContextMenu(action)
        return consumed
    }

    override fun performLongClick(): Boolean {
        hasLongPressed = true
        return super.performLongClick()
    }

    override fun onKeyboardMode() {
        mode = MODE_KEYBOARD
        if (keyboardListener != null)
            keyboardListener!!.onShowPanel(mode)
    }

    override fun onStickerMode() {
        mode = MODE_STICKER
        if (keyboardListener != null)
            keyboardListener!!.onShowPanel(mode)
    }

    override fun onAttachmentMode() {
        mode = MODE_ATTACHMENT
        if (keyboardListener != null)
            keyboardListener!!.onShowPanel(mode)
    }

}