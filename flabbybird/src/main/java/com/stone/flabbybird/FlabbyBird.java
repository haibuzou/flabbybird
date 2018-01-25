package com.stone.flabbybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stone on 2015/3/29.
 * flabby bird
 */
public class FlabbyBird extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //当前view 的尺寸
    private int mWidth;
    private int mHeight;
    private RectF mRect = new RectF();

    //绘制的初始化
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread mthread;
    private Boolean isRunning;

    //背景
    private Bitmap bgMap;

    //鸟
    private Bird bird;
    private Bitmap birdmp;

    //地板
    private Floor floor;
    private Bitmap floormp;
    private Paint mPaint;
    //移动速度
    private int speed;

    //管道
    private Pipe pipe;
    private Bitmap upbmp;
    private Bitmap downbmp;
    private RectF mPipRect = new RectF();
    private int mPipeWidth;
    //管道宽度设为60
    private static float PIPE_WIDTH = 60;
    private List<Pipe> pipeList = new ArrayList<Pipe>();

    //分数
    private final int[] Nums = new int[]{R.drawable.n0,
            R.drawable.n1, R.drawable.n2,
            R.drawable.n3, R.drawable.n4,
            R.drawable.n5, R.drawable.n6,
            R.drawable.n7, R.drawable.n8,
            R.drawable.n9};
    private Bitmap[] NumBitmap;
    private int mgrade = 0;
    //数字的高度
    private static final float NUM_POSITION = 1 / 15F;
    private int numWidth;
    private int numHeight;
    //数字的范围
    private RectF numRect = new RectF();

    //记录游戏的状态
    private GameStatus status = GameStatus.WATTING;
    //触摸上升的距离
    private static final int TOUCH_UP_DIS = -23;
    private int upDis = Util.dip2px(getContext(),TOUCH_UP_DIS);
    private int downSpeed = Util.dip2px(getContext(),4);
    private int tmpMoveDis;
    //记录需要移除的管道
    private List<Pipe> removePipeList = new ArrayList<Pipe>();
    //设定300dp的时候生成管道
    private final int DIS_CREAT_PIPE = Util.dip2px(getContext(),150);

    //记录移动的距离达到 DIS_CREAT_PIPE 时移除管道
    private int tmpPipeMoveDistance;

    //移除的管道个数
    private int mRemovePipes;


    public FlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlabbyBird(Context context) {
        super(context);
        init();
    }

    public void init() {
        //surface.holder初始化
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        //画笔初始化
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        speed = Util.dip2px(getContext(), 7);
        //管道宽度初始化
        mPipeWidth = Util.dip2px(getContext(), PIPE_WIDTH);
        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);//设置屏幕常亮
        initBitmaps();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        mthread = new Thread(this);
        mthread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //通知线程关闭
        isRunning = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            switch (status){
                case WATTING:
                    status = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    tmpMoveDis = upDis;
                    break;
            }
        }

        return true;
    }

    @Override
    public void run() {
        while (isRunning) {
            Long startTime = System.currentTimeMillis();
            logic();
            draw();
            Long endTime = System.currentTimeMillis();

            try {
                if (endTime - startTime < 50) {
                    Thread.sleep(50 - (endTime - startTime));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void initBitmaps() {
        bgMap = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
        birdmp = BitmapFactory.decodeResource(getResources(), R.drawable.b1);
        floormp = BitmapFactory.decodeResource(getResources(), R.drawable.floor_bg2);
        upbmp = BitmapFactory.decodeResource(getResources(), R.drawable.g2);
        downbmp = BitmapFactory.decodeResource(getResources(), R.drawable.g1);
        NumBitmap = new Bitmap[Nums.length];
        for (int i = 0; i < Nums.length; i++) {
            NumBitmap[i] = BitmapFactory.decodeResource(getResources(), Nums[i]);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRect.set(0, 0, w, h);
        //初始化 bird
        bird = new Bird(getContext(), mWidth, mHeight, birdmp);
        //初始化地板
        floor = new Floor(mWidth, mHeight, floormp);
        //初始化管道
        mPipRect.set(0, 0, mPipeWidth, mHeight);
        pipe = new Pipe(w, h, upbmp, downbmp);
        pipeList.add(pipe);
        //初始化分数
        numHeight = (int) (h * NUM_POSITION);
        numWidth = (int) (numHeight * 1.0F / NumBitmap[0].getHeight() * NumBitmap[0].getWidth());
        numRect.set(0, 0, numWidth, numHeight);
    }

    public void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                mCanvas.drawBitmap(bgMap, null, mRect, null);
                bird.draw(mCanvas);
                //画管道
                for (Pipe pipe : pipeList) {
                    pipe.draw(mCanvas, mPipRect);
                }
                floor.draw(mCanvas, mPaint);
                //画数字
                drawGrade();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    //绘制分数
    public void drawGrade() {
        String grade = mgrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * numWidth / 2, 1f / 8 * mHeight);
        for (int i = 0; i < grade.length(); i++) {
            String str = grade.substring(i, i + 1);
            int num = Integer.valueOf(str);
            mCanvas.drawBitmap(NumBitmap[num], null, numRect, null);
            mCanvas.translate(numWidth, 0);
        }
        mCanvas.restore();
    }

    public void logic(){
        switch (status){
            case WATTING:
                break;
            case RUNNING:
                mgrade = 0;
                //鸟的位置变化
                tmpMoveDis += downSpeed;
                bird.setY(bird.getY()+tmpMoveDis);

                //不断记录移动的距离
                tmpPipeMoveDistance += speed;
                if(tmpPipeMoveDistance >= createPipeX()){
                    Pipe pipe = new Pipe(mWidth,mHeight,upbmp,downbmp);
                    pipeList.add(pipe);
                    tmpPipeMoveDistance = 0;
                }
                //更新地板坐标
                floor.setX(floor.getX() - speed);
                //管道移动
                for(Pipe pipe: pipeList){
                    if(pipe.getX()<-mPipeWidth){
                        removePipeList.add(pipe);
                        mRemovePipes++;
                        continue;
                    }
                    pipe.setX(pipe.getX() - speed);
                }
                pipeList.removeAll(removePipeList);

                mgrade +=mRemovePipes;
                for(Pipe pipe:pipeList){
                    if(bird.getX()+bird.getmWidth()>pipe.getX()){
                        mgrade++;
                    }
                }

                checkGameOver();
                break;
            case OVER:
                //鸟在上面 先让鸟落下
                if(bird.getY()+bird.getMheight()<floor.getY()){
                    tmpMoveDis += downSpeed;
                    bird.setY(bird.getY()+tmpMoveDis);
                }else{
                    status = GameStatus.WATTING;
                    initPos();
                }

                break;
        }
    }

    public void checkGameOver(){
        //如果触碰地板 ag
        if(bird.getY()>floor.getY()-bird.getMheight()){
            status = GameStatus.OVER;
        }
        for(Pipe pipe:pipeList){
            //已经穿过的不计算
            if(pipe.getX()+mPipeWidth<bird.getX()){
                continue;
            }
            if(pipe.touchBird(bird)){
                status = GameStatus.OVER;
                break;
            }
        }
    }

    public int createPipeX(){
        if(mRemovePipes < 3) {
            return Util.dip2px(getContext(),160);
        }else if(mRemovePipes < 7){
            return Util.dip2px(getContext(),140);
        }else if(mRemovePipes < 15){
            return Util.dip2px(getContext(),135);
        }else{
            return Util.dip2px(getContext(),130);
        }
    }

    //游戏结束重置
    public void initPos(){
        pipeList.clear();
        removePipeList.clear();
        bird.setY(mHeight*1/3);
        tmpMoveDis = 0;
        mRemovePipes = 0;
    }


}
