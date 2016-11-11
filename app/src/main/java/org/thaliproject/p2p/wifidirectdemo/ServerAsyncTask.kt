package org.thaliproject.p2p.wifidirectdemo

import android.os.AsyncTask
import timber.log.Timber
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList

class ServerAsyncTask : AsyncTask<Void, String, Void>() {

    val groupIps = CopyOnWriteArrayList<String>()

    override fun doInBackground(vararg params: Void?): Void {
        val serverSocket = ServerSocket(ClientAsyncTask.PORT)
        Timber.d("Socket opened")
        while (true) {
            val client = serverSocket.accept()
            Thread(ClientRunnable(client)).start();
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        Timber.d("data from client ${values[0]}")
    }

    private fun readData(from: InputStream): String {
        Timber.d("read data")
        val buffer = ByteArray(1024)
        val result = mutableListOf<ByteArray>()
        Thread.sleep(500L)
        while (from.available() > 0) {
            from.read(buffer)
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

    inner class ClientRunnable(val client: Socket) : Runnable {

        override fun run() {
            groupIps.add(client.inetAddress.hostAddress)
            Timber.d(client.inetAddress.hostAddress)
            Timber.d("Connection done")
            val dataFromClient = readData(client.inputStream)
            Timber.d("data from client 1st $dataFromClient")
//            client.outputStream.write("Accepted!".toByteArray())
            Timber.d("ip list size = ${groupIps.size}")
            for (ip in groupIps) {
                Timber.d("write $ip")
                client.outputStream.write((ip + "\n").toByteArray())

            }
            Thread.sleep(1000L)
            client.outputStream.flush()
            client.outputStream.close()
            publishProgress(dataFromClient)
        }
    }

}