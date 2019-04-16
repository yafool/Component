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

package com.yafool.component.jsonanimator.layers;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.yafool.component.imageloader.ImageLoader;
import com.yafool.component.jsonanimator.bean.LayerBean;
import com.yafool.component.jsonanimator.control.AnimConstant;
import com.yafool.component.jsonanimator.control.Scaling;
import com.yafool.component.utils.YafoolLog;

/**
 * @Package: com.yafool.component.jsonanimator.layers
 * @ClassName: com.yafool.component.jsonanimator.layers.BackgroundDrawable.java
 * @Description: TODO
 * @CreateDate: 2019/4/16 5:10 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/16 5:10 PM
 */
public class BackgroundDrawable extends BaseDrawable implements ImageLoader.IImageLoadListener {
    private static final String TAG = BackgroundDrawable.class.getSimpleName();

    private Context mContext;
    private LayerBean mLayerBean;
    private Paint mPaint;
    private Scaling mScaling;
    private Bitmap mBitmap;

    public BackgroundDrawable(@NonNull Context context, @NonNull LayerBean layer) {
        super(context, layer);
        mContext = context;
        mLayerBean = layer;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (null == mBitmap) {
            return;
        }

        int width = mBitmap.getWidth();
        Matrix matrix = new Matrix();
        int count = canvas.getWidth() / width + 2;

        int dx = 0;
        int dy = 0;

        switch (AnimConstant.Properties.valueOf(mLayerBean.getProperties())) {
            case vertical: {
                dy = mBitmap.getHeight();
                break;
            }
            case horizontal: {
                dx = mBitmap.getWidth();
                break;
            }
            case fit:
            default: {
                break;
            }
        }

        matrix.postScale(mScaling.getHorizontal(), mScaling.getVertical());
        matrix.postTranslate(dx, dy);
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(mBitmap, matrix, mPaint);
            matrix.postTranslate(dx, dy);
        }

    }

    @Override
    public void initialize(@NonNull OnDrawListener l) {
        super.initialize(l);
        init();
    }

    @Override
    void reset() {

    }

    @Override
    public void loadComplete(Bitmap bmp) {
        if (null != bmp) {
            mBitmap = bmp;
        } else {
            YafoolLog.e(TAG, "can't load bitmap: " +mLayerBean.getRess()[0]);
        }
    }

    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    void onScaling(Scaling scaling) {
        mScaling = scaling;
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private void init() {

        if (TextUtils.isEmpty(mLayerBean.getRess()[0])) {
            throw new IllegalArgumentException("res path is null!");
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        try {
            ImageLoader.getInstance().displayImage(mContext, mLayerBean.getRess()[0], mLayerBean.getW(), mLayerBean.getH(), this);
        } catch (IllegalArgumentException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }

}
