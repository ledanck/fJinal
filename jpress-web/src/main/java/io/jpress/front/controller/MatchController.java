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
// 比赛信息控制器

@RouterMapping(url = Consts.ROUTER_MATCH)
@Before(UserInterceptor.class)
public class MatchController extends BaseFrontController {

    public void loadMatch(){
        User user = getLoginedUser();
        setAttr("user", user);
        render("match_info.html");
    }
}
