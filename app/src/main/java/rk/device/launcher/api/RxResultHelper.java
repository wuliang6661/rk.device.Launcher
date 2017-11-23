package rk.device.launcher.api;

import android.util.Log;

import rk.device.launcher.bean.BaseResult;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者 by wuliang 时间 16/11/24.
 */

public class RxResultHelper {
    private static final String TAG = "RxResultHelper";

    public static <T> Observable.Transformer<BaseResult<T>, T> httpRusult() {
        return new Observable.Transformer<BaseResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseResult<T>> apiResponseObservable) {
                return apiResponseObservable.flatMap(
                        new Func1<BaseResult<T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(BaseResult<T> mDYResponse) {
                                Log.d(TAG, "call() called with: mDYResponse = [" + mDYResponse + "]");
                                if (mDYResponse.getResult() == 200) {
                                    return createData(mDYResponse.getData());
                                } else {
                                    return Observable.error(new RuntimeException(mDYResponse.getMessage()));
                                }
                            }
                        }
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
