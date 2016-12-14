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
// 参赛记录控制器
@RouterMapping(url = Consts.ROUTER_TAKE_PART_MATCH)
@Before(UserInterceptor.class)
public class TakePartMatchController extends BaseFrontController {
    public void loadTakePartMatchInfo(){
        User user = getLoginedUser();
        setAttr("user", user);
        render("takePart_match_info.html");
    }

}
