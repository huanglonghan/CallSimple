package pw.bmyo.www.bmyobaselibrary;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

//import com.antfortune.freeline.FreelineCore;

/**
 * Created by huang on 2016/12/29.
 */

public class BMMainApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        if (BuildConfig.DEBUG) {
//            FreelineCore.init(this);
//        }
    }

    public static Context getContext() {
        return mContext;
    }

}
