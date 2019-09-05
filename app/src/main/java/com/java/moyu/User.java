package com.java.moyu;

import android.content.ContentValues;
import android.telecom.Call;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.annotations.CalledByNative;

public class User {

    private static String TAG = "User";
    private static User instance;
    private String token;
    private int ID;
    private String username;
    private String email;
    private String avatar;
    private boolean isOffline;

    private User(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
        NewsDatabase.getInstance().setToken(token);
    }

    private void init() {
        token = "";
        ID = 0;
        username = "";
        email = "";
        avatar = "";
        isOffline = true;
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User(NewsDatabase.getInstance().getToken());
        }
        return instance;
    }

    public void updateUserInfo(final Callback callback) {
        new UserNetwork.Builder("/userInfo")
            .add("token", token)
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                isOffline = true;
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                try {
                    init();
                    ID = data.getInt("id");
                    username = data.getString("username");
                    email = data.getString("email");
                    avatar = data.getString("avatar");
                    isOffline = false;
                    callback.ok(User.this);
                } catch (Exception e) {
                }
            }
        });
    }

    public void register(String username, String password, String email, final Callback callback) {
        new UserNetwork.Builder("/register")
            .add("username", username)
            .add("password", password)
            .add("email", email)
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                try {
                    init();
                    ID = data.getInt("ID");
                    setToken(data.getString("token"));
                    callback.ok(User.this);
                } catch (Exception e) {
                }
            }
        });
    }

    public void login(String username, String password, final Callback callback) {
        new UserNetwork.Builder("/login")
            .add("username", username)
            .add("password", password)
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                try {
                    init();
                    ID = data.getInt("id");
                    User.this.username = data.getString("username");
                    email = data.getString("email");
                    avatar = data.getString("avatar");
                    setToken(data.getString("token"));
                    callback.ok(User.this);
                } catch (Exception e) {
                }
            }
        });
    }

    public void getCategory(final CategoryCallback callback) {
        if (isOffline) {
            List<String> chosen = NewsDatabase.getInstance().queryCategory(1);
            List<String> remain = NewsDatabase.getInstance().queryCategory(0);
            callback.ok(chosen, remain);
        } else {
            getList("category", 0, -1, new UserNetwork.Callback() {
                @Override
                public void error(String msg) {
                    callback.error(msg);
                }

                @Override
                public void ok(JSONObject data) {
                    try {
                        JSONArray a = data.getJSONArray("data");
                        List<String> chosen = new ArrayList<>();
                        List<String> remain = new ArrayList<>();
                        for (int i = 0; i < a.length(); ++i) {
                            String s = a.getString(i);
                            if (s.startsWith("1"))
                                chosen.add(s.substring(1));
                            else
                                remain.add(s.substring(1));
                        }
                        callback.ok(chosen, remain);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    public void updateCategory(List<String> chosen, List<String> remain, Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().updateCategory(chosen, remain);
            callback.ok(User.this);
        } else {
            JSONArray data = new JSONArray();
            for (String s : chosen)
                data.put("1" + s);
            for (String s : remain)
                data.put("0" + s);
            setList("category", data, callback);
        }
    }

    public void getSearchHistory(int skip, int limit, final SearchHistoryCallback callback) {
        if (isOffline) {
            List<String> history = NewsDatabase.getInstance().querySearchHistory(skip, limit);
            callback.ok(history);
        } else {
            getList("search_history", skip, limit, new UserNetwork.Callback() {
                @Override
                public void error(String msg) {
                    callback.error(msg);
                }

                @Override
                public void ok(JSONObject data) {
                    try {
                        JSONArray a = data.getJSONArray("data");
                        List<String> historyList = new ArrayList<>();
                        for (int i = 0; i < a.length(); ++i)
                            historyList.add(a.getString(i));
                        callback.ok(historyList);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    public void getFavorite(int skip, int limit, final NewsCallback callback) {
        if (isOffline) {
            callback.ok(NewsDatabase.getInstance().queryFavorList(skip, limit));
        } else {
            getNews("favorite", skip, limit, callback);
        }
    }

    public void getHistory(int skip, int limit, final NewsCallback callback) {
        if (isOffline) {
            callback.ok(NewsDatabase.getInstance().queryHistoryList(skip, limit));
        } else {
            getNews("history", skip, limit, callback);
        }
    }

    public void addNews(News news, final Callback callback) {
        NewsDatabase.getInstance().addNews(news);
        new UserNetwork.Builder("/addNews")
            .add("data", news.toJSONObject().toString())
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                callback.ok(User.this);
            }
        });
    }

    public void addHistory(String news_id, LocalDateTime time, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addHistory(news_id, time);
        } else {
            addList("history", news_id, time, callback);
        }
    }

    public void addFavorite(String news_id, LocalDateTime time, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addFavor(news_id, time);
            callback.ok(User.this);
        } else {
            addList("favorite", news_id, time, callback);
        }
    }

    public void addSearchHistory(String keyword, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addSearchHistory(keyword);
            callback.ok(User.this);
        } else {
            addList("search_history", keyword, callback);
        }
    }

    public void delFavorite(String news_id, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delFavor(news_id);
            callback.ok(User.this);
        } else {
            delList("favorite", news_id, callback);
        }
    }

    public void delAllFavorite(final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllFavor();
            callback.ok(User.this);
        } else {
            setList("favorite", new JSONArray(), callback);
        }
    }

    public void delHistory(String news_id, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delHistory(news_id);
            callback.ok(User.this);
        } else {
            delList("history", news_id, callback);
        }
    }

    public void delAllHistory(final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllHistory();
            callback.ok(User.this);
        } else {
            setList("history", new JSONArray(), callback);
        }
    }

    public void delSearchHistory(String keyword, final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delSearchHistory(keyword);
            callback.ok(User.this);
        } else {
            delList("search_history", keyword, callback);
        }
    }

    public void delAllSearchHistory(final Callback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllSearchHistory();
            callback.ok(User.this);
        } else {
            setList("history", new JSONArray(), callback);
        }
    }

    private void addList(String type, String data, final Callback callback) {
        new UserNetwork.Builder("/addList")
            .add("token", token)
            .add("type", type)
            .add("data", data)
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                callback.ok(User.this);
            }
        });
    }

    private void addList(String type, String news_id, LocalDateTime time, final Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("news_id", news_id);
            json.put("time", Constants.TIME_FORMATTER.format(time));
        } catch (Exception e) {

        }
        new UserNetwork.Builder("/addList")
            .add("token", token)
            .add("type", type)
            .add("data", json.toString())
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                callback.ok(User.this);
            }
        });
    }

    private void delList(String type, String data, final Callback callback) {
        new UserNetwork.Builder("/delList")
            .add("token", token)
            .add("type", type)
            .add("data", data)
            .build().run(new UserNetwork.Callback() {
                @Override
                public void error(String msg) {
                    callback.error(msg);
                }

                @Override
                public void ok(JSONObject data) {
                    callback.ok(User.this);
                }
            });
    }

    private void setList(String type, JSONArray data, final Callback callback) {
        new UserNetwork.Builder("/setList")
            .add("token", token)
            .add("type", type)
            .add("data", data.toString())
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                callback.ok(User.this);
            }
        });
    }

    private void getList(String type, int skip, int limit, final UserNetwork.Callback callback) {
        new UserNetwork.Builder("/getList")
            .add("token", token)
            .add("type", type)
            .add("skip", "" + skip)
            .add("limit", "" + limit)
            .build().run(callback);
    }

    private void getNews(String type, int skip, int limit, final NewsCallback callback) {
        getList("type", skip, limit, new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                try {
                    JSONArray a = data.getJSONArray("data");
                    new UserNetwork.Builder("/getNews")
                        .add("data", a.toString())
                        .build().run(new UserNetwork.Callback() {
                        @Override
                        public void error(String msg) {
                            callback.error(msg);
                        }

                        @Override
                        public void ok(JSONObject data) {
                            try {
                                JSONArray n = data.getJSONArray("data");
                                List<News> newsList = new ArrayList<>();
                                for (int i = 0; i < n.length(); ++i)
                                    newsList.add(new News(n.getJSONObject(i)));
                                callback.ok(newsList);
                            } catch (Exception e) {

                            }
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    public interface Callback {
        void error(String msg);
        void ok(final User user);
    }

    public interface CategoryCallback {
        void error(String msg);
        void ok(final List<String> chosen, final List<String> remain);
    }

    public interface SearchHistoryCallback {
        void error(String msg);
        void ok(final List<String> historyList);
    }

    public interface NewsCallback {
        void error(String msg);
        void ok(final List<News> newsList);
    }

}
