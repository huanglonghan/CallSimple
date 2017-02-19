package pw.bmyo.www.bmyobaselibrary.source;

import android.os.Bundle;

/**
 * Created by huang on 2017/2/8.
 */

public class MPMessage {
    public String msg;
    public long id;
    public Object data;
    public Bundle extData;

    public MPMessage(String msg, long id) {
        this.id = id;
        this.msg = msg;
    }

    public MPMessage() {
    }

    public MPMessage(String msg) {
        this.msg = msg;
    }

    public MPMessage(String msg, Bundle extData) {
        this.extData = extData;
        this.msg = msg;
    }

    public MPMessage(String msg, long id, Object data, Bundle extData) {
        this.data = data;
        this.msg = msg;
        this.id = id;
        this.extData = extData;
    }

    public MPMessage(String msg, long id, Object data) {
        this.data = data;
        this.id = id;
        this.msg = msg;
    }

    public MPMessage(String msg, Object data) {
        this.data = data;
        this.msg = msg;
    }
}
