package pw.bmyo.www.bmyobaselibrary.model;

import java.util.HashMap;

import pw.bmyo.www.bmyobaselibrary.model.response.CommonResponse;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public interface CommunityService {

    @FormUrlEncoded
    @POST("/account?a=login")
    Observable<CommonResponse> login(@FieldMap HashMap<String, String> param);

    //@GET("/account?a=get_verify_code")
    //Observable<CommonResponse> getVerifyCode(@Query("phone") String phone, @Query("verify_code_type") String veriryCodeType);

    @GET("/verify_code?a=signup")
    Observable<CommonResponse> getSignupCode(@Query("phone") String phone);


}
