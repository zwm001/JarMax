package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Sokk extends Spider {
    private final Pattern regexPlay = Pattern.compile("magnet:.*?");


    public String detailContent(List<String> ids) {
        try {

            String url = "https://www.sokankan17.live/hash/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray lists = new JSONArray();
            JSONObject vodAtom = new JSONObject();
            Elements data = doc.select("div.hash-view-info li");
            String director = data.get(5).text().trim();
            String actor = data.get(4).text().trim();
            String category = data.get(0).text().replace("类型：","");
            String desc = data.get(2).text();
            String title = doc.selectFirst("section.hash-view-title > h1").text();

            vodAtom.put("vod_id", ids.get(0));
            vodAtom.put("vod_name", title);
            vodAtom.put("vod_pic", "https://imgo.shouji.com.cn/simg/20190402/2019040282564989.png");
            vodAtom.put("type_name", category);
            vodAtom.put("vod_actor", actor);
            vodAtom.put("vod_director", director);
            vodAtom.put("vod_content", desc);

            List<String> vodItems = new ArrayList<>();
            Elements playListA = doc.select("div.media-body > a");
            for (int j = 0; j < playListA.size(); j++) {
                Element vod = playListA.get(j);
                Matcher matcher = regexPlay.matcher(vod.attr("href"));
                if (!matcher.find())
                    continue;
                String playURL = vod.attr("href");
                vodItems.add(vod.text() + "$" + playURL);
            }
            vodAtom.put("vod_play_from", "吃力网");
            vodAtom.put("vod_play_url", TextUtils.join("#", vodItems));

            lists.put(vodAtom);
            result.put("list", lists);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        return headers;
    }

    protected String siteUrl;

    public void init(Context context) {
        super.init(context);
        try {
            siteUrl = geturl();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
    }


    protected String geturl() {
        try {
            Map<String, List<String>> respHeaders = new TreeMap<>();
            OkHttpUtil.stringNoRedirect("https://sokankan48.cc", getHeaders("https://sokankan48.cc"), respHeaders);
            return OkHttpUtil.getRedirectLocation(respHeaders);
        } catch (Exception unused) {
            return "https://www.sokankan31.live";
        }
    }

    private Pattern regexVid = Pattern.compile("/hash/(\\S+).html");
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            //String url = siteUrl + "/search.html?name=" + URLEncoder.encode(key);
            String url = "https://www.sokankan31.live/search.html?name=" + URLEncoder.encode(key);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();

            JSONArray videos = new JSONArray();
            Elements list = doc.select("div.list-view article.item");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = vod.select("div a h4").text().trim();;
                if (title.contains(key)) {
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div a").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", "https://www.sokankan31.live/assets/bc51d864/images/magnet.png");
                    v.put("vod_remarks", "");
                    videos.put(v);
                }
            }

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
