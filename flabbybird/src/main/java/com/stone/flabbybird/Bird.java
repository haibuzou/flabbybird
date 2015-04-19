package com.stone.flabbybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Stone on 2015/3/29.
 * 鸟
 */
public class Bird {

    //鸟在屏幕 2/3 高的位置
    private static final float BIRD_POSITION = 2/3F;
    //鸟的大小
    private static final int BIRD_SIZE = 30;
    //鸟的横坐标
    private int x;
    //鸟的纵坐标
    private int y;
    //鸟的宽度
    private int mWidth;
    //鸟的高度
    private int mheight;
    //鸟的图片
    private Bitmap birdmp;
    //鸟的绘制范围
    private RectF rect = new RectF();

    public Bird(Context context,int gameWidth,int gameHeight,Bitmap bitmap) {
        birdmp = bitmap;
        //鸟的位置
        x = gameWidth/2-bitmap.getWidth()/2;
        y = (int)(gameHeight*BIRD_POSITION);

        //计算鸟的宽高
        mWidth = Util.dip2px(context,BIRD_SIZE);
        //按宽度的比例计算高度
        mheight = (int)(mWidth*1.0F/bitmap.getWidth()*bitmap.getHeight());
    }

    public void draw(Canvas canvas){
        rect.set(x,y,x+mWidth,y+mheight);
        canvas.drawBitmap(birdmp,null,rect,null);
    }

    //对外提供获得和设置 Y坐标 以及宽高的方法
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getmWidth() {
        return mWidth;
    }

    public int getMheight() {
        return mheight;
    }
}
