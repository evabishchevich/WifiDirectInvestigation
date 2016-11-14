package org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import org.thaliproject.p2p.wifidirectdemo.service.messaging.Message
import timber.log.Timber
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList


//TODO make it just thread, because we can't execute any other async task while executing this one
class GroupIpsProvider(val ctx: Context) : AsyncTask<Void, String, Unit>() {
    companion object {
        private val READ_TIMEOUT = 5000
    }

    val groupIps = CopyOnWriteArrayList<String>()

    override fun doInBackground(vararg params: Void?) {
        var serverSocket: ServerSocket? = null
        try {
            while (true) {
                serverSocket = ServerSocket(GroupSettings.GROUP_IPS_PROVIDER_PORT)
                Timber.d("Group ip provider's socket is opened")
                while (true) {
                    val client = serverSocket.accept()
                    client.soTimeout = READ_TIMEOUT
                    publishProgress("Connected ${client.inetAddress.hostAddress}")
                    Thread(Client(client)).start();
                }
            }
        } finally {
            serverSocket?.close()
        }
    }

    override fun onProgressUpdate(vararg values: String) {
        Toast.makeText(ctx, values[0], Toast.LENGTH_SHORT).show()
        //TODO add to connected peers list
    }

    private fun readData(from: InputStream): Boolean {
        Timber.d("read data")
        val buffer = ByteArray(1024)
        while (from.read(buffer) != -1) {
            //TODO change to string without zeros
            val clientData = String(buffer)
            Timber.d("read: $clientData")
//            if (clientData == Message.GET_IPS.data) {
            return true
//            }
        }
        return false
    }

    inner class Client(val client: Socket) : Runnable {

        override fun run() {
            if (!groupIps.contains(client.inetAddress.hostAddress)) {
                groupIps.add(client.inetAddress.hostAddress)
            }
            Timber.d(client.inetAddress.hostAddress)
            Timber.d("Connection done")
            val giveIps = readData(client.inputStream)
            if (giveIps) {
                Timber.d("ip list size = ${groupIps.size}")
                for (ip in groupIps) {
                    Timber.d("write $ip")
                    client.outputStream.write((ip + "\n").toByteArray())

                }
            }
            client.outputStream.flush()
            client.outputStream.close()
            client.close()
        }
    }

}