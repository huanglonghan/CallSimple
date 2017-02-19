package pw.bmyo.www.bmyobaselibrary.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.KeyEvent;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import pw.bmyo.www.bmyobaselibrary.R;

/**
 *
 */

public class MainActivity extends BaseActivity {

    //@BindView(R.id.bottom_bar)
    //BottomNavigationView mBottomBar;


    private static final String FRAGMENT_TAG_PEOPLE = "fragment_tag_people";
    private static final String FRAGMENT_TAG_DISCOVER = "fragment_tag_discover";
    private static final String FRAGMENT_TAG_SUBSCRIBE = "fragment_tag_subscribe";

    private int PHOTO_REQUEST_GALLERY = 0x001;
    private int PHOTO_REQUEST_CUT = 0x002;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        init();
    }

    //@Override
    //protected int getContentLayoutId() {
    //    return R.layout.main_activity;
    //}

    private void init() {
        //changeFragment(R.id.content_rv, FRAGMENT_TAG_DISCOVER, DiscoverFragment.class, false);

//        mBottomBar.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.discover:
//                    changeFragment(R.id.content_rv, FRAGMENT_TAG_DISCOVER, DiscoverFragment.class, false);
//                    return true;
//                case R.id.community:
//                    return true;
//                case R.id.personal:
//                    changeFragment(R.id.content_rv, FRAGMENT_TAG_PEOPLE, MyCenterFragment.class, false);
//                    return true;
//                default:
//                    return true;
//            }
//        });
    }


    @Subscribe
    public void msgHandle(View view) {
        gallery(view);
    }

    public void gallery(View view) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                String fileName = getCacheDir().getPath() + "/" + UUID.randomUUID().toString();
                File tempFile = new File(fileName);
                try {
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                postStickyMsg(new UpLoadMsg(fileName));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    public static class UpLoadMsg {
        public String mFilePath;

        public UpLoadMsg(String fileName) {
            mFilePath = fileName;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                doubleExit();  //finish当前activity
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
