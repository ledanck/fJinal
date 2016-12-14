package io.jpress.utils;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * TodayInHistory
 *
 * @author chenkui
 * @version 1.0
 * @date 2016/12/4
 */
public class TodayInHistory {
    private static final String APPKEY = "7d52d4e8e695e4486cfa95b6761240d8";
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";


    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public static void main(String[] args){
        for(String s: getHistory()){
            System.out.println(s);
        }
    }

    public static List<String> getHistory(){
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        header.put("Host", "history.lifetime.photo:81");
        header.put("Accept-Encoding", "gzip, deflate, sdch");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        List<String> retList = new ArrayList<>();
        try {
            Map params = new HashMap();//请求参数

            params.put("key",APPKEY);//应用APPKEY(应用详细页查询)
            params.put("v","1.0");//版本，当前：1.0
            params.put("month",month+"");//月份，如：10
            params.put("day",day+"");//日，如：1
            String json = net("http://api.juheapi.com/japi/toh", params, "GET");
            if(json != null && !json.isEmpty()){
                json = HttpUtils.decode(json);
                Gson gson = new Gson();
                Resource resource = gson.fromJson(json, Resource.class);
                if(resource != null && resource.getError_code()==0){
                   // System.out.println(resource.getResult().get(0).getDes());
                    for (HistoryModel hm : resource.getResult()){
                        retList.add("["+hm.getLunar() +"] - " + hm.getDes());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    class HistoryModel{
        String _id;
        String year;
        String month;
        String day;
        String des;
        String lunar;
        String pic;
        String title;


        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getLunar() {
            return lunar;
        }

        public void setLunar(String lunar) {
            this.lunar = lunar;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    class Resource{
        List<HistoryModel> result;
        String     reason;
        int     error_code;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public int getError_code() {
            return error_code;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

        public List<HistoryModel> getResult() {
            return result;
        }

        public void setResult(List<HistoryModel> result) {
            this.result = result;
        }
    }
}
