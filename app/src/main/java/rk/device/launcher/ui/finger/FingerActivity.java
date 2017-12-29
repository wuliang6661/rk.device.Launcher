package rk.device.launcher.ui.finger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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
        setOnClick(R.id.tv_add, R.id.tv_verify, R.id.tv_find, R.id.tv_modify, R.id.tv_delete);
        mAdapter = new CommonAdapter<User>(this, dataList, R.layout.item_finger) {
            @Override
            public void convert(ViewHolder helper, User item) {
                helper.setText(R.id.tv_name, item.getName());
                helper.setText(R.id.tv_unique_id, item.getUniqueId());
            }
        };
        mListView.setAdapter(mAdapter);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                User user = new User();
                user.setCardNo("123456");
                user.setEndTime(System.currentTimeMillis() + 10000000);
                user.setStartTime(System.currentTimeMillis());
                user.setUniqueId(String.valueOf(System.currentTimeMillis()));
                user.setPopedomType("1");
                user.setFingerID("123123");
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
            case R.id.tv_verify:
                if (VerifyUtils.getInstance().verifyByNfc("123456")) {
                    T.showShort("验证成功");
                } else {
                    T.showShort("验证失败");
                }
                break;
            case R.id.tv_find:
                List<User> userList = DbHelper.queryByNFCCard("123456");
                if (userList.size() > 0) {
                    dataList.clear();
                    dataList.addAll(userList);
                    mAdapter.notifyDataSetChanged();
                }
                T.showShort("size:" + userList.size());
                break;
            case R.id.tv_modify:

                break;
            case R.id.tv_delete:

                break;
        }
    }
}
