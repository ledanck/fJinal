package io.jpress.front.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.User;
import io.jpress.router.RouterMapping;

/**
 * Created by lzl on 2016/12/9.
 */
// 公司新闻处理
@RouterMapping(url = Consts.ROUTER_COMPANNY)
@Before(UserInterceptor.class)
public class CompanyController extends BaseFrontController {
    public void loadCompanyNew(){
        User user = getLoginedUser();
        setAttr("user", user);
      render("company_new.html");
    }
}
