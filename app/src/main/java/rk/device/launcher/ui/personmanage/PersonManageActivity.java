package rk.device.launcher.ui.personmanage;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.ui.person_add.Person_addActivity;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.widget.lgrecycleadapter.LGRecycleViewAdapter;
import rk.device.launcher.widget.lgrecycleadapter.LGViewHolder;


/**
 * MVPPlugin
 * 用户管理界面
 */

public class PersonManageActivity extends MVPBaseActivity<PersonManageContract.View, PersonManagePresenter>
        implements PersonManageContract.View, OnClickListener {

    @Bind(R.id.recycle)
    RecyclerView recycle;

    List<User> users;    //用户列表


    @Override
    protected int getLayout() {
        return R.layout.act_person_manger;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle("用户管理");
        setRightButton(R.drawable.add_person, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(manager);
        setAdapter();
    }


    /**
     * 获取用户数据并展示
     */
    private void setAdapter() {
        users = DbHelper.loadAll();
        LGRecycleViewAdapter<User> adapter = new LGRecycleViewAdapter<User>(users) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_person_manager;
            }

            @Override
            public void convert(LGViewHolder holder, User user, int position) {
                holder.setText(R.id.person_id, "ID：" + user.getId());
                holder.setText(R.id.person_name, user.getName());
                if (StringUtils.isEmpty(user.getFaceID())) {
                    holder.getView(R.id.person_face).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_face).setVisibility(View.VISIBLE);
                }
                if (user.getPassWord() == 0) {
                    holder.getView(R.id.person_password).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_password).setVisibility(View.VISIBLE);
                }
                if (StringUtils.isEmpty(user.getCardNo())) {
                    holder.getView(R.id.person_card).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_card).setVisibility(View.VISIBLE);
                }
                if (StringUtils.isEmpty(user.getFingerID1()) && StringUtils.isEmpty(user.getFingerID2()) && StringUtils.isEmpty(user.getFingerID3())) {
                    holder.getView(R.id.person_finger).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_finger).setVisibility(View.VISIBLE);
                }
                if (StringUtils.isEmpty(user.getFaceID()) && user.getPassWord() == 0 && StringUtils.isEmpty(user.getCardNo()) &&
                        StringUtils.isEmpty(user.getFingerID1()) && StringUtils.isEmpty(user.getFingerID2()) && StringUtils.isEmpty(user.getFingerID3())) {
                    holder.getView(R.id.none_type).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.none_type).setVisibility(View.GONE);
                }
            }
        };
        recycle.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_right:    //增加用户
                gotoActivity(Person_addActivity.class, false);
                break;
        }
    }
}
