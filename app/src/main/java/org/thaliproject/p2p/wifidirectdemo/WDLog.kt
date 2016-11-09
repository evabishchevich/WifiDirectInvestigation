package org.thaliproject.p2p.wifidirectdemo

import timber.log.Timber

object WDLog {

    init {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement?): String {
                return super.createStackElementTag(element) + ":" + element?.lineNumber
            }
        })
    }

    var CURRENT_LEVEL: LEVEL = LEVEL.DEBUG

    fun v(msg: String) {
        Timber.v(msg)
    }

    fun d(msg: String) {
        Timber.d(msg)
    }

    fun i(msg: String) {
        Timber.i(msg)
    }

    fun w(msg: String) {
        Timber.w(msg)
    }

    fun e(msg: String) {
        Timber.e(msg)
    }

    fun wtf(msg: String) {
        Timber.wtf(msg)
    }

    enum class LEVEL {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        TERRIBLE_ERROR
    }

}