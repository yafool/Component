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

package com.yafool.component.imageloader;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Package: com.yafool.component.imageloader
 * @ClassName: com.yafool.component.imageloader.Md5.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class Md5 {

    private static final String TAG = Md5.class.getSimpleName();

    public static String fileMD5(File file) {

        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return value;
    }

    public static String stringMD5(String input) {

        String value = null;
        try {
            byte[] inputByteArray = input.getBytes();

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(inputByteArray);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return value;
    }
}
