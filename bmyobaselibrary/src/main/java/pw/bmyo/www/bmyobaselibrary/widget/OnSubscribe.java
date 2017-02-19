package pw.bmyo.www.bmyobaselibrary.widget;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.WrapRecyclerView;
import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.base.BaseAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

/**
 * Created by huang on 2017/1/8.
 */

public class OnSubscribe {
    public static final class ItemClickOnSubscribe implements Observable.OnSubscribe<View> {
        final BaseAdapter mAdapter;

        ItemClickOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public void call(final Subscriber<? super View> subscriber) {
            checkUiThread();
            View.OnClickListener listener = v -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(v);
                }
            };
            mAdapter.setOnItemClickListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    mAdapter.setOnItemClickListener(null);
                }
            });
        }
    }

    public static final class ItemLongClickOnSubscribe implements Observable.OnSubscribe<View> {
        final BaseAdapter mAdapter;

        ItemLongClickOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public void call(final Subscriber<? super View> subscriber) {
            checkUiThread();
            View.OnLongClickListener listener = v -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(v);
                }
                return mAdapter.isLongIntercept();
            };
            mAdapter.setOnItemLongClickListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    mAdapter.setOnItemLongClickListener(null);
                }
            });
        }
    }

    public static final class ItemTouchOnSubscribe implements Observable.OnSubscribe<ItemTouchOnSubscribe.TouchMsg> {
        final BaseAdapter mAdapter;

        ItemTouchOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public void call(final Subscriber<? super TouchMsg> subscriber) {
            checkUiThread();
            View.OnTouchListener listener = (v,e) -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new TouchMsg(v, e));
                }
                return mAdapter.isTouchIntercept();
            };
            mAdapter.setTouchListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    mAdapter.setTouchListener(null);
                }
            });
        }

        public class TouchMsg {
            public View mView;
            public MotionEvent mEvent;

            TouchMsg(View view, MotionEvent event) {
                mEvent = event;
                mView = view;
            }

        }
    }

    public static final class CanScrollOnSubscribe implements Observable.OnSubscribe<RecyclerView> {
        public static final int CAN_UP = 1;
        public static final int CAN_DOWN = -1;

        final RecyclerView view;
        final int direction;

        CanScrollOnSubscribe(RecyclerView view, int direction) {
            this.view = view;
            this.direction = direction;
        }

        CanScrollOnSubscribe(RecyclerView view) {
            this.view = view;
            this.direction = CAN_UP;
        }

        @Override
        public void call(final Subscriber<? super RecyclerView> subscriber) {
            checkUiThread();
            RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!ViewCompat.canScrollVertically(recyclerView, direction)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(recyclerView);
                        }
                    }
                }
            };
            view.addOnScrollListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    view.addOnScrollListener(null);
                }
            });
        }
    }

    public static final class WrapRefreshOnSubscribe implements Observable.OnSubscribe<WrapRecyclerView> {

        final WrapRecyclerView view;

        WrapRefreshOnSubscribe(WrapRecyclerView view) {
            this.view = view;
        }

        @Override
        public void call(final Subscriber<? super WrapRecyclerView> subscriber) {
            checkUiThread();

            WrapRecyclerView.OnRefreshListener listener=new WrapRecyclerView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!subscriber.isUnsubscribed()) {
                        view.setSlideDirection(WrapRecyclerView.SLIDE_DIRECTION_DOWN);
                        subscriber.onNext(view);
                    }
                }

                @Override
                public void onLoading() {
                    if (!subscriber.isUnsubscribed()) {
                        view.setSlideDirection(WrapRecyclerView.SLIDE_DIRECTION_UP);
                        subscriber.onNext(view);
                    }
                }
            };

            view.setOnRefreshListener(listener);
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    view.setOnRefreshListener(null);
                }
            });
        }
    }

}
