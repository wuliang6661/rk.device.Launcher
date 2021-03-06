package rk.device.launcher.ui.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.BlueToothBO;
import rk.device.launcher.bean.event.BlueToothEvent;
import rk.device.launcher.utils.bluetools.MoreManager;
import rk.device.launcher.utils.rxjava.RxBus;
import rk.device.launcher.widget.MyListView;
import rk.device.launcher.widget.adapter.CommonAdapter;
import rk.device.launcher.widget.adapter.ViewHolder;

/**
 * 蓝牙 Created by hanbin on 2017/11/24.
 */
public class BlueToothActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener {

    @Bind(R.id.checkbox_blue)
    CheckBox blueCheckBox;
    @Bind(R.id.checkbox_open)
    CheckBox openCheckBox;
    @Bind(R.id.ll_connected_device)
    RelativeLayout connectedDeviceLL;
    @Bind(R.id.ll_searched_device)
    RelativeLayout searchedDeviceLL;
    @Bind(R.id.list_view_searched)
    MyListView searchedListView;
    @Bind(R.id.blue_seach)
    TextView blueSeach;

    private BluetoothClient mClient = MoreManager.getBluetoothClient();

    private CommonAdapter<BlueToothBO> mAdapter = null;
    private List<BlueToothBO> dataList = new ArrayList<>();
    private List<String> addressList = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_blue_tooth;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        blueCheckBox.setOnCheckedChangeListener(this);
        openCheckBox.setOnCheckedChangeListener(this);
        mAdapter = new CommonAdapter<BlueToothBO>(this, dataList,
                R.layout.item_layout_bluetooth) {
            @Override
            public void convert(ViewHolder helper, BlueToothBO item) {
                helper.setText(R.id.tv_name, item.getName());
            }
        };
        searchedListView.setAdapter(mAdapter);
        searchedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlueToothBO item = dataList.get(position);
                RxBus.getDefault().post(new BlueToothEvent(item.getAddress(), item.getName()));
                finish();
            }
        });
        blueSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBlueTooth();
            }
        });
    }

    /**
     * 关闭蓝牙
     */
    private void closeBlueTooth() {
        blueCheckBox.setChecked(false);
        connectedDeviceLL.setVisibility(View.GONE);
        dataList.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 打开蓝牙
     */
    private void openBlueTooth() {
        blueCheckBox.setChecked(true);
        searchedDeviceLL.setVisibility(View.VISIBLE);
        searchBlueTooth();
    }

    protected void initData() {
        setTitle(getString(R.string.blue_tooth));
        //判断蓝牙是否打开
        mClient.registerBluetoothStateListener(bluetoothStateListener);
        if (mClient.isBluetoothOpened()) {
            openBlueTooth();
        } else {
            closeBlueTooth();
        }
    }

    @Override
    protected void onDestroy() {
        mClient.unregisterBluetoothStateListener(bluetoothStateListener);
        super.onDestroy();
    }

    private void searchBlueTooth() {
        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(3000, 3) // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000) // 再扫BLE设备2s
                .build();
        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                BlueToothBO model = new BlueToothBO();
                model.setAddress(device.getAddress());
                model.setName((TextUtils.isEmpty(device.getName()) || device.getName() == null
                        || device.getName().equals("NULL")) ? device.getAddress()
                        : device.getName());
                model.setRssi(device.rssi);
                model.setScanRecord(device.scanRecord);
                if (!addressList.contains(device.getAddress())) {
                    addressList.add(device.getAddress());
                    dataList.add(model);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {
                Log.d("wuliang", "blue cancle!");
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkbox_blue:
                if (isChecked) {
                    mClient.openBluetooth();
                } else {
                    mClient.closeBluetooth();
                }
                break;
            case R.id.checkbox_open:
                T.showShort("开放");
                break;
        }
    }


    BluetoothStateListener bluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed) {
                openBlueTooth();
            }
        }
    };
}
