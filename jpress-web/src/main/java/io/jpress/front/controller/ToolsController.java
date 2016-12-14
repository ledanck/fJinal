package io.jpress.front.controller;

import com.jfinal.aop.Before;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.router.RouterMapping;
import io.jpress.utils.EverydayEnglish;
import io.jpress.utils.EverydayInHistory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ToolsController
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/11/28
 */
@RouterMapping(url = "/tool")
//@Before(UserInterceptor.class)
public class ToolsController extends BaseFrontController {
    private static Object english= EverydayEnglish.getEnglish();
    private static Object history= EverydayInHistory.getEnglish();
    private static String today = "";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    public  void getEnglish(){
        synchronized (simpleDateFormat){
            String now = simpleDateFormat.format(new Date());
            if(!now.equalsIgnoreCase(today)){
                today = now;
                english= EverydayEnglish.getEnglish();
                history= EverydayInHistory.getEnglish();
            }
        }
        System.out.println(english);
        renderJson(english);
    }

    public void getHistory(){
        System.out.println(history);
        renderJson(history);
    }
}
