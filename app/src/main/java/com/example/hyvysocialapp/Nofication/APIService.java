package com.example.hyvysocialapp.Nofication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAARuZJ6P8:APA91bFIdeDV2k2FzIBGb7gQRftCp6z6WhOVH0Mauk4ju2Z3lW5O5TRUIaRT-pBZLuSR4orLSc51cus1tStErxfUgu3-ijmhUeMYIM4ULzIZ_qCbPKDlm3vkl1QqzXm7Wy6GHjOcB2O3"
    })

    @POST("fcm/send")
    Call<Respone> sendNotification(@Body Sender body);
}
