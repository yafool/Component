# component
Accumulate some components

这个仓库下都是日常开发中总结出的一些小功能集

集合目录：
1. JsonAnimator
2. imagedownloader

#### --------------------------------------------- ####
1. JsonAmimator
1.1. 概述
    JsonAmimator是通过LayerDrawable实现的，在指定帧率下，从底层开始逐层绘制json文件中描述的内容。
json文件类似下面这样的
{
    "v":"1.0",
    "w":1200,
    "h":1960,
    "base":0,
    "duration":60000,
    "loops":1,
    "layers":[
        {
            "type":"background",
            "x":0,
            "y":0,
            "w":1200,
            "h":1960,
            "speed":0,
            "properties":"horizontal",
            "ress":[
                "/sdcard/image/bg.png"
            ]
        },
        {
            "type":"property",
            "x":0,
            "y":0,
            "w":1460,
            "h":460,
            "speed":0,
            "properties":"right2left",
            "ress":[
                "/sdcard/image/scroll.png"
            ]
        },
        {
            "type":"frame",
            "x":0,
            "y":0,
            "w":320,
            "h":960,
            "speed":0,
            "properties":"",
            "ress":[
                "/sdcard/image/frame1.png",
                "/sdcard/image/frame1.png",
                "/sdcard/image/frame1.png",
                "/sdcard/image/frame1.png"
            ]
        }
    ]
}
1.2. 使用
   原理：根据json文件描述的内容生成 AnimDrawable，然后将AnimDrawable放到 JAnimView里播放
所以，对使用的人来说，只需要知道 JAnimView 并且传入json文件即可。

        JAnimView mJAnimView = new JAnimView(this);
        mJAnimView.setAnimationListener(new JAnimView.OnJsonAnimListener() {
            @Override
            public void onPrepared(JAnimView view) {
                mJAnimView.startAnimation();
            }

            @Override
            public void onEnd(JAnimView view) {

            }

            @Override
            public void onError(JAnimView view, String msg) {
                YafoolLog.e(TAG, msg);
            }
        });

        try {
            mJAnimView.setDataSource("/sdcard/animator/anim.json");
        } catch (FileNotFoundException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        } catch (NullPointerException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }

1.3. 参考资源
    



#### --------------------------------------------- ####
2. imagedownloader
2.1. 概述
   由于image太耗内存，所以，自定义LruCache，使用软引用，将回收交给GC。
2.2. 使用
2.3. 参考资源

