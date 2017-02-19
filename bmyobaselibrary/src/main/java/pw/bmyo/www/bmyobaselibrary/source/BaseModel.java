package pw.bmyo.www.bmyobaselibrary.source;

import java.util.LinkedList;

import pw.bmyo.www.bmyobaselibrary.model.CommunityService;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import pw.bmyo.www.bmyobaselibrary.utils.HttpRequest;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public abstract class BaseModel {

    private LinkedList<String> mList = new LinkedList<>();

    @NotProguard
    private static final String Tag = "onSubscribe";

    //后台
    protected CommunityService mCommunityService;

    public BaseModel() {
        mCommunityService = HttpRequest.getInstance().create(CommunityService.class);
    }

    @NotProguard
    protected <T extends CommonResponse> void onSubscribe(Observable<T> observable, HttpResponse response) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        String method = null;
        int index;
        if (elements[0].getMethodName().equals(Tag)) {
            method = elements[1].getMethodName();
        } else {
            for (int i = 0; i < elements.length; i++) {
                if (elements[i].getMethodName().equals(Tag)) {
                    index = i + 1;
                    if (index < elements.length) {
                        method = elements[index].getMethodName();
                    }
                }
            }

        }
        if (method != null) {
            if (mList.contains(method)) {
                response.onRepeat(method);
                return;
            }
            mList.add(method);

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response);

            String finalMethod = method;
            response.addCompleteCallBack(resultCode -> mList.remove(finalMethod));
        } else {
            throw new IllegalStateException("stacktrace not found method");
        }

    }

}
