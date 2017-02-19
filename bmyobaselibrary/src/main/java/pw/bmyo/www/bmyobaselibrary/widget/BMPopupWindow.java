package pw.bmyo.www.bmyobaselibrary.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import butterknife.ButterKnife;
import pw.bmyo.www.bmyobaselibrary.source.BaseActivity;
import pw.bmyo.www.bmyobaselibrary.source.BaseFragment;
import pw.bmyo.www.bmyobaselibrary.source.HttpResponse;
import pw.bmyo.www.bmyobaselibrary.source.IBaseView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by huang on 2016/12/22.
 */

public class BMPopupWindow extends android.widget.PopupWindow implements IBaseView {

    protected BaseFragment mFragment;

    @Override
    public <T extends DialogFragment> void showAlert(boolean isShow, Class<T> dialogFragment) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showAlert(isShow, dialogFragment);
    }

    @Override
    public void showAlert(boolean isShow) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showAlert(isShow);
    }

    @Override
    public void hideAlert(boolean isShow) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.hideAlert(isShow);
    }

    @Override
    public void setDelaySendMsg() {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.setDelaySendMsg();
    }

    @Override
    public void showTip(@StringRes int resId, int delay) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId, delay);
    }

    @Override
    public void showTip(String msg, int delay) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg, delay);
    }

    @Override
    public void showTip(@StringRes int resId, View view) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId, view);
    }

    @Override
    public void showTip(String msg, View view) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg, view);
    }

    @Override
    public void showTip(@StringRes int resId) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId);
    }

    @Override
    public void showTip(String msg) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg);
    }

    @Override
    public <T> void postMsg(T t) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.postMsg(t);
    }

    @Override
    public String getResourceString(@StringRes int id) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        return mFragment.getResourceString(id);
    }

    @Override
    public Context getContext() {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        return mFragment.getContext();
    }

    @Override
    public void returnResult(int type, HttpResponse.Result result) {
    }

    public void setPopupSoftInput(boolean popupSoftInput) {
        if (popupSoftInput)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public interface BuildOnClickListener {
        void buildOnClickListener(View view, View.OnClickListener mListener);
    }

    private View.OnClickListener mOnClickListener;
    private OnDismissListener mListener;

    public BMPopupWindow(View view, int width, int height) {
        super(view, width, height);
        ButterKnife.bind(this, view);
        init();
    }

    public BMPopupWindow(View view, BaseFragment fragment, int width, int height) {
        super(view, width, height);
        ButterKnife.bind(this, view);
        mFragment = fragment;
        init();
    }

    private void init() {
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            TransitionInflater inflater = TransitionInflater.from(getContentView().getContext());
//            setEnterTransition(inflater.inflateTransition(android.R.transition.slide_bottom));
//            setExitTransition(inflater.inflateTransition(android.R.transition.slide_bottom));
//        }

    }

    public static BMPopupWindow getInstant(BaseFragment fragment, @LayoutRes int resId) {
        return new BMPopupWindow(getView(fragment.getContext(), resId), fragment, MATCH_PARENT, WRAP_CONTENT);
    }

    public static BMPopupWindow getInstant(BaseActivity content, @LayoutRes int resId) {
        return new BMPopupWindow(getView(content, resId), MATCH_PARENT, WRAP_CONTENT);
    }

    protected static View getView(Context content, @LayoutRes int resId) {
        return LayoutInflater.from(content).inflate(resId, null, false);
    }

    public void setBuildOnClickListener(BuildOnClickListener listener) {
        if (mOnClickListener == null) {
            throw new IllegalArgumentException("Must be set OnClickListener");
        }
        listener.buildOnClickListener(getContentView(), mOnClickListener);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {

        setOnDismissListener(() -> {

            if (mListener != null) {
                mListener.onDismiss();
            }
        });
        super.showAtLocation(parent, gravity, x, y);

    }

    public void setDismissListener(OnDismissListener listener) {
        mListener = listener;
    }

}
