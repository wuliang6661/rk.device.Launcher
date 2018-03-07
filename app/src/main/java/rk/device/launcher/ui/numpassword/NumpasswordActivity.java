package rk.device.launcher.ui.numpassword;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.db.CodePasswordHelper;
import rk.device.launcher.db.DbHelper;
import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.db.entity.User;
import rk.device.launcher.global.VerifyTypeConstant;
import rk.device.launcher.mvp.MVPBaseActivity;
import rk.device.launcher.utils.ResUtil;
import rk.device.launcher.utils.StringUtils;
import rk.device.launcher.utils.verify.OpenUtils;
import rk.device.launcher.widget.lgrecycleadapter.LGRecycleViewAdapter;
import rk.device.launcher.widget.lgrecycleadapter.LGViewHolder;


/**
 * MVPPlugin
 * <p>
 * 密码开门
 */

public class NumpasswordActivity extends MVPBaseActivity<NumpasswordContract.View, NumpasswordPresenter>
        implements NumpasswordContract.View, View.OnClickListener {

    @Bind(R.id.edit_text)
    TextView editText;
    @Bind(R.id.clear)
    ImageView clear;
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.commit_img)
    ImageView commitImg;
    @Bind(R.id.commit_text)
    TextView commit;
    @Bind(R.id.call_commit)
    RelativeLayout callCommit;
    @Bind(R.id.clear_dan)
    ImageView clearDan;
    @Bind(R.id.hint_text)
    TextView hintText;

    List<String> callbutton;
    StringBuilder commitText;


    @Override
    protected int getLayout() {
        return R.layout.act_call;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }


    protected void initView() {
        goBack();
        setTitle(ResUtil.getString(R.string.pwd_open));
        editText.setHint(R.string.hushi_pwd_hint);
        hintText.setText(String.valueOf(getString(R.string.hushi_lizi)));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        commit.setVisibility(View.VISIBLE);
        commitImg.setVisibility(View.GONE);
        clearDan.setVisibility(View.GONE);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recycle.setLayoutManager(manager);
    }

    protected void initData() {
        setData();
        setAdapter();
        callCommit.setOnClickListener(this);
        clearDan.setOnClickListener(this);
        clear.setOnClickListener(this);
    }


    /**
     * 填充字符
     */
    private void setData() {
        callbutton = new ArrayList<>();
        commitText = new StringBuilder();
        callbutton.add("1");
        callbutton.add("2");
        callbutton.add("3");
        callbutton.add("4");
        callbutton.add("5");
        callbutton.add("6");
        callbutton.add("7");
        callbutton.add("8");
        callbutton.add("9");
        callbutton.add("*");
        callbutton.add("0");
        callbutton.add("#");
    }

    /**
     * 设置布局显示
     */
    private void setAdapter() {
        LGRecycleViewAdapter<String> adapter = new LGRecycleViewAdapter<String>(callbutton) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_call_button;
            }

            @Override
            public void convert(LGViewHolder holder, String s, int position) {
                holder.setText(R.id.call_text, s);
            }
        };
        adapter.setOnItemClickListener(R.id.call_layout, (view, position) -> {
            commitText.append(callbutton.get(position));
            editText.setText(commitText.toString());
        });
        recycle.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_dan:
                if (commitText.length() != 0) {
                    commitText.deleteCharAt(commitText.length() - 1);
                    editText.setText(commitText.toString());
                }
                break;
            case R.id.clear:
                if (commitText.length() != 0) {
                    commitText.delete(0, commitText.length());
                    editText.setText(commitText.toString());
                }
                break;
            case R.id.call_commit:    //确定密码
                String[] passwords = commitText.toString().split("#");
                if(passwords.length == 0){
                    showMessageDialog(getString(R.string.pwd_error_hint));
                    return;
                }
                List<CodePassword> passwords1 = CodePasswordHelper.getPassword(passwords[passwords.length - 1]);
                if (passwords1.isEmpty()) {
                    showMessageDialog(getString(R.string.pwd_error_hint));
                } else {
                    if (StringUtils.isEmpty(passwords1.get(0).getPassword())) {
                        showMessageDialog(getString(R.string.pwd_error_hint));
                        return;
                    }
                    List<User> users = DbHelper.queryUserById(passwords1.get(0).getPersonId());
                    long time = System.currentTimeMillis();
                    if (users.get(0).getStartTime() < time && users.get(0).getEndTime() > time) {    //在有效时间内，则开门
                        OpenUtils.getInstance().open(VerifyTypeConstant.TYPE_PASSWORD, users.get(0).getUniqueId(), users.get(0).getName());
                    } else {
                        showMessageDialog("密码已过期，请重新输入");
                    }
                }
                break;
        }
    }
}
