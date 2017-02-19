package pw.bmyo.www.bmyobaselibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import pw.bmyo.www.bmyobaselibrary.R;

/**
 * Created by huang on 2016/12/29.
 */

public class SplashActivity extends pw.bmyo.www.bmyobaselibrary.source.BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    //@Override
    //protected int getContentLayoutId() {
    //    return R.layout.splash_activity;
    //}

    private void init() {
        String[] ha2 = new String[]{"h","hu","hua","huan","huang","long","han"};
        String[] hah = new String[]{"h","hua","hu","1","huang"};
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return hah.length;
            }

            @Override
            public int getNewListSize() {
                return ha2.length;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return ha2[newItemPosition]
                        .equals(hah[oldItemPosition]);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return ha2[newItemPosition]
                        .equals(hah[oldItemPosition]);
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return ha2[newItemPosition];
            }
        },true);
        result.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                int i=0;
                i++;
            }

            @Override
            public void onRemoved(int position, int count) {
                    int i=0;
                i++;
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                int i=0;
                i++;
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                int i=0;
                i++;
            }
        });
    }

}
