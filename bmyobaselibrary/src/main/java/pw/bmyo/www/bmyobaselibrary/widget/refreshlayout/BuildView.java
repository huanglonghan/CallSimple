package pw.bmyo.www.bmyobaselibrary.widget.refreshlayout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by huang on 2016/12/16.
 */

public interface BuildView<T, V, VH extends RecyclerView.ViewHolder> {

    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_REFRESH = 1;
    int STATE_REFRESHING = 2;
    int STATE_DONE = 3;
    int STATE_LOADING = 4;
    int STATE_MOREOVER = 5;

    VH onCreateView(LayoutInflater inflater, ViewGroup parent, View.OnClickListener listener);
    void onBindView(VH holder, WrapAdapter.Data<T, V> data);
    boolean onBindView(VH holder, WrapAdapter.Data<T, V> data, List<Object> payloads);
}
