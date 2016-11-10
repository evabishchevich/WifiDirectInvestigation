package org.thaliproject.p2p.wifidirectdemo

import android.os.AsyncTask
import timber.log.Timber
import java.io.InputStream
import java.net.ServerSocket

class ServerAsyncTask : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        val serverSocket = ServerSocket(DataTransferService.PORT)
        Timber.d("Socket opened")
        val client = serverSocket.accept()
        Timber.d("Connection done")
        val dataFromClient = readData(client.inputStream)
        client.outputStream.write("Accepted!".toByteArray())
        return dataFromClient
    }

    override fun onPostExecute(result: String?) {
        Timber.d("data from client $result")
    }

    private fun readData(from: InputStream): String {
        val buffer = ByteArray(1024)
        val result = mutableListOf<ByteArray>()
        while (from.read(buffer) != -1) {
            result.add(buffer.copyOf())
        }
        val sb = StringBuilder()
        for (b in result) {
            sb.append(String(b))
        }
        return sb.toString()
    }
}