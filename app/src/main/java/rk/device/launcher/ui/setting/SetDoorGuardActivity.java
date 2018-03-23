package rk.device.launcher.ui.setting;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import peripherals.RelayHelper;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.bean.DeviceInfoBO;
import rk.device.launcher.bean.SetDoorRvBO;
import rk.device.launcher.global.Constant;
import rk.device.launcher.ui.main.home.HomeActivity;
import rk.device.launcher.utils.AppManager;
import rk.device.launcher.utils.SPUtils;
import rk.device.launcher.utils.StringUtils;

/**
 * Created by wuliang on 2017/11/22.
 * <p>
 * 门禁设置
 */
public class SetDoorGuardActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_connected_device)
    LinearLayout mLlConnectedDevice;
    @Bind(R.id.ll_turn_on_switch)
    LinearLayout mLlTurnOnSwitch;
    @Bind(R.id.ll_switch_time)
    LinearLayout mLlSwitchTime;
    @Bind(R.id.ll_door_input)
    LinearLayout mLlDoorInput;
    @Bind(R.id.ll_alert_overtime)
    LinearLayout mLlAlertOvertime;
    @Bind(R.id.ll_opendoor_input)
    LinearLayout mLlOpendoorInput;
    @Bind(R.id.ll_red_ray_sensor_input)
    LinearLayout mLlRedRaySensorInput;
    @Bind(R.id.tv_connected_device)
    TextView mTvConnectedDevice;
    @Bind(R.id.tv_lock_quantity)
    TextView mTvLockQuantity;
    @Bind(R.id.tv_alert_input)
    TextView mTvAlertInput;
    @Bind(R.id.tv_alert_overtime)
    TextView mTvAlertOvertime;
    @Bind(R.id.tv_open_door_input)
    TextView mTvOpenDoorInput;
    @Bind(R.id.tv_red_ray_input)
    TextView mTvRedRayInput;
    @Bind(R.id.btn_finish_setting)
    Button mBtnFinishSetting;
    @Bind(R.id.checkbox_off)
    CheckBox checkboxOff;
    @Bind(R.id.edit_open_time)
    EditText editOpenTime;
    @Bind(R.id.langunge)
    TextView langunge;
    @Bind(R.id.select_langunge)
    LinearLayout selectLangunge;

    DeviceInfoBO device; //关联设备数据
    ArrayList<SetDoorRvBO> mDataList;


    private String deviceName;    //选中的关联设备数据
    private int selectPosition = Integer.MAX_VALUE;

    @Override
    protected int getLayout() {
        return R.layout.activity_set_door_guard;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        setTitle(getString(R.string.setting_menjin));
        mBtnFinishSetting.setBackgroundResource(R.drawable.shape_btn_finish_setting);
    }

    protected void initData() {
        setSettingData();
        mBtnFinishSetting.setOnClickListener(this);
        mLlConnectedDevice.setOnClickListener(this);
        selectLangunge.setOnClickListener(this);
        selectPosition = SPUtils.getInt("locale");
        langunge.setText(SPUtils.getInt("locale") == 0 ? getString(R.string.english) : getString(R.string.chinese));
    }


    /**
     * 初始化UI显示（以本地存储的设置为准）
     */
    private void setSettingData() {
        String spDevice = SPUtils.getString(Constant.DEVICE_TYPE);
        if (!StringUtils.isEmpty(spDevice)) {
            String[] device = spDevice.split("_");
            mTvConnectedDevice.setText(device[1]);
        }
        checkboxOff.setChecked(SPUtils.getBoolean(Constant.DEVICE_OFF, true));
        editOpenTime.setText(SPUtils.getString(Constant.DEVICE_TIME));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 0x11:      //关联设备
                deviceName = data.getStringExtra("data");
                selectPosition = data.getIntExtra("position", Integer.MAX_VALUE);
                mTvConnectedDevice.setText(deviceName.split("_")[1]);
                break;
            case 0x22:
                selectPosition = data.getIntExtra(SelectItemListActivity.KEY_CHECKED_INDEX, -1);
                if (selectPosition > -1) {
                    for (SetDoorRvBO setDoorRvBean : mDataList) {
                        setDoorRvBean.isChecked = false;
                    }
                    SetDoorRvBO checkedBean = mDataList.get(selectPosition);
                    checkedBean.isChecked = true;
                    langunge.setText(checkedBean.text);
                }
                break;
        }
    }


    /**
     * 完成设置的点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_finish_setting:      //此处保存本页面的所有设置
                String text = editOpenTime.getText().toString();
                double time = Double.parseDouble(StringUtils.isEmpty(text) ? "0.5" : text);
                if (time < 0.5 || time > 6) {
                    showMessageDialog("开关时间范围为0.5到6秒");
                    return;
                }
                SPUtils.putString(Constant.DEVICE_TIME, StringUtils.isEmpty(text) ? "0.5" : text);
//                SPUtils.putString(Constant.DEVICE_TYPE, deviceName);
                SPUtils.putBoolean(Constant.DEVICE_OFF, checkboxOff.isChecked());
                if (checkboxOff.isChecked()) {
                    RelayHelper.RelaySetOn();
                } else {
                    RelayHelper.RelaySetOff();
                }
                boolean isFirstSetting = SPUtils.getBoolean(Constant.IS_FIRST_SETTING, true);    //是否第一次进入设置
                if (isFirstSetting) {
                    SPUtils.putInt(Constant.SETTING_NUM, Constant.SETTING_TYPE5);
                    if (selectPosition != SPUtils.getInt("locale")) {
                        setLanguage(selectPosition);
                    } else {
                        gotoActivity(SetSysActivity.class, false);
                    }
                } else {
                    if (selectPosition != SPUtils.getInt("locale")) {
                        setLanguage(selectPosition);
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.ll_connected_device:    //关联设备
                if (device == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.guanlian_device));
                bundle.putSerializable("data", device);
                bundle.putInt("code", 1);     //辨别是哪个选项
                bundle.putInt("position", selectPosition);
                Intent intent = new Intent(this, SelectItemListActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0x11);
                break;
            case R.id.select_langunge:     //语言选择
                mDataList = new ArrayList<>();
                mDataList.add(new SetDoorRvBO("English", 0));
                mDataList.add(new SetDoorRvBO("简体中文", 0));
                Bundle bundle1 = new Bundle();
                bundle1.putString(Constant.KEY_TITLE, getString(R.string.select_langunge));
                bundle1.putParcelableArrayList(Constant.KEY_BUNDLE, mDataList);
                Intent intent1 = new Intent(this, SelectItemListActivity.class);
                intent1.putExtra(Constant.KEY_INTENT, bundle1);
                startActivityForResult(intent1, 0x22);
                break;
        }
    }


    private void setLanguage(int language) {
        try {
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (language == Constant.LANGUAGE_EN) {
                config.locale = Locale.ENGLISH;
            } else {
                config.locale = Locale.SIMPLIFIED_CHINESE;
            }
            getResources().updateConfiguration(config, dm);
            SPUtils.putInt("locale", language);
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
