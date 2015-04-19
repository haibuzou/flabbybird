package com.stone.flabbybird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Stone on 2015/3/30.
 * 管道 上管道与下管道
 */
public class Pipe {

    //上下管道间的距离
    private static final float DISTANCE = 1/5F;
    //上管道的最大高度
    private static final float MAX_HEIGHT_UP = 2/5F;
    //上管道的最小高度
    private static final float MIN_HEIGHT_DOWN = 1/5F;
    //管道的横坐标
    private int x;
    //上管道的高度
    private int height;
    //上下管道的距离
    private int margin;
    //上管道图片
    private Bitmap upbmp;
    //下管道图片
    private Bitmap downbmp;

    private Random random = new Random();

    public Pipe(int gameWidth,int gameHeight,Bitmap up,Bitmap down) {
        //上下管道的间距
        margin = (int)(gameHeight*DISTANCE);
        //默认从最右边出现
        x = gameWidth;
        upbmp = up;
        downbmp = down;
        randomHeight(gameHeight);

    }

    public void randomHeight(int gameHeight){
        height = random.nextInt((int)(gameHeight*(MAX_HEIGHT_UP-MIN_HEIGHT_DOWN)));
        height = (int)(height+gameHeight*MIN_HEIGHT_DOWN);
    }

    public void draw(Canvas mCanvas,RectF rect){
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        //rect 为整个管道 假设整个管道为100 需要绘制20 则需要向上移动80
        mCanvas.translate(x,-(rect.bottom-height));
        mCanvas.drawBitmap(upbmp,null,rect,null);
        //下管道 向下偏移间距+管道高度+之前偏移的高度
        mCanvas.translate(0,(rect.bottom-height)+height+margin);
        mCanvas.drawBitmap(downbmp,null,rect,null);
        mCanvas.restore();
    }

    public boolean touchBird(Bird bird){
        if(bird.getX()+bird.getmWidth()>x&&(bird.getMheight()+bird.getY()>height+margin||bird.getY()<height)){
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
