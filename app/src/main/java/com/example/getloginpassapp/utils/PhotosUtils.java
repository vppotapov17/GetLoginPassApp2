package com.example.getloginpassapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotosUtils {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeigth = options.outHeight;

        int inSampleSize = 1;

        if (srcHeigth > destHeight || srcWidth > destWidth){
            if (srcWidth > srcHeigth){
                inSampleSize = Math.round(srcWidth / destWidth);
            }
            else {
                inSampleSize = Math.round(srcHeigth / destHeight);
            }

        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

}
