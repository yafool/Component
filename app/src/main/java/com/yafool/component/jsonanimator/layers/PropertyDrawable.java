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
 * @ClassName: com.yafool.component.jsonanimator.layers.PropertyDrawable.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class PropertyDrawable extends BaseDrawable implements ImageLoader.IImageLoadListener {
    private static final String TAG = PropertyDrawable.class.getSimpleName();

    private Context mContext;

    private LayerBean mLayerBean;

    private Paint mPaint;
    private Bitmap mBitmap;
    private int mOffset;
    private int mSpeedPx;

    private Scaling mScaling;

    public PropertyDrawable(@NonNull Context context, @NonNull LayerBean layer) {
        super(context, layer);
        mContext = context;
        mLayerBean = layer;
    }

    @Override
    public void initialize(@NonNull OnDrawListener l) {
        super.initialize(l);
        init();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (null == mBitmap) {
            return;
        }

        int width = mBitmap.getWidth();
        Matrix matrix = new Matrix();
        int count = canvas.getWidth() / width + 2;
        int dx = mSpeedPx;

        switch (AnimConstant.Properties.valueOf(mLayerBean.getProperties())) {
            case right2left: {
                dx = -mSpeedPx;
                break;
            }
            case left2right: {
                width = -width;
                break;
            }
            default:
                dx = -mSpeedPx;
                break;
        }

        matrix.postScale(mScaling.getHorizontal(), mScaling.getVertical());
        matrix.postTranslate(mOffset, mLayerBean.getY() * mScaling.getVertical());
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(mBitmap, matrix, mPaint);
            matrix.postTranslate(width, 0);
        }
        mOffset += dx;
        if (Math.abs(mOffset) > Math.abs(width)) {
            mOffset += width;
        }
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    void onScaling(Scaling scaling) {
        mScaling = scaling;
    }

    @Override
    void reset() {
        mOffset = 0;
    }

    @Override
    public void loadComplete(Bitmap bmp) {
        if (null == bmp) {
            YafoolLog.e(TAG, "make bitmap error!!!");
            mListener.onError(PropertyDrawable.this, "make bitmap error!!! " + mLayerBean.getRess()[0]);
        } else {
            mBitmap = bmp;
            mListener.onPrepared(PropertyDrawable.this);
        }
    }

    @Override
    public boolean isCancel() {
        return false;
    }

    private void init() {

        if (TextUtils.isEmpty(mLayerBean.getRess()[0])) {
            throw new IllegalArgumentException("res path is null!");
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        try {
            mOffset = 0;
            if (0 < mLayerBean.getSpeed()) {
                mSpeedPx = mLayerBean.getSpeed() * refreshRate / 1000 + 1;
            }

            ImageLoader.getInstance().displayImage(mContext, mLayerBean.getRess()[0], mLayerBean.getW(), mLayerBean.getH(), this);

        } catch (IllegalArgumentException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }

}
