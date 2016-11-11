package org.thaliproject.p2p.wifidirectdemo.service.messaging

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import timber.log.Timber
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

class MessagingServer(val ctx: Context) : AsyncTask<Void, Pair<String, String>, Unit>() {

    private companion object {
        val MESSAGE_READ_TIMEOUT = 5000
    }

    override fun doInBackground(vararg params: Void?) {
        var serverSocket: ServerSocket? = null
        try {
            while (true) {
                serverSocket = ServerSocket(GroupSettings.MESSAGING_SERVER_PORT)
                Timber.d("Messaging socket is opened")
                while (true) {
                    val client = serverSocket.accept()
                    client.soTimeout = MESSAGE_READ_TIMEOUT
                    Thread(MessageReadRunnable(client)).start();
                }
            }
        } finally {
            serverSocket?.close()
        }
    }

    override fun onProgressUpdate(vararg values: Pair<String, String>) {
        val data = "${values[0].first}: ${values[0].second}"
        Timber.i("Data from client: $data")
        Toast.makeText(ctx, data, Toast.LENGTH_LONG).show()
    }

    inner class MessageReadRunnable(val client: Socket) : Runnable {
        override fun run() {
            Timber.d("Connection done")
            val dataFromClient = readData(client.inputStream)
            val peerIp = client.inetAddress.hostAddress
            publishProgress(Pair(peerIp, dataFromClient))
            //TODO move it into separate messaging
//                client.outputStream.write("YO!".toByteArray())
//
//                client.outputStream.flush()
//                client.outputStream.close()

        }

        private fun readData(from: InputStream): String {
            Timber.d("read data")
            val buffer = ByteArray(1024)
            val result = mutableListOf<ByteArray>()
            while (from.read(buffer) != -1) {
                Timber.d("read: ${String(buffer)}")
                result.add(buffer.copyOf())
            }
            val sb = StringBuilder()
            for (b in result) {
                sb.append(String(b))
            }
            Timber.d("read data finished: ${sb.toString()}")
            return sb.toString()
        }

    }
}