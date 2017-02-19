package pw.bmyo.www.bmyobaselibrary.model;

import java.util.List;

import okhttp3.RequestBody;
import pw.bmyo.www.bmyobaselibrary.bean.api.UploadImg;
import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by huang on 2016/11/29.
 */
public interface FileUploadService {
    @Multipart
    @POST("/img")
    Observable<CommonResponse<List<UploadImg>>> uploadImg(@Part("file\";filename=\"avatar.png") RequestBody body);

}