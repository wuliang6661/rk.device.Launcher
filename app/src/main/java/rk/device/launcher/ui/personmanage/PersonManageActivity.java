package rk.device.launcher.ui.personmanage;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.db.CardHelper;
import rk.device.launcher.db.CodePasswordHelper;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.FaceHelper;
import rk.device.launcher.db.FingerHelper;
import rk.device.launcher.db.entity.Card;
import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.db.entity.Face;
import rk.device.launcher.db.entity.Finger;
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

    LGRecycleViewAdapter<User> adapter;


    @Override
    protected int getLayout() {
        return R.layout.act_person_manger;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitle(getString(R.string.person_manger));
        setRightButton(R.drawable.add_person, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(manager);
    }


    /**
     * 获取用户数据并展示
     */
    private void setAdapter() {
        if (adapter != null) {
            adapter.setDataList(users);
            return;
        }
        adapter = new LGRecycleViewAdapter<User>(users) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_person_manager;
            }

            @Override
            public void convert(LGViewHolder holder, User user, int position) {
                holder.setText(R.id.person_id, "ID：" + user.getUniqueId());
                holder.setText(R.id.person_name, user.getName());
                List<Face> faces = FaceHelper.getList(user.getUniqueId());
                if (faces.isEmpty()) {
                    holder.getView(R.id.person_face).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_face).setVisibility(View.VISIBLE);
                }
                List<CodePassword> password = CodePasswordHelper.getList(user.getUniqueId());
                if (password.isEmpty()) {
                    holder.getView(R.id.person_password).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_password).setVisibility(View.VISIBLE);
                }
                List<Card> cards = CardHelper.getList(user.getUniqueId());
                if (cards.isEmpty()) {
                    holder.getView(R.id.person_card).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_card).setVisibility(View.VISIBLE);
                }
                List<Finger> fingers = FingerHelper.getList(user.getUniqueId());
                if (fingers.isEmpty()) {
                    holder.getView(R.id.person_finger).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.person_finger).setVisibility(View.VISIBLE);
                }
                if (faces.isEmpty() && password.isEmpty() && cards.isEmpty() && fingers.isEmpty()) {
                    holder.getView(R.id.none_type).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.none_type).setVisibility(View.GONE);
                }
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) -> {
            User user = users.get(position);
            if (DbHelper.queryByUniqueId(user.getUniqueId()).size() == 0) {
                T.showShort(getString(R.string.person_no_have));
                users.remove(position);
                adapter.notifyDataSetChanged();
                return;
            }
            Intent intent = new Intent(PersonManageActivity.this, Person_addActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
        recycle.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        users = DbHelper.loadAll();
        Log.d("wuliang", "users Size = " + users.size());
        setAdapter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_right:    //增加用户
                if (DbHelper.loadAll().size() > 1000) {
                    showMessageDialog(getString(R.string.person_shangxian));
                } else {
                    gotoActivity(Person_addActivity.class, false);
                }
                break;
        }
    }
}