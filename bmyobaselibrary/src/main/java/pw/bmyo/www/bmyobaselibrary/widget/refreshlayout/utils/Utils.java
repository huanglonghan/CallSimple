package pw.bmyo.www.bmyobaselibrary.widget.refreshlayout.utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by huang on 2017/1/20.
 */

public class Utils {

    public static void swap(List<?> list, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

}
