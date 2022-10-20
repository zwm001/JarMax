package com.github.catvod.spider;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import com.github.catvod.utils.CBC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


/**
 * Author: @SDL
 */
public class Mrys extends Spider {


    private static final String siteUrl = "https://api.yakangyl.com";
    private static final String siteHost = "api.yakangyl.com";


    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    protected JSONObject ext = null;


    private JSONArray classes;



    @Override
    public void init(Context context,String extend) {
        super.init(context);
        try {
            String content = OkHttpUtil.string(extend, null);
            ext = new JSONObject(content);
            filterConfig = ext.getJSONObject("filters");
            classes = new JSONArray("[{\"type_id\":\"1\",\"type_name\":\"电影\"},{\"type_id\":\"2\",\"type_name\":\"连续剧\"},{\"type_id\":\"3\",\"type_name\":\"综艺\"},{\"type_id\":\"4\",\"type_name\":\"动漫世界\"},{\"type_id\":\"6\",\"type_name\":\"豆瓣高分\"}]");

        }
        catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    private String uAgent = "Dalvik/2.1.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " + Build.MODEL + " Build/" + Build.ID + ")";

    private HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("user-agent", uAgent);
        headers.put("Host", siteHost);
        headers.put("user-Connection", "Keep-Alive");
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
        JSONObject results = new JSONObject();
        try {
            results.put("class", classes);
            if (filter) {
                results.put("filters", ext.getJSONObject("filter"));
            }
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
        return results.toString();
    }
    /**
     * 首页最近更新视频列表数据
     *
     * @return
     */
    @Override
    public String homeVideoContent() {
        try {
                JSONArray videos = new JSONArray();
                String url = siteUrl + "/TomorrowMovies/getHome";
                String content = OkHttpUtil.string(url, getHeaders(url));
                JSONObject homejson = new JSONObject(content);
                JSONArray hotVodslist = homejson.getJSONObject("data").getJSONArray("hotVods");
                for (int i = 0; i < hotVodslist.length(); i++) {
                        JSONObject vObj = hotVodslist.getJSONObject(i);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("d_id"));
                        v.put("vod_name", vObj.getString("d_name"));
                        v.put("vod_pic", vObj.getString("d_pic"));
                        v.put("vod_remarks", vObj.getString("d_remarks") + " 评分：" + vObj.getString("d_score"));
                        videos.put(v);
                }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {
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
            //先获取分类信息
                String furl = siteUrl + "/TomorrowMovies/getType?version=9&pid=" + tid;
                String fcontent = OkHttpUtil.string(furl, getHeaders(furl));
                JSONObject fdataObject = new JSONObject(fcontent);
                JSONArray fjsonArray = new JSONArray(decryptResponse(fdataObject.optString("data").trim()));
                String url = siteUrl + "/TomorrowMovies/getVodWithPage?version=9"+"&d_type=" +new JSONObject(fjsonArray.getString(0)).optString("t_id")+"&page=" + pg + "&pageSize=15";
                String content = OkHttpUtil.string(url, getHeaders(url));
                JSONObject dataObject = new JSONObject(content);
                JSONArray jsonArray = new JSONArray(decryptResponse(dataObject.getString("data")));
                JSONArray videos = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject vObj = jsonArray.getJSONObject(i);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vObj.getString("d_id"));
                    v.put("vod_name", vObj.getString("d_name"));
                    v.put("vod_pic", vObj.getString("d_pic"));
                    v.put("vod_remarks", vObj.getString("d_remarks"));
                    videos.put(v);
                }
                JSONObject result = new JSONObject();
                int limit = 24;
                int page = 1;
                int total = dataObject.getInt("total");
                int pageCount = 15;
                result.put("page", page);
                result.put("pagecount", pageCount);
                result.put("limit", limit);
                result.put("total", total);
                result.put("list", videos);
                return result.toString();

        } catch (Throwable th) {
            SpiderDebug.log(th);
        }
        return "";
    }



    @Override
    public String detailContent(List<String> ids) {
        try {
            String url = siteUrl + "/TomorrowMovies/getVodById?version=9&d_id="+ ids.get(0)+"&userID=";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject dataObject = new JSONObject(content);

            JSONObject vObj = new JSONObject(decryptResponse(dataObject.getString("data")));

            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            JSONObject vodAtom = new JSONObject();

            String title = vObj.getString("d_name");

            vodAtom.put("vod_id", vObj.getString("d_id"));
			//电影名字
            vodAtom.put("vod_name", title);
			//电影缩略图
            vodAtom.put("vod_pic", vObj.getString("d_pic"));
			//电影类型
            vodAtom.put("type_name", vObj.getString("d_type"));
			//电影年份
            vodAtom.put("vod_year", vObj.getString("d_year"));
			//电影地区
            vodAtom.put("vod_area", vObj.getString("d_area"));
			//电影状态
            vodAtom.put("vod_remarks", vObj.getString("d_remarks"));
			//电影演员
            vodAtom.put("vod_actor", vObj.getString("d_starring"));
			//电影导演
            vodAtom.put("vod_director", vObj.optString("director"));
			//电影内容简介
            vodAtom.put("vod_content", vObj.getString("d_content").trim());
            //获取视频的播放源和播放列表
            vodAtom.put("vod_play_from", "极速播放");
            String d_playurl = vObj.optString("d_playurl").trim();
            vodAtom.put("vod_play_url", d_playurl);
            list.put(vodAtom);
            result.put("list", list);
            return result.toString();
        } catch (Throwable th) {
            SpiderDebug.log(th);
        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            String videoUrl = id;
            JSONObject result = new JSONObject();
            result.put("parse", 0);
            result.put("playUrl", "");
            result.put("url", videoUrl);
            return result.toString();
        } catch (Throwable th) {
            SpiderDebug.log(th);
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        if (quick)
            return "";
        try {
            String url = siteUrl + "/TomorrowMovies/search?version=9&keys=" + URLEncoder.encode(key)+"&page=0&pageSize=30";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject dataObject = new JSONObject(content);
            JSONArray jsonArray = new JSONArray(decryptResponse(dataObject.optString("data")));
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                String title = vObj.getString("d_name");
                if (!title.contains(key))
                    continue;
                v.put("vod_id", vObj.getString("d_id"));
                v.put("vod_name", title);
                v.put("vod_pic", vObj.getString("d_pic"));
                v.put("vod_remarks", vObj.getString("d_remarks") + "评分"+ vObj.getString("d_score"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    protected String decryptResponse(String src) {
        try {
            String key ="hkckfglfdjkdksdk" ;
            String iv ="cmfjdocktdeslfcg" ;
            String decodestr = CBC.AES_decode(src,key,iv);
            return decodestr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
