package com.stone.flabbybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Stone on 2015/3/27.
 * 一个SurfaceView的简单示例
 */
public class GameFlabbyBird extends SurfaceView implements SurfaceHolder.Callback,Runnable{


    private SurfaceHolder mHolder;
    //与Surface绑定的Canvas
    private Canvas mcanvas;
    //用于绘制的线程
    private Thread thread;
    //线程的控制开关
    private boolean isRunning;

    public GameFlabbyBird(Context context, AttributeSet attrs) {

        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);//设置画布透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置屏幕常亮
        setKeepScreenOn(true);
    }

    @Override
    public void run() {

        while (isRunning){
            long startTime = System.currentTimeMillis();
            draw();
            long endTime = System.currentTimeMillis();

            try {
                if(endTime-startTime<50){
                    Thread.sleep(50-(endTime-startTime));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启线程
        isRunning = true;
        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //通知线程关闭
        isRunning = false;

    }

    public void draw(){

        try {
            mcanvas = mHolder.lockCanvas();
            if(mcanvas != null){
                //draw ...
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mcanvas != null){
                mHolder.unlockCanvasAndPost(mcanvas);
            }
        }
    }
}
