package io.jpress.utils;

import com.google.gson.Gson;

/**
 * EveryDayEnglish
 * 每日一句英语
 * @author chenkui
 * @version 1.0
 * @date 2016/11/1
 */
public class EverydayEnglish {

    public static void main(String[] args){
        System.out.println(getEnglish());
    }

    public static EnglishModel getEnglish(){
        try {
            String json = HttpUtils.get("http://open.iciba.com/dsapi/");
            if(json != null && !json.isEmpty()){
                json = HttpUtils.decode(json);
                Gson gson = new Gson();
                EnglishModel englishModel = gson.fromJson(json, EnglishModel.class);
                return englishModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
