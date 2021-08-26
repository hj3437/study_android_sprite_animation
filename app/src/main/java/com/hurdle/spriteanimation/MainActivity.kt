package com.hurdle.spriteanimation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var logView: LogDrawingView
    private lateinit var logThread: LogDrawingView.LogDrawingThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logView = findViewById(R.id.log_view)
        logThread = logView.getThread()
    }
}