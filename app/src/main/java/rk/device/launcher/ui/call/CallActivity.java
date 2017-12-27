package rk.device.launcher.ui.call;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import peripherals.NumberpadHelper;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.widget.lgrecycleadapter.LGRecycleViewAdapter;
import rk.device.launcher.widget.lgrecycleadapter.LGViewHolder;


/**
 * Created by wuliang on 2017/12/22.
 * <p>
 * 拨号界面
 */

public class CallActivity extends MVPBaseActivity<CallContract.View, CallPresenter>
        implements CallContract.View, View.OnClickListener {

    @Bind(R.id.edit_text)
    TextView editText;
    @Bind(R.id.clear)
    ImageView clear;
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.call_commit)
    RelativeLayout callCommit;
    @Bind(R.id.clear_dan)
    ImageView clearDan;

    List<String> callbutton;
    StringBuilder commitText;


    @Override
    protected int getLayout() {
        return R.layout.act_call;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    protected void initView() {
        goBack();
        setTitle("拨号通话");
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recycle.setLayoutManager(manager);
    }

    protected void initData() {
        setData();
        setAdapter();
        callCommit.setOnClickListener(this);
        clearDan.setOnClickListener(this);
        clear.setOnClickListener(this);
    }


    /**
     * 填充字符
     */
    private void setData() {
        callbutton = new ArrayList<>();
        commitText = new StringBuilder();
        callbutton.add("1");
        callbutton.add("2");
        callbutton.add("3");
        callbutton.add("4");
        callbutton.add("5");
        callbutton.add("6");
        callbutton.add("7");
        callbutton.add("8");
        callbutton.add("9");
        callbutton.add("*");
        callbutton.add("0");
        callbutton.add("#");
    }

    /**
     * 设置布局显示
     */
    private void setAdapter() {
        LGRecycleViewAdapter<String> adapter = new LGRecycleViewAdapter<String>(callbutton) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_call_button;
            }

            @Override
            public void convert(LGViewHolder holder, String s, int position) {
                holder.setText(R.id.call_text, s);
            }
        };
        adapter.setOnItemClickListener(R.id.call_layout, new LGRecycleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                if (commitText.length() < 4) {
                    commitText.append(callbutton.get(position));
                    editText.setText(commitText.toString());
                }
            }
        });
        recycle.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_commit:
                if (commitText.length() != 4) {
                    showMessageDialog("输入的房间号必须为4位！");
                } else {
                    int suress = NumberpadHelper.PER_numberpadPress(commitText.toString());
                    Log.d("wuliang", "call_button " + commitText.toString() + "  code = " + suress);
                }
                break;
            case R.id.clear_dan:
                if (commitText.length() != 0) {
                    commitText.deleteCharAt(commitText.length() - 1);
                    editText.setText(commitText.toString());
                }
                break;
            case R.id.clear:
                if (commitText.length() != 0) {
                    commitText.delete(0, commitText.length());
                    editText.setText(commitText.toString());
                }
                break;
        }
    }
}