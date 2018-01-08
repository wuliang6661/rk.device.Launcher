package rk.device.launcher.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.service.persistentdata.PersistentDataBlockManager;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.os.storage.ExternalStorageFormatter;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.base.BaseActivity;
import rk.device.launcher.ui.faceadd.FaceAddActivity;
import rk.device.launcher.ui.fragment.RecoveryDialogFragment;
import rk.device.launcher.ui.managedata.ManagedataActivity;
import rk.device.launcher.ui.person_add.Person_addActivity;
import rk.device.launcher.ui.personmanage.PersonManageActivity;
import rk.device.launcher.utils.SettingUtils;

/**
 * Created by mundane on 2017/11/9 上午10:56
 */

public class SettingActivity extends BaseActivity {


    @Bind(R.id.ll_set_time)
    LinearLayout mLlSetTime;
    @Bind(R.id.iv_set_time)
    ImageView mIvSetTime;
    @Bind(R.id.iv_set_network)
    ImageView mIvSetNetwork;
    @Bind(R.id.iv_set_door)
    ImageView mIvSetDoor;
    @Bind(R.id.iv_set_system)
    ImageView mIvSetSystem;
    @Bind(R.id.iv_sys_info)
    ImageView mIvSysInfo;
    @Bind(R.id.iv_recovery)
    ImageView mIvRecovery;
    @Bind(R.id.tv_set_time)
    TextView mTvSetTime;
    @Bind(R.id.tv_set_network)
    TextView mTvSetNetwork;
    @Bind(R.id.tv_set_door)
    TextView mTvSetDoor;
    @Bind(R.id.tv_set_sys)
    TextView mTvSetSys;
    @Bind(R.id.tv_sys_info)
    TextView mTvSysInfo;
    @Bind(R.id.tv_recovery)
    TextView mTvRecovery;
    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.ll_set_net)
    LinearLayout mLlSetNet;
    @Bind(R.id.ll_set_door)
    LinearLayout mLlSetDoor;
    @Bind(R.id.ll_set_sys)
    LinearLayout mLlSetSys;
    @Bind(R.id.ll_sys_info)
    LinearLayout mLlSysInfo;
    @Bind(R.id.ll_recovery)
    LinearLayout mLlRecovery;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_manage_user)
    ImageView mIvManageUser;
    @Bind(R.id.ll_manage_user)
    LinearLayout mLlManageUser;
    @Bind(R.id.iv_manage_data)
    ImageView mIvManageData;
    @Bind(R.id.ll_manage_data)
    LinearLayout mLlManageData;
    @Bind(R.id.tv_manage_user)
    TextView mTvManageUser;
    @Bind(R.id.tv_manage_data)
    TextView mTvManageData;
    private boolean mEraseSdCard = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        goBack();
        setTitle("设置");
    }

    protected void initData() {
        processOnTouchListener(mLlSetTime, mIvSetTime, mTvSetTime, R.drawable.basic_setting_normal, R.drawable.basic_setting_pressed);
        processOnTouchListener(mLlSetNet, mIvSetNetwork, mTvSetNetwork, R.drawable.set_network_normal, R.drawable.set_network_pressed);
        processOnTouchListener(mLlSetDoor, mIvSetDoor, mTvSetDoor, R.drawable.set_door_normal, R.drawable.set_door_pressed);
        processOnTouchListener(mLlSetSys, mIvSetSystem, mTvSetSys, R.drawable.set_system_normal, R.drawable.set_system_pressed);
        processOnTouchListener(mLlSysInfo, mIvSysInfo, mTvSysInfo, R.drawable.system_info_normal, R.drawable.set_system_pressed);
        processOnTouchListener(mLlRecovery, mIvRecovery, mTvRecovery, R.drawable.recovery_normal, R.drawable.recovery_pressed);
        processOnTouchListener(mLlManageUser, mIvManageUser, mTvManageUser, R.drawable.manage_user_normal, R.drawable.manage_user_pressed);
        processOnTouchListener(mLlManageData, mIvManageData, mTvManageData, R.drawable.manage_data_normal, R.drawable.manage_data_pressed);
        mLlManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoActivity(PersonManageActivity.class, false);
//                gotoActivity(FaceAddActivity.class, false);
            }
        });
        mLlManageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(ManagedataActivity.class, false);
            }
        });
        mLlSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetBasicInfoActivity.class);
                startActivity(intent);
            }
        });
        mLlSetNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetNetWorkActivity.class);
                startActivity(intent);
            }
        });
        mLlSetDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetDoorGuardActivity.class);
                startActivity(intent);
            }
        });
        mLlSetSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SetSysActivity.class);
                startActivity(intent);
            }
        });
        mLlSysInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SystemInfoActivity.class);
                startActivity(intent);
            }
        });
        mLlRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecoveryDialogFragment recoveryDialogFragment = RecoveryDialogFragment.newInstance();
                recoveryDialogFragment.setOnCancelClickListener(new RecoveryDialogFragment.onCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        recoveryDialogFragment.dismiss();
                    }
                })
                        .setOnConfirmClickListener(new RecoveryDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick() {
                                if (SettingUtils.isMonkeyRunning()) {
                                    return;
                                }

                                final PersistentDataBlockManager pdbManager = (PersistentDataBlockManager)
                                        SettingActivity.this.getSystemService("persistent_data_block");

                                if (pdbManager != null && !pdbManager.getOemUnlockEnabled() &&
                                        Settings.Global.getInt(SettingActivity.this.getContentResolver(),
                                                Settings.Global.DEVICE_PROVISIONED, 0) != 0) {
                                    // if OEM unlock is enabled, this will be wiped during FR process. If disabled, it
                                    // will be wiped here, unless the device is still being provisioned, in which case
                                    // the persistent data block will be preserved.
                                    final ProgressDialog progressDialog = getProgressDialog();
                                    progressDialog.show();

                                    // need to prevent orientation changes as we're about to go into
                                    // a long IO request, so we won't be able to access inflate resources on flash
                                    final int oldOrientation = SettingActivity.this.getRequestedOrientation();
                                    SettingActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            pdbManager.wipe();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            progressDialog.hide();
                                            SettingActivity.this.setRequestedOrientation(oldOrientation);
                                            doMasterClear();
                                        }
                                    }.execute();
                                } else {
                                    doMasterClear();
                                }
                            }
                        });
                recoveryDialogFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private ProgressDialog getProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("清除中");
        progressDialog.setMessage("请稍后");
        return progressDialog;
    }

    private void doMasterClear() {
        if (mEraseSdCard) {
            Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
            intent.putExtra("android.intent.extra.REASON", "WipeAllFlash");
            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
            startService(intent);
        } else {
            Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
            sendBroadcast(intent);
            // Intent handling is asynchronous -- assume it will happen soon.
        }
    }

    private void processOnTouchListener(final LinearLayout ll, final ImageView iv, final TextView tv, final int normalResource, final int pressedResource) {
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv.setTextColor(getResources().getColor(R.color.white));
//						ll.getBackground().clearColorFilter();
//						ll.getBackground().setColorFilter(getResources().getColor(R.color.half_transparent_black), PorterDuff.Mode.SRC_ATOP);
                        ll.setBackgroundResource(R.color.half_transparent_black);
                        iv.setImageResource(pressedResource);
//						return true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                        tv.setTextColor(getResources().getColor(R.color.blue_338eff));
                        iv.setImageResource(normalResource);
//						ll.getBackground().clearColorFilter();
//						ll.setBackgroundResource(R.drawable.item_background_normal);
                        ll.setBackgroundResource(R.color.transparent);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        tv.setTextColor(getResources().getColor(R.color.blue_338eff));
                        iv.setBackgroundResource(normalResource);
//						ll.getBackground().clearColorFilter();
//						ll.setBackgroundResource(R.drawable.item_background_normal);
                        ll.setBackgroundResource(R.color.transparent);
                        break;
                }
                return false;
            }
        });
    }


}
