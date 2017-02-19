package pw.bmyo.www.bmyobaselibrary.source;

import android.support.design.widget.Snackbar;
import android.support.v4.util.Pools;

import java.util.LinkedList;
import java.util.List;

import pw.bmyo.www.bmyobaselibrary.BuildConfig;
import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import pw.bmyo.www.bmyobaselibrary.widget.AlertFragment;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class HttpResponse extends Subscriber<CommonResponse> {

    public static final int DEFAULT = -20000;
    private static final int IGNORE = -20001;

    private Integer mResultCode;
    private int mRequestCode;
    private List<Callback> mCompleteRun;
    private Callback mCacheRun;
    private Result mResult;
    private PerformResponse mPerformResponse;

    private boolean isCheckLogin = true;
    private boolean isShowLoading = true;
    private boolean isUse = false;
    private IBaseView mView;

    private static final Pools.SynchronizedPool<HttpResponse> sPool =
            new Pools.SynchronizedPool<>(15);

    public static HttpResponse obtain(IBaseView view, int resultCode, int requestCode) {
        HttpResponse instance = sPool.acquire();
        if (instance == null) {
            instance = new HttpResponse(view, resultCode, requestCode);
        } else {
            instance.init(view, resultCode, requestCode);
        }
        return instance;
    }

    public static HttpResponse obtain(IBaseView view, int resultCode) {
        return obtain(view, resultCode, DEFAULT);
    }

    public static HttpResponse obtain(IBaseView view) {
        return obtain(view, DEFAULT, DEFAULT);
    }

    public void recycle() {
        reset();
        sPool.release(this);
    }

    public final boolean isCheckLogin() {
        return isCheckLogin;
    }

    public final HttpResponse setCheckLogin(boolean checkLogin) {
        isCheckLogin = checkLogin;
        return this;
    }

    public final boolean isShowLoading() {
        return isShowLoading;
    }

    public final HttpResponse setShowLoading(boolean showLoading) {
        isShowLoading = showLoading;
        return this;
    }

    public final HttpResponse reset() {
        mCompleteRun.clear();
        mResult.clear();
        mCacheRun = null;
        isShowLoading = true;
        isCheckLogin = true;
        mResultCode = DEFAULT;
        mRequestCode = DEFAULT;
        mPerformResponse.recycle();
        mPerformResponse = null;
        isUse = false;
        mView = null;
        return this;
    }

    public void setCacheRun(Callback cacheRun) {
        mCacheRun = cacheRun;
    }

    public final void init(IBaseView view, int resultCode, int requestCode) {
        mView = view;
        mResultCode = resultCode;
        mRequestCode = requestCode;
        if (mResult == null)
            mResult = new Result();
        if (mCompleteRun == null)
            mCompleteRun = new LinkedList<>();
        mCacheRun = null;
    }

    public HttpResponse(IBaseView view, int resultCode) {
        this(view, resultCode, DEFAULT);
    }

    public HttpResponse(IBaseView view, int resultCode, int requestCode) {
        init(view, resultCode, requestCode);
    }

    public HttpResponse(IBaseView view) {
        this(view, DEFAULT, DEFAULT);
    }

    @Override
    public final void onCompleted() {
        mView.hideAlert(isShowLoading);
        mPerformResponse.onPerCompleted();
        if (mCompleteRun != null && mCompleteRun.size() > 0) {
            for (Callback runnable : mCompleteRun) {
                runnable.run(mResultCode);
            }
        }
    }

    @Override
    public final void onStart() {
        mView.showAlert(isShowLoading);
        if (mPerformResponse == null) mPerformResponse = PerformBoolean.obtain();
        mPerformResponse.onPerStart();
    }

    @Override
    public final void onError(Throwable e) {
        mView.hideAlert(isShowLoading);
        if (BuildConfig.DEBUG) {
            mView.showTip(e.toString(), Snackbar.LENGTH_LONG);
        }
        mView.returnResult(mResultCode, mResult);
        mPerformResponse.onPerError(e);
        if (mCompleteRun != null && mCompleteRun.size() > 0) {
            for (Callback runnable : mCompleteRun) {
                runnable.run(mResultCode);
            }
        }
    }

    public final void onRepeat(String tag) {
        if (BuildConfig.DEBUG) {
            mView.showTip("http请求重复, http方法:" + tag, Snackbar.LENGTH_LONG);
        }
        mView.returnResult(mResultCode, mResult);
    }

    @Override
    public final void onNext(CommonResponse o) {
        mView.hideAlert(isShowLoading);
        if (!isCheckLogin && o.getStatus() == Config.HTTP_STATUS_NOT_LOGIN) {
            o.setStatus(IGNORE);
        }

//        if (o.getStatus() >= Config.HTTP_ERROR_SCOPE) {
//            mView.showTip(R.string.http_default_tip);
//            o.setStatus(IGNORE);
//        }

        switch (o.getStatus()) {
            case Config.HTTP_STATUS_NOT_LOGIN:
                mView.showAlert(true, AlertFragment.class);
                o.setStatus(IGNORE);
                break;
            case IGNORE:
                break;
            default:
                Object result = mPerformResponse.onPerform(o);
                if (mCacheRun != null) {
                    mCacheRun.run(result);
                }
                if (o.getStatus() == Config.HTTP_DIALOG_TIP ||
                        o.getStatus() == Config.HTTP_CHECK_ERROR ||
                        o.getStatus() == Config.HTTP_DIALOG_TIMEOUT)
                    mView.showTip(o.getMsg());
                if (mResultCode == IGNORE) break;
                mView.returnResult(mResultCode,
                        mResult.set(result,
                                mRequestCode,
                                o.getStatus(),
                                o.getMsg()));
                return;
        }
        mView.returnResult(mResultCode, mResult);
    }

    public HttpResponse addCompleteCallBack(Callback callback) {
        mCompleteRun.add(callback);
        return this;
    }

    public HttpResponse setPerformResponse(PerformResponse performResponse) {
        mPerformResponse = performResponse;
        return this;
    }

    public boolean isUse() {
        return isUse;
    }

    public HttpResponse setUse(boolean use) {
        isUse = use;
        return this;
    }

    public static class Result {

        public int requestCode;
        public int statusCode;
        public Object data;
        public String msg;

        Result() {
            this(null, DEFAULT, DEFAULT);
        }

        void clear() {
            requestCode = DEFAULT;
            requestCode = DEFAULT;
            data = null;
            msg = null;
        }

        Result(Object o) {
            this(o, DEFAULT, DEFAULT);
        }

        Result(Object o, Integer request) {
            this(o, request, DEFAULT);
        }

        Result(Object o, Integer request, Integer status) {
            requestCode = request;
            statusCode = status;
            data = o;
            msg = null;
        }

        Result set(Object o, int request, String msg) {
            data = o;
            requestCode = request;
            this.msg = msg;
            return this;
        }

        Result set(Object o, int request, int status, String msg) {
            data = o;
            requestCode = request;
            statusCode = status;
            this.msg = msg;
            return this;
        }

        Result setData(Object o) {
            data = o;
            return this;
        }

        Result setRequestCode(int code) {
            requestCode = code;
            return this;
        }

        Result setResultCode(int code) {
            statusCode = code;
            return this;
        }
    }

    public static boolean isDefault(Result o) {
        return o != null && o.requestCode == DEFAULT;
    }

    public Integer getResultCode() {
        return mResultCode;
    }

    public HttpResponse setResultCode(Integer resultCode) {
        mResultCode = resultCode;
        return this;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public HttpResponse setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }
}

