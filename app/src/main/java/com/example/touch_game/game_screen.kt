package com.example.touch_game

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import kotlinx.android.synthetic.main.activity_game_screen.*
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class game_screen : Activity() {

    val imgList = arrayOf(R.drawable.kuma00,R.drawable.kuma01,R.drawable.kuma02)
    lateinit var outputStream: OutputStream
    lateinit var inputStream: InputStream
    lateinit var sock :Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        val getIntent = intent
        var gradeResult = getIntent.getStringExtra("gradeResult")
        var nickname = gradeResult

        var a= StringTokenizer(nickname,"\n")
        nickname = a.nextToken().toString()

        var buttonClickCount =0

        var buffer = ByteArray(100)


        println("gradeReuslt = "+gradeResult)

        imgbtn.setOnClickListener {

            if(buttonClickCount == 1){     // 왜 2번 실행 되는거지;
                progressbar.progress = 0
                CoroutineScope(Dispatchers.Default).launch {        //프로그레스바 10초동안 동작
                    for(i in 0..9) {
                        progressbar.progress = progressbar.progress +1
                        SystemClock.sleep(1000)
                    }
                }.start()
            }

            if(progressbar.progress ==10) {
                imgbtn.isEnabled = false
                progressbar.progress =0
                btnSend.visibility = View.VISIBLE

                var dlg = AlertDialog.Builder(this@game_screen)

                dlg.setTitle("점수를 등록하시겠습니까?")
                dlg.setPositiveButton("예") { dialog, which ->
                    imgbtn.isEnabled = true
                    textView1.visibility = View.VISIBLE
                    btnSend.visibility = View.GONE
                    buttonClickCount =0


                    CoroutineScope(Dispatchers.Main).launch {
                        CoroutineScope(Dispatchers.IO).launch {
                            //tcp  송신 수신 구현하기
                            try {
                                sock = ServerInfo.returnSocket()
                                outputStream = sock.getOutputStream()

                                var output = nickname +"#"+ textGrade.text.toString()
                                println(output)
                                outputStream.write(
                                   output.toByteArray(Charsets.UTF_8)
                                )
                                outputStream.flush()


                                inputStream = sock.getInputStream()
                                var test = DataInputStream(inputStream)
                                test.read(buffer)

                            } catch (e: IOException) {
                                println("오류 발생 : ${e}")
                            }
                        }.join()
                    }


                }

                dlg.setNegativeButton("아니오"){dialog, which ->
                    imgbtn.isEnabled = true
                    textView1.visibility = View.VISIBLE
                    btnSend.visibility = View.GONE
                    buttonClickCount =0
                }
                dlg.show()
            }

            textView1.visibility = View.INVISIBLE
            textGrade.text = buttonClickCount.toString()
            buttonClickCount++

            when(buttonClickCount%3){
                0 -> imgbtn.setImageResource(imgList[0])
                1 -> imgbtn.setImageResource(imgList[1])
                2 -> imgbtn.setImageResource(imgList[2])
            }

        }


        imgbtnStart.setOnClickListener {
            textView1.visibility = View.VISIBLE
            imgbtn.visibility = View.VISIBLE
            imgbtnStart.visibility = View.GONE
        }


        //서버에서 점수표 가져와서 보여줌
        btnGrade.setOnClickListener {

            gradeResult = String(buffer, Charsets.UTF_8).replace("\\n","")

            var a= StringTokenizer(gradeResult,"\n")

            gradeResult = a.nextToken().toString()

            var userList = Vector<String>()
            var index1 =0
            var index2 =0
            var nameList = Vector<String>()
            var gradeList = Vector<String>()

            var st = StringTokenizer(gradeResult,"|")   //사람 수 분류
            while(st.hasMoreTokens()) {
                userList.add( st.nextToken().toString())
                index1++
            }

            for(i in 0..index1-1) {
                if (userList[i].indexOf("#" ) >= 0) {
                    var st = StringTokenizer(userList[i], "#")   //점수 닉네임 분류
                    while (st.hasMoreTokens()) {
                        nameList.add(st.nextToken().toString())
                        gradeList.add(st.nextToken().toString())
                        index2++
                    }
                }
            }


            var  dialogView = View.inflate(this@game_screen,R.layout.dialog,null)
            var dlg = AlertDialog.Builder(this@game_screen)

            dlg.setTitle("점수표")

            for(i in 0..index2-1) {
                dialogView.txt1.append("${i+1}\n")
                dialogView.txt2.append("${nameList.get(i)}\n")
                dialogView.txt3.append("${gradeList.get(i)}\n")
            }
            dlg.setView(dialogView)
            dlg.setNegativeButton("닫기",null)

            dlg.show()

        }


    }
}