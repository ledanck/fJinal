package io.jpress.front.controller;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.Signup;
import io.jpress.model.query.SignupQuery;
import io.jpress.router.RouterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * SignupController
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/11/9
 */
@RouterMapping(url = Consts.ROUTER_SIGNUP)
@Before(UserInterceptor.class)
public class SignupController extends BaseFrontController {
    private static final Log log = Log.getLog(SignupController.class);
    public void getAllSignup(){
        try {
            List<Signup> signupList = SignupQuery.me().findAllByUser(getLoginedUser().getId());
            if(signupList == null){
                renderJson(new ArrayList<>());
            }
            renderJson(signupList);

        }catch (Exception e){
            renderAjaxResult("获取报名信息失败", 400);
            log.error("获取报名信息失败："+e.getMessage());
        }
    }


    public void addSignup(){

    }


}
