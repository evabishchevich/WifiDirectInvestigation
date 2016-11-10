package org.thaliproject.p2p.wifidirectdemo

import android.app.IntentService
import android.content.Intent
import timber.log.Timber
import java.net.InetSocketAddress
import java.net.Socket

class DataTransferService : IntentService("DataTransferService") {

    companion object {
        val ACTION_SEND_DATA = "org.thaliproject.p2p.wifidirectdemo.SEND_DATA"
        val GO_ADDRESS = "go_address"
        val GROUP_PARTICIPANTS_IPS = "group_participants_ips"
        val GO_PORT = "go_port"
        val PORT = 8890
        private val data = "He_l_l_o_O"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null && intent.action == ACTION_SEND_DATA) {
            val address = intent.extras.getString(GO_ADDRESS)
            val port = PORT
            val socket = Socket()
            try {
                socket.connect(InetSocketAddress(address, port), 5000)

                Timber.d("connected")

                socket.outputStream.write(data.toByteArray())


            } finally {
                socket.close()
            }
        }
    }
}