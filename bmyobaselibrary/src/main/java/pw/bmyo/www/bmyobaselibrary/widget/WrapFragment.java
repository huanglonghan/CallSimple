package pw.bmyo.www.bmyobaselibrary.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.source.BaseFragment;
import pw.bmyo.www.bmyobaselibrary.source.HttpResponse;
import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.SpaceItem;
import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.WrapAdapter;
import pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.WrapRecyclerView;

/**
 * Created by huang on 2016/12/16.
 */

public abstract class WrapFragment<T, V, A extends WrapAdapter<T>>
        extends BaseFragment implements OnItemClickListener<T> {

    private WrapRecyclerView mWrapRecyclerView;

    private V mDown;
    private V mUp;

    private A mAdapter;

    private LinkedList<Integer> mFilterType = new LinkedList<>();

    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;

    @Override
    protected void initPrepare(@Nullable Bundle savedInstanceState) {
        super.initPrepare(savedInstanceState);
        initComponent();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPerform();
    }

    private synchronized void initPerform() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPerform();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }


    protected void onUserVisible() {

    }

    protected void onUserInvisible() {

    }

    protected void onFirstUserInvisible() {

    }

    protected void onFirstUserVisible() {
        onFirstPerform();
    }

    protected abstract void onFirstPerform();


    private void initComponent() {

        mWrapRecyclerView = setRecyclerView();

        if (mWrapRecyclerView == null) {
            throw new IllegalStateException(
                    "Must be set RecyclerView");
        }

        mAdapter = setAdapter();

        if (mAdapter == null) {
            throw new IllegalStateException(
                    "Must be set Adapter");
        }

        mWrapRecyclerView.setLayoutManager(setLayoutManager());
        mWrapRecyclerView.setItemAnimator(new BMItemAnimator());
        RecyclerView.ItemDecoration decoration = addItemDecoration();
        if (decoration != null) {
            mWrapRecyclerView.addItemDecoration(decoration);
        }
        mWrapRecyclerView.setHasFixedSize(true);
        mWrapRecyclerView.setAdapter(mAdapter);

        BMRxView.wrapRefresh(mWrapRecyclerView)
                .subscribe(view -> {
                    switch (view.getSlideDirection()) {
                        case WrapRecyclerView.SLIDE_DIRECTION_DOWN:
                            WrapFragment.this.onRefresh();
                            break;
                        case WrapRecyclerView.SLIDE_DIRECTION_UP:
                            onSlideUp(mUp);
                            break;
                    }
                });

        BMRxView.itemClicks(mAdapter)
                .throttleFirst(Config.EVENT_THROTTLE_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(view -> {
                    int position = mWrapRecyclerView.getChildAdapterPosition(mWrapRecyclerView.findContainingItemView(view));
                    onItemClick(view, mAdapter.getItemData(position), position);
                });

    }

    public abstract void onSlideUp(V up);

    public abstract void onRefresh();

    @Override
    public void returnResult(int type, HttpResponse.Result result) {
        if (mFilterType.contains(type)) {
            mWrapRecyclerView.refreshComplete();
            switch (result.statusCode) {
                case Config.HTTP_NO_MORE:
                case Config.HTTP_NO_MORE_A:
                    mWrapRecyclerView.moreOver(result.msg);
                    break;
            }
        }
    }

    public void setUp(V up) {
        mUp = up;
    }

    public void setDown(V down) {
        mDown = down;
    }

    public abstract WrapRecyclerView setRecyclerView();

    public RecyclerView.ItemDecoration addItemDecoration() {
        return new SpaceItem(getContext(), SpaceItem.VERTICAL_LIST, 1);
    }

    public RecyclerView.ItemAnimator setItemAnimator() {
        return new BMItemAnimator();
    }

    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    public A getAdapter() {
        return mAdapter;
    }

    public abstract A setAdapter();

    public WrapRecyclerView getRecycler() {
        return mWrapRecyclerView;
    }

    public V getDown() {
        return mDown;
    }

    public V getUp() {
        return mUp;
    }

    public void initUpDown(V up, V down) {
        mUp = up;
        mDown = down;
    }

    public void addFilterResultType(int filter) {
        mFilterType.add(filter);
    }

}
