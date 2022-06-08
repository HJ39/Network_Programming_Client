package com.example.touch_game

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.InetAddress
import java.net.Socket

class MainActivity : Activity() {

    lateinit var loginSocket : Socket

    lateinit var outputStream: OutputStream
    lateinit var inputStream: InputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnGameStart.setOnClickListener {
            var nickname = edtLogin.text.toString()
            var inputData: String = ""

            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        loginSocket = ServerInfo.get()
                        outputStream = loginSocket.getOutputStream()

                        outputStream.write(nickname.toByteArray(Charsets.UTF_8))
                        outputStream.flush()

                        inputStream = loginSocket.getInputStream()

                        var test = DataInputStream(inputStream)

                        var buffer = ByteArray(10)
                        var size = test.read(buffer)

                        inputData = String(buffer, Charsets.UTF_8).replace("\\n","")
                    } catch (e: IOException) {
                        println("입출력 오류발생함 : ${e}")
                    }
                }.join()

                val intent = Intent(applicationContext, game_screen::class.java)
                Toast.makeText(applicationContext,inputData,Toast.LENGTH_SHORT).show()
                intent.putExtra("gradeResult", inputData)
                startActivity(intent)

            }
        }
    }
}