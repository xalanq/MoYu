package com.java.moyu;

import android.content.res.Resources;

import java.time.Duration;
import java.time.LocalDateTime;

public class Util {

    static String parseTime(LocalDateTime date) {
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

    static String parseCommentCount(int count) {
        return String.format(
            BasicApplication.getContext().getResources().getString(R.string.default_news_comment_count),
            count
        );
    }

}
