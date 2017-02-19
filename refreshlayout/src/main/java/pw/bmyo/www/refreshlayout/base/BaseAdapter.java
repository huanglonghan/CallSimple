package pw.bmyo.www.refreshlayout.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import pw.bmyo.www.refreshlayout.utils.RoundedTransformation;

/**
 * Created by huang on 2016/12/14.
 */

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected LayoutInflater mLayoutInflater;
    protected RequestManager mGlide;
    protected Context mContext;
    protected RoundedTransformation mTransformation;

    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;

    protected View.OnTouchListener mTouchListener;

    private boolean isTouchIntercept = false;

    private boolean isLongIntercept = false;

    public boolean isLongIntercept() {
        return isLongIntercept;
    }

    public void setLongIntercept(boolean longIntercept) {
        isLongIntercept = longIntercept;
    }

    public boolean isTouchIntercept() {
        return isTouchIntercept;
    }

    public void setTouchIntercept(boolean touchIntercept) {
        isTouchIntercept = touchIntercept;
    }

    public BaseAdapter(Context context) {
        mContext = context;
        mTransformation = new RoundedTransformation(context);
        mLayoutInflater = LayoutInflater.from(context);
        mGlide = Glide.with(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        };
    }

    public final void setOnItemClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public final void setOnItemLongClickListener(View.OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }

}
