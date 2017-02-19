package pw.bmyo.www.bmyobaselibrary.source;

import com.google.gson.Gson;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import pw.bmyo.www.bmyobaselibrary.utils.ACache;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class BasePresenter<T extends BaseModel> {

    protected IBaseView mView;
    protected T mModel;
    private ACache mCache;
    private Gson mGson;

    public BasePresenter(IBaseView view, T model) {
        mView = view;
        mModel = model;
        mCache = ACache.get(mView.getContext());
        mGson = new Gson();
    }

    protected final HttpResponse getResponse(int resultCode, int requestCode) {
        return new HttpResponse(mView, resultCode, requestCode);
    }

    protected final HttpResponse getResponse(int resultCode) {
        return getResponse(resultCode, HttpResponse.DEFAULT);
    }

    protected final <V> void setCache(String key, HttpResponse response, Callback<HttpResponse> callback, Class<V> tClass, int saveTime) {
        Observable.just(key)
                .map((str) -> mCache.getAsString(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((s) -> {
                    if (s != null) {
                        CommonResponse common = null;
                        response.onStart();
                        try {
                            V t = mGson.fromJson(s, tClass);
                            common = CommonResponse.obtain(t);
                            response.onNext(common);
                        } catch (Exception e) {
                            response.onError(e);
                            if (common != null)
                                common.recycle();
                        }
                        response.onCompleted();
                        if (common != null)
                            common.recycle();
                    } else {
                        response.setCacheRun((obj) ->
                                Observable.just(obj)
                                        .observeOn(Schedulers.io())
                                        .subscribe((o) -> {
                                            mCache.put(key, mGson.toJson(o), saveTime);
                                        }));
                        callback.run(response);
                    }
                });
    }

    protected final <V> void setCache(String key, HttpResponse response, Callback<HttpResponse> callback, Class<V> tClass) {
        setCache(key, response, callback, tClass, Config.CACHE_SAVE_TIME);
    }

    protected String formatKey(String format, Object... o) {
        return String.format(format, o);
    }

}
