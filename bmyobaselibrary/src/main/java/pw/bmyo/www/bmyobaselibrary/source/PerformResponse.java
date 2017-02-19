package pw.bmyo.www.bmyobaselibrary.source;

import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;

/**
 * Created by huang on 2016/12/23.
 */

public interface PerformResponse {

    void recycle();

    Object onPerform(CommonResponse o);

    void onPerError(Throwable e);

    void onPerCompleted();

    void onPerStart();

}
