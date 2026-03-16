package com.floodrescue.mobile.core.network;

import com.floodrescue.mobile.data.model.request.LoginRequest;
import com.floodrescue.mobile.data.model.request.SendChatMessageRequest;
import com.floodrescue.mobile.data.model.request.UpdateMyProfileRequest;
import com.floodrescue.mobile.data.model.response.LoginResponse;
import com.floodrescue.mobile.data.model.response.RescueChatMessageResponse;
import com.floodrescue.mobile.data.model.response.UserProfileResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/user/profile")
    Call<UserProfileResponse> getMyProfile();

    @PUT("api/user/profile")
    Call<UserProfileResponse> updateMyProfile(@Body UpdateMyProfileRequest request);

    @GET("api/chat/rescue-requests/{id}/messages")
    Call<List<RescueChatMessageResponse>> getRescueChatMessages(@Path("id") long rescueRequestId);

    @POST("api/chat/rescue-requests/{id}/messages")
    Call<RescueChatMessageResponse> sendRescueChatMessage(
            @Path("id") long rescueRequestId,
            @Body SendChatMessageRequest request
    );
}
