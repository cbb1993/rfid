package com.app.rfidmaster.activity.base

/**
 * @author:       cbb
 * @date:         2020/11/23 16:55
 * @description:  基类中定义的常用方法
 */
interface IBaseActivity {
    /**
     * 绑定页面layout
     * */
    fun initLayout(): Int
    /**
     * 初始化view
     * */
    fun initView()
}