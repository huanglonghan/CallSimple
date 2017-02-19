package pw.bmyo.www.bmyobaselibrary.widget;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.WrapRecyclerView;
import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.base.BaseAdapter;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Created by huang on 2017/1/8.
 */

public final class BMRxView {

    @CheckResult
    @NonNull
    public static Observable<View> itemClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return Observable.create(new OnSubscribe.ItemClickOnSubscribe(adapter));
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemLongClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return Observable.create(new OnSubscribe.ItemLongClickOnSubscribe(adapter));
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribe.ItemTouchOnSubscribe.TouchMsg> itemTouch(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return Observable.create(new OnSubscribe.ItemTouchOnSubscribe(adapter));
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view, int direction) {
        checkNotNull(view, "view == null");
        return Observable.create(new OnSubscribe.CanScrollOnSubscribe(view, direction));
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view) {
        checkNotNull(view, "view == null");
        return Observable.create(new OnSubscribe.CanScrollOnSubscribe(view));
    }

    @CheckResult
    @NonNull
    public static Observable<WrapRecyclerView> wrapRefresh(@NonNull WrapRecyclerView view) {
        checkNotNull(view, "view == null");
        return Observable.create(new OnSubscribe.WrapRefreshOnSubscribe(view));
    }
}
