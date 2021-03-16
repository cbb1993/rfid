package com.app.rfidmaster.view.popwindow

import android.R.attr.name
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.app.rfidmaster.R
import com.app.rfidmaster.view.seletor.PickerScrollView
import com.app.rfidmaster.view.seletor.Pickers


/**
 * @author:       cbb
 * @date:         2020/11/24 18:22
 * @description:  test
 */
class SelectPopWindow(val view: View, val listener: OnSelectClickListener) : PopupWindow(
    view,
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.MATCH_PARENT,
    true
) {
    companion object {
        fun get(context: Context, listener: OnSelectClickListener): SelectPopWindow {
            val inflate = LayoutInflater.from(context).inflate(R.layout.pop_seletor, null)
            return SelectPopWindow(inflate, listener)
        }
    }

    private lateinit var pickerscrlllview: PickerScrollView
    private val list = ArrayList<Pickers>()

    init {
        // 设置PopupWindow的背景
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        isOutsideTouchable = false
        // 设置PopupWindow是否能响应点击事件
        isTouchable = true
        // 设置动画
        animationStyle = R.style.pop_anim_style

        initView()
    }

    private fun initView() {
        pickerscrlllview = view.findViewById(R.id.pickerscrlllview)

        view.findViewById<View>(R.id.cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<View>(R.id.confirm).setOnClickListener {
            val currentSelected = pickerscrlllview.currentSelected
            val pickers = list[currentSelected]
            listener.select(pickers)
            dismiss()
        }
    }

    fun getListSize() = list.size

    fun setData(dataList: List<String>) {
        list.clear()
        for (i in dataList.indices) {
            list.add(Pickers(dataList[i], i))
        }
        // 设置数据，默认选择第一条
        pickerscrlllview.setData(list)
        pickerscrlllview.setSelected(0)
    }


    fun show(view: View) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    interface OnSelectClickListener {
        fun select(pickers: Pickers)
    }
}