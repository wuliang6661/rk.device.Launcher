package rk.device.launcher.ui.person_add;

import rk.device.launcher.db.entity.CodePassword;
import rk.device.launcher.mvp.BasePresenter;
import rk.device.launcher.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class Person_addContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * 增加密码
         */
        void addPassWord(CodePassword codePassword);

        /**
         * 编辑密码
         */
        void editPassWord(CodePassword codePassword);

        /**
         * 删除密码
         */
        void deletePassWord(CodePassword codePassword);

    }
}
