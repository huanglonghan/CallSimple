package pw.bmyo.www.bmyobaselibrary.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import pw.bmyo.www.bmyobaselibrary.R;

/**
 * Created by Administrator on 2016/10/17 0017.
 */

public class LoadingFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }
}
