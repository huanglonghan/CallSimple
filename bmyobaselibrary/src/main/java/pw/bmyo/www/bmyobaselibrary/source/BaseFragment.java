package pw.bmyo.www.bmyobaselibrary.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pw.bmyo.www.bmyobaselibrary.widget.LoadingFragment;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class BaseFragment extends Fragment implements IBaseView {

    private Unbinder mUnbinder;
    protected RequestManager mGlide;
    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mGlide = Glide.with(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getContentLayoutId();
        switch (resId) {
            case 0:
                return null;
            default:
                if (mView == null) {
                    mView = inflater.inflate(resId, container, false);
                    bindButterKnife(mView);
                    onInitViewsAndEvents(mView);
                    initPrepare(savedInstanceState);
                }
                return mView;
        }
    }

    protected void initPrepare(@Nullable Bundle savedInstanceState) {

    }

    protected abstract int getContentLayoutId();

    protected void onInitViewsAndEvents(View view) {

    }

    private void bindButterKnife(View view) {
        if (mUnbinder == null)
            mUnbinder = ButterKnife.bind(this, view);
    }

    protected final void exit() {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.exit();
    }

    @Override
    public void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        onDestroyAnything();
        super.onDestroy();
    }

    protected void onDestroyAnything() {

    }

    @Override
    public void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void msgHandle(String msg) {

    }

    /**
     * 显示提示
     *
     * @param msg  提示的消息
     * @param view 如果为空则自动获取activity的view
     */
    public void showTip(String msg, View view) {
        if (view != null)
            //Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示提示
     *
     * @param msg   提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    public final void showTip(String msg, int delay) {
        View view = getView();
        if (view != null)
            //Snackbar.make(view, msg, delay).show();
            Toast.makeText(getContext(), msg, delay).show();
    }

    /**
     * 显示提示
     *
     * @param msg 提示的消息
     */
    public final void showTip(String msg) {
        View view = getView();
        if (view != null)
            showTip(msg, view);
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param view  如果为空则自动获取activity的view
     */
    public final void showTip(@StringRes int resId, View view) {
        if (view != null) {
            String msg = getContext().getResources().getString(resId);
            showTip(msg, view);
        }
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    public final void showTip(@StringRes int resId, int delay) {
        View view = getView();
        if (view != null) {
            String msg = getContext().getResources().getString(resId);
            showTip(msg, delay);
        }
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     */
    public final void showTip(@StringRes int resId) {
        View view = getView();
        if (view != null)
            showTip(resId, view);
    }

    /**
     * 显示加载非模态等候
     */
    public void showAlert(boolean isShow) {
        showAlert(isShow, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public <T extends DialogFragment> void showAlert(boolean isShow, Class<T> fragment) {
        postMsg(new BaseActivity.DialogMsg<>(isShow,
                BaseActivity.DialogEnum.SHOW_DIALOG,
                fragment));
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert(boolean isShow) {
        postMsg(new BaseActivity.DialogMsg<>(isShow, BaseActivity.DialogEnum.HIDE_DIALOG));
    }

    /**
     * 被子类选择性实现
     */
    public void setDelaySendMsg() {
    }

    public final <T> void postMsg(T t) {
        EventBus.getDefault().post(t);
    }

    public final <T> void postStickyMsg(T t) {
        EventBus.getDefault().postSticky(t);
    }

    public final String getResourceString(@StringRes int id) {
        return getResources().getString(id);
    }
}
