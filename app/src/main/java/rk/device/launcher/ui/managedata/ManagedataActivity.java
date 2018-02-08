package rk.device.launcher.ui.managedata;


import android.app.ProgressDialog;
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
    @Bind(R.id.view_bg)
    View mBg;
    private ProgressDialog mProgressDialog;
    private View.OnClickListener mPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.popupMenu(mLlSelectType);
            mBg.setVisibility(View.VISIBLE);
            mIvArrow.setRotation(180);
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
        setTitle(getString(R.string.data_manager));
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

    @Override
    public void refreshTypeText(String name) {
        mTvPop.setText(name);
    }

    @Override
    public void dismissPopupWindow() {
        mIvArrow.setRotation(0);
        mBg.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        mProgressDialog.dismiss();
    }
}
