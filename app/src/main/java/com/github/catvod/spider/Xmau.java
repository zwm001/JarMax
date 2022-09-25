package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;


import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import okhttp3.Call;
import okhttp3.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.net.URLDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Xmau extends Spider {
    private static final String siteUrl = "http://eg.xmau.cn";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    private Pattern regexCategory = Pattern.compile("/index.php/vod/type/id/(\\d+).html");
    private Pattern regexVid = Pattern.compile("/index.php/vod/detail/id/(\\d+).html");
    private Pattern regexPlay = Pattern.compile("/index.php/vod/play/id/(\\d+)/sid/(\\d+)/nid/(\\d+).html");
    private Pattern regexPage = Pattern.compile("/index.php/vod/show/id/(\\S+)/page/(\\d+).html");
    private Pattern regexIv = Pattern.compile("\"([^\"]*)\"");
    private Pattern regexUrl = Pattern.compile("getVideoInfo\\(\"([^\"]*)\"\\)");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"mgtv\":{\"sh\":\"芒果视频\",\"des\":\"支持手机电脑在线播放\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"pptv\":{\"sh\":\"PPTV视频\",\"des\":\"支持手机电脑在线播放\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"letv\":{\"sh\":\"乐视视频\",\"des\":\"支持手机电脑在线播放\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"qiyi\":{\"sh\":\"奇艺视频\",\"des\":\"支持手机电脑在线播放\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"youku\":{\"sh\":\"优酷视频\",\"des\":\"支持手机电脑在线播放\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"qq\":{\"sh\":\"腾讯视频\",\"des\":\"qq.com\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":999},\"aliyun\":{\"sh\":\"二狗专线1\",\"des\":\"\",\"ps\":\"1\",\"pu\":\"https://jx.xmau.cn/ergou.php?url=\",\"sn\":1,\"or\":990}}");
            filterConfig = new JSONObject("{}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        return headers;
    }

    protected static HashMap<String, String> Headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("Referer", siteUrl);
        return headers;
    }

    protected static HashMap<String, String> Headers2() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("origin", "https://jx.xmau.cn");
        return headers;
    }
//    @Override
//    public void init(Context context) {super.init(context);}


    @Override
    public String homeContent(boolean filter) {
        try {
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, Headers()));
            // 分类节点
            Elements elements = doc.select("ul[class='navbar-items swiper-wrapper'] >li a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = true;
                if (filter) {
                    show = name.equals("电影") ||
                            name.equals("连续剧") ||
                            name.equals("综艺") ||
                            name.equals("动漫");
                }
                if (show) {
                    Matcher mather = regexCategory.matcher(ele.attr("href"));
                    if (!mather.find())
                        continue;
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
            // 取首页推荐视频列表
            try {
                Elements list = doc.select("div.module div.module-main div.module-poster-items-base >a");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    Matcher matcher = regexVid.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String title = vod.attr("title");
                    String cover = vod.selectFirst("div.module-item-cover img").attr("data-original");
                    String remark = vod.selectFirst("div.module-item-cover div.module-item-note").text();
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
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

            // 获取分类数据的url
            url += "/page/" + pg + ".html";
            String html = OkHttpUtil.string(url, Headers());
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            Elements pageInfo = doc.select("div[id='page'] *");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
//                    Element a = li.selectFirst("a");
//                    if (a == null)
//                        continue;
                    String name = li.text();
                    if (page == -1 && li.hasClass("page-current")) {
                        page = Integer.parseInt(name);
                    }
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(li.attr("href"));
                        if (matcher.find()) {
                            pageCount = Integer.parseInt(matcher.group(2));
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("div[class='module-items module-poster-items-base'] >a");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.attr("title");
                    String cover = vod.selectFirst("div.module-item-cover img").attr("data-original");
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
                    String remark = vod.selectFirst("div.module-item-cover div.module-item-note").text();
                    Matcher matcher = regexVid.matcher(vod.attr("href"));
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
            result.put("limit", 24);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 24);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    private static String Regex(Pattern pattern, String content) {
        if (pattern == null) {
            return content;
        }
        try {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return content;
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = siteUrl + "/index.php/vod/detail/id/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, Headers()));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("div.module-item-pic img").attr("data-original");
            if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                cover = siteUrl + cover;
            }
            String title = doc.selectFirst("div.module-info-heading h1").text();
            String category = "", area = "", year = "", remark = "", director = "", actor = "", desc = "";
            Elements data = doc.select("div.module-info-items > div");
            desc = doc.selectFirst("div.module-info-introduction-content p").text().trim();
            category = doc.select("div.module-info-tag div").get(2).text().trim();
            year = doc.select("div.module-info-tag div").get(0).text().trim();
            area = doc.select("div.module-info-tag div").get(1).text().trim();
            //year = Regex(Pattern.compile("上映：(\\S+)"), data.get(0).text());
            actor = Regex(Pattern.compile("主演：(\\S+)"), data.nextAll().text());
            director = Regex(Pattern.compile("导演：(\\S+)"), data.nextAll().text());
            // remark=data.select("div.module-info-item-content").text().trim();

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);

            Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    try {
                        int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                        int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                        if (sort1 == sort2) {
                            return -1;
                        }
                        return sort1 - sort2 > 0 ? -1 : 1;
                    } catch (JSONException e) {
                        SpiderDebug.log(e);
                    }
                    return -1;
                }
            });

            // 取播放列表数据
            Elements sources = doc.select("div[id=y-playList] > div");
            //Elements sourceList = doc.select("div.stui-vodlist__head > ul.stui-content__playlist");

            for (int i = 0; i < sources.size(); i++)
            {
                Element source = sources.get(i);
                String sourceName = source.attr("data-dropdown-value");
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("sh").equals(sourceName)) {
                        // sourceName = flag;
                        sourceName = playerConfig.getJSONObject(flag).getString("sh");
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;

                String playList = "";
                Elements playListA = doc.select("div.module-play-list").get(i).select("div a");
                List<String> vodItems = new ArrayList<>();
                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "/sid/" + matcher.group(2) + "/nid/" + matcher.group(3);
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


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {


            String url = siteUrl + "/index.php/vod/play/id/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, Headers()));
            Elements allScript = doc.select("script");


            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    if (playerConfig.has(player.getString("from"))) {
                        JSONObject pCfg = playerConfig.getJSONObject(player.getString("from"));
                        String videoUrl = pCfg.getString("pu") + player.getString("url");
                        Document docs = Jsoup.parse(OkHttpUtil.string(videoUrl, Headers()));
                        Pattern pattern = Pattern.compile("(?<=urls\\s=\\s').*?(?=')");
                        Elements allScripts = docs.select("script");
                        String iv = "";
                        String enyurl = "";
                        for (int j = 0; j < allScripts.size(); j++) {
                            String scContent2 = allScripts.get(j).toString();
                            if (scContent2.contains("var bt_token")) {

                                Matcher matcher = regexIv.matcher(scContent2);
                                if (matcher.find()) {
                                    iv = matcher.group(1);

                                }
                            }
                            if (scContent2.contains("var config")) {

                                Matcher matcher = regexUrl.matcher(scContent2);
                                if (matcher.find()) {
                                    enyurl = matcher.group(1);

                                }
                            }
                        }
                        String enyurlb64dec = toHex(Base64.decode(enyurl.getBytes(), Base64.DEFAULT));
                        String key = "A92AD40DEEC47A68";
                        String videolink = decrypt(enyurlb64dec, key, iv);
                        JSONObject result = new JSONObject();
                        result.put("url", videolink);
                        result.put("header", Headers2().toString());
                        result.put("parse", 0);
                        result.put("playUrl", "");
                        return result.toString();


                    }

                }
            }


        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    private HashMap<String, String> sHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "User-Agent:okhttp/4.1.0");
        return headers;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected String getapikey() {
        try {
            Calendar cal = Calendar.getInstance();
            int y = cal.get(Calendar.YEAR);
            int h = cal.get(Calendar.HOUR_OF_DAY);
            int m = cal.get(Calendar.MINUTE);
            String apikeyori = y + ":" + (h < 10 ? ("0" + h) : h) + ":" + y + ":" + (m < 10 ? ("0" + m) : m) + ":lvdoutv-1.0.0";
            String apikey = Misc.MD5(apikeyori, StandardCharsets.UTF_8);
            return apikey;
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    protected String decrypt(String src, String KEY, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(hex2byte(src)));
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    protected String encrypt(String src, String KEY, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            return bytesToHexStr(cipher.doFinal(src.getBytes())).toUpperCase();
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    protected byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("invalid hex string");
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    protected String bytesToHexStr(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }


    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String toHex(byte[] data) {
        final StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            sb.append(DIGITS[(data[i] >>> 4) & 0x0F]);
            sb.append(DIGITS[data[i] & 0x0F]);
        }
        return sb.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
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
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
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
