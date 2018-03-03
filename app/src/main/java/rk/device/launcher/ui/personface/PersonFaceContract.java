package rk.device.launcher.ui.personface;

import rk.device.launcher.db.entity.Face;
import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonFaceContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * 增加人脸
         */
        void addFace(Face face);

        /**
         * 修改人脸
         */
        void updateFace(Face face);

        /**
         * 删除人脸
         */
        void deleteFace(Face face);
    }
}
