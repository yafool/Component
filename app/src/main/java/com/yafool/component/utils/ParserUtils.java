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
import android.util.Log;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.stream.JsonReader;

/**
 * @Package: com.yafool.component.utils
 * @ClassName: com.yafool.component.utils.ParserUtils.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class ParserUtils {

    public static <T> T parser(@NonNull InputStream inputStream, Class<T> classOfT, String tag) {
        T t = null;
        try {
            JsonParser jsonParser = new JsonParser();
            Gson mGson = new Gson();
            JsonReader jreader = new JsonReader(new InputStreamReader(inputStream));
            JsonElement element = jsonParser.parse(jreader);
            t = mGson.fromJson(element, classOfT);
        } catch (JsonIOException ex) {
            YafoolLog.e(tag, Log.getStackTraceString(ex));
        } catch (JsonSyntaxException ex) {
            YafoolLog.e(tag, Log.getStackTraceString(ex));
        }finally {
            return t;
        }
    }
}
