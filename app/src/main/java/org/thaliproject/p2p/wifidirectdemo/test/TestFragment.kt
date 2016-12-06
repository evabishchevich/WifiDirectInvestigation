package org.thaliproject.p2p.wifidirectdemo.test

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.BaseFragment
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.GroupCredits
import org.thaliproject.p2p.wifidirectdemo.R

class TestFragment : BaseFragment(), TestContract.View {

    private lateinit var tvTotalDuration: TextView
    private lateinit var tvGroupCredits: TextView
    private lateinit var presenter: TestContract.Presenter


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_test, container, false)
        view?.findViewById(R.id.test_btn_server)?.setOnClickListener { startServer() }
        view?.findViewById(R.id.test_btn_client)?.setOnClickListener { startClient() }
        tvTotalDuration = view?.findViewById(R.id.test_tv_total_duration) as TextView
        tvGroupCredits = view?.findViewById(R.id.test_tv_group_credits) as TextView
        presenter = TestPresenter(this, messagingService, wifiService, this)
        return view
    }

    private fun startServer() {
        presenter.onStartServerClicked()
    }

    private fun startClient() {
        presenter.onStartClientClicked()
    }

    override fun showTotalDuration(duration: String) {
        activity.runOnUiThread { tvTotalDuration.text = duration }
    }

    override fun setDeviceName(name: String) {
        val m = wifiDirectState.wifiDirectInfo.wifiP2pManager.javaClass.getMethod(
                "setDeviceName",
                *arrayOf(WifiP2pManager.Channel::class.java, String::class.java, WifiP2pManager.ActionListener::class.java))

        m.invoke(wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel,
                name, DefaultActionListener("Device name changed", "Device name NOT changed"))
    }

    override fun showGroupCredits(groupCredits: GroupCredits) {
        tvGroupCredits.text = "Group credits: \n Network name: ${groupCredits.networkName}\n Password: ${groupCredits.passphrase}"
    }

    override fun disableStartServer() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {

    }

    override fun disableStartClient() {
        view?.findViewById(R.id.test_btn_client)?.isEnabled = false
    }

    override fun enableStartClient() {
        view?.findViewById(R.id.test_btn_client)?.isEnabled = true
    }
}