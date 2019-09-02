package com.java.moyu;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NewsNetwork {

    private final static String TAG = "NewsNetwork";

    private String url;

    NewsNetwork(String url) {
        this.url = url;
    }

    public void run(Callback callback) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setReadTimeout(10000);
            httpUrlConn.setConnectTimeout(3000);
            httpUrlConn.setDoInput(true);
            httpUrlConn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }
            br.close();
            httpUrlConn.disconnect();

            JSONObject jsonData = new JSONObject(sb.toString());
            JSONArray allNewsData = jsonData.getJSONArray("data");
            List<News> data = new ArrayList<>();
            for (int i = 0; i < allNewsData.length(); ++i) {
                JSONObject newsData = allNewsData.getJSONObject(i);
                try {
                    data.add(new News(newsData));
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
            callback.ok(data);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, e.toString());
            callback.timeout();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            callback.error();
        }
    }

    public interface Callback {

        void timeout();

        void error();

        void ok(List<News> data);

    }

    public static class Builder {

        Uri.Builder builder;

        Builder() {
            builder = Uri.parse(Constants.APIUrl).buildUpon();
        }

        Builder add(String key, String value) {
            builder.appendQueryParameter(key, value);
            return this;
        }

        NewsNetwork build() {
            return new NewsNetwork(builder.build().toString());
        }

    }

}
