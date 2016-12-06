package org.thaliproject.p2p.wifidirectdemo.test

import org.thaliproject.p2p.wifidirectdemo.GroupCredits

interface TestContract {

    interface View {

        fun showTotalDuration(duration: String)

        fun setDeviceName(name: String)

        fun showGroupCredits(groupCredits: GroupCredits)

        fun disableStartServer()

        fun showLoading()

    }

    interface Presenter {

        fun onStartServerClicked()

        fun onStartClientClicked()
    }
}