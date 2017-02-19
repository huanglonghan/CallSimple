package pw.bmyo.www.bmyobaselibrary.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import pw.bmyo.www.bmyobaselibrary.BuildConfig;
import pw.bmyo.www.bmyobaselibrary.BMMainApplication;
import pw.bmyo.www.bmyobaselibrary.bean.db.CookieObject;
import pw.bmyo.www.bmyobaselibrary.config.Config;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HTTP 请求对象
 * Created by Administrator on 2016/10/11 0011.
 */

public class HttpRequest {

    private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final String HTTP_CACHE_DIRECTORY= "HttpCache";

    private static class RETROFIT_REQ {
        static {
            cookieStore = new CookieStore();
            httpRequest = new HttpRequest();
        }

        private static final CookieStore cookieStore;
        private static final HttpRequest httpRequest;
    }

    private HttpRequest() {
        init();
    }

    public static HttpRequest getInstance() {
        return RETROFIT_REQ.httpRequest;
    }

    private Retrofit retrofit;

    /**
     * 初始化retrofit
     */
    private void init() {
        OkHttpClient okHttpClient = createOkHttpClient();
        retrofit = createRetrofit(okHttpClient, Config.API_HTTP_HOST).build();
    }

    public static Retrofit.Builder createRetrofit(OkHttpClient okHttpClient, String baseUrl) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    }

    public static OkHttpClient createOkHttpClient() {
        return createOkHttpClient(RETROFIT_REQ.cookieStore);
    }

    public static OkHttpClient createOkHttpClient(CookieJar cookieJar) {
        //增加okhttp日志
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(BMMainApplication.getContext().getCacheDir().getAbsolutePath(), HTTP_CACHE_DIRECTORY);
        Cache cache = new Cache(cacheFile, MAX_SIZE);

        //构建okhttp并添加cookie处理
        OkHttpClient.Builder build = new OkHttpClient()
                .newBuilder()
                .cache(cache)
                .cookieJar(cookieJar)
                .readTimeout(Config.HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(Config.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);

        build = build.addInterceptor(new ModifyHeaderInterceptor());

        //if (BuildConfig.DEBUG) {
        build = build.addInterceptor(logging);
        //}

        return build.build();
    }

    /**
     * 创建访问接口实例
     *
     * @param t
     * @param <T>
     * @return T
     */
    public <T> T create(Class<T> t) {
        return retrofit.create(t);
    }

    private static class CookieStore implements CookieJar {
        private final HashMap<String, List<Cookie>> cookieStore = new StoreHashMap();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (!cookies.isEmpty()) {
                List<Cookie> oldCookie = cookieStore.get(url.host());
                if (oldCookie != null) {
                    removeAll(cookies, oldCookie);
                    oldCookie.addAll(cookies);
                } else {
                    oldCookie = new ArrayList<>(cookies);
                }
                cookieStore.put(url.host(), oldCookie);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<>();
        }
    }

    private static void removeAll(List<Cookie> cookies, List<Cookie> oldCookies) {

        if (cookies == null || oldCookies == null) {
            return;
        }

        List<Cookie> delCookie = new ArrayList<>();
        try {
            for (Cookie c : cookies) {
                for (Cookie oldC : oldCookies) {
                    if (oldC.name().equals(c.name())) {
                        delCookie.add(oldC);
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d("", e.toString());
            }
        }

        try {
            oldCookies.removeAll(delCookie);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d("", e.toString());
            }
        }

    }

    private static class StoreHashMap extends HashMap<String, List<Cookie>> {
        private boolean isInit = false;

        StoreHashMap() {
            super();
            init();
        }

        private void init() {
            Realm realm = Db.getInstance(BMMainApplication.getContext());
            RealmResults<CookieObject> objects = realm.where(CookieObject.class).findAll();
            int size = objects.size();
            LinkedList<String> name = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                CookieObject object = objects.get(i);
                String url = object.getUrl();
                Cookie cookie = Cookie.parse(HttpUrl.parse("http://" + url), object.getCookie());
                if (containsKey(url)) {
                    List<Cookie> temp = new LinkedList<>();
                    for (Cookie c : get(url)) {
                        if (!c.name().equals(cookie.name()) &&
                                !name.contains(cookie.name())) {
                            temp.add(cookie);
                            name.add(cookie.name());
                        }
                    }
                    get(url).addAll(temp);
                } else {
                    List<Cookie> list = new LinkedList<>();
                    if (!name.contains(cookie.name())) {
                        list.add(cookie);
                        name.add(cookie.name());
                    }
                    put(object.getUrl(), list);
                }
            }
            realm.close();
            isInit = true;
        }

        @Override
        public List<Cookie> put(String key, List<Cookie> value) {
            if (isInit) saveData(key, value);
            return super.put(key, value);
        }

        private void saveData(String key, List<Cookie> value) {
            Realm realm = Db.getInstance(BMMainApplication.getContext());
            if (!containsKey(key)) {
                realm.executeTransaction(r ->
                        r.insert(CookieObject.toSet(key, value)));
            } else {
                delItem(realm, key);
                realm.executeTransaction(r ->
                        r.insert(CookieObject.toSet(key, value)));
            }
            realm.close();
        }

        private void delItem(Realm realm, String key) {
            RealmResults<CookieObject> results =
                    realm.where(CookieObject.class).equalTo("url", key).findAll();
            realm.executeTransaction(r -> results.deleteAllFromRealm());
        }
    }

    private static class ModifyHeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request authorised = chain.request().newBuilder()
                    .header("Origin", "http://android.moplus.live")
                    .header("User-Agent", "keke")
                    .build();
            return chain.proceed(authorised);
        }
    }

}
