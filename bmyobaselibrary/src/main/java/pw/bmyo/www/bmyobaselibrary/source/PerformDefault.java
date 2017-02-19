package pw.bmyo.www.bmyobaselibrary.source;

import android.support.v4.util.Pools;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;

/**
 * Created by huang on 2016/12/23.
 */

public class PerformDefault implements PerformResponse {

    private static final Pools.SynchronizedPool<PerformDefault> sPool =
            new Pools.SynchronizedPool<>(20);

    public static PerformDefault obtain() {
        PerformDefault instance = sPool.acquire();
        if (instance == null) {
            instance = new PerformDefault();
        }
        return instance;
    }

    @Override
    public void recycle() {
        sPool.release(this);
    }

    @Override
    public Object onPerform(CommonResponse o) {
        switch (o.getStatus()) {
            case Config.HTTP_STATUS_SUCCEED:
                return o.getData();
            default:
                return null;
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
