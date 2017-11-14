package rk.device.launcher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.adapter.SetDoorSelectListRvAdapter;
import rk.device.launcher.bean.SetDoorRvBean;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;

public class SelectItemListActivity extends AppCompatActivity {

	@BindView(R.id.iv_back)
	ImageView mIvBack;
	@BindView(R.id.tv_title)
	TextView mTvTitle;
	@BindView(R.id.rv)
	RecyclerView mRv;
	private ArrayList<SetDoorRvBean> mDataList;
	private SetDoorSelectListRvAdapter mSetDoorSelectListRvAdapter;
	public static final String KEY_CHECKED_INDEX = "key_checked_index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getBundleExtra(SetDoorGuardActivity.INTENT_KEY);
		String title = bundle.getString(SetDoorGuardActivity.KEY_TITLE);
		mDataList = bundle.getParcelableArrayList(SetDoorGuardActivity.BUNDLE_KEY);
		setContentView(R.layout.activity_select_item);
		ButterKnife.bind(this);
		if (!TextUtils.isEmpty(title)) {
			mTvTitle.setText(title);
		}
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mSetDoorSelectListRvAdapter = new SetDoorSelectListRvAdapter(mDataList);
		mSetDoorSelectListRvAdapter.setOnItemClickedListener(new SetDoorSelectListRvAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(int position) {
				mDataList.get(position).isChecked = true;
				mSetDoorSelectListRvAdapter.notifyDataSetChanged();
				Intent intent = new Intent();
				intent.putExtra(KEY_CHECKED_INDEX, position);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		mRv.setAdapter(mSetDoorSelectListRvAdapter);
		mRv.addItemDecoration(new WifiListRvItemDecoration(this));

	}
}
