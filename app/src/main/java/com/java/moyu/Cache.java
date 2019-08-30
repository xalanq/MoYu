package com.java.moyu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class Cache {

    private static HttpProxyCacheServer videoProxy;

    public static String getVideoProxyUrl(Context context, String url) {
        if (videoProxy == null)
            videoProxy = new HttpProxyCacheServer(context.getApplicationContext());
        return videoProxy.getProxyUrl(url);
    }

    public static class GetSizeTask extends AsyncTask<File, Long, Long> {

        final TextView resultView;

        GetSizeTask(TextView resultView) {
            this.resultView = resultView;
        }

        private static long calculateSize(File dir) {
            if (dir == null) return 0;
            if (!dir.isDirectory()) return dir.length();
            long result = 0;
            File[] children = dir.listFiles();
            if (children != null)
                for (File child : children)
                    result += calculateSize(child);
            return result;
        }

        @Override
        protected void onPreExecute() {
            resultView.setText(R.string.cache_calculate);
        }

        @Override
        protected Long doInBackground(File... dirs) {
            try {
                long totalSize = 0;
                for (File dir : dirs) {
                    publishProgress(totalSize);
                    totalSize += calculateSize(dir);
                }
                return totalSize;
            } catch (RuntimeException ex) {
                final String message = ex.toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        resultView.setText(R.string.cache_error);
                        Toast.makeText(resultView.getContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }
            return (long) -1;
        }

        @Override
        protected void onPostExecute(Long size) {
            String sizeText = android.text.format.Formatter.formatFileSize(resultView.getContext(), size);
            resultView.setText(String.format(resultView.getResources().getString(R.string.cache_content), sizeText));
        }

    }

    public static class ClearCacheTask extends AsyncTask<Void, Void, Void> {

        final Context context;

        ClearCacheTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Glide.get(context).clearDiskCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            Glide.get(context).clearMemory();
            Toast.makeText(context, R.string.cache_done, Toast.LENGTH_SHORT).show();
        }

    }

}
