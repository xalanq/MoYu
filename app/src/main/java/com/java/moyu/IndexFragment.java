package com.java.moyu;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页碎片
 */
class IndexFragment extends BasicFragment {

    private CardAdapter adapter;

    public IndexFragment() {
        super();
        setTitleId(R.string.index_fragment_title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.index_fragment, container, false);
        init(view);
        test();
        return view;
    }


    private void init(View view) {
        adapter = new CardAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.index_fragment_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void test() {
        for (int i = 0; i < 20; ++i) {
            News news = new News();
            news.title = String.format("这是标题 %d 啊", i);
            news.publisher = String.format("第%d号", i);
            news.publishTime = LocalDateTime.now().minusMinutes(i * i * i * i * 30);
            adapter.add(news);
        }
    }

    public static class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        List<News> data;

        CardAdapter() {
            data = new ArrayList<>();
        }

        /**
         * 在第 position 个位置插入 News
         * @param news 新闻
         * @param position 插入位置
         */
        void add(News news, int position) {
            data.add(position, news);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, data.size());
        }

        /**
         * 在末尾插入 News
         * @param news
         */
        void add(News news) {
            add(news, data.size());
        }

        /**
         * 删除第 position 个位置的 news
         * @param position 删除位置
         */
        void remove(int position) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(View itemView) {
                super(itemView);
            }

        }

        static class NewsHolder extends ViewHolder {

            @BindView(R.id.title) TextView title;
            @BindView(R.id.publisher) TextView publisher;
            @BindView(R.id.comment_count) TextView comment_count;
            @BindView(R.id.publishTime) TextView publishTime;
            @BindView(R.id.imageThumb) ImageView imageThumb;

            NewsHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
            final NewsHolder holder = new NewsHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BasicApplication.getContext(), "Click News", Toast.LENGTH_SHORT).show();
                }
            });
            return holder;
        }

        String parseTime(LocalDateTime date) {
            LocalDateTime now = LocalDateTime.now();
            Resources r = BasicApplication.getContext().getResources();
            if (now.compareTo(date) < 0) {
                return r.getString(R.string.time_error);
            }
            Duration d = Duration.between(date, now);
            if (d.getSeconds() < 60) {
                return r.getString(R.string.time_recent);
            } else if (d.toMinutes() < 60) {
                return String.format(r.getString(R.string.time_minute), d.toMinutes());
            } else if (d.toHours() < 24) {
                return String.format(r.getString(R.string.time_hour), d.toHours());
            } else if (d.toDays() < 2) {
                return r.getString(R.string.time_last_day);
            } else if (d.toDays() < 3) {
                return r.getString(R.string.time_last_last_day);
            } else if (d.toDays() < 7) {
                return String.format(r.getString(R.string.time_day), d.toDays());
            } else if (d.toDays() < 30) {
                return String.format(r.getString(R.string.time_week), d.toDays() / 7);
            } else if (d.toDays() < 365) {
                return String.format(r.getString(R.string.time_month), d.toDays() / 30);
            }
            return String.format(r.getString(R.string.time_year), d.toDays() / 365);
        }

        String parseCommentCount(int count) {
            return String.format(
                BasicApplication.getContext().getResources().getString(R.string.default_news_comment_count),
                count
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder _holder, int position) {
            News d = data.get(position);
            NewsHolder holder = (NewsHolder) _holder;
            holder.title.setText(d.title);
            holder.publisher.setText(d.publisher);
            holder.comment_count.setText(parseCommentCount(0));
            holder.publishTime.setText(parseTime(d.publishTime));
            holder.imageThumb.setImageResource(R.drawable.default_avatar);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
