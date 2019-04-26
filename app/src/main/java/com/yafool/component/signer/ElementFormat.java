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

package com.yafool.component.signer;

import android.text.TextUtils;


/**
 * @Package: com.yafool.component.signer
 * @ClassName: com.yafool.component.signer.ElementFormat.java
 * @Description: TODO
 * @CreateDate: 2019/4/26 4:11 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/26 4:11 PM
 */
public enum ElementFormat {
    symbolism("xxx=xxx"){
        @Override
        String format(String prefix, String suffix) {
            return String.format("%s=%s", prefix, suffix);
        }
    },
    semicolon("xxx:xxx"){
        @Override
        String format(String prefix, String suffix) {
            return String.format("%s:%s", prefix, suffix);
        }
    };

    private String format;
    ElementFormat(String format){
        this.format = format;
    }


    public static ElementFormat formatOf(final String format){
        if (TextUtils.isEmpty(format)){
            throw new IllegalArgumentException("format == null");
        }

        String tmp = format.toLowerCase();
        for (ElementFormat e : ElementFormat.values()){
            if (e.format.equals(tmp)){
                return e;
            }
        }

        return null;
    }

    abstract String format(String prefix, String suffix);
}
