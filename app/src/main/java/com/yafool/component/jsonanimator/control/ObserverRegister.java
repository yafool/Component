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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;


import java.util.concurrent.CopyOnWriteArrayList;

import com.yafool.component.utils.YafoolLog;

/**
 * @Package: com.yafool.component.jsonanimator.control
 * @ClassName: com.yafool.component.jsonanimator.control.ObserverRegister.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class ObserverRegister {
    private static final String TAG = ObserverRegister.class.getSimpleName();

    private CopyOnWriteArrayList<IInvalidateObserver> mInvalidateList = new CopyOnWriteArrayList<>();

    private static final int MSG_WHAT_INVALIDATE = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_INVALIDATE: {
                    for (IInvalidateObserver observer : mInvalidateList) {
                        observer.onInvalidate();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    public void registObserver(@NonNull IInvalidateObserver observer) {
        if (null == observer) {
            throw new IllegalArgumentException("can't register null observer!");
        }

        if (mInvalidateList.contains(observer)) {
            YafoolLog.w(TAG, "this observer has been registered repeatedly!");
            return;
        }

        mInvalidateList.add(observer);
    }

    public int unRegistObserver(@NonNull IInvalidateObserver observer) {
        if (null == observer) {
            throw new IllegalArgumentException("can't unregister null observer!");
        }

        int ret = -1;
        if (mInvalidateList.contains(observer)) {
            mInvalidateList.remove(observer);
            ret = mInvalidateList.size();
        }

        return ret;
    }

    public void clearObserver() {
        mInvalidateList.clear();
    }

    public void notifyObserver() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            for (IInvalidateObserver observer : mInvalidateList) {
                observer.onInvalidate();
            }
        } else {
            mHandler.sendEmptyMessage(MSG_WHAT_INVALIDATE);
        }
    }
}
