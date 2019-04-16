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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yafool.component.jsonanimator.bean.LayerBean;
import com.yafool.component.jsonanimator.control.AnimConstant;
import com.yafool.component.jsonanimator.control.*;


/**
 * @Package: com.yafool.component.jsonanimator.layers
 * @ClassName: com.yafool.component.jsonanimator.layers.BaseDrawable.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public abstract class BaseDrawable extends Drawable {

    int refreshRate;

    OnDrawListener mListener;

    public BaseDrawable(@NonNull Context context, @NonNull LayerBean layer) {
        if (null == context) {
            throw new NullPointerException("context is null in " + this.getClass().getName());
        }

        if (null == layer) {
            throw new NullPointerException("LayerBean is null in " + this.getClass().getName());
        }

        if (TextUtils.isEmpty(layer.getType())) {
            throw new IllegalArgumentException("unknown layer type in " + this.getClass().getName());
        }

        if (null == layer.getRess() || 0 >= layer.getRess().length) {
            throw new IllegalArgumentException("have no res in " + this.getClass().getName());
        }

    }

    public void initialize(@NonNull OnDrawListener l) {
        if (null == l) {
            throw new NullPointerException("OnDrawListener is null in " + this.getClass().getName());
        }

        mListener = l;
        refreshRate = AnimConstant.Default.REFRESH_RATE;
    }

    public void setOnDrawListener(OnDrawListener l) {
        mListener = l;
    }

    /**
     * mark ------ abstract function
     */
    abstract void reset();
    abstract void onScaling(Scaling scaling);
    /**
     * mark ------ enum or class or interface
     */

    public static interface OnDrawListener {
        public void onPrepared(Drawable self);

        public void onError(Drawable self, String message);

        public void onEnd(Drawable self);
    }
}
