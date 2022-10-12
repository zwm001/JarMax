// Decompiled by Jadx (from NP Manager)
package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class Nmys extends XPathMacFilter {
    private static Pattern H = Pattern.compile("video src=\"(.*?)\"");
    private static Pattern Hr = Pattern.compile("vod-play-id-\\d+-src-(\\d+)-num-(\\d+)");
    private static Pattern cN = Pattern.compile("mac_url='(.*?)';");
    private static Pattern l9 = Pattern.compile("var url='(.*?)';");
    
    @Override
    public void init(Context context, String str) {
        if(str.isEmpty())str="https://kimlee-cmd.github.io/nmys.json";
        super.init(context, str);
    }

    public String playerContent(String str, String str2, List<String> list) {
        JSONObject jSONObject = new JSONObject();
        fetchRule();
        String replace =  rule.getPlayUrl().isEmpty() ? str2 : rule.getPlayUrl().replace("{playUrl}", str2);
        try {
            Integer valueOf;
            Matcher matcher = Hr.matcher(str2);
            Integer num = null;
            if (matcher.find()) {
                num = Integer.valueOf(Integer.parseInt(matcher.group(1)));
                valueOf = Integer.valueOf(Integer.parseInt(matcher.group(2)));
            } else {
                valueOf = null;
            }
            Matcher matcher2 = cN.matcher(fetch(replace));

            if (matcher2.find()) {
                String[] split = matcher2.group(1).split("\\$\\$\\$")[num.intValue() - 1].split("#")[valueOf.intValue() - 1].split("\\$");
                StringBuilder stringBuilder;
                if (split[1].contains("dsjh")) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("https://api.emsdn.cn/webcloud/nm.php?url=");
                    stringBuilder.append(split[1]);
                    matcher2 = H.matcher(fetch(stringBuilder.toString()));
                    if (matcher2.find()) {
                        jSONObject.put("url", matcher2.group(1));
                        jSONObject.put("jx", "0");
                        jSONObject.put("parse", "0");
                        jSONObject.put("header", getHeaders(rule.getHomeUrl()));
                        return jSONObject.toString();
                    }
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("https://api.emsdn.cn/webcloud/m3u8.php?url=");
                stringBuilder.append(split[1]);
                matcher2 = l9.matcher(fetch(stringBuilder.toString()));
                if (matcher2.find()) {
                    str2 = matcher2.group(1);
                    if (Misc.isVideoFormat(str2)) {
                        jSONObject.put("url", str2);
                        jSONObject.put("jx", "0");
                        jSONObject.put("header", getHeaders(rule.getHomeUrl()));
                    } else if (Misc.isVip(str2)) {
                        jSONObject.put("url", str2);
                        jSONObject.put("jx", "1");
                        jSONObject.put("parse", "1");
                    }
                    return jSONObject.toString();
                }
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("https://api.emsdn.cn/webcloud/?url=");
            stringBuilder2.append(str2);
            jSONObject.put("url", stringBuilder2.toString());
            jSONObject.put("jx", "1");
            jSONObject.put( "parse", "0");
            jSONObject.put("header", getHeaders(rule.getHomeUrl()));
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    protected HashMap<String, String> getHeaders(String str) {
        HashMap<String, String> Header = super.getHeaders(str);
        Header.put("user-agent", "Mozilla/5.0 (Linux; Android 10; LYA-AL00; HMSCore 6.4.0.312; GMSCore 20.15.16) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 HuaweiBrowser/11.0.4.300 Mobile Safari/537.36");
        Header.put("referer", str);
        return Header;
    }
}
