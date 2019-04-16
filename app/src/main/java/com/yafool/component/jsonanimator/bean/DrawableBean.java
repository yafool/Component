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

package com.yafool.component.jsonanimator.bean;

import java.util.ArrayList;

/**
 * @Package: com.yafool.component.jsonanimator.bean
 * @ClassName: com.yafool.component.jsonanimator.bean.DrawableBean.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class DrawableBean {
    private String v;
    private int w;
    private int h;
    private int base;
    private int duration;
    private int loops;
    private ArrayList<LayerBean> layers;

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public ArrayList<LayerBean> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<LayerBean> layers) {
        this.layers = layers;
    }
}
