package com.app.rfidmaster.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.app.rfidmaster.net.Fault
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Error

/**
 * @author:       cbb
 * @date:         2020/12/2 12:33
 * @description:  协程类
 */
class ScopeUtil {
    companion object {
        private var instance: ScopeUtil = ScopeUtil()
        fun get(): ScopeUtil {
            return instance
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    fun launch(
        block: suspend () -> Unit
    ) = MainScope().launch {
        try {
            block()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun launch(
        block: suspend () -> Unit,
        error: suspend (Fault) -> Unit
    ) = MainScope().launch {
        try {
            block()
        } catch (e: Throwable) {
            Log.e("Throwable", "----------$e")
            error(Fault(e))
        }
    }

    fun launch(
        block: suspend () -> Unit,
        error: suspend (Fault) -> Unit,
        complete: suspend () -> Unit
    ) = MainScope().launch {
        try {
            block()
        } catch (e: Throwable) {
            error(Fault(e))
        } finally {
            complete()
        }
    }
}