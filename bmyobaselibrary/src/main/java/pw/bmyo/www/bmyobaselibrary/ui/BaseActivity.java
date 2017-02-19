package pw.bmyo.www.bmyobaselibrary.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.anthony.ultimateswipetool.activity.SwipeBackLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import pw.bmyo.www.bmyobaselibrary.source.MPMessage;


/**
 * Created by huang on 2016/11/24.
 */

public class BaseActivity extends pw.bmyo.www.bmyobaselibrary.source.BaseActivity {

    public static final String MSG_TAG_MAIN = "msg_tag_main";
    public static final String MSG_TAG_SETTING = "msg_tag_setting";
    public static final String MSG_TAG_MY_CENTER = "msg_tag_my_center";
    public static final String MSG_TAG_ACCOUNT = "msg_tag_account";
    public static final String MSG_TAG_INTERESTED = "msg_tag_interested";
    public static final String MSG_TAG_PERSONAL = "msg_tag_personal";
    public static final String MSG_TAG_ALERT_CANCEL = "msg_tag_alert_cancel";
    public static final String MSG_TAG_ALERT_CONFIRM = "msg_tag_alert_confirm";


    public static final String MSG_TAG_WEB_VIEW = "msg_tag_web_view";
    public static final String MSG_TAG_IMAGE_VIEW = "msg_tag_image_view";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScrollDirection(SwipeBackLayout.EDGE_LEFT);
    }

    @Subscribe
    public void msgHandel(MPMessage msg) {
        Intent intent;
        switch (msg.msg) {
            case MSG_TAG_MAIN:
                startActivity(new Intent(this, MainActivity.class));
                exit();
                break;
            case MSG_TAG_WEB_VIEW:
                intent = new Intent(this, WebViewActivity.class);
                intent.setData(Uri.parse((String) msg.data));
                startActivity(intent);
                break;
            case MSG_TAG_IMAGE_VIEW:
                intent = new Intent(this, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(ImageViewActivity.EXT_TAG_IMAGE_URLS, (ArrayList<String>)msg.data);
                bundle.putLong(ImageViewActivity.EXT_TAG_IMAGE_INDEX, msg.id);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }

    @Subscribe
    public void msgHandel(String msg) {
        msgHandel(new MPMessage(msg));
    }

    @Override
    protected int getContentLayoutId() {
        return 0;
    }
}
