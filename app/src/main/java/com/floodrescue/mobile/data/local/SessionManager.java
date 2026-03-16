package com.floodrescue.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.floodrescue.mobile.core.util.Constants;
import com.floodrescue.mobile.data.model.response.LoginResponse;

public class SessionManager {

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLoginSession(LoginResponse response) {
        preferences.edit()
                .putString(Constants.KEY_TOKEN, response.getToken())
                .putLong(Constants.KEY_USER_ID, response.getUserId() == null ? -1L : response.getUserId())
                .putString(Constants.KEY_FULL_NAME, response.getFullName())
                .putString(Constants.KEY_ROLE, response.getRole())
                .apply();
    }

    public String getToken() {
        return preferences.getString(Constants.KEY_TOKEN, "");
    }

    public String getFullName() {
        return preferences.getString(Constants.KEY_FULL_NAME, "");
    }

    public String getRole() {
        return preferences.getString(Constants.KEY_ROLE, "");
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
