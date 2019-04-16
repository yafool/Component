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

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Package: com.yafool.component.utils
 * @ClassName: com.yafool.component.utils.FileUtils.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class FileUtils {

    public static boolean isValidFile(String path, String tag) {
        if (TextUtils.isEmpty(path)) {
            Log.d(tag, "isValidFile path == null");
            return false;
        }

        if (path.startsWith(Constants.Prefix.ASSETS)) {
            return true;
        }

        File file = new File(path);
        if (file.exists()) {//判断文件目录的存在
            return true;
        }


        return isUrl(path);
    }

    public static boolean isUrl(String urls) {
        String regex = "(((gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();//判断是否匹配
    }
}
