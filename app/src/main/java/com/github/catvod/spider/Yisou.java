package com.github.catvod.spider;

import android.content.Context;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Yisou extends Spider {
    private static final Pattern aliyun = Pattern.compile("(https://www.aliyundrive.com/s/[^\"]+)");
    private PushAgent yisou;

    protected static HashMap<String, String> Headers() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2049A Build/SP1A.210812.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/103.0.5060.129 Mobile Safari/537.36");
        hashMap.put("Referer", "https://yiso.fun/");
        return hashMap;
    }

    public String detailContent(List<String> list) {
        try {
            return yisou.detailContent(list);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    public void init(Context context, String str) {
        super.init(context, str);
        yisou = new PushAgent();
        yisou.init(context,str);
    }


    public String playerContent(String flag, String id, List<String> vipFlags) {
        return yisou.playerContent(flag, id, vipFlags);
    }


    public String searchContent(String key, boolean quick) {
        try {
            String url = "https://yiso.fun/api/search?name=" + URLEncoder.encode(key)+"&from=ali";
            String content = OkHttpUtil.string(url, Headers());
            JSONArray resultList = new JSONObject(content).getJSONObject("data").getJSONArray("list");
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            for (int i = 0; i < resultList.length(); i++) {
                JSONObject jSONObject = resultList.getJSONObject(i);
                String title = jSONObject.getJSONArray("fileInfos").getJSONObject(0).getString("fileName");
                String remark = jSONObject.getString("gmtCreate");
                String id = jSONObject.getString("url");
                JSONObject v = new JSONObject();
                v.put("vod_id", id);
                v.put("vod_name", title);
                v.put("vod_pic", "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000");
                v.put("vod_remarks", remark);
                videos.put(v);
            }

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }
}