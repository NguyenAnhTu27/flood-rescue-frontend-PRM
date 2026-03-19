package com.floodrescue.mobile.ui.role.citizen.rescue.create;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.request.RescueRequestCreatePayload;
import com.floodrescue.mobile.data.model.response.AttachmentUploadResponse;
import com.floodrescue.mobile.data.repository.CitizenRescueRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateRescueRequestActivity extends AppCompatActivity {

    private static final int MAX_IMAGES = 3;

    private CitizenRescueRepository rescueRepository;
    private TextInputLayout layoutAddress;
    private TextInputLayout layoutLocationDetail;
    private TextInputLayout layoutDescription;
    private TextInputEditText editAddress;
    private TextInputEditText editLocationDetail;
    private TextInputEditText editDescription;
    private TextView textAffectedCount;
    private TextView textLocationMeta;
    private TextView textCreateRescueError;
    private TextView buttonSubmitRescue;
    private android.view.View layoutCreateRescueError;
    private TextView buttonPriorityHigh;
    private TextView buttonPriorityMedium;
    private TextView buttonPriorityLow;
    private android.view.View cardAddPhoto;
    private final List<Uri> selectedImageUris = new ArrayList<>();
    private final android.view.View[] photoSlots = new android.view.View[3];
    private final ImageView[] photoImages = new ImageView[3];
    private double latitude = Double.NaN;
    private double longitude = Double.NaN;
    private int affectedPeopleCount = 1;
    private String selectedPriority = "HIGH";

    private final ActivityResultLauncher<String[]> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null || uris.isEmpty()) {
                    return;
                }
                int remaining = MAX_IMAGES - selectedImageUris.size();
                if (remaining <= 0) {
                    toast(getString(R.string.citizen_create_photo_limit));
                    return;
                }
                int added = 0;
                for (Uri uri : uris) {
                    if (added >= remaining) {
                        break;
                    }
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    selectedImageUris.add(uri);
                    added++;
                }
                if (uris.size() > remaining) {
                    toast(getString(R.string.citizen_create_photo_limit));
                }
                renderPhotoSlots();
            });

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onLocationPermissionResult);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_create_rescue_request);

        rescueRepository = new CitizenRescueRepository(this);
        bindViews();
        bindActions();
        renderAffectedPeopleCount();
        renderPriority();
        renderPhotoSlots();
        tryResolveLocation(false);
    }

    private void bindViews() {
        layoutAddress = findViewById(R.id.layoutAddress);
        layoutLocationDetail = findViewById(R.id.layoutLocationDetail);
        layoutDescription = findViewById(R.id.layoutDescription);
        editAddress = findViewById(R.id.editAddress);
        editLocationDetail = findViewById(R.id.editLocationDetail);
        editDescription = findViewById(R.id.editDescription);
        textAffectedCount = findViewById(R.id.textAffectedCount);
        textLocationMeta = findViewById(R.id.textLocationMeta);
        textCreateRescueError = findViewById(R.id.textCreateRescueError);
        buttonSubmitRescue = findViewById(R.id.buttonSubmitRescue);
        layoutCreateRescueError = findViewById(R.id.layoutCreateRescueError);
        buttonPriorityHigh = findViewById(R.id.buttonPriorityHigh);
        buttonPriorityMedium = findViewById(R.id.buttonPriorityMedium);
        buttonPriorityLow = findViewById(R.id.buttonPriorityLow);
        cardAddPhoto = findViewById(R.id.cardAddPhoto);
        photoSlots[0] = findViewById(R.id.photoSlot1);
        photoSlots[1] = findViewById(R.id.photoSlot2);
        photoSlots[2] = findViewById(R.id.photoSlot3);
        photoImages[0] = findViewById(R.id.imagePhoto1);
        photoImages[1] = findViewById(R.id.imagePhoto2);
        photoImages[2] = findViewById(R.id.imagePhoto3);
    }

    private void bindActions() {
        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonInfo).setOnClickListener(v -> toast(getString(R.string.citizen_create_info_message)));
        findViewById(R.id.buttonAutoLocate).setOnClickListener(v -> tryResolveLocation(true));
        findViewById(R.id.buttonDecreaseCount).setOnClickListener(v -> {
            if (affectedPeopleCount > 1) {
                affectedPeopleCount--;
                renderAffectedPeopleCount();
            }
        });
        findViewById(R.id.buttonIncreaseCount).setOnClickListener(v -> {
            affectedPeopleCount++;
            renderAffectedPeopleCount();
        });
        buttonPriorityHigh.setOnClickListener(v -> selectPriority("HIGH"));
        buttonPriorityMedium.setOnClickListener(v -> selectPriority("MEDIUM"));
        buttonPriorityLow.setOnClickListener(v -> selectPriority("LOW"));
        cardAddPhoto.setOnClickListener(v -> pickImagesLauncher.launch(new String[]{"image/*"}));
        findViewById(R.id.buttonRemovePhoto1).setOnClickListener(v -> removePhoto(0));
        findViewById(R.id.buttonRemovePhoto2).setOnClickListener(v -> removePhoto(1));
        findViewById(R.id.buttonRemovePhoto3).setOnClickListener(v -> removePhoto(2));
        buttonSubmitRescue.setOnClickListener(v -> submitRescueRequest());
    }

    private void renderAffectedPeopleCount() {
        textAffectedCount.setText(String.valueOf(affectedPeopleCount));
    }

    private void selectPriority(String priority) {
        selectedPriority = priority;
        renderPriority();
    }

    private void renderPriority() {
        stylePriority(buttonPriorityHigh, "HIGH".equals(selectedPriority));
        stylePriority(buttonPriorityMedium, "MEDIUM".equals(selectedPriority));
        stylePriority(buttonPriorityLow, "LOW".equals(selectedPriority));
    }

    private void stylePriority(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_rescue_priority_active : R.drawable.bg_rescue_priority_idle);
        int color = ContextCompat.getColor(this, active ? R.color.white : R.color.text_secondary);
        view.setTextColor(color);
    }

    private void renderPhotoSlots() {
        for (int i = 0; i < photoSlots.length; i++) {
            if (i < selectedImageUris.size()) {
                photoSlots[i].setVisibility(android.view.View.VISIBLE);
                photoImages[i].setImageURI(selectedImageUris.get(i));
            } else {
                photoSlots[i].setVisibility(android.view.View.GONE);
                photoImages[i].setImageDrawable(null);
            }
        }
        cardAddPhoto.setVisibility(selectedImageUris.size() >= MAX_IMAGES ? android.view.View.GONE : android.view.View.VISIBLE);
    }

    private void removePhoto(int index) {
        if (index < 0 || index >= selectedImageUris.size()) {
            return;
        }
        selectedImageUris.remove(index);
        renderPhotoSlots();
    }

    private void tryResolveLocation(boolean askPermissionIfNeeded) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (askPermissionIfNeeded) {
                locationPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
            return;
        }

        textLocationMeta.setText(getString(R.string.citizen_create_locating));
        Location location = readLastKnownLocation();
        if (location == null) {
            textLocationMeta.setText(getString(R.string.citizen_create_locate_failed));
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        textLocationMeta.setText(String.format(
                Locale.getDefault(),
                "Lat %.5f, Lng %.5f",
                latitude,
                longitude
        ));
    }

    private void onLocationPermissionResult(Map<String, Boolean> result) {
        boolean granted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
        if (granted) {
            tryResolveLocation(false);
            return;
        }
        textLocationMeta.setText(getString(R.string.citizen_create_location_permission_denied));
        toast(getString(R.string.citizen_create_location_permission_denied));
    }

    @Nullable
    private Location readLastKnownLocation() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager == null) {
                return null;
            }
            Location best = null;
            String[] providers = new String[]{
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER
            };
            for (String provider : providers) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (best == null || location.getAccuracy() < best.getAccuracy()) {
                    best = location;
                }
            }
            return best;
        } catch (SecurityException ignored) {
            return null;
        }
    }

    private void submitRescueRequest() {
        clearErrors();

        String address = textOf(editAddress);
        String locationDetail = textOf(editLocationDetail);
        String description = textOf(editDescription);

        if (address.isEmpty()) {
            layoutAddress.setError(getString(R.string.citizen_create_address_error));
            return;
        }
        if (locationDetail.isEmpty()) {
            layoutLocationDetail.setError(getString(R.string.citizen_create_location_detail_error));
            return;
        }
        if (description.isEmpty()) {
            layoutDescription.setError(getString(R.string.citizen_create_description_error));
            return;
        }
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            showError(getString(R.string.citizen_create_location_error));
            return;
        }

        setSubmitting(true, getString(R.string.citizen_create_submitting));

        if (selectedImageUris.isEmpty()) {
            createRescueRequest(new ArrayList<>(), address, locationDetail, description);
            return;
        }

        buttonSubmitRescue.setText(getString(R.string.citizen_create_uploading));
        rescueRepository.uploadAttachments(selectedImageUris, new RepositoryCallback<List<AttachmentUploadResponse>>() {
            @Override
            public void onSuccess(List<AttachmentUploadResponse> data) {
                runOnUiThread(() -> createRescueRequest(data, address, locationDetail, description));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setSubmitting(false, getString(R.string.citizen_create_submit));
                    showError(message);
                });
            }
        });
    }

    private void createRescueRequest(
            List<AttachmentUploadResponse> uploads,
            String address,
            String locationDetail,
            String description
    ) {
        List<RescueRequestCreatePayload.AttachmentPayload> attachments = new ArrayList<>();
        for (AttachmentUploadResponse upload : uploads) {
            attachments.add(new RescueRequestCreatePayload.AttachmentPayload(
                    upload.getFileUrl(),
                    upload.getFileType()
            ));
        }

        RescueRequestCreatePayload payload = new RescueRequestCreatePayload(
                affectedPeopleCount,
                description,
                address,
                latitude,
                longitude,
                locationDetail,
                selectedPriority,
                attachments
        );

        rescueRepository.createRescueRequest(payload, new RepositoryCallback<Long>() {
            @Override
            public void onSuccess(Long requestId) {
                runOnUiThread(() -> {
                    setSubmitting(false, getString(R.string.citizen_create_submit));
                    toast(getString(R.string.citizen_create_success));
                    Intent intent = new Intent(CreateRescueRequestActivity.this, CitizenRescueDetailActivity.class);
                    if (requestId != null && requestId > 0) {
                        intent.putExtra(CitizenRescueDetailActivity.EXTRA_REQUEST_ID, requestId);
                    }
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setSubmitting(false, getString(R.string.citizen_create_submit));
                    showError(message);
                });
            }
        });
    }

    private void clearErrors() {
        layoutAddress.setError(null);
        layoutLocationDetail.setError(null);
        layoutDescription.setError(null);
        layoutCreateRescueError.setVisibility(android.view.View.GONE);
    }

    private void showError(String message) {
        textCreateRescueError.setText(message);
        layoutCreateRescueError.setVisibility(android.view.View.VISIBLE);
    }

    private void setSubmitting(boolean submitting, String label) {
        buttonSubmitRescue.setText(label);
        buttonSubmitRescue.setEnabled(!submitting);
        buttonSubmitRescue.setAlpha(submitting ? 0.7f : 1f);
    }

    private String textOf(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
