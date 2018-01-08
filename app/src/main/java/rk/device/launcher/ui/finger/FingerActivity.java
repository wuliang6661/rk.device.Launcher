package rk.device.launcher.ui.finger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.Constant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.verify.VerifyUtils;
import rk.device.launcher.widget.adapter.CommonAdapter;
import rk.device.launcher.widget.adapter.ViewHolder;

/**
 * MVPPlugin 邮箱 784787081@qq.com
 */

public class FingerActivity extends MVPBaseActivity<FingerContract.View, FingerPresenter>
        implements FingerContract.View, View.OnClickListener {

    @Bind(R.id.listView)
    ListView                    mListView;
    @Bind(R.id.et_nfc)
    EditText                    nfcEt;
    private CommonAdapter<User> mAdapter = null;
    private List<User>          dataList = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_finger;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setOnClick(R.id.tv_add_open, R.id.tv_add_patrol, R.id.tv_add_admin, R.id.tv_verify,
                R.id.tv_find, R.id.tv_modify, R.id.tv_delete);
        mAdapter = new CommonAdapter<User>(this, dataList, R.layout.item_finger) {
            @Override
            public void convert(ViewHolder helper, User item) {
                helper.setText(R.id.tv_name, item.getName());
                helper.setText(R.id.tv_unique_id, item.getCardNo());
            }
        };
        mListView.setAdapter(mAdapter);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_open:
                String nfc = nfcEt.getText().toString().trim();
                if (TextUtils.isEmpty(nfc)) {
                    T.showShort("请录入卡");
                    break;
                }
                User user = new User();
                user.setCardNo(nfc);
                user.setEndTime(System.currentTimeMillis() + 10000000);
                user.setStartTime(System.currentTimeMillis());
                user.setUniqueId(String.valueOf(System.currentTimeMillis()));
                user.setPopedomType("1");
                user.setFingerID1("123123");
                user.setFingerCode("123code");
                user.setName("hanbin");
                user.setPassWord(67341);
                long result = DbHelper.insertUser(user);
                Log.i(getClass().getName(), "result:" + result);
                switch ((int) result) {
                    case Constant.NULL_NAME:
                        T.showShort("请输入用户名称");
                        break;
                    case Constant.NULL_POPEDOMTYPE:
                        T.showShort("请选择权限类型");
                        break;
                    case Constant.NULL_UNIQUEID:
                        T.showShort("请设置用户唯一标识");
                        break;
                    default:
                        if (result > 0) {
                            T.showShort("插入成功");
                        } else {
                            T.showShort("插入失败");
                        }
                        break;
                }
                break;
            case R.id.tv_add_patrol:
                nfc = nfcEt.getText().toString().trim();
                if (TextUtils.isEmpty(nfc)) {
                    T.showShort("请录入卡");
                    break;
                }
                user = new User();
                user.setCardNo(nfc);
                user.setEndTime(System.currentTimeMillis() + 10000000);
                user.setStartTime(System.currentTimeMillis());
                user.setUniqueId(String.valueOf(System.currentTimeMillis()));
                user.setPopedomType("2");
                user.setFingerID1("123123");
                user.setFingerCode("123code");
                user.setName("hanbin");
                user.setPassWord(67341);
                result = DbHelper.insertUser(user);
                Log.i(getClass().getName(), "result:" + result);
                switch ((int) result) {
                    case Constant.NULL_NAME:
                        T.showShort("请输入用户名称");
                        break;
                    case Constant.NULL_POPEDOMTYPE:
                        T.showShort("请选择权限类型");
                        break;
                    case Constant.NULL_UNIQUEID:
                        T.showShort("请设置用户唯一标识");
                        break;
                    default:
                        if (result > 0) {
                            T.showShort("插入成功");
                        } else {
                            T.showShort("插入失败");
                        }
                        break;
                }
                break;
            case R.id.tv_add_admin:
                nfc = nfcEt.getText().toString().trim();
                if (TextUtils.isEmpty(nfc)) {
                    T.showShort("请录入卡");
                    break;
                }
                user = new User();
                user.setCardNo(nfc);
                user.setEndTime(System.currentTimeMillis() + 10000000);
                user.setStartTime(System.currentTimeMillis());
                user.setUniqueId(String.valueOf(System.currentTimeMillis()));
                user.setPopedomType("3");
                user.setFingerID1("123123");
                user.setFingerCode("123code");
                user.setName("hanbin");
                user.setPassWord(67341);
                result = DbHelper.insertUser(user);
                Log.i(getClass().getName(), "result:" + result);
                switch ((int) result) {
                    case Constant.NULL_NAME:
                        T.showShort("请输入用户名称");
                        break;
                    case Constant.NULL_POPEDOMTYPE:
                        T.showShort("请选择权限类型");
                        break;
                    case Constant.NULL_UNIQUEID:
                        T.showShort("请设置用户唯一标识");
                        break;
                    default:
                        if (result > 0) {
                            T.showShort("插入成功");
                        } else {
                            T.showShort("插入失败");
                        }
                        break;
                }
                break;
            case R.id.tv_verify:
                nfc = nfcEt.getText().toString().trim();
                User userModel = VerifyUtils.getInstance().verifyByNfc(nfc);
                switch (userModel.getPopedomType()) {
                    case Constant.USER_TYPE_OPEN_ONLY://只能开门
                        T.showShort("open door only.");
                        break;
                    case Constant.USER_TYPE_PATROL_ONLY://巡更
                        T.showShort("patrol only.");
                        break;
                    case Constant.USER_TYPE_ADMINISTRATOR://管理员，都可以
                        T.showShort("I'm an administrator.");
                        break;
                }
                break;
            case R.id.tv_find:
                nfc = nfcEt.getText().toString().trim();
                List<User> userList = DbHelper.queryByNFCCard(nfc);
                dataList.clear();
                if (userList.size() > 0) {
                    dataList.addAll(userList);
                }
                mAdapter.notifyDataSetChanged();
                T.showShort("size:" + userList.size());
                break;
            case R.id.tv_modify:

                break;
            case R.id.tv_delete:
                nfc = nfcEt.getText().toString().trim();
                userList = DbHelper.queryByNFCCard(nfc);
                if (userList.size() > 0) {
                    T.showShort("删除成功" + userList.get(0).getName());
                    DbHelper.delete(userList.get(0));
                }
                break;
        }
    }
}
