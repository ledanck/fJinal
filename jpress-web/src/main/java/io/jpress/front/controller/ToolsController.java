package io.jpress.front.controller;

import com.jfinal.aop.Before;
import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.interceptor.UserInterceptor;
import io.jpress.router.RouterMapping;
import io.jpress.utils.EverydayEnglish;
import io.jpress.utils.EverydayInHistory;
import io.jpress.utils.TodayInHistory;

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
    private static Object history= TodayInHistory.getHistory();
    private static String today = "";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     *  返回Json对象，根据需要挑选字段显示，请求url:http://localhost/tool/getEnglish
     */
    /*{
        "love": "0",
            "note": "没有不可能，连这个词自己都说：“不，可能！”—— 奥黛丽·赫本",
            "tts": "http://news.iciba.com/admin/tts/2016-12-05-day.mp3",
            "dateline": "2016-12-05",
            "fenxiang_img": "http://cdn.iciba.com/web/news/longweibo/imag/2016-12-05.jpg",
            "translation": "词霸小编：投稿人——好心情。相信对奥黛丽赫本这个名字大家都很熟悉。英国的电影和舞台剧女演员，也是这个世界上最著名的演员之一。公认的世界上最美丽的女人之一，联合国儿童基金会在其纽约总部为一尊7英尺高的青铜雕像揭幕，雕像名字为奥黛丽精神（The Spirit of Audrey），以表彰赫本为联合国所做的贡献。很难想象这个世界上真的存在这样天使一般的人，可能现在，她真的变回了天使吧~\r\n",
            "caption": "词霸每日一句",
            "content": "Nothing is impossible, the word itself says \"I'm possible\"! ",
            "picture": "http://cdn.iciba.com/news/word/20161205.jpg",
            "sid": "2435",
            "picture2": "http://cdn.iciba.com/news/word/big_20161205b.jpg"
    }*/
    public  void getEnglish(){
        synchronized (simpleDateFormat){
            String now = simpleDateFormat.format(new Date());
            if(!now.equalsIgnoreCase(today)){
                today = now;
                english= EverydayEnglish.getEnglish();
                history= TodayInHistory.getHistory();
            }
        }
        System.out.println(english);
        renderJson(english);
    }

    /**
     *  返回字符串数组，根据需要显示，请求url:http://localhost/tool/getHistory
     */
    /*[
    戊子年冬月十八] - 在608年前的今天，1408年12月5日 (农历冬月十八)，金帐汗国军队在亦敌忽率领下进抵莫斯科城下。
    [壬子年冬月十六] - 在524年前的今天，1492年12月5日 (农历冬月十六)，欧洲航海家哥伦布第一次踏上伊斯帕尼奥拉岛。
    [丁丑年十月廿四] - 在259年前的今天，1757年12月5日 (农历十月廿四)，普鲁士与奥匈帝国间的洛伊滕之战爆发。
    [辛亥年冬月初十] - 在225年前的今天，1791年12月5日 (农历冬月初十)，音乐神童莫扎特逝世。
    [戊戌年十月十九] - 在178年前的今天，1838年12月5日 (农历十月十九)，中国平民教育家武训诞生。
    [丁未年十月廿八] - 在169年前的今天，1847年12月5日 (农历十月廿八)，广州黄竹岐人民抗英。
    [庚午年闰十月十三] - 在146年前的今天，1870年12月5日 (农历闰十月十三)，法国著名作家大仲马病逝。
    [癸巳年十月廿八] - 在123年前的今天，1893年12月5日 (农历十月廿八)，清政府与英国签订《中英会议藏印条款》。
    [辛丑年十月廿五] - 在115年前的今天，1901年12月5日 (农历十月廿五)，德国物理学家、量子力学创立者海森堡出生。
    [辛丑年十月廿五] - 在115年前的今天，1901年12月5日 (农历十月廿五)，娱乐业巨头沃尔特·迪斯尼诞生。
    [丙寅年冬月初一] - 在90年前的今天，1926年12月5日 (农历冬月初一)，毛泽东主编《政治周报》。
    [丙寅年冬月初一] - 在90年前的今天，1926年12月5日 (农历冬月初一)，郭松龄与张作霖在东北混战。
    [丙寅年冬月初一] - 在90年前的今天，1926年12月5日 (农历冬月初一)，法国印象派主要画家克劳德·莫奈逝世。
      */
    public void getHistory(){
        System.out.println(history);
        renderJson(history);
    }
}
