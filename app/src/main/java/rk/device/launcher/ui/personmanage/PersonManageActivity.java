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
    }


    /**
     * 获取用户数据并展示
     */
    private void setAdapter() {
        List<User> users = DbHelper.loadAll();


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_right:    //增加用户

                break;


        }
    }
}
