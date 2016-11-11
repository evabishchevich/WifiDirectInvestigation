//package org.thaliproject.p2p.wifidirectdemo
//
//import android.os.AsyncTask
//import android.os.Handler
//import android.os.Looper
//import android.widget.Toast
//import timber.log.Timber
//import java.io.BufferedReader
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.net.InetSocketAddress
//import java.net.ServerSocket
//import java.net.Socket
//
//class ClientAsyncTask(val wifiDirectInfo: WifiDirectInfo, val groupOwnerIp: String) :
//        AsyncTask<Void, Void, Void>() {
//
//    companion object {
//        val ACTION_SEND_DATA = "org.thaliproject.p2p.wifidirectdemo.SEND_DATA"
//        val GO_ADDRESS = "go_address"
//        val GROUP_PARTICIPANTS_IPS = "group_participants_ips"
//        val GO_PORT = "go_port"
//        val PORT = 8890
//        val CLIENT_PORT = 8893
//        private val data = "He_l_l_o_O"
//        private val dataForClient = "Hi_I!"
//    }
//
//    private lateinit var currentIp: String
//
//    override fun doInBackground(vararg params: Void?): Void? {
//        val address = groupOwnerIp
//        val port = PORT
//        val socket = Socket()
//        try {
//            socket.connect(InetSocketAddress(address, port), 5000)
//            wifiDirectInfo.ipInfo = IpInfo(socket.localAddress.hostAddress)
//            currentIp = socket.localAddress.hostAddress
//
//            Timber.d("current ip : $currentIp")
//            Timber.d("send data: connected")
//
//            socket.outputStream.write(data.toByteArray())
//
//            Timber.d("data: $data was written")
//            val res = readData(socket.inputStream)
//            //start server for clients
//            Thread(ServerRunnable()).start()
//
//            if (!res.isEmpty()) {
//                sendDataToClients(res)
//            }
//        } finally {
//            socket.close()
//        }
//        return null
//    }
//
//    private fun readData(from: InputStream): List<String> {
//        Timber.d("read data")
//        val reader = BufferedReader(InputStreamReader(from))
//        val ips = mutableListOf<String>()
//
//        var line: String? = null;
//        while ({ line = reader.readLine(); line }() != null) {
//            ips.add(line!!)
//        }
//        ips.forEach { Timber.d("Ip: $it") }
//        return ips.toList()
//    }
//
//    private fun sendDataToClients(clients: List<String>) {
//        for (ip in clients) {
//            Timber.d("Current ip = $currentIp")
//            Timber.d("client  ip = $ip")
//            if (!ip.equals(currentIp)) {
//                //TODO start new thread instead of doing in one
//                sendToClient(ip, dataForClient)
//            }
//        }
//    }
//
//    private fun sendToClient(clientIp: String, data: String) {
//        Timber.d("Send to client")
//        val port = CLIENT_PORT
//        val socket = Socket()
//        try {
//            socket.connect(InetSocketAddress(clientIp, port), 5000)
//            Timber.d("send data: connected")
//
//            socket.outputStream.write(data.toByteArray())
//
//            Timber.d("data: $data was written")
//            readData(socket.inputStream)
//
//        } finally {
//            socket.close()
//        }
//    }
//
//    class ServerRunnable() : Runnable {
//
//        override fun run() {
//            var serverSocket: ServerSocket? = null
//            try {
//                serverSocket = ServerSocket(CLIENT_PORT)
//                Timber.d("Socket opened")
//                val client = serverSocket.accept()
//                Timber.d("Connection done")
//                val dataFromClient = readData(client.inputStream)
//                val peerIp = client.inetAddress.hostAddress
//                Timber.d("data from client 1st $dataFromClient")
//
//                client.outputStream.write("YO!".toByteArray())
//
//                client.outputStream.flush()
//                client.outputStream.close()
//            } finally {
//                serverSocket?.close()
//            }
//        }
//
//        private fun readData(from: InputStream): String {
//            Timber.d("read data")
//            val buffer = ByteArray(1024)
//            val result = mutableListOf<ByteArray>()
//            while (from.available() > 0) {
//                from.read(buffer)
//                Timber.d("read: ${String(buffer)}")
//                result.add(buffer.copyOf())
//            }
//            val sb = StringBuilder()
//            for (b in result) {
//                sb.append(String(b))
//            }
//            Timber.d("read data finished: ${sb.toString()}")
//            return sb.toString()
//        }
//
//        private fun sendToMainThread(){
//            Handler(Looper.getMainLooper()).post { Toast.makeText() }
//        }
//    }
//
//
//}