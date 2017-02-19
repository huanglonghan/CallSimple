package pw.bmyo.www.bmyobaselibrary.widget.refreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huang on 2017/1/6.
 */

public abstract class BaseAdapter<T, V, VH extends RecyclerView.ViewHolder> extends
        pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.base.BaseAdapter<VH> {

    public static final int TYPE_TAG_DEFAULT = -90000;
    public static final int STATUS_TAG_DEFAULT = -90000;

    protected final LinkedList<Data<T, V>> mContent = new LinkedList<>();

    public BaseAdapter(Context context) {
        super(context);
    }

    public BaseAdapter(Context context, CompareItem<T, V> compare) {
        this(context);
        mCompare = compare;
    }

    private boolean mDetectMove = true;

    private CompareItem<T, V> mCompare;

    @Override
    public int getItemViewType(int position) {
        try {
            return mContent.get(position).getType();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final void add(Map<T, Integer> data) {
        if (data.size() <= 0) return;
        Set<Map.Entry<T, Integer>> set = data.entrySet();
        for (Map.Entry<T, Integer> t : set) {
            int index = getLastPosition(t.getValue());
            mContent.add(index, new Data<>(t.getValue(), t.getKey()));
            notifyItemInserted(index);
        }
    }

    public final void add(Data<T, V> data) {
        int index = getLastPosition(data.getType());
        add(data, index);
    }

    public final void add(Data<T, V> data, int position) {
        mContent.add(position, data);
        notifyItemInserted(position);
    }

    public final void add(T data, int type, int position) {
        mContent.add(position, new Data<>(type, data));
        notifyItemInserted(position);
    }

    public final void add(T data, int type) {
        int index = getLastPosition(type);
        add(data, type, index);
    }

    public final void add(T data) {
        add(data, TYPE_TAG_DEFAULT);
    }

    public final void add(List<T> data, int defaultType) {
        int index = getLastPosition(defaultType);
        add(data, defaultType, index);
    }

    public final void add(List<T> data) {
        add(data, TYPE_TAG_DEFAULT, mContent.size());
    }

    public void setCompareItem(CompareItem<T, V> compare) {
        mCompare = compare;
    }

    public final void add(List<T> data, int defaultType, int position) {
        if (data.size() <= 0) return;
        List<Data<T, V>> list = new LinkedList<>();
        for (T t : data) {
            list.add(new Data<>(defaultType, t));
        }
        mContent.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }

    public final void remove(int position) {
        mContent.remove(position);
        notifyItemRemoved(position);
    }

    //删除连续类型
    public final void removeType(int type) {
        int start = getFirstPosition(type);
        int count = getTypeCount(type);
        for (int i = 0; i < count; i++) {
            mContent.remove(start);
        }
        notifyItemRangeRemoved(start, count);
    }

    public final void updateStatus(int position, int status) {
        mContent.get(position).setStatus(status);
        notifyItemChanged(position);
    }

    public final void updateTip(int position, String tip) {
        mContent.get(position).setTip(tip);
        notifyItemChanged(position);
    }

    public final void update(Map<T, Integer> data, int position) {
        List<Data<T, V>> list = new LinkedList<>();
        Set<Map.Entry<T, Integer>> set = data.entrySet();
        for (Map.Entry<T, Integer> t : set) {
            list.add(new Data<>(t.getValue(), t.getKey()));
        }
        updateData(list, position, list.size());
    }

    public final void update(List<T> data, int type, int size) {
        int position = getFirstPosition(type);
        List<Data<T, V>> list = new LinkedList<>();
        for (T t : data) {
            list.add(new Data<>(type, t));
        }
        updateData(list, position, size);
    }

    public final void update(List<T> data, int type) {
        int size = getTypeCount(type);
        update(data, type, size);
    }

    public final void update(List<T> data) {
        int size = getTypeCount(TYPE_TAG_DEFAULT);
        update(data, TYPE_TAG_DEFAULT, size);
    }

    public final void update(T data, int type, int position) {
        List<Data<T, V>> list = new LinkedList<>();
        list.add(new Data<>(type, data));
        updateData(list, position, 1);
    }

    public final void update(T data, int position) {
        update(data, TYPE_TAG_DEFAULT, position);
    }

    public final void update(int position, UpdateTypeData<T, V> data) {
        data.update(mContent.get(position));
        notifyItemChanged(position);
    }

    public final void update(Data<T, V> data, int position) {
        List<Data<T, V>> list = new LinkedList<>();
        list.add(data);
        updateData(list, position, 1);
    }

    protected void updateData(List<Data<T, V>> data, int index, int size) {
        updateData(data, index, size, true);
    }

    protected void updateData(List<Data<T, V>> newData, int index, int size, boolean isNotify) {
        if (mCompare == null) {
            throw new RuntimeException("mCompare is null");
        }
        List<Data<T, V>> oldData = getRange(index, size);
        replaceRange(newData, index, size);
        if (!isNotify) return;
        Observable.just(new CallBack<>(oldData, newData, mCompare))
                .map(callback -> DiffUtil.calculateDiff(callback, mDetectMove))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(diffUtil -> {
                    diffUtil.dispatchUpdatesTo(new ListUpdateCallback() {
                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(index + position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(index + position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(fromPosition, toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count, Object payload) {
                            notifyItemRangeChanged(index + position, count, payload);
                        }
                    });
                });
    }

    private List<Data<T, V>> getRange(int index, int size) {
        LinkedList<Data<T, V>> list = new LinkedList<>();
        for (int i = index; i < index + size; i++) {
            list.add(mContent.get(i));
        }
        return list;
    }

    private void replaceRange(List<Data<T, V>> data, int index, int size) {
        for (int i = 0; i < size; i++) {
            mContent.remove(index);
        }
        mContent.addAll(index, data);
    }

    public final void delete(int type) {
        int start = getFirstPosition(type);
        int end = getLastPosition(type);
        if (end == mContent.size()) return;
        for (int i = start; i < end; i++) {
            mContent.remove(start);
        }
        notifyItemRangeRemoved(start, end - start);
    }

    public void setDetectMove(boolean detectMove) {
        mDetectMove = detectMove;
    }

    public final T getItemData(int position) {
        return mContent.get(position).data;
    }

    public final void clear() {
        mContent.clear();
        notifyDataSetChanged();
    }

    public final void updateTypeData(int type, UpdateTypeData<T, V> data) {
        int start = getFirstPosition(type);
        int end = getLastPosition(type);
        if (end == mContent.size()) return;
        for (int i = start; i < end; i++) {
            data.update(mContent.get(i));
        }
        notifyItemRangeChanged(start, end - start);
    }

    public final int findTypePosition(int type) {
        return findTypePosition(type, -1);
    }

    public final int findTypePosition(int type, int defaultVal) {
        int size = mContent.size();
        for (int i = 0; i < size; i++) {
            if (mContent.get(i).getType() == type)
                return i;
        }
        return defaultVal;
    }

    public final int getFirstPosition(int type) {
        return getFirstPosition(type, 0);
    }

    public final int getFirstPosition(int type, int defaultVal) {
        int size = mContent.size();
        for (int i = 0; i < size; i++) {
            if (mContent.get(i).getType() == type)
                return i;
        }
        return defaultVal;
    }

    public final int getLastPosition(int type) {
        int size = mContent.size();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (mContent.get(i).getType() == type)
                index = i + 1;
        }
        if (index == -1) index = size;
        return index;
    }

    public final int getTypeCount(int type) {
        int size = 0;
        for (Data data : mContent) {
            if (data.getType() == type) size++;
        }
        return size;
    }

    public final int getTypeCount() {
        return getTypeCount(TYPE_TAG_DEFAULT);
    }

    public final ArrayList<T> getTypeData(int type) {
        int count = getTypeCount(type);
        ArrayList<T> ts = new ArrayList<>();
        if (count <= 0) return ts;
        int start = getFirstPosition(type);
        for (int i = start; i < start + count; i++) {
            ts.add(mContent.get(i).data);
        }
        return ts;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateHolder(mLayoutInflater, parent, viewType);
    }

    public abstract VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindHolder(holder, mContent.get(position));
    }

    public abstract void onBindHolder(VH holder, Data<T, V> data);

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (payloads.size() <= 0 || !onBindHolder(holder, mContent.get(position), payloads)) {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    public LinkedList<Data<T, V>> getContent() {
        return mContent;
    }

    public boolean onBindHolder(VH holder, Data<T, V> data, List<Object> payloads) {
        return false;
    }

    public final List<Data<T, V>> get() {
        return mContent;
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public static class Data<T, V> {

        public V exData;
        public T data;
        private int mType = TYPE_TAG_DEFAULT;
        private int mStatus = STATUS_TAG_DEFAULT;
        private Integer position;
        private String mTip;

        public Data(int type, T data) {
            mType = type;
            this.data = data;
        }

        public Data(int type, String tip) {
            mType = type;
            mTip = tip;
        }

        public Data(int type) {
            mType = type;
        }

        public String getTip() {
            return mTip;
        }

        public Data<T, V> setTip(String tip) {
            mTip = tip;
            return this;
        }

        public int getType() {
            return mType;
        }

        public Data<T, V> setType(int type) {
            this.mType = type;
            return this;
        }

        public int getStatus() {
            return mStatus;
        }

        public Data<T, V> setStatus(int status) {
            mStatus = status;
            return this;
        }

        public Integer getPosition() {
            return position;
        }

        public Data<T, V> setPosition(Integer position) {
            this.position = position;
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Data && data.equals(obj);
        }

    }

    private static class CallBack<T, V> extends DiffUtil.Callback {

        private List<Data<T, V>> mOldList;
        private List<Data<T, V>> mNewList;
        private CompareItem<T, V> mCompareItem;

        CallBack(List<Data<T, V>> oldList, List<Data<T, V>> newList, CompareItem<T, V> compare) {
            mOldList = oldList;
            mNewList = newList;
            mCompareItem = compare;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mCompareItem.areItemsTheSame(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mCompareItem.areContentsTheSame(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return mCompareItem.getChangePayload(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
        }
    }

    public interface UpdateTypeData<T, V> {
        void update(Data<T, V> data);
    }
}
