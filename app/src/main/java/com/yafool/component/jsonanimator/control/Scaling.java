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

package com.yafool.component.jsonanimator.control;

import android.graphics.Canvas;

/**
 * @Package: com.yafool.component.jsonanimator.control
 * @ClassName: com.yafool.component.jsonanimator.control.Scaling.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class Scaling {

    private float horizontal;
    private float vertical;

    public Scaling toScaling(int width, int height, Canvas canvas) {
        if (null == canvas) {
            horizontal = 1.0f;
            vertical = 1.0f;
            return this;
        }

        if (0 >= height) {
            vertical = 1.0f;
        } else {
            vertical = (float) canvas.getHeight() / height;
        }

        if (0 >= width) {
            horizontal = 1.0f;
        } else {
            horizontal = (float) canvas.getWidth() / width;
        }

        return this;
    }

    public float getHorizontal() {
        return horizontal;
    }

    public float getVertical() {
        return vertical;
    }
}
