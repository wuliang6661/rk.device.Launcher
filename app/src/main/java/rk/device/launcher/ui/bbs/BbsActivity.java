package rk.device.launcher.ui.bbs;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.recoder.RecordPlayer;
import rk.device.launcher.utils.recoder.RecorderUtil;


/**
 * MVPPlugin
 * <p>
 * 留言页面
 */

public class BbsActivity extends MVPBaseActivity<BbsContract.View, BbsPresenter> implements BbsContract.View,
        View.OnTouchListener, View.OnClickListener {

    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.commit_text)
    TextView commitText;
    @Bind(R.id.bbs_text)
    TextView bbsText;


    RecorderUtil recorderUtil;
    RecordPlayer recordPlayer;


    @Override
    protected int getLayout() {
        return R.layout.act_bbs;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle("留言");
        commitText.setOnTouchListener(this);
        commitText.setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(manager);

        recorderUtil = new RecorderUtil();
        recordPlayer = new RecordPlayer(this);

        bbsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordPlayer.playRecordFile(new File(recorderUtil.getFilePath()));
            }
        });
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:    //按下
                Log.i("wuliang", "start recorder");
                recorderUtil.startRecording();
                break;
            case MotionEvent.ACTION_UP:    //松开
                Log.i("wuliang", "stop recorder");
                recorderUtil.stopRecording();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }
}
