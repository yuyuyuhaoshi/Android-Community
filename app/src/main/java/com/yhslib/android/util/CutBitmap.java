package com.yhslib.android.util;

import android.graphics.Bitmap;


public class CutBitmap {
    public static Bitmap imageCrop(Bitmap bitmap, int width,int height) {
        // 得到图片的宽，高
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //边长不可以比原来小
        width = w<width ? w:width;
        height = h<height ? h:height;
        int retX = (w - width) / 2;
        int retY = (h - height) / 2;
        return Bitmap.createBitmap(bitmap, retX, retY, width, height, null, false);
    }
}
