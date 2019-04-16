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


import java.util.Timer;
import java.util.TimerTask;

/**
 * @Package: com.yafool.component.jsonanimator.control
 * @ClassName: com.yafool.component.jsonanimator.control.InvalidateManager.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class InvalidateManager{
    private static final String TAG = InvalidateManager.class.getSimpleName();

    public ObserverRegister mObserverRegister = new ObserverRegister();


    private static InvalidateManager mInstance = new InvalidateManager();

    private Timer mTimer;
    private InvalidateTimerTask mInvalidateTimerTask;

    private InvalidateManager(){}

    public static InvalidateManager getInstance(){
        return mInstance;
    }

    public void startObserve(IInvalidateObserver observer){
        mObserverRegister.registObserver(observer);
        if (null == mTimer || null == mInvalidateTimerTask){
            mTimer = new Timer();
            mInvalidateTimerTask = new InvalidateTimerTask();
            mTimer.schedule(mInvalidateTimerTask, 0, AnimConstant.Default.REFRESH_RATE);
        }
    }

    public void cancelObserve(IInvalidateObserver observer){
        if (0 == mObserverRegister.unRegistObserver(observer)){
            mTimer.purge();
            mTimer.cancel();
            mInvalidateTimerTask.cancel();
            mTimer = null;
            mInvalidateTimerTask =null;
        }
    }

    class InvalidateTimerTask extends TimerTask {
        @Override
        public void run() {
            mObserverRegister.notifyObserver();
        }
    };
}
