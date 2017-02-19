package pw.bmyo.www.bmyobaselibrary.source;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.anthony.ultimateswipetool.SwipeHelper;
import com.anthony.ultimateswipetool.activity.SwipeBackLayout;
import com.anthony.ultimateswipetool.activity.interfaces.SwipeBackActivityBase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pw.bmyo.www.bmyobaselibrary.R;
import pw.bmyo.www.bmyobaselibrary.widget.LoadingFragment;


/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class BaseActivity extends AppCompatActivity implements SwipeBackActivityBase {

    private static boolean isExit = false;
    private DialogFragment mDialogFragment;
    private SwipeHelper mHelper;

    protected Unbinder mUnbinder;
    protected FragmentManager mFragmentManager;
    protected Fragment mCurrentFragment;
    protected RequestManager mGlide;

    public static final String FRAGMENT_TAG_DIALOG = "fragment_tag_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreateBefore();
        super.onCreate(savedInstanceState);
        mHelper = new SwipeHelper(this);
        mHelper.onActivityCreate();
        mFragmentManager = getSupportFragmentManager();
        mGlide = Glide.with(this);
        onInflateBefore();
        initContentView();
    }

    private void initContentView() {
        int resId = getContentLayoutId();
        if (resId == 0) return;
        setContentView(resId);
        bindButterKnife(getWindow().getDecorView());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    protected void onCreateBefore() {
        //填充iconfont
//        LayoutInflaterCompat.setFactory(getLayoutInflater(),
//                new IconFontLayoutFactory(this, getDelegate()));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            window.setExitTransition(new Slide());
//            window.setReturnTransition(new Slide());
//            window.setEnterTransition(new Explode());
//            window.setReenterTransition(new Slide());
//        }
    }

    protected void onInflateBefore() {
    }

    protected final void bindButterKnife(View view) {
        mUnbinder = ButterKnife.bind(this, view);
    }

    protected abstract int getContentLayoutId();

    @Override
    public final View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public final SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public final void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public final void scrollToFinishActivity() {
        SwipeHelper.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public final void setScrollDirection(int edgeFlags) {
        //SwipeBackLayout.EDGE_ALL| EDGE_LEFT | EDGE_RIGHT | EDGE_BOTTOM | EDGE_TOP
        getSwipeBackLayout().setEdgeTrackingEnabled(edgeFlags);
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    @Subscribe
    public void msgHandle(String msg) {

    }

    @Subscribe
    public void msgHandle(DialogMsg msg) {
        switch (msg.type) {
            case HIDE_DIALOG:
                hideAlert(msg.isShow);
                break;
            case SHOW_DIALOG:
                showAlert(msg.isShow, msg.mClass);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    public final void changeFragment(@IdRes int id, String tag, Class<? extends BaseFragment> tClass) {
        changeFragment(id, tag, tClass, true);
    }

    public final void changeFragment(@IdRes int id, String tag, Class<? extends BaseFragment> tClass, boolean isAddStackBack) {
        BaseFragment t = null;
        if (mFragmentManager.getFragments() != null && mFragmentManager.getFragments().size() > 0) {
            t = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mCurrentFragment != null && mCurrentFragment != t) {
            ft.hide(mCurrentFragment);
        }
        if (isAddStackBack) {
            ft.addToBackStack(null);
        }
        if (t != null) {
            if (!t.isVisible()) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.show(t).commit();
            } else {
                ft.commit();
            }
        } else {
            try {
                t = tClass.newInstance();
            } catch (Exception ignored) {
                throw new RuntimeException("changeFragment(): create fragment error!");
            }
            ft.add(id, t, tag).commit();
        }
        mCurrentFragment = t;
    }

    public final <T> void postMsg(T t) {
        EventBus.getDefault().post(t);
    }

    public final <T> void postStickyMsg(T t) {
        EventBus.getDefault().postSticky(t);
    }

    /**
     * 显示加载非模态等候
     */
    public final void showAlert(boolean isShow) {
        showAlert(isShow, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public final void showAlert() {
        showAlert(true, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public final <T extends DialogFragment> void showAlert(boolean isShow, Class<T> fragment) {
        if (!isShow) {
            return;
        }

        if (mDialogFragment == null || !(mDialogFragment.getClass() == fragment)) {
            try {
                mDialogFragment = fragment.newInstance();
            } catch (Exception e) {
                return;
            }
        }

        if (mFragmentManager == null) {
            return;
        }

        if (!mDialogFragment.isVisible()) {
            mDialogFragment.show(mFragmentManager, FRAGMENT_TAG_DIALOG);
        }
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert(boolean isShow) {
        if (!isShow) {
            return;
        }
        if (mDialogFragment != null) {
            mDialogFragment.dismissAllowingStateLoss();
        }
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert() {
        hideAlert(true);
    }

    public final static class DialogMsg<T extends DialogFragment> {
        DialogEnum type;
        boolean isShow;
        Class<T> mClass;

        DialogMsg(boolean isShow, DialogEnum type) {
            this.isShow = isShow;
            this.type = type;
        }

        DialogMsg(boolean isShow, DialogEnum type, Class<T> aClass) {
            this.isShow = isShow;
            mClass = aClass;
            this.type = type;
        }
    }

    public enum DialogEnum {
        SHOW_DIALOG,
        HIDE_DIALOG
    }

    protected final void exit() {
        runOnUiThread(() -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ActivityCompat.finishAfterTransition(this);
//            } else {
            finish();
            overridePendingTransition(R.anim.hold, R.anim.slide_out);
//            }
        });
    }

    protected final void doubleExit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, R.string.double_click_exit_tip,
                    Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> isExit = false, 2000);
        } else {
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            exit();  //finish当前activity
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void startActivity(Intent intent) {
        runOnUiThread(() -> {
//           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//////                //ActivityOptionsCompat ao = ActivityOptionsCompat
//////                 ActivityOptionsCompat ao = ActivityOptionsCompat.makeCustomAnimation(this,android.R.transition.slide_left,android.R.transition.slide_left);
//////                    startActivity(intent,ao.toBundle());
////
//                super.startActivity(intent,
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, (Pair<View, String>[]) null).toBundle());
//                return;
//            }
            super.startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.hold);
        });
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void setCurrentFragment(Fragment current) {
        mCurrentFragment = current;
    }
}
