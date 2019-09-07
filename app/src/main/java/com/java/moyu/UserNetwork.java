package com.java.moyu;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserNetwork {

    private final static String TAG = "UserNetwork";

    private String url;
    private byte[] data;

    UserNetwork(String url, byte[] data) {
        this.url = url;
        this.data = data;
    }

    public void run(Callback callback) {
        new Task(url, data, callback).execute();
    }

    public interface Callback {

        void error(String msg);

        void ok(JSONObject data);

    }

    public static class Builder {

        Uri.Builder builder;
        String url;

        Builder(String apiUrl) {
            builder = new Uri.Builder();
            url = Constants.UserAPIUrl + apiUrl;
        }

        Builder add(String key, String value) {
            builder.appendQueryParameter(key, value);
            return this;
        }

        UserNetwork build() {
            byte[] data;
            try {
                data = builder.build().toString().substring(1).getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                data = new byte[1];
            }
            return new UserNetwork(url, data);
        }

    }

    private static class Task extends AsyncTask<Void, Void, Task.Result> {

        String url;
        byte[] data;
        Callback callback;

        Task(String url, byte[] data, Callback callback) {
            this.url = url;
            this.data = data;
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(Void... voids) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
                httpUrlConn.setRequestMethod("POST");
                httpUrlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpUrlConn.setRequestProperty("Content-Length", String.valueOf(data.length));
                httpUrlConn.setRequestProperty("charset", "utf-8");
                httpUrlConn.setReadTimeout(3000);
                httpUrlConn.setConnectTimeout(3000);
                httpUrlConn.setDoOutput(true);
                httpUrlConn.getOutputStream().write(data);

                BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                    sb.append('\n');
                }
                br.close();
                httpUrlConn.disconnect();
                // Log.d(TAG, "doInBackground put: " + new String(data, "UTF-8"));
                // Log.d(TAG, "doInBackground get: " + sb.toString());

                JSONObject jsonData = new JSONObject(sb.toString());
                if (jsonData.has("error"))
                    return new Result(State.ERROR, jsonData);
                return new Result(State.OK, jsonData);
            } catch (SocketTimeoutException e) {
                Log.e(TAG, e.toString());
                return new Result(State.TIMEOUT, null);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return new Result(State.UNKNOWN, null);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            switch (result.state) {
            case OK:
                callback.ok(result.data);
                break;
            case ERROR:
                try {
                    callback.error(result.data.getString("msg"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TIMEOUT:
                callback.error(BasicApplication.getContext().getResources().getString(R.string.timeout));
                break;
            case UNKNOWN:
                callback.error(BasicApplication.getContext().getResources().getString(R.string.unknown_error));
                break;
            }
        }

        enum State {OK, TIMEOUT, ERROR, UNKNOWN}

        class Result {

            State state;
            JSONObject data;

            Result(State state, JSONObject data) {
                this.state = state;
                this.data = data;
            }

        }

    }

}
