package com.hurdle.spriteanimation


import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

// SurfaceView 입력후 알트 엔터, 파라미터 3개 생성자로 자동완성
// attrs, defStyleAttr 의 default로 null, 0 을 설정해야 진입이 가능합니다.
class LogDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private val logThread: LogDrawingThread
    private val mHandler = LogDrawingHandler()

    init {
        holder.addCallback(this)
        logThread = LogDrawingThread(context = context, surfaceHolder = holder, handler = mHandler)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        logThread.start()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        try {
            logThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getThread(): LogDrawingThread = logThread

    class LogDrawingThread(
        val context: Context,
        val handler: Handler,
        val surfaceHolder: SurfaceHolder
    ) : Thread() {
        override fun run() {
            super.run()

            while (true) {
                var canvas: Canvas? = null

                try {
                    // 이미지 딜레이
                    sleep(200)

                    canvas = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        canvas.drawARGB(0, 0, 0, 0)
                    }

                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }
    }

    class LogDrawingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }
}