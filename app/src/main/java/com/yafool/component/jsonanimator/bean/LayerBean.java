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

/**
 * @Package: com.yafool.component.jsonanimator.bean
 * @ClassName: com.yafool.component.jsonanimator.bean.LayerBean.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class LayerBean {
    private String type;
    private int x;
    private int y;
    private int w;
    private int h;
    private int speed;
    private String properties;
    private String decode;
    private String[] ress;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getDecode() {
        return decode;
    }

    public void setDecode(String decode) {
        this.decode = decode;
    }

    public String[] getRess() {
        return ress;
    }

    public void setRess(String[] ress) {
        this.ress = ress;
    }
}
