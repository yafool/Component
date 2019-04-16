/*
 *   Copyright (C) 2019 yafool Individual developer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.yafool.component.imageloader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.yafool.component.utils.Constants;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Package: com.yafool.component.imageloader
 * @ClassName: com.yafool.component.imageloader.BitmapDecode.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class BitmapDecode {
    private static final String TAG = BitmapDecode.class.getSimpleName();

    /**
     * 返回压缩后的流
     *
     * @path String --- 图片路径
     * @reqWidth int 视图容器的 宽度
     * @reqHeight int 视图容器的 高度
     * <p>
     * 有四种等比压缩策略
     * 1. 按 reqWidth 与 reqHeight 跟原图的最大压缩比进行等比压缩
     * 2. reqWidth 设置为 0, 按 reqHeight 与原图的最大压缩比例进行压缩.
     * 3. reqHeight 设置为0, 按 reqWidth 与原图的最大压缩比例进行压缩.
     * 4. reqWidth 和 reqHeight 都为0, 则原尺寸输出
     */
    public Bitmap toBitmap(@NonNull Context ctx, @NonNull String path, int reqWidth, int reqHeight) throws Exception {
        if (null == ctx || TextUtils.isEmpty(path)){
            throw new Exception("toBitmap input params error!");
        }

        Bitmap bm = null;
        InputStream inputStream = toInputStream(ctx, path);  // 远程读取输入流
        if (inputStream == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
        options.inJustDecodeBounds = true;
        // 获取图片大小
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        // 获取图片压缩比
        int size = calculateInSampleSize(ctx, options, reqWidth, reqHeight);
        if (size < 0) {
            return null;
        }

        options.inSampleSize = size;   // 找到合适的倍率
        // 当inJustDecodeBounds设为false,加载图片到内存
        options.inJustDecodeBounds = false;
        inputStream = toInputStream(ctx, path);  // 远程读取输入流,要再读一次，否则之前的inputStream已无效了
        bm = BitmapFactory.decodeStream(inputStream, null, options);
        if (null != inputStream) {
            inputStream.close();
        }
        return bm;
    }

    /*********************************
     * @function: 计算出合适的图片倍率
     * @options: 图片bitmapFactory选项
     * @reqWidth: 需要的图片宽
     * @reqHeight: 需要的图片长
     * @return: 成功返回倍率, 异常-1
     ********************************/
    private int calculateInSampleSize(@NonNull Context ctx, BitmapFactory.Options options, int reqWidth,
                                      int reqHeight) throws Exception {
        if (null == ctx){
            throw new Exception("calculateInSampleSize ctx is null");
        }

        // 获取屏幕宽和高
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        // 获取原图片长宽
        int width = options.outWidth;
        int height = options.outHeight;
        // 目标宽高未知, 图片原尺寸又太大时, 则按屏幕尺寸压缩
        if (0 == reqWidth && width > screenWidth){
            reqWidth = screenWidth;
        }
        if (0 == reqHeight && height > screenHeight){
            reqHeight = screenHeight;
        }

        // 设置初始压缩率为1
        int inSampleSize = 1;
        try {

            if (0 == reqWidth && 0 == reqHeight) {
                inSampleSize = 1;
            } else if (0 == reqWidth) {
                inSampleSize = height / reqHeight;
            } else if (0 == reqHeight) {
                inSampleSize = width / reqWidth;
            } else {
                // reqWidth/width,reqHeight/height两者中最大值作为压缩比
                int w_size = width / reqWidth;
                int h_size = height / reqHeight;
                inSampleSize = w_size > h_size ? w_size : h_size;  // 取w_size和h_size两者中最大值作为压缩比
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            Log.i(TAG, "inSampleSize: " + inSampleSize);
            return inSampleSize;
        }

    }

    /***********
     * @获得原始流*
     ***********/
    private InputStream toInputStream(@NonNull Context ctx, String path) throws Exception {

        if (null == ctx){
            throw new Exception("toInputStream ctx is null");
        }

        InputStream is;
        if (path.startsWith(Constants.Prefix.ASSETS)) {
            path = path.replace(Constants.Prefix.ASSETS, "");
            is = ctx.getAssets().open(path);
        } else {
            is = new FileInputStream(path.replace("file://", ""));
        }
        return is;
    }

    /***********
     * 根据imageview 的Background drwable 做圆角处理
     ***********/
    public boolean gradientImage(@NonNull Bitmap bitmap, @NonNull ImageView imgView) {

        GradientDrawable drawable = null;

        if (null == bitmap || null == imgView) {
            Log.e(TAG, "bitmap: " + bitmap + "  imgView: " + imgView);
            return false;
        }

        if (null == imgView.getBackground()) {
            Log.w(TAG, "imageview have no background!");
            return false;
        }

        if (imgView.getBackground() instanceof GradientDrawable) {
            drawable = (GradientDrawable) imgView.getBackground();
        }

        if (null == drawable) {
            Log.w(TAG, "imageview' background not instanceof GradientDrawable!");
            return false;
        }

        float[] radii = drawable.getCornerRadii();

        if (null == radii || 0 == radii.length) {
            Log.w(TAG, "imageview set rect bitmap!");
            return false;
        }

        boolean needmake = false;
        for (float f : radii) {
            if (0.0f != f) {
                needmake = true;
                break;
            }
        }

        if (!needmake) {
            Log.w(TAG, "imageview set rect bitmap!");
            return false;
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);


        final int color = 0xff424242;
        final Paint paint = new Paint();

        // 抗锯齿
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        Path path = new Path();
        path.addRoundRect(rectF, radii, Path.Direction.CW);
        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        imgView.setImageBitmap(output);
        return true;
    }
}
