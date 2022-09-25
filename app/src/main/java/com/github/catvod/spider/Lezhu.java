package com.github.catvod.spider;

import android.content.Context;
import android.util.Base64;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class Lezhu extends XPathMac {

    @Override
    public void init(Context context, String str) {
        if(str.isEmpty())str="https://kimlee-cmd.github.io/Lezhu.json";
        super.init(context, str);
    }

    public String playerContent(String str, String str2, List<String> list) {
        try {
            String str3 = "http://www.lezhutv.com/play/" + str2 + ".html";
            String trim = Jsoup.parse(OkHttpUtil.string(str3, header(str3))).selectFirst("div.mplayer script").html().trim();
//            System.out.println("trim :-------------------"+trim);
            String substring = trim.substring(trim.indexOf("var view_path = '") + 17, trim.lastIndexOf("var view_from") - 4);
            String ue2 = Jsoup.parse(OkHttpUtil.string("http://www.lezhutv.com/hls2/index.php?url=" + substring, header(str3))).selectFirst("input").attr("value");
            JSONObject jSONObject = new JSONObject();
//            System.out.println("ue2 "+ue2);
            HashMap hashMap = new HashMap();
            hashMap.put("id", substring);
            hashMap.put("type", "vid");
            hashMap.put("siteuser", "");
            hashMap.put("md5", getMd5(ue2));
            hashMap.put("referer", str3);
            hashMap.put("hd", "");
            hashMap.put("lg", "");
            String res= postMap("http://www.lezhutv.com/hls2/url.php", hashMap,header("http://www.lezhutv.com/hls2/url.php"));
            System.out.println(res);
            JSONObject Obj = new JSONObject(res);
            if(Obj.has("media"))jSONObject.put("url", Obj.optJSONObject("media").optString("url"));
            jSONObject.put("parse", 0);
            jSONObject.put("playUrl", "");
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    private static String postMap(String url, Map<String, String> paramsMap, Map<String, String> headerMap) {
        OKCallBack.OKCallBackString callback = new OKCallBack.OKCallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {

            }
        };
        OkHttpUtil.post(OkHttpUtil.defaultClient(), url, paramsMap, headerMap, callback);
        return callback.getResult();
    }

    public HashMap<String, String> header(String str) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("method", "GET");
        hashMap.put("Upgrade-Insecure-Requests", "1");
        hashMap.put("DNT", "1");
        hashMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        hashMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        hashMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return hashMap;
    }

    public String getMd5(String str) {
        try {
            String ue2 = Misc.MD5(Base64.encodeToString((Base64.encodeToString(str.getBytes(), 10) + "NTY2").getBytes(), 10), Misc.CharsetUTF8);
            String $2 = "abcdefghijklmnopqrstuvwxyz";
            String $3 = "zyxwvutsrqponmlkjihgfedcba";
            String str2 = "";
            for (int i = 0; i < ue2.length(); i++) {
                char charAt = ue2.charAt(i);
                str2 = Character.isDigit(charAt) ? str2 + ue2.charAt(i) : str2 + $3.charAt($2.indexOf(charAt));
            }
            return str2;
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }
}
