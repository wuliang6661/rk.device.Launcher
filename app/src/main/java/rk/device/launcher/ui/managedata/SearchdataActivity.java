package rk.device.launcher.ui.managedata;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.widget.ClearEditText;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SearchdataActivity extends MVPBaseActivity<SearchdataContract.View, SearchdataPresenter> implements SearchdataContract.View, TextView.OnEditorActionListener, TextWatcher {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.et)
    ClearEditText mEt;
    @Bind(R.id.rv)
    RecyclerView mRv;

    @Override
    protected int getLayout() {
        return R.layout.activity_search_data;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        bindListener();
        mPresenter.initData(mRv);
    }

    private void bindListener() {
        mEt.setOnEditorActionListener(this);
        mEt.addTextChangedListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String keyword = v.getText().toString();
            // 点击搜索按键进行搜索
            mPresenter.immedidateSearchData(keyword);
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 实时搜索
        mPresenter.immedidateSearchData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
