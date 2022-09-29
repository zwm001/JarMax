package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Demo for self study
 * <p>
 * Source from Author: CatVod
 */


public class Zjmiao extends Spider {
    private static final String siteUrl = "https://zjmiao.com";
    private static final String siteHost = "zjmiao.com";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;
    private Pattern regexCategory = Pattern.compile("/index.php/vod/type/id/(\\d+)/");
    private Pattern regexVid = Pattern.compile("/index.php/vod/detail/id/(\\d+)/");
    private Pattern regexPlay = Pattern.compile("/index.php/vod/play/id/(\\d+)/sid/(\\d+)/nid/(\\d+)/");
    //private Pattern regexPlay = Pattern.compile("/index.php/vod/play/id/(\\S+)/");
    private Pattern regexPage = Pattern.compile("\\S+/page/(\\d+)/");


    protected String ext = null;

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"qiyi\":{\"sh\":\"奇奇\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"youku\":{\"sh\":\"优优\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"mgtv\":{\"sh\":\"芒芒\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"qq\":{\"sh\":\"腾腾\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"bilibili\":{\"sh\":\"哩哩\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"xinluan\":{\"sh\":\"蓝光一\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"ltnb\":{\"sh\":\"蓝光二\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"wuduzy\":{\"sh\":\"蓝光三\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"xfyun\":{\"sh\":\"蓝光四\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"rx\":{\"sh\":\"蓝光五\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"zjmzy\":{\"sh\":\"蓝光Miao\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"pptv\":{\"sh\":\"皮皮\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"sohu\":{\"sh\":\"搜搜\",\"sn\":1,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"letv\":{\"sh\":\"乐乐\",\"sn\":1,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"xigua\":{\"sh\":\"西西\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"dplayer\":{\"sh\":\"TV\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"report errors\":{\"sh\":\"zjmRe\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"miaolink\":{\"sh\":\"追喵资源\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"renrenmi\":{\"sh\":\"备用Rrm\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999}}");
            //playerConfig = new JSONObject("{\"qiyi\":{\"sh\":\"奇奇\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"youku\":{\"sh\":\"优优\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"mgtv\":{\"sh\":\"芒芒\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"qq\":{\"sh\":\"腾腾\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"bilibili\":{\"sh\":\"哩哩\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"ltnb\":{\"sh\":\"蓝光二\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"wuduzy\":{\"sh\":\"蓝光三\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"xfyun\":{\"sh\":\"蓝光四\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"rx\":{\"sh\":\"蓝光五\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"zjmzy\":{\"sh\":\"蓝光Miao\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"pptv\":{\"sh\":\"皮皮\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"sohu\":{\"sh\":\"搜搜\",\"sn\":1,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"letv\":{\"sh\":\"乐乐\",\"sn\":1,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"xigua\":{\"sh\":\"西西\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"dplayer\":{\"sh\":\"TV\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"report errors\":{\"sh\":\"zjmRe\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"miaolink\":{\"sh\":\"追喵资源\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999},\"renrenmi\":{\"sh\":\"备用Rrm\",\"sn\":0,\"pu\":\"https://jx.zjmiao.com/?url=\",\"or\":999}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春偶像\",\"v\":\"青春偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"剧情片\",\"v\":\"11\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"情景\",\"v\":\"情景\"},{\"n\":\"商战\",\"v\":\"商战\"},{\"n\":\"网剧\",\"v\":\"网剧\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", siteHost);
        //headers.put("Referer", siteUrl);
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6");
        return headers;
    }

    protected HashMap<String, String> jiexiHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Host", " play.videomiao.vip");
        //headers.put("Referer", " https://zjmiao.com/index.php/");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6");
        return headers;
    }

    protected static HashMap<String, String> postHeaders() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        hashMap.put("Origin", " https://play.videomiao.vip");
        return hashMap;
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
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("div.nav-channel>a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = name.equals("电影") ||
                        name.equals("连续剧") ||
                        name.equals("综艺") ||
                        name.equals("动漫");
                if (show) {
                    Matcher mather = regexCategory.matcher(ele.attr("href"));
                    if (!mather.find())
                        continue;
                    // 把分类的id和名称取出来加到列表里
                    String id = mather.group(1).trim();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type_id", id);
                    jsonObject.put("type_name", name);
                    classes.put(jsonObject);
                }
            }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Element homeList = doc.select("div.vodlist").get(0);
                Elements list = homeList.select("div.pack-ykpack a.aplus-exp");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.attr("title");
                    String cover = vod.selectFirst("img.bj").attr("data-original");
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
                    String remark = vod.selectFirst("span.pack-prb").text();
                    Matcher matcher = regexVid.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_remarks", remark);
                    v.put("vod_pic", cover);
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
            String url = siteUrl + "/index.php/vod/show/id/";
            if (extend != null && extend.size() > 0 && extend.containsKey("tid") && extend.get("tid").length() > 0) {
                url += extend.get("tid");
            } else {
                url += tid;
            }
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.length() > 0) {
                        url += "/" + key + "/" + URLEncoder.encode(value);
                    }
                }
            }
            url += "/page/" + pg + "/";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("div.page_info a");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element a = pageInfo.get(i);
                    String name = a.text();
                    if (page == -1 && a.hasClass("zbs")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            page = Integer.parseInt(matcher.group(1).trim());
                        } else {
                            page = 0;
                        }
                    }
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            pageCount = Integer.parseInt(matcher.group(1).trim());
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("div.pack-packcover");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a.aplus-exp").attr("title");
                    String cover = vod.selectFirst("div.bj").attr("data-original");
                    String remark = vod.selectFirst("span.pack-prb").text();
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.aplus-exp").attr("href"));
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
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 48);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 48);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    /**
     * 提取字符串中文字符
     * @param str
     * @return
     */
    public static String toStrByChinese(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(m.find()){
            sb.append(m.group());
        }
        return sb.toString();
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
            // 视频详情url
            String url = siteUrl + "/index.php/vod/detail/id/" + ids.get(0) + "/";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String vid = doc.selectFirst("div.fn-left").attr("data-id");

            String cover = doc.selectFirst("div.s-cover img").attr("src");
            if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                cover = siteUrl + cover;
            }
            String title = doc.selectFirst("div.s-top-info-title h1").text();
            String desc = doc.selectFirst("div.desc_txt span").text();
            //String desc = Jsoup.parse(doc.selectFirst("meta[name=description]").attr("content")).text();
            //String category = "", area = "", year = "", remark = "", director = "", actor = "";

            vodList.put("vod_id", vid);
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("vod_content", desc);
            //vodList.put("type_name", category);
            //vodList.put("vod_year", year);
            //vodList.put("vod_area", area);
            //vodList.put("vod_remarks", remark);
            //vodList.put("vod_actor", actor);
            //vodList.put("vod_director", director);


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

            // 取播放列表数据
            Elements sources = doc.select("div.play_source_tab>div>a");
            Elements sourceList = doc.select("div.playlist_notfull");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                //String sourceName = source.text().replaceAll("&nbsp;","");
                String sourceName = toStrByChinese(source.text());
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("sh").equals(sourceName)) {
                        sourceName = playerConfig.getJSONObject(flag).getString("sh");
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = sourceList.get(i).select("ul>li>a");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "/sid/" + matcher.group(2) + "/nid/" + matcher.group(3);
                    //String playURL = matcher.group(1);
                    vodItems.add(vod.text() + "$" + playURL);
                }
                if (vodItems.size() > 0)
                    playList = TextUtils.join("#", vodItems);

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

    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */


    private final Pattern urlt = Pattern.compile("\"url\": *\"([^\"]*)\",");
    private final Pattern key = Pattern.compile("\"key\": *\"([^\"]*)\",");
    private final Pattern vkey = Pattern.compile("\"vkey\": *\"([^\"]*)\",");
    private final Pattern time = Pattern.compile("\"time\": *\"([^\"]*)\",");

    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject headers = new JSONObject();
            headers.put("Referer", " https://zjmiao.com");
            headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
            //headers.put("User-Agent", " Mozilla/5.0 (Linux; Android 10; JEF-AN00 Build/HUAWEIJEF-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/88.0.4324.93 Mobile Safari/537.36");
            headers.put("Accept", " */*");
            headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
            headers.put("Accept-Encoding", " gzip, deflate");
            String url = siteUrl + "/index.php/vod/play/id/" + id + "/";
            Elements allScript = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url))).select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_aaaa")) {
                    JSONObject player = new JSONObject(scContent.substring(scContent.indexOf('{'), scContent.lastIndexOf('}') + 1));
                    String jxurl = "https://play.videomiao.vip/?url=" + player.getString("url") + "&key=" + player.getString("key") + "&tm=" + player.getString("tm");
                    Document doc = Jsoup.parse(OkHttpUtil.string(jxurl, jiexiHeaders()));
                    Elements script = doc.select("body>script");
                    for (int j = 0; j < script.size(); j++) {
                        String Content = script.get(j).html().trim();
                        Matcher matcher = urlt.matcher(Content);
                        if (!matcher.find()) {
                            return "";
                        }
                        String urlt = matcher.group(1);
                        Matcher matcher1 = key.matcher(Content);
                        if (!matcher1.find()) {
                            return "";
                        }
                        String key = matcher1.group(1);
                        Matcher matcher2 = vkey.matcher(Content);
                        if (!matcher2.find()) {
                            return "";
                        }
                        String vkey = matcher2.group(1);
                        Matcher matcher3 = time.matcher(Content);
                        if (!matcher3.find()) {
                            return "";
                        }
                        String time = matcher3.group(1);
                        HashMap hashMap = new HashMap();
                        hashMap.put("key", key);
                        hashMap.put("url", urlt);
                        hashMap.put("time", time);
                        OkHttpUtil.post(OkHttpUtil.defaultClient(), "https://play.videomiao.vip/API.php", hashMap, postHeaders(),new OKCallBack.OKCallBackString() {
                            @Override
                            protected void onFailure(Call call, Exception exc) {
                            }

                            public void onResponse(String str) {
                                try {
                                    String url = new JSONObject(str).getString("url");
                                    result.put("url", new JSONObject(str).getString("url"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        result.put("header", headers.toString());
                        result.put("parse", 0);
                        result.put("playUrl", "");

                    }
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
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(url)));
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
