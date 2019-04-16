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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import com.yafool.component.jsonanimator.bean.DrawableBean;
import com.yafool.component.jsonanimator.bean.LayerBean;
import com.yafool.component.jsonanimator.control.AnimConstant;
import com.yafool.component.jsonanimator.control.IInvalidateObserver;
import com.yafool.component.jsonanimator.control.InvalidateManager;
import com.yafool.component.jsonanimator.control.Scaling;
import com.yafool.component.utils.Constants;
import com.yafool.component.utils.YafoolLog;

/**
 * @Package: com.yafool.component.jsonanimator.layers
 * @ClassName: com.yafool.component.jsonanimator.layers.AnimDrawable.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class AnimDrawable extends LayerDrawable  implements BaseDrawable.OnDrawListener, IInvalidateObserver {
    private static final String TAG = AnimDrawable.class.getSimpleName();

    private static final int LOOP_FOREVER = -1;

    private static DrawableBean mDrawableBean;

    private AnimConstant.AnimatorState mAnimatorState;
    private BaseDrawable[] mDrawables;
    private int mLayerSum = Constants.Invalid.INT;
    private int mCurLoopTimes;

    private AnimatorDrawableListener mListener;

    public static AnimDrawable create(@NonNull final Context context,
                                      @NonNull DrawableBean drawableBean,
                                      AnimatorDrawableListener listener) throws IllegalArgumentException {
        if (null == context) {
            throw new IllegalArgumentException("can't create AnimatorDrawable by null context!");
        }

        if (null == drawableBean) {
            throw new IllegalArgumentException("can't create AnimatorDrawable by null bean!");
        }

        if (null == listener) {
            throw new IllegalArgumentException("can't create AnimatorDrawable by null listener!");
        }

        ArrayList<LayerBean> layers = drawableBean.getLayers();
        if (null == layers || layers.isEmpty()) {
            throw new IllegalArgumentException("can't create AnimatorDrawable by null layer bean!");
        }

        mDrawableBean = drawableBean;

        BaseDrawable[] drawables = new BaseDrawable[layers.size()];

        int index = 0;
        for (LayerBean bean : layers) {
            BaseDrawable drawable = null;
            switch (AnimConstant.LayerType.valueOf(bean.getType())) {
                case background: {
                    drawable = new BackgroundDrawable(context, bean);
                    break;
                }
                case frame: {
                    drawable = new FrameDrawable(context, bean);
                    break;
                }
                case json: {
                    break;
                }
                case property: {
                    drawable = new PropertyDrawable(context, bean);
                    break;
                }
                default:
                    throw new IllegalArgumentException("can't create AnimatorDrawable by unknown layer type!");
            }

            drawables[index++] = drawable;
        }

        AnimDrawable animDrawable = new AnimDrawable(drawables);
        animDrawable.initialize(listener);
        return animDrawable;
    }

    /**
     * Creates a new layer drawable with the list of specified layers.
     *
     * @param layers a list of drawables to use as layers in this new drawable,
     *               must be non-null
     */
    public AnimDrawable(@NonNull BaseDrawable[] layers) {
        super(layers);
        mAnimatorState = AnimConstant.AnimatorState.idle;
        mDrawables = layers;
    }

    @Override
    public void draw(Canvas canvas) {
        for (BaseDrawable drawable : mDrawables){
            drawable.onScaling(new Scaling().toScaling(mDrawableBean.getW(), mDrawableBean.getH(), canvas));
            drawable.draw(canvas);
        }
    }

    @Override
    public void onPrepared(Drawable self) {
        for (Drawable drawable : mDrawables) {
            if (self == drawable) {
                mLayerSum--;
                break;
            }
        }

        YafoolLog.i(TAG, "onPrepared mLayerSum: " + mLayerSum + "  self: " + self.getClass().getName());
        mListener.onPrepared();
    }

    @Override
    public void onError(Drawable self, String message) {
        mListener.onError(String.format("%s in %s", message, self.getClass().getName()));
    }

    ;

    @Override
    public void onEnd(Drawable self) {
        for (int i = 0; i < mDrawables.length; i++) {
            if (mDrawables[i] == self && mDrawableBean.getBase() != i) {
                return;
            }
        }

        if (mCurLoopTimes >= mDrawableBean.getLoops() && mDrawableBean.getLoops() != LOOP_FOREVER) {
            cancel();
            mListener.onEnd();
        }
    }

    @Override
    public void onInvalidate() {
        if (AnimConstant.AnimatorState.playing == mAnimatorState) {
            AnimDrawable.this.invalidateSelf();
        }
    }

    public void start() {
        YafoolLog.d(TAG, "start");
        mAnimatorState = AnimConstant.AnimatorState.playing;
        InvalidateManager.getInstance().startObserve(this);
    }

    public void pause() {
        YafoolLog.d(TAG, "pause");
        mAnimatorState = AnimConstant.AnimatorState.idle;
    }

    public void resume() {
        YafoolLog.d(TAG, "resume");
        mAnimatorState = AnimConstant.AnimatorState.playing;
    }

    public void cancel() {
        YafoolLog.d(TAG, "cancel");
        mAnimatorState = AnimConstant.AnimatorState.idle;
        InvalidateManager.getInstance().cancelObserve(this);
        for (BaseDrawable drawable : mDrawables) {
            drawable.reset();
        }
    }

    /**
     * # mark ------------private function
     */
    private void initialize(@NonNull AnimatorDrawableListener l) {
        mCurLoopTimes = 0;
        mListener = l;
        for (BaseDrawable drawable : mDrawables) {
            drawable.initialize(this);
        }
    }

    /**
     * # mark ------------class or interface
     */
    public static interface AnimatorDrawableListener {
        public void onPrepared();

        public void onEnd();

        public void onError(String msg);
    }
}
