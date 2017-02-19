package pw.bmyo.www.bmyobaselibrary.ui;

import pw.bmyo.www.bmyobaselibrary.source.BaseFragment;

/**
 * Created by huang on 2017/1/23.
 */

public abstract class MainFragment extends BaseFragment {
    @Override
    public void onResume() {
        if (isVisible()) {
            ((pw.bmyo.www.bmyobaselibrary.source.BaseActivity) getActivity()).setCurrentFragment(this);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isVisible()) {
            ((pw.bmyo.www.bmyobaselibrary.source.BaseActivity) getActivity()).setCurrentFragment(this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((pw.bmyo.www.bmyobaselibrary.source.BaseActivity) getActivity()).setCurrentFragment(this);
        }
    }
}
