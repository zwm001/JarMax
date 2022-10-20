package com.github.catvod.spider;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


public class Ysgc extends Spider {

    private static final String siteUrl = "https://www.ysgc.cc";

    protected JSONObject playerConfig;
    protected JSONObject filterConfig;

    protected Pattern regexCategory = Pattern.compile("/vodtype/(\\d+).html");
    protected Pattern regexVoddetail = Pattern.compile("/voddetail/(\\d+).html");
    protected Pattern regexPlay = Pattern.compile("/vodplay/(\\S+).html");
    protected Pattern regexPage = Pattern.compile("/vodshow/\\d+--------(\\d+)---.html");
    protected Pattern regexJxConfig = Pattern.compile("var config = ([\\s\\S]*?)player");
    protected Pattern regexDDJxConfig = Pattern.compile("var urls = '([\\s\\S]*?)'");

    private final String filterString = "{\"1\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"动画\",\"v\":\"动画\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春偶像\",\"v\":\"青春偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"情景\",\"v\":\"情景\"},{\"n\":\"商战\",\"v\":\"商战\"},{\"n\":\"网剧\",\"v\":\"网剧\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"3\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"港台\",\"v\":\"港台\"},{\"n\":\"日韩\",\"v\":\"日韩\"},{\"n\":\"欧美\",\"v\":\"欧美\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"5\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"番剧\",\"v\":\"番剧\"},{\"n\":\"国创\",\"v\":\"国创\"},{\"n\":\"动画片\",\"v\":\"动画片\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}";

    private final String playerString = "{\"xg_app_player\":{\"show\":\"app全局播放器\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://www.x-n.cc/api.php?url=\"},\"duoduozy\":{\"show\":\"极速蓝光\",\"des\":\"\\/static\\/images\\/ico\\/ysgc.png\",\"ps\":\"0\",\"parse\":\"\"},\"ysgc\":{\"show\":\"工场专线\",\"des\":\"\\/static\\/images\\/ico\\/newysgc.png\",\"ps\":\"0\",\"parse\":\"\"},\"sohu\":{\"show\":\"搜狐视频\",\"des\":\"\\/static\\/images\\/ico\\/sohu.png\",\"ps\":\"0\",\"parse\":\"\"},\"qq\":{\"show\":\"腾讯视频\",\"des\":\"\\/static\\/images\\/ico\\/qq.png\",\"ps\":\"0\",\"parse\":\"\"},\"bilibili\":{\"show\":\"哔哩哔哩\",\"des\":\"\\/static\\/images\\/ico\\/bili.png\",\"ps\":\"0\",\"parse\":\"\"},\"youku\":{\"show\":\"优酷视频\",\"des\":\"\\/static\\/images\\/ico\\/youku.png\",\"ps\":\"0\",\"parse\":\"\"},\"qiyi\":{\"show\":\"爱奇艺\",\"des\":\"\\/static\\/images\\/ico\\/iqiyi.png\",\"ps\":\"0\",\"parse\":\"\"},\"mgtv\":{\"show\":\"芒果TV\",\"des\":\"\\/static\\/images\\/ico\\/mgtv.png\",\"ps\":\"0\",\"parse\":\"\"},\"xigua\":{\"show\":\"西瓜视频\",\"des\":\"\\/static\\/images\\/ico\\/xigua.png\",\"ps\":\"0\",\"parse\":\"\"},\"pptv\":{\"show\":\"PPTV\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"zhibo\":{\"show\":\"直播线路\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"wjm3u8\":{\"show\":\"无尽资源\",\"des\":\"\\/static\\/images\\/ico\\/wj.jpg\",\"ps\":\"0\",\"parse\":\"\"}}";



    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject(playerString);
            filterConfig = new JSONObject(filterString);
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param refererUrl
     * @return
     */
    protected HashMap<String, String> getHeaders(String refererUrl) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(refererUrl)) {
            headers.put("Referer", refererUrl);
        }
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            String url = siteUrl + '/';
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            Elements elements = doc.select("ul.myui-header__menu > li > a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                String href = ele.attr("href");
                Matcher mather = regexCategory.matcher(href);
                if (!mather.find())
                    continue;
                // 把分类的id和名称取出来加到列表里
                String id = mather.group(1).trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", id);
                jsonObject.put("type_name", name);
                classes.put(jsonObject);
            }

            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Elements list = doc.select("div.myui-panel:nth-child(1) ul.myui-vodlist li div.myui-vodlist__box");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst(".myui-vodlist__thumb").attr("title");
                    String cover = vod.selectFirst(".myui-vodlist__thumb").attr("data-original");
                    String remark = vod.selectFirst("span.pic-text").text();
                    Matcher matcher = regexVoddetail.matcher(vod.selectFirst(".myui-vodlist__thumb").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            int page = 1;
            if (!TextUtils.isEmpty(pg) && Integer.parseInt(pg.trim()) > 1) {
                page = Integer.parseInt(pg.trim());
            }
            Map<String, String> ext = new HashMap<>();
            ext.put("id", tid);
            ext.put("pg", "" + page);
            if (extend != null && extend.size() > 0) {
                ext.putAll(extend);
            }
//            13-内地-hits-古装-国语-A------2022.html
            String id = ext.get("id") == null ? "" : ext.get("id");
            String area = ext.get("area") == null ? "" : ext.get("area");
            String by = ext.get("by") == null ? "" : ext.get("by");
            String clazz = ext.get("class") == null ? "" : ext.get("class");
            String lang = ext.get("lang") == null ? "" : ext.get("lang");
            String spg = ext.get("pg") == null ? "" : ext.get("pg");
            String year = ext.get("year") == null ? "" : ext.get("year");
            String url = siteUrl + String.format("/vodshow/%s-%s-%s-%s-%s----%s---%s.html", id, area, by, clazz, lang, spg, year);
            SpiderDebug.log(url);

            String html = OkHttpUtil.string(url, getHeaders(siteUrl));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 1;

            // 取页码相关信息
            Elements pageInfo = doc.select("ul.myui-page > li > a:last-child");
            if (pageInfo.size() > 0) {
                String href = pageInfo.attr("href");
                href = href.substring(9, href.length() - 4);
                String pageCountNum = href.split("-")[9];
                if (!TextUtils.isEmpty(pageCountNum)) {
                    pageCount = Integer.parseInt(pageCountNum);
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("div.myui-panel:nth-child(2) ul.myui-vodlist li div.myui-vodlist__box");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst(".myui-vodlist__thumb").attr("title");
                    String cover = vod.selectFirst(".myui-vodlist__thumb").attr("data-original");
                    String remark = vod.selectFirst("span.pic-text").text();
                    Matcher matcher = regexVoddetail.matcher(vod.selectFirst(".myui-vodlist__thumb").attr("href"));                  
                    if (!matcher.find())
                        continue;
                    String vodId = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vodId);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 40);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 40);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
    

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
     @Override
     public String detailContent(List<String> ids) {
         try {
             String url = siteUrl + "/voddetail/" + ids.get(0) + ".html";
             Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
             JSONObject result = new JSONObject();
             JSONObject vodList = new JSONObject();
             String cover = doc.selectFirst("div.myui-content__thumb>a>img").attr("data-original");
             String title = doc.selectFirst("div.myui-content__detail>h1").text();
             String desc = Jsoup.parse(doc.selectFirst("meta[name=description]").attr("content")).text();
             //String desc = doc.selectFirst("span.data>p").text();
             String category = "", area = "", remark = "", director = "", actor = "";
             Elements span_text_muted = doc.select("div.myui-content__detail span.text-muted");

             for (int i = 0; i < span_text_muted.size(); i++) {
                 Element text = span_text_muted.get(i);
                 String info = text.text();
                 if (info.equals("地区：")) {
                     area = text.nextElementSibling().text();
                 } else if (info.equals("更新：")) {
                     remark = text.nextSibling().outerHtml().trim();
                 } else if (info.equals("导演：")) {
                     List<String> directors = new ArrayList<>();
                     Elements aa = text.parent().select("a");
                     for (int j = 0; j < aa.size(); j++) {
                         directors.add(aa.get(j).text());
                     }
                     director = TextUtils.join(",", directors);
                 } else if (info.equals("主演：")) {
                     List<String> actors = new ArrayList<>();
                     Elements aa = text.parent().select("a");
                     for (int j = 0; j < aa.size(); j++) {
                         actors.add(aa.get(j).text());
                     }
                     actor = TextUtils.join(",", actors);
                 } else if (info.equals("类型：")) {
                     List<String> categorys = new ArrayList<>();
                     Elements aa = text.parent().select("a");
                     for (int j = 0; j < aa.size(); j++) {
                         categorys.add(aa.get(j).text());
                     }
                     category = TextUtils.join(",", categorys);
                 }
             }

             vodList.put("vod_id", ids.get(0));
             vodList.put("vod_name", title);
             vodList.put("vod_pic", cover);
             vodList.put("vod_content", desc);
             vodList.put("type_name", category);
             //vodList.put("vod_year", year);
             vodList.put("vod_area", area);
             vodList.put("vod_remarks", remark);
             vodList.put("vod_actor", actor);
             vodList.put("vod_director", director);

             Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
                 @Override
                 public int compare(String o1, String o2) {
                     try {
                         int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                         int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                         if (sort1 == sort2) {
                             return 1;
                         }
                         return sort1 - sort2 > 0 ? 1 : -1;
                     } catch (JSONException e) {
                         SpiderDebug.log(e);
                     }
                     return 1;
                 }
             });
             Elements sources = doc.select("ul.nav > li > a");
             Elements sourceList = doc.select("ul.myui-content__list");
             for (int i = 0; i < sources.size(); i++) {
                 Element source = sources.get(i);
                 String sourceName = source.text();
                 boolean found = false;
                 for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                     String flag = it.next();
                     if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
                         sourceName = playerConfig.getJSONObject(flag).getString("show");
                         found = true;
                         break;
                     }
                 }
                 if (!found)
                     continue;
                 String playList = "";
                 Elements playListA = sourceList.get(i).select("li > a");
                 List<String> vodItems = new ArrayList<>();
                 for (int j = 0; j < playListA.size(); j++) {
                     Element vod = playListA.get(j);
                     Matcher matcher = regexPlay.matcher(vod.attr("href"));
                     if (!matcher.find())
                         continue;
                     String playURL = matcher.group(1);
                     vodItems.add(vod.text() + "$" + playURL);
                 }
                 if (vodItems.size() > 0) {
                     playList = TextUtils.join("#", vodItems);
                 }
                 if (playList.length() == 0)
                     continue;
                 vod_play.put(sourceName, playList);
             }
             if (vod_play.size() > 0) {
                 String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
                 String vod_play_url = TextUtils.join("$$$", vod_play.values());
                 vodList.put("vod_play_from", vod_play_from);
                 vodList.put("vod_play_url", vod_play_url);
             }
             JSONArray list = new JSONArray();
             list.put(vodList);
             result.put("list", list);
             return result.toString();
         } catch (Exception e) {
             SpiderDebug.log(e);
         }
         return "";
     }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            // 播放页 url
            String url = siteUrl + "/vodplay/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    String videoUrl = player.getString("url");
                    String from = player.getString("from");
                    if (!from.isEmpty()) {
                        if (from.equals("duoduozy"))from="dplayer";
                        if (from.equals("wjm3u8"))from="wujin";
                        String jxurl = "https://www.ysgc.cc/static/player/"+from+".php?url=";
                        String html = OkHttpUtil.string(jxurl + videoUrl, getHeaders(siteUrl));
                        Matcher matcher = regexDDJxConfig.matcher(html);
                        if (matcher.find()) {
                            String playurl = matcher.group(1);
                            playurl = playurl.substring(8);
                            byte[] decode = Base64.decode(playurl.getBytes(), Base64.DEFAULT);
                            String decodeString = new String(decode);
                            String realUrl = decodeString.substring(8, decodeString.length() - 8);
                            result.put("url", realUrl);
                        }
                    } else {
                        String jxHtml = OkHttpUtil.string("https://player.tjomet.com/ysgc/?url=" + url, getHeaders(siteUrl));
                        Matcher matcher = regexJxConfig.matcher(jxHtml);
                        if (matcher.find()) {
                            String configStr = matcher.group(1);
                            JSONObject config = new JSONObject(configStr);
                            String jxUrl = "https://player.tjomet.com/ysgc/sVXqr8PMlsg9ip12.jpg";
                            Map<String, String> paramsMap = new HashMap<>();
                            paramsMap.put("url", videoUrl);
                            paramsMap.put("token", config.optString("token"));
                            paramsMap.put("vkey", config.optString("vkey"));
                            paramsMap.put("sign", "Rl178F8ljpvJ9ggF");
                            OkHttpUtil.post(OkHttpUtil.defaultClient(), jxUrl, paramsMap, new OKCallBack.OKCallBackString() {
                                @Override
                                protected void onFailure(Call call, Exception e) {

                                }

                                @Override
                                protected void onResponse(String response) {
                                    try {
                                        JSONObject resJson = new JSONObject(response);
                                        String aesUrl = resJson.getString("url");
                                        aesUrl = aesUrl.substring(8);
                                        byte[] decode = Base64.decode(aesUrl.getBytes(), Base64.DEFAULT);
                                        String decodeString = new String(decode);
                                        String realUrl = decodeString.substring(8, decodeString.length() - 8);
                                        result.put("url", realUrl);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                    Map<String, String> playHeader = new HashMap<>();
                    playHeader.put("User-Agent", " Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
                    result.put("header", new JSONObject(playHeader).toString());
                    result.put("parse", "0");
                    result.put("playUrl", "");
                    break;
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    @Override
    public String searchContent(String key, boolean quick) {
        try {
            long currentTime = System.currentTimeMillis();
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=10&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(siteUrl)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("total") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("id");
                    String title = vod.getString("name");
                    String cover = vod.getString("pic");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
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
