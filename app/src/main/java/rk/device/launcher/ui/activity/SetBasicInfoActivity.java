package rk.device.launcher.ui.activity;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.api.T;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.event.TimeEvent;
import rx.Subscriber;

/**
 * 基础设置
 * Created by hanbin on 2017/11/24.
 */

public class SetBasicInfoActivity extends BaseCompatActivity implements View.OnClickListener {

    private long when_time = 0;
    @Bind(R.id.tv_time)
    TextView timeTv;

    @Override
    protected int getLayout() {
        return R.layout.activity_basic_info;
    }

    @Override
    protected void initView() {
        registerRxBus();
        setOnClick(R.id.ll_set_time, R.id.ll_set_blue_tooth);
        goBack();
    }

    private void registerRxBus() {
        addSubscription(RxBus.getDefault().toObserverable(TimeEvent.class).subscribe(new Subscriber<TimeEvent>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TimeEvent timeEvent) {
                try {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, timeEvent.year);
                    c.set(Calendar.MONTH, timeEvent.month);
                    c.set(Calendar.DAY_OF_MONTH, timeEvent.day);
                    c.set(Calendar.HOUR_OF_DAY, timeEvent.hour);
                    c.set(Calendar.MINUTE, timeEvent.minute);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    when_time = c.getTimeInMillis();
                    timeTv.setText(timeEvent.year+"-"+timeEvent.month+"-"+timeEvent.day+" "+timeEvent.hour+":"+timeEvent.minute);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.basic_settting));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_set_time:
                gotoActivity(SetTimeActivity.class, false);
                break;
            case R.id.ll_set_blue_tooth:

                break;
            case R.id.btn_finish_setting:
                if (when_time == 0) {
                    T.showShort(getString(R.string.time_setting_illeagel));
                    break;
                }
                if (when_time / 1000 < Integer.MAX_VALUE) {
                    SystemClock.setCurrentTimeMillis(when_time);
                }
                break;
        }
    }
}
