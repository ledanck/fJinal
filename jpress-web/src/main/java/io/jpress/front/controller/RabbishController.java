package io.jpress.front.controller;

import com.jfinal.aop.Before;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.User;
import io.jpress.router.RouterMapping;

/**
 * Created by jsc on 2016/12/9.
 */
// 垃圾箱控制器
@RouterMapping(url = Consts.ROUTER_RUBBISH)
@Before(UserInterceptor.class)
public class RabbishController extends BaseFrontController{

    public void loadRubbishInfo(){
        User user = getLoginedUser();
        setAttr("user", user);
        render("rubbish_info.html");
    }

}
