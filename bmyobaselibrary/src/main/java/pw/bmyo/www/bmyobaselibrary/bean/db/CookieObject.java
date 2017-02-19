package pw.bmyo.www.bmyobaselibrary.bean.db;

import java.util.LinkedList;
import java.util.List;

import io.realm.RealmObject;
import okhttp3.Cookie;

/**
 * Created by huang on 2016/12/29.
 */

public class CookieObject extends RealmObject {
    private String url;
    private String cookie;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static List<CookieObject> toSet(String key, List<Cookie> list) {
        LinkedList<CookieObject> temp = new LinkedList<>();
        for (Cookie c : list) {
            CookieObject object = new CookieObject();
            object.setCookie(c.toString());
            object.setUrl(key);
            temp.add(object);
        }
        return temp;
    }
}
