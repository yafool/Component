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

package com.yafool.component.utils;

import android.support.annotation.NonNull;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;


/**
 * @Package: com.yafool.component.utils
 * @ClassName: com.yafool.component.utils.YafoolLog.java
 * @Description: 对player的日志统一分级管理.
 * 1. 用户相关的信息, 用PlayLog.d 打印, release版本中不输入
 * 2. 与私人信息无关, 代码跟踪必要的信息 YafoolLog.i打印
 * 3. 逻辑出错后有默认处理的地方 YafoolLog.w 打印
 * 4. 一旦出错,程序将不能运行的地方 YafoolLog.e打印
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class YafoolLog {
    private static final String TAG = "yafool";

    public static void d(@NonNull String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("%s  %s", tag, message));
        }
    }

    public static void i(@NonNull String tag, String message) {
        Log.i(TAG, String.format("%s  %s", tag, message));
    }

    public static void w(@NonNull String tag, String message) {
        Log.w(TAG, String.format("%s  %s", tag, message));
    }

    public static void e(@NonNull String tag, String message) {
        Log.e(TAG, String.format("%s  %s", tag, message));
    }
}
