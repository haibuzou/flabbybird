package com.stone.flabbybird;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by Administrator on 2015/3/30.
 * 地板 使用BitmapShader 填充的方式进行绘制
 */
public class Floor {

    //地板的位置
    public static float  FLOOR_POSITION = 4/5F;
    //x坐标
    private int x;
    //y坐标
    private int y;
    //填充物
    private BitmapShader mFloorShader;

    private int mGameWidth;

    private int mGameHeight;

    public Floor(int gameWidth,int gameHeight,Bitmap floorBg) {
        mGameWidth = gameWidth;
        mGameHeight = gameHeight;
        y = (int)(gameHeight*FLOOR_POSITION);
        mFloorShader = new BitmapShader(floorBg, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    public void draw(Canvas mcanvas,Paint mpaint){
        if(-x > mGameWidth){
            x = x%mGameWidth;
        }
        mcanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mpaint.setShader(mFloorShader);
        mcanvas.translate(x,y);
        mcanvas.drawRect(x,0,mGameWidth-x,mGameHeight-y,mpaint);
        mcanvas.restore();
        mpaint.setShader(null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
}
