package pw.bmyo.www.bmyobaselibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huang on 2017/2/14.
 */

public class GridItem extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int width;

    public GridItem(Context context, int width) {
        super();
        mContext = context;
        this.width = width;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index = parent.getChildAdapterPosition(view);
        if ((index + 1) % 2 != 0) {
            outRect.set(0, 0, width, width);
        }else{
            outRect.set(0, 0, 0, width);
        }

    }
}
