package pw.bmyo.www.bmyobaselibrary.widget.refreshlayout;

/**
 * Created by huang on 2016/12/27.
 */

public interface CompareItem<T, V> {
    boolean areItemsTheSame(BaseAdapter.Data<T, V> oldItem,
                            BaseAdapter.Data<T, V> newItem);

    boolean areContentsTheSame(BaseAdapter.Data<T, V> oldItem,
                               BaseAdapter.Data<T, V> newItem);

    Object getChangePayload(BaseAdapter.Data<T, V> oldItem,
                            BaseAdapter.Data<T, V> newItem);
}
