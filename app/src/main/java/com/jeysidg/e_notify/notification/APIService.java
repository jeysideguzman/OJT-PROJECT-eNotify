package com.jeysidg.e_notify.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAWdNbdcY:APA91bEivXWOTTvyxZSV1eipRLTRCEO3wX_5qv81oa-JHt8DZDQgMOGB_oXMQ7xENF7CnJsFBMBqGzgO7rfFg3fl47O17iVFYM0wuRQVTFg98QzLCF3ziuqOOpMNzS6-1uR__RalpsDb"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender sender);

}
