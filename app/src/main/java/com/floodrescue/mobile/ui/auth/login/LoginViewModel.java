package com.floodrescue.mobile.ui.auth.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.model.response.LoginResponse;
import com.floodrescue.mobile.data.repository.AuthRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<Resource<LoginResponse>> loginState = new MutableLiveData<>();
    private final AuthRepository authRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application.getApplicationContext());
    }

    public LiveData<Resource<LoginResponse>> getLoginState() {
        return loginState;
    }

    public void login(String identifier, String password) {
        loginState.setValue(Resource.loading(null));
        authRepository.login(identifier, password, new RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                loginState.postValue(Resource.success(data));
            }

            @Override
            public void onError(String message) {
                loginState.postValue(Resource.error(message, null));
            }
        });
    }
}
