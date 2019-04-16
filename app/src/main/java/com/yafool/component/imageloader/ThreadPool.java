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

import com.yafool.component.utils.YafoolLog;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Package: com.yafool.component.imageloader
 * @ClassName: com.yafool.component.imageloader.ThreadPool.java
 * @Description: 线程池管理(线程统一调度管理)
 * @Params: mPendingQueue --- 任务缓冲队列
 * @Params: mScheduler --- 创建一个调度线程池
 * @Params: mRejectedExecutionHandler --- 线程池超出界线时将任务加入缓冲队列
 * @Params: mThreadPool --- 线程池
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public final class ThreadPool {
    private static final String TAG = ThreadPool.class.getSimpleName();

    private static ThreadPool sThreadPool = new ThreadPool();

    // 线程池维护线程的最少数量
    private static final int SIZE_CORE_POOL = 2;
    // 线程池维护线程的最大数量
    private static final int SIZE_MAX_POOL = 4;
    // 线程池维护线程所允许的空闲时间 (秒:s)
    private static final int TIME_KEEP_ALIVE = 60;
    // 线程池所使用的缓冲队列大小
    private static final int SIZE_PENDING_QUEUE = 10;
    // 任务调度周期
    private static final int PERIOD_SCHEDULE = 1000;

    private final Queue<Runnable> mPendingQueue = new LinkedList<Runnable>();
    private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private final RejectedExecutionHandler mRejectedExecutionHandler;
    private final ThreadPoolExecutor mThreadPool;

    public static ThreadPool getInstance() {
        return sThreadPool;
    }

    private ThreadPool() {
        mRejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
                YafoolLog.d(TAG, "task queue offer rejected task");
                mPendingQueue.offer(task);
            }
        };

        mThreadPool = new ThreadPoolExecutor(
                SIZE_CORE_POOL,
                SIZE_MAX_POOL,
                TIME_KEEP_ALIVE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(SIZE_PENDING_QUEUE),
                new MyThreadFactory(),
                mRejectedExecutionHandler);

        schedule();
    }

    public void prepare() {
        if (mThreadPool.isShutdown() && !mThreadPool.prestartCoreThread()) {
            @SuppressWarnings("unused")
            int allCores = mThreadPool.prestartAllCoreThreads();
            Log.d(TAG, "allCores: " + allCores);
        }
    }

    /**
     * 向线程池中添加任务
     */
    public void addExecuteTask(Runnable task) {
        if (task != null) {
            mThreadPool.execute(task);
        }
    }

    public boolean isTaskOver() {
        if (0 == mThreadPool.getActiveCount()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 关闭线程池
     *
     * @NOTE: 关闭线程池后, 线程池状态会一直处于 非RUNNING状态. 该线程池也将无法继续使用.
     */
    @Deprecated
    public void shutdown() {
        mPendingQueue.clear();
        mThreadPool.shutdown();
    }

    /**
     * 消息队列检查方法
     */
    private boolean hasPendingTask() {
        return !mPendingQueue.isEmpty();
    }

    /**
     * 通过调度线程周期性的执行缓冲队列中任务
     */
    private void schedule() {
        mScheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {// 将缓冲队列中的任务重新加载到线程池
                        if (hasPendingTask()) {
                            YafoolLog.d(TAG, "execute task from task queue");
                            mThreadPool.execute(mPendingQueue.poll());
                        }
                    }
                },
                0,
                PERIOD_SCHEDULE,
                TimeUnit.MILLISECONDS);
    }

    class MyThreadFactory implements ThreadFactory {
        protected final AtomicInteger mCount = new AtomicInteger(1);
        protected String mThreadNamePrefix = "Thread from factory";

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, mThreadNamePrefix + " #" + mCount.getAndIncrement());
        }
    }
}
