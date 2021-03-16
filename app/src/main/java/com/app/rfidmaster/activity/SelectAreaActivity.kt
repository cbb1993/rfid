package com.app.rfidmaster.activity

import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import com.app.rfidmaster.R
import com.app.rfidmaster.activity.base.BaseActivity
import com.app.rfidmaster.bean.AreaBean
import com.app.rfidmaster.net.RequestApi
import com.app.rfidmaster.util.Constants
import com.app.rfidmaster.util.ToastUtil
import com.app.rfidmaster.view.popwindow.SelectPopWindow
import com.app.rfidmaster.view.seletor.Pickers
import kotlinx.android.synthetic.main.activity_select_area.*


/**
 * @author:       cbb
 * @date:         2020/12/20 15:49
 * @description:
 */
class SelectAreaActivity : BaseActivity() {

    override fun initLayout(): Int = R.layout.activity_select_area
    private var firstIndex = -1
    private var secondIndex = -1
    private var hospital: String? = null
    private var subject: String? = null
    private val list = ArrayList<AreaBean>()
    private lateinit var firstPop: SelectPopWindow
    private lateinit var secondPop: SelectPopWindow

    override fun initView() {
        super.initView()
        initPop()

        btn_hospital.setOnClickListener {
            if(firstPop.getListSize() == 0){
                ToastUtil.show("数据为空")
                return@setOnClickListener
            }
            firstPop.show(btn_hospital)
        }

        btn_subject.setOnClickListener {
            if(secondPop.getListSize() == 0){
                ToastUtil.show("数据为空")
                return@setOnClickListener
            }
            if (hospital == null) {
                ToastUtil.show("请先选择医院")
                return@setOnClickListener
            }
            secondPop.show(btn_subject)
        }

        btn_next.setOnClickListener {
            if (validate()) {
                val intent = Intent(this@SelectAreaActivity, ReadRFIDActivity::class.java)
                intent.putExtra(Constants.customerId,list[firstIndex].id)
                intent.putExtra(Constants.regionId,list[firstIndex].listRegister[secondIndex].id)
                intent.putExtra(Constants.cusName,list[firstIndex].cusName)
                intent.putExtra(Constants.regionName,list[firstIndex].listRegister[secondIndex].regionName)
                startActivity(intent)
            }
        }
        RequestApi.getInstance().getCustomer(this, object : RequestApi.ResultList<AreaBean> {
            override fun success(t: List<AreaBean>) {
                list.addAll(t)
                firstPop.setData(makeData(-1))
            }
        })
    }

    private fun initPop() {
        // 第一选择
        firstPop =
            SelectPopWindow.get(this@SelectAreaActivity,
                object : SelectPopWindow.OnSelectClickListener {
                    override fun select(pickers: Pickers) {
                        firstIndex = pickers.index
                        tv_hospital.text = pickers.showContent
                        // 不是一个医院
                        if (pickers.showContent != hospital) {
                            secondIndex = -1
                            subject = null
                            tv_subject.setText(R.string.select_subject)
                            // 设置科目数据
                            secondPop.setData(makeData(pickers.index))
                        }
                        hospital = pickers.showContent
                    }
                })
        //第二选择
        secondPop =
            SelectPopWindow.get(this@SelectAreaActivity,
                object : SelectPopWindow.OnSelectClickListener {
                    override fun select(pickers: Pickers) {
                        secondIndex = pickers.index
                        tv_subject.text = pickers.showContent
                        subject = pickers.showContent
                    }
                })
    }

    private fun makeData(index: Int): List<String> {
        val dataList = ArrayList<String>()
        // 第一层
        if (index == -1) {
            list.forEach {
                dataList.add(it.cusName)
            }
        } else {
            val listRegister = list[index].listRegister
            listRegister.forEach {
                dataList.add(it.regionName)
            }
        }
        return dataList
    }

    private fun validate(): Boolean {
        if (hospital == null) {
            ToastUtil.show("请选择医院")
            return false
        }
        if (subject == null) {
            ToastUtil.show("请选择科室")
            return false
        }
        return true
    }


    private var firstClick: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onAppExit()
            return true
        }
        return false
    }

    private fun onAppExit() {
        if (System.currentTimeMillis() - firstClick > 2000L) {
            firstClick = System.currentTimeMillis()
            ToastUtil.show("再按一次退出")
            return
        }
        finish()
    }
}