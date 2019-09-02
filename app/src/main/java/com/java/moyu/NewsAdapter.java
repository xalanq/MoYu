package com.java.moyu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final int PLAIN = 0;
    private static final int SINGLE = 1;
    private static final int MULTI = 2;
    private static final int VIDEO = 3;
    private final Context context;
    private List<News> data;
    private OnClick onClick;

    private NewsAdapter(Context context, OnClick onClick) {
        data = new ArrayList<>();
        this.onClick = onClick;
        this.context = context.getApplicationContext();
    }

    static OnClick defaultOnclick(final Activity activity) {
        return new OnClick() {
            @Override
            public void click(final News news) {
                Intent intent = new Intent(activity, NewsActivity.class);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        NewsDatabase db = NewsDatabase.getInstance();
                        db.addNews(news);
                        db.addHistory(news.id, LocalDateTime.now());
                    }
                });
                intent.putExtra("news", news.toJSONObject().toString());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        };
    }

    static NewsAdapter newAdapter(final Context context, View view, OnClick onClick) {
        final NewsAdapter adapter = new NewsAdapter(context, onClick);
        RecyclerView rv = (RecyclerView) view;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int position = GSYVideoManager.instance().getPlayPosition();
                    if ((position < firstVisibleItem || position > lastVisibleItem) &&
                        !GSYVideoManager.isFullState((Activity) context)) {
                        GSYVideoManager.releaseAllVideos();
                        adapter.notifyItemChanged(position);
                    }
                }
            }
        });
        return adapter;
    }

    /**
     * 在第 position 个位置插入 News
     *
     * @param news     新闻
     * @param position 插入位置
     */
    void add(News news, int position) {
        data.add(position, news);
        notifyItemInserted(position);
    }

    /**
     * 在末尾插入 News
     *
     * @param news
     */
    void add(News news) {
        add(news, data.size());
    }

    /**
     * 在第 position 个位置插入 data
     *
     * @param data     新闻列表
     * @param position 插入位置
     */
    void add(List<News> data, int position) {
        this.data.addAll(position, data);
        notifyItemRangeInserted(position, data.size());
    }

    /**
     * 在末尾插入 data
     *
     * @param data
     */
    void add(List<News> data) {
        add(data, this.data.size());
    }

    /**
     * 删除第 position 个位置的 news
     *
     * @param position 删除位置
     */
    void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    News get(int position) {
        return data.get(position);
    }

    void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemViewType(int position) {
        News news = data.get(position);
        if (news.video != null && !news.video.isEmpty()) {
            return VIDEO;
        } else if (news.image == null) {
            return PLAIN;
        } else if (news.image.length <= 2) {
            return SINGLE;
        }
        return MULTI;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PLAIN) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_plain, parent, false);
        } else if (viewType == SINGLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_single, parent, false);
        } else if (viewType == MULTI) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_multi, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_video, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        News d = get(position);
        int viewType = getItemViewType(position);
        holder.title.setText(d.title);
        holder.publisher.setText(d.publisher);
        holder.publishTime.setText(Util.parseTime(d.publishTime));
        holder.setOnClick(onClick, d);
        if (viewType == SINGLE) {
            Glide.with(context).load(d.image[0])
                .placeholder(R.drawable.loading_cover)
                .error(R.drawable.error).centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.image_view));
        } else if (viewType == MULTI) {
            Glide.with(context).load(d.image[0])
                .placeholder(R.drawable.loading_cover)
                .error(R.drawable.error).centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.image_view_1));
            Glide.with(context).load(d.image[1])
                .placeholder(R.drawable.loading_cover)
                .error(R.drawable.error).centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.image_view_2));
            Glide.with(context).load(d.image[2])
                .placeholder(R.drawable.loading_cover)
                .error(R.drawable.error).centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.image_view_3));
        } else if (viewType == VIDEO) {
            CoverVideoPlayer player = holder.itemView.findViewById(R.id.video_player);
            player.setup(context, d.video, d.title, position);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnClick {

        void click(News news);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.publisher)
        TextView publisher;
        @BindView(R.id.publish_time)
        TextView publishTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setOnClick(final OnClick onClick, final News news) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.click(news);
                }
            });
        }

    }

}
