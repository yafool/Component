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


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;


import com.yafool.component.imageloader.cache.BitmapCache;
import com.yafool.component.utils.Constants;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Package: com.yafool.component.imageloader
 * @ClassName: com.yafool.component.imageloader.ImageLoader.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();

    private static final int MESSAGE_WHAT_SETIMAGE = 22;

    private BitmapDecode mBitmapDecode;
    private BitmapCache mBitmapCache;
    private Handler mHandler;

    private LoadHolder mLoadLoadHolder;

    private static ImageLoader mInstance;

    private ImageLoader() {
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() >> 12);
        mBitmapCache = new BitmapCache(cacheSize);

        mBitmapDecode = new BitmapDecode();
        mLoadLoadHolder = new LoadHolder();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_WHAT_SETIMAGE: {
                        MessageObject obj = (MessageObject) msg.obj;
                        ImageView imgView = obj.imageView;
                        Bitmap bmp = obj.bmp;

                        if (!mBitmapDecode.gradientImage(bmp, imgView)) {
                            if (null != bmp) {
                                imgView.setImageBitmap(bmp);
                            } else {
                                Log.e(TAG, "this imageView have no Drawable!");
                            }

                        }
                        break;
                    }
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

    }

    public static synchronized ImageLoader getInstance() {
        if (null == mInstance) {
            mInstance = new ImageLoader();
        }

        return mInstance;
    }

    class LoadMessage {
        IImageLoadListener loadListener;
        Bitmap bmp;

        LoadMessage(IImageLoadListener loadListener, Bitmap bmp) {
            this.loadListener = loadListener;
            this.bmp = bmp;
        }
    }

    public void displayImage(final Context ctx, final String path, final int width, final int height
            , final IImageLoadListener loadListener) throws NullPointerException{
        if (null == ctx || TextUtils.isEmpty(path) || null == loadListener) {
            throw new NullPointerException("path is empty || imageView == null");
        }

        if (!isValidFile(path)) {
            throw new NullPointerException("path is invalid! path: " + path);
        }

        Bitmap bmp = mBitmapCache.get(Md5.stringMD5(path));
        if (null == bmp) {
            ThreadPool.getInstance().addExecuteTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap tmpBmp = null;
                    try {
                        if (!loadListener.isCancel())
                            tmpBmp = mBitmapDecode.toBitmap(ctx, path, width, height);
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    } finally {
                        final LoadMessage loadMessage = new LoadMessage(loadListener, tmpBmp);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMessage.loadListener.loadComplete(loadMessage.bmp);
                            }
                        });
                    }
                }
            });
        } else {
            loadListener.loadComplete(bmp);
        }
    }

    public void displayImage(final Context ctx, final String path, final ImageView imageView, final int defRes) {
        if (null == ctx || (TextUtils.isEmpty(path) && 0 > defRes) || null == imageView) {
            throw new NullPointerException("ctx == null || path is empty || imageView == null");
        }

        if (!isValidFile(path) && 0 > defRes) {
            throw new NullPointerException("path is invalid! path: " + path);
        }

        final int viewWidth = imageView.getWidth();
        final int viewHeight = imageView.getHeight();

        final String key = TextUtils.isEmpty(path) ? ctx.getResources().getResourceName(defRes) : Md5.stringMD5(path);
        final Bitmap bmp = mBitmapCache.get(key);
        if (null == bmp) {
            ThreadPool.getInstance().addExecuteTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap tmpBmp = null;
                    try {
                        tmpBmp = mBitmapDecode.toBitmap(ctx, path, viewWidth, viewHeight);
                        if (null == tmpBmp) {
                            Log.w(TAG, "BitmapDecode failed! path: " + path);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    } finally {
                        if (null == tmpBmp && 0 < defRes) {
                            tmpBmp = BitmapFactory.decodeResource(ctx.getResources(), defRes);
                            if (null == tmpBmp) {
                                Log.e(TAG, "decode default Resource failed! defRes: " + ctx.getResources().getResourceName(defRes));
                            }
                        }

                        if (null != tmpBmp) {
                            mBitmapCache.put(key, tmpBmp);
                            updateImageView(imageView, tmpBmp);
                        }
                    }
                }
            });

        } else {
            updateImageView(imageView, bmp);
        }
    }

    /**
     * 可以预加载一组图片
     * 回调后需要用 getCacheBitmap 方法从缓存中取图片
     */
    public void preLoadBitmap(@NonNull final Context context,
                              @NonNull final String[] paths,
                              final int width,
                              final int height,
                              final IImageLoadListener loadListener) throws NullPointerException{

        if (null == context){
            throw new NullPointerException("preLoadBitmap context is null!");
        }

        for (String path : paths) {
            if (!isValidFile(path)) {
                throw new NullPointerException("path is invalid! path: " + path);
            }
        }

        ThreadPool.getInstance().addExecuteTask(new Runnable() {
            @Override
            public void run() {

                try {
                    for (String path : paths) {
                        if (loadListener.isCancel()) {
                            Log.w(TAG, "preLoadBitmap has been canceled!");
                            break;
                        }

                        Bitmap bmp = mBitmapDecode.toBitmap(context, path, width, height);
                        if (bmp != null) {
                            mBitmapCache.put(Md5.stringMD5(path), bmp);
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } finally {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadListener.loadComplete(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * 可以丢弃掉path所指定的图片,不处理
     * 丢弃时如果已经缓存了,就不予理睬; 如果还没有解码,就放弃解码
     */
    public void cancelFlashLoad(@NonNull final String path) {
        mLoadLoadHolder.remove(path);
    }

    /**
     * 从缓存中去指定图片
     */
    @AnyThread
    public Bitmap getCacheBitmap(@NonNull final String path) {
        if (!isValidFile(path)) {
            throw new NullPointerException("path is invalid! path: " + path);
        }

        return mBitmapCache.get(Md5.stringMD5(path));
    }

    /**
     * 命令去缓存指定图片
     */
    public void flashLoadBitmap(@NonNull final Context context,
                                @NonNull final String path,
                                final int width,
                                final int height) {
        if (null == context){
            throw new NullPointerException("flashLoadBitmap context is null!");
        }

        if (!isValidFile(path)) {
            throw new NullPointerException("path is invalid! path: " + path);
        }

        final String key = Md5.stringMD5(path);
        if (null != mBitmapCache.get(key)) {
            return;
        }

        mLoadLoadHolder.add(path);
        ThreadPool.getInstance().addExecuteTask(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!mLoadLoadHolder.contains(path)) {
                        Log.w(TAG, path + " has been canceled!");
                        return;
                    }

                    Bitmap bmp = mBitmapDecode.toBitmap(context, path, width, height);
                    if (bmp != null) {
                        mBitmapCache.put(Md5.stringMD5(path), bmp);
                        mLoadLoadHolder.remove(path);
                    }

                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        });
    }

    /**
     * 这个接口必须在 非主UI线程调用!!!
     * 因为从绝对路径加载图片到内存中会有 图片解码过程,所以 放在了非UI线程下处理了.
     */
    @WorkerThread
    public Bitmap getBitmap(@NonNull final Context context,
                            @NonNull String path,
                            int width,
                            int height) throws NullPointerException{
        if (null == context){
            throw new NullPointerException("getBitmap context is null!");
        }

        if (!isValidFile(path)) {
            throw new NullPointerException("path is invalid! path: " + path);
        }

        final String key = Md5.stringMD5(path);
        Bitmap bmp = mBitmapCache.get(key);
        if (null == bmp) {
            try {
                bmp = mBitmapDecode.toBitmap(context, path, width, height);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        if (null == bmp) {
            Log.e(TAG, String.format("%s no cache and can't decode by BitmapFactory"));
            return null;
        } else {
            mBitmapCache.put(key, bmp);
            return bmp;
        }
    }

    private void updateImageView(ImageView imageView, Bitmap bmp) {
        MessageObject obj = new MessageObject();
        obj.imageView = imageView;
        obj.bmp = bmp;
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_SETIMAGE;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    private boolean isValidFile(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.d(TAG, "isValidFile path == null");
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

    private boolean isUrl(String urls) {
        String regex = "(((gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();//判断是否匹配
    }

    class MessageObject {
        public ImageView imageView;
        public Bitmap bmp;
    }

    class LoadHolder {
        private List<String> mVector = new Vector();

        public void add(String element) {
            if (!mVector.contains(element)) {
                mVector.add(element);
            }
        }

        public void add(String[] els) {
            for (String element : els) {
                if (!mVector.contains(element)) {
                    mVector.add(element);
                }
            }
        }

        public void remove(String element) {
            if (mVector.contains(element)) {
                mVector.remove(element);
            }
        }

        public void remove(String[] els) {
            for (String element : els) {
                if (mVector.contains(element)) {
                    mVector.remove(element);
                }
            }
        }

        public boolean contains(String element) {
            return mVector.contains(element);
        }

        public void clear() {
            mVector.clear();
        }
    }

    public interface IImageLoadListener {
        void loadComplete(Bitmap bmp);

        boolean isCancel();
    }
}
