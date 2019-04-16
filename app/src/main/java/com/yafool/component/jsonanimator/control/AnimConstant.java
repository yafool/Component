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

/**
 * @Package: com.yafool.component.jsonanimator.control
 * @ClassName: com.yafool.component.jsonanimator.control.AnimConstant.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class AnimConstant {

    public static class Default{
        public static final int REFRESH_RATE = 40; // 单位: ms
    }

    public static enum AnimatorState {
        idle, playing;
    }

    public static enum Properties {
        right2left("right2left"),
        left2right("left2right"),
        down2up("down2up"),
        up2down("up2down"),
        vertical("vertical"),
        horizontal("horizontal"),
        fit("fit");

        private String name;

        Properties(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static enum LayerType {
        background("background"),
        property("property"),
        json("json"),
        frame("frame");

        private String mName;

        LayerType(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}
