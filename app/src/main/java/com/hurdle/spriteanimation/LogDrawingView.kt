package com.hurdle.spriteanimation


import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView

// SurfaceView 입력후 알트 엔터, 파라미터 3개 생성자로 자동완성
// attrs, defStyleAttr 의 default로 null, 0 을 설정해야 진입이 가능합니다.
class LogDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    companion object {
        var displayW = 0
        var displayH = 0
    }

    private val logThread: LogDrawingThread
    private val mHandler = LogDrawingHandler()

    init {
        holder.addCallback(this)
        logThread = LogDrawingThread(context = context, surfaceHolder = holder, handler = mHandler)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        displayW = width
        displayH = height
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
        private val mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.log)
        private val mBitmapSize = 32
        private val mBitmapColumn = 6

        private var index = 0
        private var lv = 1
        private var lvTop = 0
        private var lvBottom = 1

        var mSrc = Rect()
        var mDst = Rect()

        // ex) 380x60 이미지에서 60x60 크기로 이미지를 보여주고 싶을때 방법
        // Rect(0,0,60,60), 하지만 실제 다른 사이즈로 보여줌
        // ***원인/해결방법은 dp to px
        // fx값을 로그로 찍어보면 2, 3, 3.5 이렇게 나옴
        // 의미: 이미지 크기의 2배, 3배, 3.5배
        // 방법 Rect(0,0,60*fx, 60*fx) 처리

        // (주의) 다른결과값 발생
        // 에러 : 60*fx.toInt()
        // 정상 : (60*fx).toInt()
        val fx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics
        )

        override fun run() {
            super.run()

            // 캔버스 화면의 해당 비트맵을 확대해서 별도로 보여줄 사각형의 크기
            // 현재화면의 20% 크기 설정
            val size = (displayW * 0.2).toInt()

            // 캔버스위에 보여줄 사각형 확대비율
            val scale = 3

            val mDstSize = size * scale

            // 가운데 위치를 잡기위한 계산
            // (Math) 화면절반 = 화면전체크기 / 2
            val centerW = (displayW - mDstSize) / 2
            val centerH = (displayH - mDstSize) / 2

            // target width
            val w = (mBitmapSize * fx).toInt()
            val h = (mBitmapSize * fx).toInt()

            while (true) {
                var canvas: Canvas? = null

                try {
                    // 이미지 딜레이
                    sleep(200)

                    canvas = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        // 배경설정
                        canvas.drawARGB(220, 0, 0, 0)

                        when (lv) {
                            1 -> {
                                if (index >= mBitmapColumn) {
                                    index = 0
                                    lv = 2
                                    lvTop = 1
                                    lvBottom = 2
                                }
                            }
                            2 -> {
                                if (index >= mBitmapColumn) {
                                    index = 0

                                    lv = 3
                                    lvTop = 2
                                    lvBottom = 3
                                }
                            }
                            3 -> {
                                if (index >= mBitmapColumn) {
                                    index = 0
                                    lv = 4
                                    lvTop = 3
                                    lvBottom = 4
                                }
                            }
                            4 -> {
                                if (index >= mBitmapColumn) {
                                    index = 0
                                    lv = 1
                                    lvTop = 0
                                    lvBottom = 1
                                }
                            }
                        }

                        mSrc = Rect(
                            w * index, h * lvTop,
                            w * (index + 1), h * lvBottom
                        )

                        mDst = Rect(
                            centerW, centerH,
                            centerW + mDstSize, centerH + mDstSize
                        )

                        canvas.drawBitmap(mBitmap, 0f, 0f, null)

                        val paint = Paint()
                        paint.color = Color.MAGENTA
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 8f
                        // 빈 검정 사각형, 비트맵에서 보여줄 위치
                        canvas.drawRect(mSrc, paint)

                        paint.color = Color.RED
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 24f
                        // 빨간색 사각형, 확대해서 보여줄 사각형
                        canvas.drawRect(mDst, paint)

                        canvas.drawBitmap(mBitmap, mSrc, mDst, null)
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                        index++
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