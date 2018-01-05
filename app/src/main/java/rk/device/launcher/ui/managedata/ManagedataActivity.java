package rk.device.launcher.ui.managedata;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ManagedataActivity extends MVPBaseActivity<ManagedataContract.View, ManagedataPresenter> implements ManagedataContract.View {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.title_right)
    ImageView mTitleRight;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.rv)
    RecyclerView mRv;
    @Bind(R.id.tv_pop)
    TextView mTvPop;
    @Bind(R.id.iv_arrow)
    ImageView mIvArrow;
    @Bind(R.id.ll_select_type)
    LinearLayout mLlSelectType;
    private View.OnClickListener mPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.popupMenu(mLlSelectType);
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_manage_data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        setTitle("数据管理");
        mIvSearch.setVisibility(View.VISIBLE);
        bindListener();
        mPresenter.initData(mRv);
    }

    private void bindListener() {
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.goToSearchActivity();
            }
        });
        mTvPop.setOnClickListener(mPopupListener);
        mIvArrow.setOnClickListener(mPopupListener);


    }

}
