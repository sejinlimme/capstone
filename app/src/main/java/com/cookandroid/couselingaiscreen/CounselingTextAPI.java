package com.cookandroid.couselingaiscreen;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CounselingTextAPI {
    @GET("/echo_call/{chatText}")
    Call<CounselingTextDTO> getAIReply(
            @Path("chatText") String chatText);
}
