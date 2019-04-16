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

package com.yafool.component.jsonanimator.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.yafool.component.jsonanimator.bean.DrawableBean;
import com.yafool.component.jsonanimator.bean.LayerBean;
import com.yafool.component.jsonanimator.layers.AnimDrawable;
import com.yafool.component.utils.Constants;
import com.yafool.component.utils.FileUtils;
import com.yafool.component.utils.ParserUtils;
import com.yafool.component.utils.YafoolLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Package: com.yafool.component.jsonanimator.view
 * @ClassName: com.yafool.component.jsonanimator.view.JAnimView.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

@SuppressLint("AppCompatCustomView")
public class JAnimView extends ImageView implements AnimDrawable.AnimatorDrawableListener{
    private static final String TAG = JAnimView.class.getSimpleName();

    private Context mContext;
    private Handler mParseHandler;
    private OnJsonAnimListener mListener;
    private AnimDrawable mAnimDrawable;
    
    private static final int MSG_WHAT_CREATE = 111;
    private static final int MSG_WHAT_NOTIFY_END = 112;
    private static final int MSG_WHAT_NOTIFY_ERROR = 113;

    private Handler mHandler;

    public JAnimView(Context context) {
        this(context, null);
    }

    public JAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
    }

    /**
     *  -------mark --- private function
     * */
    private void initialize(){

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_WHAT_CREATE: {
                        setImageDrawable(mAnimDrawable);
                        mListener.onPrepared(JAnimView.this);
                        break;
                    }
                    case MSG_WHAT_NOTIFY_END: {
                        if (null != mListener) {
                            mListener.onEnd(JAnimView.this);
                        }
                        break;
                    }
                    case MSG_WHAT_NOTIFY_ERROR: {
                        if (null != mListener) {
                            mListener.onError(JAnimView.this, (String) msg.obj);
                        }
                        break;
                    }
                    default: {
                        YafoolLog.e(TAG, "msg.what: " + msg.what);
                        break;
                    }
                }
            }
        };

        HandlerThread thread = new HandlerThread(this.getClass().getName());
        thread.start();
        mParseHandler = new Handler(thread.getLooper());

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation();
    }

    @Override
    public void onPrepared() {
        mHandler.removeMessages(MSG_WHAT_CREATE);
        mHandler.sendEmptyMessage(MSG_WHAT_CREATE);
    }

    @Override
    public void onEnd() {
        cancelAnimation();
        mHandler.removeMessages(MSG_WHAT_NOTIFY_END);
        mHandler.sendEmptyMessage(MSG_WHAT_NOTIFY_END);
    }

    @Override
    public void onError(String msg) {
        mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, msg));
    }

    public void setDataSource(@NonNull final String jsonFile) throws FileNotFoundException {
        if (TextUtils.isEmpty(jsonFile) || !FileUtils.isValidFile(jsonFile, TAG)) {
            throw new FileNotFoundException("json file path is null in " + this.getClass().getName());
        }

        clean();

        mParseHandler.post(new Runnable() {
            @Override
            public void run() {

                DrawableBean drawableBean;

                if (jsonFile.startsWith(Constants.Prefix.ASSETS)) {
                    drawableBean = parserAssets(jsonFile);
                } else {
                    drawableBean = parserFile(new File(jsonFile));
                }

                if (null != drawableBean) {
                    try {
                        mAnimDrawable = AnimDrawable.create(getContext(), drawableBean, JAnimView.this);
                    } catch (IllegalArgumentException e) {
                        YafoolLog.e(TAG, Log.getStackTraceString(e));
                        mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, e.getMessage()));
                    }
                }
            }
        });
    }


    /**
     * 设置动画监听
     *
     * @callback onPrepared(): 容器内每层动画都是通过解析 json文件获取动画属性参数的,所以需要准备时间. 准备好后就可以开始动画了
     * @callback onEnd(): 动画结束
     * @callback onError(String msg): 在动画资源解析 或 播放过程中有一层动画出错都回调该方法
     */
    public void setAnimationListener(OnJsonAnimListener l) {
        mListener = l;
    }

    /**
     * 开始 动画
     */
    public void startAnimation() {
        if (!this.isAttachedToWindow()) {
            YafoolLog.w(TAG, "start error! " + this.getClass().getName() + " haven't attached!");
            return;
        }

        if (null != mAnimDrawable) {
            mAnimDrawable.start();
        } else {
            YafoolLog.w(TAG, "start error! AnimatorDrawable is null!");
        }
    }

    /**
     * 结束/清除 动画
     */
    public void cancelAnimation() {
        if (!this.isAttachedToWindow()) {
            YafoolLog.w(TAG, "start error! " + this.getClass().getName() + " haven't attached!");
            return;
        }

        clean();

        if (null != mAnimDrawable) {
            mAnimDrawable.cancel();
        } else {
            YafoolLog.w(TAG, "cancel error! AnimatorDrawable is null!");
        }
    }

    /**
     * 暂停动画
     */
    public void pauseAnimation() {
        if (!this.isAttachedToWindow()) {
            YafoolLog.w(TAG, "pause error! " + this.getClass().getName() + " haven't attached!");
            return;
        }

        if (null != mAnimDrawable) {
            mAnimDrawable.pause();
        } else {
            YafoolLog.w(TAG, "pause error! AnimatorDrawable is null!");
        }
    }

    /**
     * 恢复动画
     */
    public void resumeAnimation() {
        if (!this.isAttachedToWindow()) {
            YafoolLog.w(TAG, "resume error! " + this.getClass().getName() + " haven't attached!");
            return;
        }

        if (null != mAnimDrawable) {
            mAnimDrawable.resume();
        } else {
            YafoolLog.w(TAG, "resume error! AnimatorDrawable is null!");
        }
    }

    /**
     * # mark ------------private function
     */
    private void clean() {
        if (null != mHandler) {
            mHandler.removeMessages(MSG_WHAT_CREATE);
            mHandler.removeMessages(MSG_WHAT_NOTIFY_END);
            mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
        }
    }

    private void init() {
        HandlerThread thread = new HandlerThread(this.getClass().getName());
        thread.start();
        mParseHandler = new Handler(thread.getLooper());

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_WHAT_CREATE: {
                        setImageDrawable(mAnimDrawable);
                        mListener.onPrepared(JAnimView.this);
                        break;
                    }
                    case MSG_WHAT_NOTIFY_END: {
                        if (null != mListener) {
                            mListener.onEnd(JAnimView.this);
                        }
                        break;
                    }
                    case MSG_WHAT_NOTIFY_ERROR: {
                        if (null != mListener) {
                            mListener.onError(JAnimView.this, (String) msg.obj);
                        }
                        break;
                    }
                    default: {
                        YafoolLog.e(TAG, "msg.what: " + msg.what);
                        break;
                    }
                }
            }
        };
    }

    private DrawableBean parserAssets(@NonNull final String jsonFile) {
        DrawableBean drawableBean = null;
        InputStream inputStream = null;
        try {
            inputStream = getContext().getAssets().open(jsonFile.replace(Constants.Prefix.ASSETS, ""));
            drawableBean = ParserUtils.parser(inputStream, DrawableBean.class, TAG);
        } catch (Exception ex) {
            YafoolLog.e(getClass().getSimpleName(), Log.getStackTraceString(ex));
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    YafoolLog.w(getClass().getSimpleName(), Log.getStackTraceString(e));
                }
            }

            if (null == drawableBean) {
                mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, "parse json file error!"));
                return null;
            }

            ArrayList<LayerBean> layers = drawableBean.getLayers();
            if (null == layers || layers.isEmpty()) {
                mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, "layers is empty!"));
                return null;
            }

            return drawableBean;
        }
    }

    private DrawableBean parserFile(@NonNull final File file) {

        DrawableBean drawableBean = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            drawableBean = ParserUtils.parser(inputStream, DrawableBean.class, TAG);
        } catch (Exception ex) {
            YafoolLog.e(getClass().getSimpleName(), Log.getStackTraceString(ex));
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    YafoolLog.w(getClass().getSimpleName(), Log.getStackTraceString(e));
                }
            }

            if (null == drawableBean) {
                mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, "parse json file error!"));
                return null;
            }

            ArrayList<LayerBean> layers = drawableBean.getLayers();
            if (null == layers || layers.isEmpty()) {
                mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, "layers is empty!"));
                return null;
            }

            String dir = file.getParent();
            for (LayerBean bean : layers) {
                String[] ress = bean.getRess();
                if (null == ress || 0 >= ress.length) {
                    mHandler.removeMessages(MSG_WHAT_NOTIFY_ERROR);
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT_NOTIFY_ERROR, bean.getType() + " have no res!"));
                    return null;
                }

                for (int i = 0; i < ress.length; i++) {
                    ress[i] = dir + File.separatorChar + ress[i];
                }

                bean.setRess(ress);
            }
        }

        return drawableBean;
    }


    /**
     * # mark ------------class or interface
     */
    public static interface OnJsonAnimListener {
        public void onPrepared(JAnimView view);

        public void onEnd(JAnimView view);

        public void onError(JAnimView view, String msg);
    }
}
