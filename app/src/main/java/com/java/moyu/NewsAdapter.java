package com.java.moyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> data;
    private OnClick onClick;

    private NewsAdapter(OnClick onClick) {
        data = new ArrayList<>();
        this.onClick = onClick;
    }

    static NewsAdapter newAdapter(Context context, View view, OnClick onClick) {
        NewsAdapter adapter = new NewsAdapter(onClick);
        RecyclerView rv = (RecyclerView)view;
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);
        return adapter;
    }

    /**
     * 在第 position 个位置插入 News
     * @param news 新闻
     * @param position 插入位置
     */
    void add(News news, int position) {
        data.add(position, news);
        notifyItemInserted(position);
    }

    /**
     * 在末尾插入 News
     * @param news
     */
    void add(News news) {
        add(news, data.size());
    }

    /**
     * 在第 position 个位置插入 data
     * @param data 新闻列表
     * @param position 插入位置
     */
    void add(List<News> data, int position) {
        this.data.addAll(position, data);
        notifyItemRangeInserted(position, data.size());
    }

    /**
     * 在末尾插入 data
     * @param data
     */
    void add(List<News> data) {
        add(data, this.data.size());
    }

    /**
     * 删除第 position 个位置的 news
     * @param position 删除位置
     */
    void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    News get(int position) {
        return data.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.publisher) TextView publisher;
        @BindView(R.id.comment_count) TextView commentCount;
        @BindView(R.id.publish_time) TextView publishTime;
        @BindView(R.id.image_card) CardView imageCard;
        @BindView(R.id.image_thumb) ImageView imageThumb;
        @BindView(R.id.image_loading) ProgressBar imageLoading;

        ViewHolder(View itemView, final OnClick onClick) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.click(view, getAdapterPosition());
                }
            });
        }

    }

    public interface OnClick {

        void click(View view, int position);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        return new ViewHolder(view, onClick);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        News d = data.get(position);
        holder.title.setText(d.title);
        holder.publisher.setText(d.publisher);
        holder.commentCount.setText(Util.parseCommentCount(0));
        holder.publishTime.setText(Util.parseTime(d.publishTime));
        if (d.image != null && !d.image.isEmpty()) {
            holder.imageThumb.setVisibility(View.GONE);
            holder.imageLoading.setVisibility(View.VISIBLE);
            holder.imageCard.setVisibility(View.VISIBLE);
            new DownloadImageTask(holder.imageThumb, new OnTaskCompleted() {
                @Override
                public void onTaskCompleted() {
                    holder.imageThumb.setVisibility(View.VISIBLE);
                    holder.imageLoading.setVisibility(View.GONE);
                }
            }).execute(d.image);
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        OnTaskCompleted callback;

        public DownloadImageTask(ImageView bmImage, OnTaskCompleted callback) {
            this.bmImage = bmImage;
            this.callback = callback;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("image", urldisplay);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            callback.onTaskCompleted();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
