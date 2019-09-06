package com.java.moyu;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private static User instance;
    private String token;
    private int ID;
    private String username;
    private String email;
    private String avatar;
    private boolean isOffline = true;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
            instance.init();
            instance.token = NewsDatabase.getInstance().getToken();
        }
        return instance;
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

    public boolean isLogged() {
        return !isOffline;
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

    public void updateUserInfo(final DefaultCallback callback) {
        if (!token.isEmpty()) {
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
                        setToken(data.getString("token"));
                        isOffline = false;
                        callback.ok();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void editUserInfo(final String avatar, final DefaultCallback callback) {
        new UserNetwork.Builder("/userEdit")
            .add("token", token)
            .add("avatar", avatar)
            .build().run(new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                User.this.avatar = avatar;
                callback.ok();
            }
        });
    }

    public void login(String username, String password, final DefaultCallback callback) {
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
                    isOffline = false;
                    callback.ok();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logout() {
        init();
        setToken("");
    }

    public void register(String username, String password, String email, final DefaultCallback callback) {
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
                    ID = data.getInt("id");
                    setToken(data.getString("token"));
                    callback.ok();
                } catch (Exception e) {
                    e.printStackTrace();
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
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void updateCategory(List<String> chosen, List<String> remain, DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().updateCategory(chosen, remain);
            callback.ok();
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
                        e.printStackTrace();
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

    public void addNews(News news, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addNews(news);
        } else {
            new UserNetwork.Builder("/addNews")
                .add("data", news.toJSONObject().toString())
                .build().run(new UserNetwork.Callback() {
                @Override
                public void error(String msg) {
                    callback.error(msg);
                }

                @Override
                public void ok(JSONObject data) {
                    callback.ok();
                }
            });
        }
    }

    public void addHistory(String news_id, LocalDateTime time, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addHistory(news_id, time);
        } else {
            addList("history", news_id, time, callback);
        }
    }

    public void addFavorite(String news_id, LocalDateTime time, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addFavor(news_id, time);
            callback.ok();
        } else {
            addList("favorite", news_id, time, callback);
        }
    }

    public void addSearchHistory(String keyword, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().addSearchHistory(keyword);
            callback.ok();
        } else {
            addList("search_history", keyword, callback);
        }
    }

    public void delFavorite(String news_id, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delFavor(news_id);
            callback.ok();
        } else {
            delList("favorite", news_id, callback);
        }
    }

    public void delAllFavorite(final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllFavor();
            callback.ok();
        } else {
            setList("favorite", new JSONArray(), callback);
        }
    }

    public void delHistory(String news_id, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delHistory(news_id);
            callback.ok();
        } else {
            delList("history", news_id, callback);
        }
    }

    public void delAllHistory(final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllHistory();
            callback.ok();
        } else {
            setList("history", new JSONArray(), callback);
        }
    }

    public void delSearchHistory(String keyword, final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delSearchHistory(keyword);
            callback.ok();
        } else {
            delList("search_history", keyword, callback);
        }
    }

    public void delAllSearchHistory(final DefaultCallback callback) {
        if (isOffline) {
            NewsDatabase.getInstance().delAllSearchHistory();
            callback.ok();
        } else {
            setList("history", new JSONArray(), callback);
        }
    }

    public void hasStarred(String news_id, final HasCallback callback) {
        if (isOffline) {
            callback.ok(NewsDatabase.getInstance().queryFavor(news_id));
        } else {
            new UserNetwork.Builder("/hasList")
                .add("token", token)
                .add("type", "favorite")
                .add("data", news_id)
                .build().run(new UserNetwork.Callback() {
                @Override
                public void error(String msg) {
                    callback.error(msg);
                }

                @Override
                public void ok(JSONObject data) {
                    try {
                        callback.ok(data.getBoolean("data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void addList(String type, String data, final DefaultCallback callback) {
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
                callback.ok();
            }
        });
    }

    private void addList(String type, String news_id, LocalDateTime time, final DefaultCallback callback) {
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
                callback.ok();
            }
        });
    }

    private void delList(String type, String data, final DefaultCallback callback) {
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
                callback.ok();
            }
        });
    }

    private void setList(String type, JSONArray data, final DefaultCallback callback) {
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
                callback.ok();
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
        getList(type, skip, limit, new UserNetwork.Callback() {
            @Override
            public void error(String msg) {
                callback.error(msg);
            }

            @Override
            public void ok(JSONObject data) {
                try {
                    JSONArray a = data.getJSONArray("data");
                    List<String> newsID = new ArrayList<>();
                    for (int i = 0; i < a.length(); ++i)
                        newsID.add(a.getJSONObject(i).getString("news_id"));
                    new UserNetwork.Builder("/getNews")
                        .add("data", new JSONArray(newsID).toString())
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
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    public interface DefaultCallback {

        void error(String msg);

        void ok();

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

    public interface HasCallback {

        void error(String msg);

        void ok(final boolean has);

    }

}
