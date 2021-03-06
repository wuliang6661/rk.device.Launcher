package rk.device.launcher.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.adapter.SetDoorSelectListRvAdapter;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.SetDoorRvBO;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;
import rk.device.launcher.widget.lgrecycleadapter.LGRecycleViewAdapter;
import rk.device.launcher.widget.lgrecycleadapter.LGViewHolder;

public class SelectItemListActivity extends BaseActivity {

    @Bind(R.id.rv)
    RecyclerView mRv;
    public static final String KEY_CHECKED_INDEX = "key_checked_index";

    DeviceInfoBO infoBean;      //设备类型

    int type = 1;


    @Override
    protected int getLayout() {
        return R.layout.activity_select_item;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("code", -1000);
        switch (type) {
            case 1:                       //关联设备
                infoBean = (DeviceInfoBO) bundle.getSerializable("data");
                selectPosition = bundle.getInt("position", Integer.MAX_VALUE);
                setTitle(bundle.getString("title", getString(R.string.setting)));
                break;
            default:
                setDefalt();
                break;
        }
    }

    protected void initData() {
        switch (type) {
            case 1:       //关联设备
                setDeviceTypeAdapter();
                break;
        }
    }


    int selectPosition = Integer.MAX_VALUE;

    /**
     * 设置关联设备数据
     */
    private void setDeviceTypeAdapter() {
        LGRecycleViewAdapter<DeviceInfoBO.TlistBean> adapter = new LGRecycleViewAdapter<DeviceInfoBO.TlistBean>(infoBean.getTlist()) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_select_item_list;
            }

            @Override
            public void convert(LGViewHolder holder, DeviceInfoBO.TlistBean tlistBean, int position) {
                if (selectPosition == position) {
                    holder.getView(R.id.iv_check).setVisibility(View.VISIBLE);
                } else {
                    if (selectPosition == Integer.MAX_VALUE) {
                        String spDevice = SPUtils.getString(Constant.DEVICE_TYPE);
                        if (StringUtils.isEmpty(spDevice)) {
                            holder.getView(R.id.iv_check).setVisibility(View.GONE);
                        } else {
                            String[] devices = spDevice.split("_");
                            int id = Integer.parseInt(devices[0]);
                            if (tlistBean.getId() == id) {
                                holder.getView(R.id.iv_check).setVisibility(View.VISIBLE);
                            } else {
                                holder.getView(R.id.iv_check).setVisibility(View.GONE);
                            }
                        }
                    } else {
                        holder.getView(R.id.iv_check).setVisibility(View.GONE);
                    }
                }
                holder.setText(R.id.tv_name, tlistBean.getName());
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, new LGRecycleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                selectPosition = position;
                adapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("data", infoBean.getTlist().get(position).getId() + "_" + infoBean.getTlist().get(position).getName());
                intent.putExtra("position", position);
                setResult(type, intent);
                finish();
            }
        });
        mRv.setAdapter(adapter);
    }


    private ArrayList<SetDoorRvBO> mDataList;
    private SetDoorSelectListRvAdapter mSetDoorSelectListRvAdapter;


    /**
     * 兼容其他最初的页面
     */
    private void setDefalt() {
        Bundle bundle = getIntent().getBundleExtra(Constant.KEY_INTENT);
        String title = bundle.getString(Constant.KEY_TITLE);
        setTitle(title);
        mDataList = bundle.getParcelableArrayList(Constant.KEY_BUNDLE);
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
