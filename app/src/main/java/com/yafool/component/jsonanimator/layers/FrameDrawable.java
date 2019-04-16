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
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yafool.component.imageloader.ImageLoader;
import com.yafool.component.jsonanimator.bean.LayerBean;
import com.yafool.component.jsonanimator.control.AnimConstant;
import com.yafool.component.jsonanimator.control.Scaling;
import com.yafool.component.utils.YafoolLog;


/**
 * @Package: com.yafool.component.jsonanimator.layers
 * @ClassName: com.yafool.component.jsonanimator.layers.FrameDrawable.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class FrameDrawable extends BaseDrawable implements ImageLoader.IImageLoadListener {
    private static final String TAG = FrameDrawable.class.getSimpleName();

    private static final int PRE_LOAD_MAX = 3;

    private Context mContext;
    private LayerBean mLayerBean;
    private Paint mPaint;
    private Rect mSrcRect;
    private Rect mDetRect;
    private int mCurFrame;
    private int mPreFrame;
    private Bitmap curBitmap;

    private Equation mEquation;

    public FrameDrawable(@NonNull Context context, @NonNull LayerBean layer) {
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

        if (null != curBitmap) {
            canvas.drawBitmap(curBitmap, mSrcRect, mDetRect, mPaint);
        } else {
            YafoolLog.w(TAG, "draw(): null == curBitmap");
        }

        mEquation.invalidate();
        if (mCurFrame != mEquation.pitchOn()) {
            loadNext();
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
        int dw = (int) (mLayerBean.getW() * scaling.getHorizontal());
        int dh = (int) (mLayerBean.getH() * scaling.getVertical());

        int l = (int) (mLayerBean.getX() * scaling.getHorizontal());
        int t = (int) (mLayerBean.getY() * scaling.getVertical());
        int r = l + dw;
        int b = t + dh;
        if (null == mDetRect) {
            mDetRect = new Rect(l, t, r, b);
        }else {
            mDetRect.left = l;
            mDetRect.top = t;
            mDetRect.right = r;
            mDetRect.bottom = b;
        }
    }

    @Override
    void reset() {
        mCurFrame = 0;
        mEquation = new Equation(mLayerBean.getRess().length, AnimConstant.Default.REFRESH_RATE, mLayerBean.getSpeed());
    }

    @Override
    public void loadComplete(Bitmap bmp) {
        for (; mCurFrame < mPreFrame; mCurFrame++) {
            curBitmap = ImageLoader.getInstance().getCacheBitmap(mLayerBean.getRess()[mCurFrame]);
            if (null != curBitmap) {
                break;
            }
        }
        if (mCurFrame >= mPreFrame) {
            mListener.onError(this, "preload error!");
        } else {
            mListener.onPrepared(FrameDrawable.this);
        }

    }

    @Override
    public boolean isCancel() {
        return false;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mSrcRect = new Rect(0, 0, mLayerBean.getW(), mLayerBean.getH());

        mCurFrame = -1;
        mEquation = new Equation(mLayerBean.getRess().length, AnimConstant.Default.REFRESH_RATE, mLayerBean.getSpeed());

        try {
            if (++mCurFrame >= mLayerBean.getRess().length) {
                mListener.onError(FrameDrawable.this, "have no frame res!");
                return;
            }

            String[] pres = new String[mLayerBean.getRess().length > PRE_LOAD_MAX ? PRE_LOAD_MAX : mLayerBean.getRess().length];
            for (int i=0; i<pres.length; i++){
                pres[i] = mLayerBean.getRess()[i];
            }
            mPreFrame = pres.length;
            ImageLoader.getInstance().preLoadBitmap(mContext, pres, mLayerBean.getW(), mLayerBean.getH(), this);

        } catch (IllegalArgumentException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void loadNext() {
        try {
            if (++mCurFrame >= mLayerBean.getRess().length) {
                mListener.onEnd(FrameDrawable.this);
                mCurFrame = 0;
            }
            if (mPreFrame >= mLayerBean.getRess().length) {
                mPreFrame = 0;
            }

            ImageLoader.getInstance().flashLoadBitmap(mContext, mLayerBean.getRess()[mPreFrame++], mLayerBean.getW(), mLayerBean.getH());

            curBitmap = ImageLoader.getInstance().getCacheBitmap(mLayerBean.getRess()[mCurFrame]);
            if (null == curBitmap) {
                ImageLoader.getInstance().cancelFlashLoad(mLayerBean.getRess()[mCurFrame]);
            }

        } catch (IllegalArgumentException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * draw时, 落在了哪一帧的计算公式
     */
    class Equation {
        private int frameLength;
        private int frameRate;
        private int frameSpeed;
        private int countdown;

        public Equation(int length, int rate, int speed) {
            super();
            frameLength = length;
            frameRate = rate;
            frameSpeed = speed;
            countdown = 0;
        }

        public void invalidate() {
            countdown += frameRate;
            if (frameSpeed * frameLength == countdown / frameRate) {
                countdown = frameSpeed;
            }
        }

        public int pitchOn() {
            return countdown / frameSpeed % frameLength;
        }
    }
}
