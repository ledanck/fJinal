package io.jpress.utils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TodayInHistory
 * 历史上的今天
 * @author chenkui
 * @version 1.0
 * @date 2016/11/28
 */
public class EverydayInHistory {
    public static void main(String[] args){
        System.out.println(getEnglish());
    }

    /**
     *  暂时有点乱码，后面会换数据源
     * @return
     */
    public static List<String> getEnglish(){
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            header.put("Host", "history.lifetime.photo:81");
            header.put("Accept-Encoding", "gzip, deflate, sdch");
            header.put("Upgrade-Insecure-Requests", "1");
            header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            String json = HttpUtils.get("http://history.lifetime.photo:81/api/history", null, header);
            if(json != null && !json.isEmpty()){
                json = HttpUtils.decode(json);
                Gson gson = new Gson();
                Resource resource = gson.fromJson(json, Resource.class);
                List<String> retList = new ArrayList<>();
                for(AllHistory ah : resource.getRes()){
                    for (HistoryModel hm : ah.getLists()){
                        retList.add(new String(hm.getTitle().getBytes(), "utf-8"));
                    }
                }
                return retList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    class HistoryModel{
        String year;
        String title;

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    class AllHistory{
        String id;
        List<HistoryModel> lists;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<HistoryModel> getLists() {
            return lists;
        }

        public void setLists(List<HistoryModel> lists) {
            this.lists = lists;
        }
    }

    class Resource{
        List<AllHistory> res;

        public List<AllHistory> getRes() {
            return res;
        }

        public void setRes(List<AllHistory> res) {
            this.res = res;
        }
    }
}
