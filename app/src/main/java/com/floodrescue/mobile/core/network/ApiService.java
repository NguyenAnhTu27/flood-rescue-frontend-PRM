package com.floodrescue.mobile.core.network;

import com.floodrescue.mobile.data.model.request.CitizenFeedbackRequest;
import com.floodrescue.mobile.data.model.request.LoginRequest;
import com.floodrescue.mobile.data.model.request.RegisterCitizenRequest;
import com.floodrescue.mobile.data.model.request.SendChatMessageRequest;
import com.floodrescue.mobile.data.model.request.UpdateMyProfileRequest;
import com.floodrescue.mobile.data.model.response.ApiMessageResponse;
import com.floodrescue.mobile.data.model.response.LoginResponse;
import com.floodrescue.mobile.data.model.response.RescueChatMessageResponse;
import com.floodrescue.mobile.data.model.response.UserProfileResponse;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiMessageResponse> registerCitizen(@Body RegisterCitizenRequest request);

    @GET("api/auth/me")
    Call<UserProfileResponse> getMyProfile();

    @PUT("api/user/profile")
    Call<UserProfileResponse> updateMyProfile(@Body UpdateMyProfileRequest request);

    @GET("api/rescue/citizen/requests")
    Call<JsonElement> getCitizenRescueRequests(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("status") String status
    );

    @GET("api/rescue/citizen/requests/{id}")
    Call<JsonElement> getCitizenRescueRequest(@Path("id") long id);

    @GET("api/relief/citizen/requests")
    Call<JsonElement> getCitizenReliefRequests(
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/relief/requests/{id}")
    Call<JsonElement> getReliefRequest(@Path("id") long id);

    @POST("api/feedback/citizen")
    Call<JsonElement> submitCitizenFeedback(@Body CitizenFeedbackRequest request);

    @GET("api/rescue/coordinator/dashboard")
    Call<JsonElement> getCoordinatorDashboard();

    @GET("api/rescue/coordinator/requests")
    Call<JsonElement> getCoordinatorRescueRequests(
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("keyword") String keyword,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/rescue/coordinator/requests/{id}")
    Call<JsonElement> getCoordinatorRescueRequest(@Path("id") long id);

    @GET("api/rescue/coordinator/citizens/blocked")
    Call<JsonElement> getBlockedCitizens();

    @GET("api/rescue/coordinator/task-groups")
    Call<JsonElement> getCoordinatorTaskGroups(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/rescue/coordinator/task-groups/{id}")
    Call<JsonElement> getCoordinatorTaskGroup(@Path("id") long id);

    @GET("api/rescue/rescuer/dashboard")
    Call<JsonElement> getRescuerDashboard();

    @GET("api/rescue/rescuer/tasks")
    Call<JsonElement> getRescuerTasks(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/rescue/rescuer/tasks/{id}")
    Call<JsonElement> getRescuerTask(@Path("id") long id);

    @PUT("api/rescue/rescuer/tasks/{id}/status")
    Call<JsonElement> updateRescuerTaskStatus(
            @Path("id") long id,
            @Query("status") String status,
            @Query("note") String note
    );

    @GET("api/rescue/rescuer/task-groups")
    Call<JsonElement> getRescuerTaskGroups(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/rescue/rescuer/task-groups/{id}")
    Call<JsonElement> getRescuerTaskGroup(@Path("id") long id);

    @GET("api/rescue/rescuer/task-groups/{id}/emergency-acks")
    Call<JsonElement> getRescuerEmergencyAcks(@Path("id") long id);

    @GET("api/relief/dashboard")
    Call<JsonElement> getManagerDashboard();

    @GET("api/relief/dispatch-dashboard")
    Call<JsonElement> getManagerDispatchDashboard();

    @GET("api/relief/requests")
    Call<JsonElement> getManagerReliefRequests(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/relief/requests/generate-code")
    Call<JsonElement> generateReliefRequestCode();

    @GET("api/relief/rescuer/requests")
    Call<JsonElement> getRescuerReliefRequests(
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/areas")
    Call<JsonElement> getAreas();

    @GET("api/inventory/stock")
    Call<JsonElement> getInventoryStock();

    @GET("api/inventory/receipts")
    Call<JsonElement> getInventoryReceipts(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/inventory/issues")
    Call<JsonElement> getInventoryIssues(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/inventory/receipts/{id}")
    Call<JsonElement> getInventoryReceipt(@Path("id") long id);

    @GET("api/inventory/issues/{id}")
    Call<JsonElement> getInventoryIssue(@Path("id") long id);

    @GET("api/inventory/issues/temporary")
    Call<JsonElement> getTemporaryInventoryIssues();

    @GET("api/inventory/issues/generate-code")
    Call<JsonElement> generateInventoryIssueCode();

    @GET("api/assets")
    Call<JsonElement> getAssets(@Query("status") String status);

    @POST("api/assets")
    Call<JsonElement> createAsset(@Body JsonElement request);

    @GET("api/inventory/items")
    Call<JsonElement> getItemCategories();

    @GET("api/inventory/item-classifications")
    Call<JsonElement> getItemClassifications();

    @GET("api/inventory/item-units")
    Call<JsonElement> getItemUnits();

    @GET("api/admin/stats")
    Call<JsonElement> getAdminStats();

    @GET("api/admin/users")
    Call<JsonElement> getAdminUsers(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("keyword") String keyword,
            @Query("roleId") Integer roleId
    );

    @GET("api/admin/audit-logs")
    Call<JsonElement> getAdminAuditLogs(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("action") String action,
            @Query("keyword") String keyword
    );

    @GET("api/admin/system-settings")
    Call<JsonElement> getAdminSystemSettings();

    @GET("api/admin/content-pages")
    Call<JsonElement> getAdminContentPages();

    @GET("api/admin/permissions")
    Call<JsonElement> getAdminPermissions();

    @GET("api/admin/catalogs")
    Call<JsonElement> getAdminCatalogs();

    @GET("api/admin/catalog-groups")
    Call<JsonElement> getAdminCatalogGroups();

    @GET("api/admin/teams")
    Call<JsonElement> getAdminTeams();

    @POST("api/admin/teams")
    Call<JsonElement> createAdminTeam(@Body JsonElement request);

    @GET("api/admin/teams/member-candidates")
    Call<JsonElement> getAdminTeamMemberCandidates();

    @GET("api/feedback/admin")
    Call<JsonElement> getAdminFeedbacks(
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/feedback/admin/summary")
    Call<JsonElement> getAdminFeedbackSummary();

    @POST("api/relief/requests")
    Call<JsonElement> createManagerReliefRequest(@Body JsonElement request);

    @POST("api/inventory/receipts")
    Call<JsonElement> createInventoryReceipt(@Body JsonElement request);

    @POST("api/inventory/issues")
    Call<JsonElement> createInventoryIssue(@Body JsonElement request);

    @GET("api/chat/rescue-requests/{id}/messages")
    Call<List<RescueChatMessageResponse>> getRescueChatMessages(@Path("id") long rescueRequestId);

    @POST("api/chat/rescue-requests/{id}/messages")
    Call<RescueChatMessageResponse> sendRescueChatMessage(
            @Path("id") long rescueRequestId,
            @Body SendChatMessageRequest request
    );
}
