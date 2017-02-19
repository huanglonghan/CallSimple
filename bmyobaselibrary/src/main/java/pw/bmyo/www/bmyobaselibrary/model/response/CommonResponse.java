package pw.bmyo.www.bmyobaselibrary.model.response;

import android.support.v4.util.Pools;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.source.NotProguard;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

@NotProguard
public class CommonResponse<T> {

    /**
     * status : 401
     * msg : 参数错误
     * data :
     */
    private int status;
    private T data;
    private String msg;

    private static final Pools.SynchronizedPool<CommonResponse> sPool =
            new Pools.SynchronizedPool<>(10);

    public CommonResponse(T data) {
        this(data, Config.HTTP_STATUS_SUCCEED);
    }

    public CommonResponse(T data, int status) {
        this.data = data;
        this.status = status;
    }


    public static CommonResponse obtain(Object o) {
        CommonResponse instance = sPool.acquire();
        if (instance == null) {
            instance = new CommonResponse(o);
        } else {
            instance.setData(o);
            instance.setStatus(Config.HTTP_STATUS_SUCCEED);
        }
        return instance;
    }

    public void recycle() {
        data = null;
        msg = null;
        status = 0;
        sPool.release(this);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
