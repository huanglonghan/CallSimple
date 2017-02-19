package pw.bmyo.www.bmyobaselibrary.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pw.bmyo.www.bmyobaselibrary.R;
import pw.bmyo.www.bmyobaselibrary.ui.MainActivity;


public class AlertFragment extends DialogFragment {

    @BindView(R.id.tip_alert_tv)
    TextView mTipAlertTv;
    @BindView(R.id.cancel_bt)
    Button mCancelBt;
    @BindView(R.id.confirm_bt)
    Button mConfirmBt;

    private Unbinder mUnbinder;

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.4f;
        window.setAttributes(windowParams);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.alert_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.cancel_bt, R.id.confirm_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_bt:
                dismissAllowingStateLoss();
                EventBus.getDefault().post(MainActivity.MSG_TAG_ALERT_CANCEL);
                break;
            case R.id.confirm_bt:
                dismissAllowingStateLoss();
                EventBus.getDefault().post(MainActivity.MSG_TAG_ALERT_CONFIRM);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
}
