package pw.bmyo.www.bmyobaselibrary.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;
import pw.bmyo.www.bmyobaselibrary.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huang on 2017/2/9.
 */

public class ImageViewActivity extends pw.bmyo.www.bmyobaselibrary.source.BaseActivity
        implements ViewPager.OnPageChangeListener, PhotoViewAttacher.OnPhotoTapListener {

    @BindView(R.id.very_image_viewpager)
    ViewPager mVeryImageViewpager;
    @BindView(R.id.very_image_viewpager_text)
    TextView mVeryImageViewpagerText;
    @BindView(R.id.save_image_tv)
    TextView mTvSaveBigImage;

    // 接收传过来的uri地址
    List<String> imageuri;
    // 接收穿过来当前选择的图片的数量
    int count;

    public static final String EXT_TAG_IMAGE_INDEX = "ext_tag_image_index";
    public static final String EXT_TAG_IMAGE_URLS = "ext_tag_image_urls";
    // 当前页数
    private int index;

    ViewPagerAdapter adapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.image_view_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        imageuri = bundle.getStringArrayList(EXT_TAG_IMAGE_URLS);
        index = (int)bundle.getLong(EXT_TAG_IMAGE_INDEX);
        count = imageuri.size();
        adapter = new ViewPagerAdapter();
        mVeryImageViewpager.setAdapter(adapter);
        mVeryImageViewpager.setCurrentItem(index);
        mVeryImageViewpager.setOnPageChangeListener(this);
        mVeryImageViewpager.setEnabled(false);
        mVeryImageViewpagerText.setText((index + 1) + " / " + imageuri.size());
    }

    /**
     * 保存图片至相册
     */
    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Moplus");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsoluteFile())));
        return file.getAbsolutePath();
    }


    /**
     * Glide 获得图片缓存路径
     */
    private String getImagePath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(ImageViewActivity.this)
                .load(imgUrl)
                .downloadOnly(500, 500);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }

    @OnClick(R.id.save_image_tv)
    public void onClick() {
        Toast.makeText(ImageViewActivity.this, "正在下载图片...", Toast.LENGTH_SHORT).show();
        final BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Observable.just(imageuri.get(index))
                .map(this::getImagePath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((path)->{
                    if (path != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
                        if (bitmap != null) {
                            String savePath = saveImageToGallery(ImageViewActivity.this, bitmap);
                            Toast.makeText(ImageViewActivity.this, "保存成功! "+ savePath, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * ViewPager的适配器
     *
     * @author guolin
     */
    class ViewPagerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        ViewPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.image_view_pager, container, false);
            final PhotoView zoom_image_view = (PhotoView) view.findViewById(R.id.zoom_image_view);
            final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading);
            // 保存网络图片的路径
            String adapter_image_Entity = (String) getItem(position);
            //TODO
            String imageUrl;
            imageUrl = adapter_image_Entity;

            spinner.setVisibility(View.VISIBLE);
            spinner.setClickable(false);
            Glide.with(ImageViewActivity.this).load(imageUrl)
                    .crossFade(700)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Toast.makeText(getApplicationContext(), "资源加载异常", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                            return false;
                        }

                        //这个用于监听图片是否加载完成
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            Toast.makeText(getApplicationContext(), "图片加载完成", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);

                            /**这里应该是加载成功后图片的高*/
                            int height = zoom_image_view.getHeight();

                            int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                            if (height > wHeight) {
                                zoom_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                zoom_image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }
                            return false;
                        }
                    }).into(zoom_image_view);

            zoom_image_view.setOnPhotoTapListener(ImageViewActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            if (imageuri == null || imageuri.size() == 0) {
                return 0;
            }
            return imageuri.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        Object getItem(int position) {
            return imageuri.get(position);
        }
    }

    /**
     * 下面是对Viewpager的监听
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    /**
     * 本方法主要监听viewpager滑动的时候的操作
     */
    @Override
    public void onPageSelected(int arg0) {
        // 每当页数发生改变时重新设定一遍当前的页数和总页数
        mVeryImageViewpagerText.setText((arg0 + 1) + " / " + imageuri.size());
        index = arg0;
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        finish();
    }

}
