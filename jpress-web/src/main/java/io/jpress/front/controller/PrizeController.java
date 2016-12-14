package io.jpress.front.controller;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.model.Prize;
import io.jpress.model.User;
import io.jpress.model.query.PrizeQuery;
import io.jpress.router.RouterMapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * PrizeController
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/11/9
 */
@RouterMapping(url = Consts.ROUTER_PRIZE)
@Before(UserInterceptor.class)
public class PrizeController  extends BaseFrontController {
    private static final Log log = Log.getLog(PrizeController.class);

    /***
     *  获取学生所有获奖信息
     */
    public void getAllPrize(){
        BigInteger userId = getLoginedUser().getId();
        try {
            List<Prize> prizeList = PrizeQuery.me().findAllByUser(userId);
            if(prizeList == null){
                renderJson(new ArrayList<>());
            }
            else {
                renderJson(prizeList);
            }
        }catch (Exception e){
            renderJson(new ArrayList<>());
            log.error("获取获奖信息失败："+e.getMessage());
        }
    }
    //url: /prize/12345
    public void getPrize(){
        try {
            BigInteger messageId = new BigInteger(getPara(0));
            Prize prize = PrizeQuery.me().findById(messageId);
            if(prize == null){
                renderAjaxResult("获取获奖详细信息失败:未找到指定获奖消息", 400);
            }
            if(prize.getUserId().equals(getLoginedUser().getId())){
                renderJson(prize);
            }
        }catch (Exception e){
            renderAjaxResult("获取获奖详细信息失败", 400);
            log.error("获取获奖详细信息失败"+e.getMessage());
        }
    }


    public void addPrize(){
        BigInteger userId = getParaToBigInteger("user_id");
        String subject = getPara("subject");
        String path = getPara("path");
        String info = getPara("info");
        int score = getParaToInt("score");

        Prize prize = new Prize();
        prize.setUserId(userId);
        prize.setSubject(subject);
        prize.setPath(path);
        prize.setInfo(info);
        prize.setScore((long)score);

        prize.save();
    }
    public void loadPrizeInfo(){
        User user = getLoginedUser();
        setAttr("user", user);
        render("prize_info.html");
    }
}
