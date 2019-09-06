package com.java.moyu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShareUtil {

    private static ShareUtil instance;
    private final String SHARE_PANEL_TITLE = "Share to:";

    private ShareUtil() { }

    public static ShareUtil getInstance() {

        if (instance == null) {
            synchronized (ShareUtil.class) {
                if (instance == null) {
                    instance = new ShareUtil();
                }
            }
        }
        return instance;

    }

    private final boolean copyFile(String oldPath$Name, String newPath$Name) {

        try {

            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    private final String fileName(String file) {
        return file.substring(file.lastIndexOf('/')+1);
    }

    private final String fileExtension(String file) {
        return file.substring(file.lastIndexOf('.'));
    }

    private final String moveToExternal(Context context, String imgUrl, String imgPath) {
        String externalPath = context.getExternalCacheDir().getAbsolutePath() + '/'
            + fileName(imgPath)
            + fileExtension(imgUrl);
        copyFile(imgPath, externalPath);
        return externalPath;
    }

    private static Uri shareImageUri;
    private class getImageCacheAsyncTask extends AsyncTask<String, Void, Void> {

        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            String imgUrl = params[0];
            try {
                String imgPath = Glide.with(context)
                    .load(imgUrl)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get().getPath();
                shareImageUri = Uri.parse(moveToExternal(context, imgUrl, imgPath));
                return null;
            } catch (Exception ex) {
                return null;
            }
        }

    }

    private Uri getImageUri(Context context, String image) {

        try {
            new getImageCacheAsyncTask(context).execute(image).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shareImageUri;
    }

    private ArrayList<Uri> getImagesUri(Context context, String[] images) {

        ArrayList<Uri> uriList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            uriList.add(getImageUri(context, images[i]));
        }
        return uriList;

    }

    public void shareText(Context context, String shareText) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, SHARE_PANEL_TITLE));

    }

    public void shareSingleImage(Context context, String imagePath) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, imagePath));
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, SHARE_PANEL_TITLE));

    }

    public void shareMultipleImage(Context context, String[] images) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getImagesUri(context, images));
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, SHARE_PANEL_TITLE));

    }

    /**
     * 分享图文
     * @param context       上下文
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param images        图片路径，不分享图片则传null
     */
    public void shareImageText(Context context, String msgTitle, String msgText, String[] images) {

        Intent intent;
        if (images == null || images.length == 0) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain"); // 纯文本
        } else if (images.length == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, images[0]));
        } else {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getImagesUri(context, images));
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, SHARE_PANEL_TITLE));

    }
}
