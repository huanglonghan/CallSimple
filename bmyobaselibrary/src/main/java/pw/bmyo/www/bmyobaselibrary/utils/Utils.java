package pw.bmyo.www.bmyobaselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mzule.activityrouter.router.Routers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pw.bmyo.www.bmyobaselibrary.config.Config;
import pw.bmyo.www.bmyobaselibrary.model.FileUploadService;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import pw.bmyo.www.bmyobaselibrary.source.HttpResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pw.bmyo.www.bmyobaselibrary.BMMainApplication;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static pw.bmyo.www.bmyobaselibrary.R.*;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public class Utils {

    /**
     * 延时函数
     *
     * @param time 要延迟的时间
     * @return
     */
    public static Observable<Integer> setCallbackDelay(int time) {
        if (time < 0) time = 0;
        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(increaseTime -> countTime - increaseTime.intValue())
                .take(countTime + 1);
    }

    /**
     * 重复发送短信延时函数
     *
     * @param view
     * @param resource
     */
    public static void setDelaySendMsg(TextView view, Fragment resource) {
        String strFormat = resource.getString(string.msg_tip);
        Utils.setCallbackDelay(Config.SEND_MSG_INTERVAL)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (resource.isVisible()) {
                            view.setText(resource.getString(string.send_verify_code));
                            view.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (resource.isVisible()) {
                            view.setText(resource.getString(string.send_verify_code));
                            view.setEnabled(true);
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (resource.isVisible()) {
                            view.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (resource.isVisible()) {
                            view.setText(String.format(strFormat, integer.toString()));
                        }
                    }
                });
    }

    /**
     * 给图片添加圆形遮罩
     *
     * @param context
     * @param view
     * @param id
     */
    public static void setCirclePortrait(Context context, ImageView view, @DrawableRes int id) {
        view.setScaleType(ImageView.ScaleType.CENTER);
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), id);
        Bitmap mask = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getHeight() * 3 / 6, paint);

        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        canvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        paint.setXfermode(null);
        view.setImageBitmap(result);
    }

    // 上传图片
    public static void uploadImg(File file, Subscriber<CommonResponse> response) {
        Retrofit.Builder builder = HttpRequest.createRetrofit(HttpRequest.createOkHttpClient(), Config.OP_HTTP_HOST);
        Retrofit retrofit = builder.build();
        FileUploadService service = retrofit.create(FileUploadService.class);
        RequestBody body = RequestBody.create(MediaType.parse("image/png"), file);
        service.uploadImg(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response);
    }

    public enum PositionType {
        LEFT,
        TOP,
        BOTTOM,
        RIGHT
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(Context context,
                                           TextView view,
                                           @DrawableRes int redRes,
                                           @ColorRes int colorRes,
                                           PositionType position) {
        setTextViewDrawable(context, view, redRes, colorRes, null, position);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(Context context,
                                           TextView view,
                                           @DrawableRes int redRes,
                                           @Nullable Size size,
                                           PositionType position) {
        Size sizeBounds = size;
        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable = context.getResources().getDrawable(redRes, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(redRes);
        }

        if (drawable == null) {
            return;
        }

        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        float density = context.getResources().getDisplayMetrics().density;
        if (sizeBounds == null) {
            sizeBounds = new Size(drawable.getMinimumWidth(), drawable.getMinimumHeight());
        } else {
            sizeBounds = new Size((int) (size.getWidth() * density), (int) (size.getHeight() * density));
        }
        drawable.setBounds(0, 0, sizeBounds.getWidth(), sizeBounds.getHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(Context context,
                                           TextView view,
                                           @DrawableRes int redRes,
                                           @ColorRes int colorRes,
                                           @Nullable Size size,
                                           PositionType position) {
        Size sizeBounds = size;
        Drawable drawable;
        int color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable = context.getResources().getDrawable(redRes, context.getTheme());
            color = context.getResources().getColor(colorRes, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(redRes);
            color = context.getResources().getColor(colorRes);
        }

        if (drawable == null) {
            return;
        }

        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        float density = context.getResources().getDisplayMetrics().density;
        if (sizeBounds == null) {
            sizeBounds = new Size(drawable.getMinimumWidth(), drawable.getMinimumHeight());
        } else {
            sizeBounds = new Size((int) (size.getWidth() * density), (int) (size.getHeight() * density));
        }
        drawable.setBounds(0, 0, sizeBounds.getWidth(), sizeBounds.getHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        view.setTextColor(color);
    }

    //资源id获取drawable
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(resId, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(resId);
        }

        return drawable;
    }

    public static boolean isFalse(@Nullable HttpResponse.Result o) {
        return (o == null || (o.statusCode != Config.HTTP_STATUS_SUCCEED) || o.data == null || o.data instanceof Boolean && !(Boolean) o.data);
    }

    public static boolean isCanScroll(RecyclerView v, int scrollY) {
        return scrollY + v.getMeasuredHeight()
                >= ((v.getChildAt(0).getMeasuredHeight() * 0.9));
    }

    public static int getStatusBarHeight() {
        Resources resources = BMMainApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getNavigationBarHeight() {
        Resources resources = BMMainApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static String getParam(Intent intent, String param) {
        Uri uri = intent.getData();
        if (uri == null) {
            uri = Uri.parse(intent.getStringExtra(Routers.KEY_RAW_URL));
        }
        return uri.getQueryParameter(param);
    }

    public static int dp2px(float dp) {
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, BMMainApplication.getContext().getResources().getDisplayMetrics());
            return Math.round(px);
    }

    public static int px2dp(float pxValue) {
        final float scale = BMMainApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    public static String getAssetsFileAscii(Context context, String fileName) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(fileName);
            int size = stream.available();
            byte[] buff = new byte[size];
            stream.read(buff);
            stream.close();
            return new String(buff, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compareList(List<?> listOne, List<?> listTwo) {
        if (listOne == null) {
            if (listTwo != null) {
                return false;
            }
        } else {
            if (listTwo == null) return false;
            if (listOne == listTwo) return true;
            int size = listTwo.size();
            if (size != listOne.size()) return false;
            for (int i = 0; i < size; i++) {
                if (!listOne.get(i).equals(listOne.get(i))) return false;
            }
        }
        return true;
    }

    public static <T> boolean compareT(T listOne, T listTwo) {
        if (listOne == null) {
            if (listTwo != null) {
                return false;
            }
        } else {
            if (listTwo == null) return false;
            if (!listOne.equals(listTwo)) return false;
        }
        return true;
    }

    public static void move(List<?> list, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            //向下拖动
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            //向上拖动
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

}
