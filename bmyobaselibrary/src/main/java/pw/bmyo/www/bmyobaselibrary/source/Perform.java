package pw.bmyo.www.bmyobaselibrary.source;

import android.support.v4.util.Pools;

import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;

/**
 * Created by huang on 2016/12/23.
 */

public class Perform implements PerformResponse {

    private static final Pools.SynchronizedPool<Perform> sPool =
            new Pools.SynchronizedPool<>(10);

    public static Perform obtain() {
        Perform instance = sPool.acquire();
        if (instance == null) {
            instance = new Perform();
        }
        return instance;
    }

    @Override
    public void recycle() {
        sPool.release(this);
    }

    @Override
    public Object onPerform(CommonResponse o) {
        return null;
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
