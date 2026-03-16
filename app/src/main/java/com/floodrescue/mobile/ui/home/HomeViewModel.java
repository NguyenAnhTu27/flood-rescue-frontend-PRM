package com.floodrescue.mobile.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.model.response.UserProfileResponse;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.UserRepository;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<Resource<UserProfileResponse>> profileState = new MutableLiveData<>();
    private final UserRepository userRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public LiveData<Resource<UserProfileResponse>> getProfileState() {
        return profileState;
    }

    public void loadProfile() {
        profileState.setValue(Resource.loading(null));
        userRepository.getMyProfile(new RepositoryCallback<UserProfileResponse>() {
            @Override
            public void onSuccess(UserProfileResponse data) {
                profileState.postValue(Resource.success(data));
            }

            @Override
            public void onError(String message) {
                profileState.postValue(Resource.error(message, null));
            }
        });
    }
}
