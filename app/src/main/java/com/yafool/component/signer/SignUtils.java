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
import android.util.Log;

import com.yafool.component.utils.Base64;
import com.yafool.component.utils.YafoolLog;

import java.lang.reflect.Field;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @Package: com.yafool.component.signer
 * @ClassName: com.yafool.component.signer.SignUtils.java
 * @Description: TODO
 * @CreateDate: 2019/4/26 3:23 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/26 3:23 PM
 */
public class SignUtils {
    private static final String TAG = SignUtils.class.getSimpleName();

    public static String sign(Object bean, String privateKey) {

        String content = stringOf(bean);
        if (TextUtils.isEmpty(content)){
            return null;
        }

        if (TextUtils.isEmpty(privateKey)){
            privateKey = SignConstant.Config.PRIVATE_KEY;
        }

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(SignConstant.Config.ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SignConstant.Config.SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(SignConstant.Config.DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String stringOf(Object bean){
        String retString = null;
        if (null == bean) {
            YafoolLog.e(TAG, "bean == null");
            return retString;
        }

        try {
            String format = null;
            String connector = null;
            int length = -1;
            Class<?> clazz = Class.forName(bean.getClass().getName());
            SignFormat signFormat = clazz.getAnnotation(SignFormat.class);
            if (null != signFormat) {
                format = signFormat.format();
                connector = signFormat.connector();
                length = signFormat.maxSize();
            }

            if (TextUtils.isEmpty(format) || TextUtils.isEmpty(connector) || length <= 0){
                YafoolLog.e(TAG, String.format("format:%s; connector:%s; length:%d", format, connector, length));
                return null;
            }

            ElementFormat elementFormat = ElementFormat.formatOf(format);
            String[] elements = new String[length];

            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                MustSigned annotation = f.getAnnotation(MustSigned.class);
                if (null!=annotation && annotation.value()){
                    elements[annotation.index()] = elementFormat.format(f.getName(), String.valueOf(f.get(bean)));
                }
            }

            StringBuffer stringBuf = new StringBuffer();
            for (int i=0; i<elements.length; i++){
                if (TextUtils.isEmpty(elements[i])){
                    continue;
                }
                stringBuf.append(elements[i]);
                if (i<elements.length-1){
                    stringBuf.append(connector);
                }
            }

            retString = stringBuf.toString();
        } catch (ClassNotFoundException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        } catch (IllegalAccessException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        } finally {
            return retString;
        }
    }
}
