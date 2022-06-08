package com.example.touch_game


import java.io.IOException
import java.net.InetAddress
import java.net.Socket

class ServerInfo {
    companion object{
        const val ServerIp = "192.168.112.1"
        const val ServerPort = 9241
        lateinit var  connectSocket:Socket

        fun get(): Socket{
            try {
                val socketAddress = InetAddress.getByName(ServerIp)
                connectSocket = Socket(socketAddress, ServerPort)


            } catch (e: IOException) {
                println("소켓 오류 발생\n ${e}")
            }

            return connectSocket
        }
        fun returnSocket() : Socket{
            return connectSocket
        }

    }

}