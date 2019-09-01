package com.java.moyu;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    LocationData location; // 新闻提及位置，位置经纬度，提及次数
    String json;

    private final DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String getID() {
        return this.id;
    }

    public String getJSON() {
        return this.json;
    }

    public News() {

    }

    public News(JSONObject data) {
        try {
            this.id = data.getString("newsID");
            this.title = data.getString("title");
            this.publisher = data.getString("publisher");
            this.publishTime = LocalDateTime.parse(data.getString("publishTime"), dataFormatter);
            String images = data.getString("image");
            if (!images.isEmpty()) {
                String arr = images.substring(1, images.length() - 1);
                if (!arr.isEmpty()) {
                    this.image = arr.split("\\s*,\\s*");
                }
            }
            this.video = data.getString("video");
            this.json = data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

class ScoreData {

    Double score; // 相关度，数值在 0 ~ 1 之间
    String word; // 关键字

}

class MentionData {

    Integer count; // 出现次数
    String url; // 相关链接
    String word; // 关键字

}

class LocationData {

    Double longitude; // 经度
    Double latitude; // 纬度
    Integer count; // 出现次数
    String url; // 相关链接
    String word; // 关键字

}
