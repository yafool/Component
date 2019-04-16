package com.yafool.component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yafool.component.jsonanimator.view.JAnimView;
import com.yafool.component.utils.YafoolLog;

import java.io.FileNotFoundException;

/**
 * @Package: com.yafool.component
 * @ClassName: com.yafool.component.MainActivity.java
 * @Description: TODO
 * @CreateDate: 2019/4/11 4:01 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/11 4:01 PM
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private JAnimView mJAnimView = new JAnimView(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJAnimView = (JAnimView)findViewById(R.id.json_anim_view);

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
            mJAnimView.setDataSource("file:///android_asset/dance_emoji/dance_emoji.animator");
        } catch (FileNotFoundException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        } catch (NullPointerException e) {
            YafoolLog.e(TAG, Log.getStackTraceString(e));
        }
    }


}
