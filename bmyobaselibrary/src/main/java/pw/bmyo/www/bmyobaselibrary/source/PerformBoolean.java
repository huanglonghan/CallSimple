package pw.bmyo.www.bmyobaselibrary.source;

import android.support.v4.util.Pools;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;

/**
 * Created by huang on 2016/12/23.
 */

public class PerformBoolean implements PerformResponse {

    private static final Pools.SynchronizedPool<PerformBoolean> sPool =
            new Pools.SynchronizedPool<>(10);

    public static PerformBoolean obtain() {
        PerformBoolean instance = sPool.acquire();
        if (instance == null) {
            instance = new PerformBoolean();
        }
        return instance;
    }

    @Override
    public void recycle() {
        sPool.release(this);
    }

    @Override
    public Boolean onPerform(CommonResponse o) {
        switch (o.getStatus()) {
            case Config.HTTP_STATUS_SUCCEED:
                return Boolean.TRUE;
            default:
                return Boolean.FALSE;
        }
    }

    @Override
    public void onPerError(Throwable e) {

    }

    @Override
    public void onPerCompleted() {

    }

    @Override
    public void onPerStart() {

    }
}
