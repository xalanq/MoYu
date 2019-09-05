package com.java.moyu;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

class News {

    String id; // 新闻ID
    String title; // 新闻题目
    String content; // 正文
    LocalDateTime publishTime; // 新闻发布时间，部分新闻由于自身错误可能时间会很大（如9102年）
    String language; // 新闻语言
    String category; // 类别
    String[] image; // 图片链接，可能为空（需缓存到本地，并替换成本地 Uri）
    String video; // 视频链接，一般为空（需缓存到本地，并替换成本地 Uri）
    String publisher; // 出版者
    ScoreData[] keyword; // 关键词
    ScoreData[] when; // 新闻中相关时间和相关度
    ScoreData[] where; // 新闻相关位置和相关度
    ScoreData[] who; // 新闻相关人和相关度
    MentionData[] organization; // 发布新闻组织
    MentionData[] person; // 新闻提及人物，提及次数和在 xlore 中的知识卡片 URL
    LocationData[] location; // 新闻提及位置，位置经纬度，提及次数

    public News() {

    }

    public News(JSONObject data) throws JSONException {
        this.id = data.getString("newsID");
        this.title = data.getString("title");
        this.content = data.getString("content");
        this.publishTime = LocalDateTime.parse(data.getString("publishTime"), Constants.TIME_FORMATTER);
        this.language = data.getString("language");
        this.category = data.getString("category");
        String images = data.getString("image");
        if (!images.isEmpty()) {
            String arr = images.substring(1, images.length() - 1);
            if (!arr.isEmpty()) {
                this.image = arr.split("\\s*,\\s*");
                for (int i = 0; i < this.image.length; i++) {
                    this.image[i] = this.image[i].replaceAll("\\\\/", "/").replaceAll("\"", "");
                }
            }
        }
        this.video = data.getString("video");
        this.publisher = data.getString("publisher");

        JSONArray keyword = data.getJSONArray("keywords");
        this.keyword = new ScoreData[keyword.length()];
        for (int i = 0; i < keyword.length(); ++i) {
            this.keyword[i] = new ScoreData(keyword.getJSONObject(i));
        }
        JSONArray when = data.getJSONArray("when");
        this.when = new ScoreData[when.length()];
        for (int i = 0; i < when.length(); ++i) {
            this.when[i] = new ScoreData(when.getJSONObject(i));
        }
        JSONArray where = data.getJSONArray("where");
        this.where = new ScoreData[where.length()];
        for (int i = 0; i < where.length(); ++i) {
            this.where[i] = new ScoreData(where.getJSONObject(i));
        }
        JSONArray who = data.getJSONArray("who");
        this.who = new ScoreData[who.length()];
        for (int i = 0; i < who.length(); ++i) {
            this.who[i] = new ScoreData(who.getJSONObject(i));
        }
        JSONArray organization = data.getJSONArray("organizations");
        this.organization = new MentionData[organization.length()];
        for (int i = 0; i < organization.length(); ++i) {
            this.organization[i] = new MentionData(organization.getJSONObject(i));
        }
        JSONArray person = data.getJSONArray("persons");
        this.person = new MentionData[person.length()];
        for (int i = 0; i < person.length(); ++i) {
            this.person[i] = new MentionData(person.getJSONObject(i));
        }
        JSONArray location = data.getJSONArray("locations");
        this.location = new LocationData[location.length()];
        for (int i = 0; i < location.length(); ++i) {
            this.location[i] = new LocationData(location.getJSONObject(i));
        }
    }

    public String getID() {
        return this.id;
    }

    public LocalDateTime getTime() { return this.publishTime; }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            Collection<JSONObject> items = new ArrayList<JSONObject>();
            Collection<String> items_str = new ArrayList<String>();

            json.put("newsID", this.id);
            json.put("title", this.title);
            json.put("content", this.content);
            json.put("publishTime", Constants.TIME_FORMATTER.format(this.publishTime));
            json.put("language", this.language);
            json.put("category", this.category);
            items_str.clear();
            for (int i = 0; i < (this.image == null ? 0 : this.image.length); i++) {
                items_str.add(this.image[i]);
            }
            json.put("image", new JSONArray(items_str));
            json.put("video", this.video);
            json.put("publisher", this.publisher);
            items.clear();
            for (int i = 0; i < (this.keyword == null ? 0 : this.keyword.length); i++) {
                items.add(this.keyword[i].toJSONObject());
            }
            json.put("keywords", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.when == null ? 0 : this.when.length); i++) {
                items.add(this.when[i].toJSONObject());
            }
            json.put("when", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.where == null ? 0 : this.where.length); i++) {
                items.add(this.where[i].toJSONObject());
            }
            json.put("where", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.who == null ? 0 : this.who.length); i++) {
                items.add(this.who[i].toJSONObject());
            }
            json.put("who", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.organization == null ? 0 : this.organization.length); i++) {
                items.add(this.organization[i].toJSONObject());
            }
            json.put("organizations", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.person == null ? 0 : this.person.length); i++) {
                items.add(this.person[i].toJSONObject());
            }
            json.put("persons", new JSONArray(items));
            items.clear();
            for (int i = 0; i < (this.location == null ? 0 : this.location.length); i++) {
                items.add(this.location[i].toJSONObject());
            }
            json.put("locations", new JSONArray(items));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public LocalDateTime getPublishTime() {
        return this.publishTime;
    }

    class ScoreData {

        Double score; // 相关度，数值在 0 ~ 1 之间
        String word; // 关键字

        public ScoreData(JSONObject data) {
            try {
                this.score = data.getDouble("score");
                this.word = data.getString("word");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ScoreData", data.toString());
            }
        }

        public JSONObject toJSONObject() {
            JSONObject json = new JSONObject();
            try {
                json.put("score", this.score);
                json.put("word", this.word);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

    }

    class MentionData {

        Integer count; // 出现次数
        String url; // 相关链接
        String word; // 关键字

        public MentionData(JSONObject data) {
            try {
                this.count = data.getInt("count");
                this.url = data.getString("linkedURL");
                this.word = data.getString("mention");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MentionData", data.toString());
            }
        }

        public JSONObject toJSONObject() {
            JSONObject json = new JSONObject();
            try {
                json.put("count", this.count);
                json.put("linkedURL", this.url);
                json.put("mention", this.word);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

    }

    class LocationData {

        Double longitude; // 经度
        Double latitude; // 纬度
        Integer count; // 出现次数
        String url; // 相关链接
        String word; // 关键字

        public LocationData(JSONObject data) {
            try {
                if (data.has("lng"))
                    this.longitude = data.getDouble("lng");
                if (data.has("lat"))
                    this.latitude = data.getDouble("lat");
                this.count = data.getInt("count");
                this.url = data.getString("linkedURL");
                this.word = data.getString("mention");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("LocationData", data.toString());
            }
        }

        public JSONObject toJSONObject() {
            JSONObject json = new JSONObject();
            try {
                json.put("lng", this.longitude);
                json.put("lat", this.latitude);
                json.put("count", this.count);
                json.put("linkedURL", this.url);
                json.put("mention", this.word);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

    }

}
