package com.floodrescue.mobile.ui.role.citizen.rescue.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.BuildConfig;
import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.core.util.RemoteImageLoader;
import com.floodrescue.mobile.data.model.request.CitizenRescueUpdateRequest;
import com.floodrescue.mobile.data.model.response.AttachmentUploadResponse;
import com.floodrescue.mobile.data.model.ui.CitizenRescueDetailState;
import com.floodrescue.mobile.data.repository.CitizenRescueRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitizenRescueUpdateActivity extends BaseActivity {

    private static final int MAX_IMAGES = 3;
    public static final String EXTRA_REQUEST_ID = "request_id";

    private long requestId = -1L;
    private CitizenRescueRepository rescueRepository;

    private TextInputLayout layoutDescription;
    private TextInputLayout layoutAddress;
    private TextInputEditText editDescription;
    private TextInputEditText editAddress;
    private TextView textPeopleCount;
    private TextView buttonPriorityHigh;
    private TextView buttonPriorityMedium;
    private TextView buttonPriorityLow;
    private View cardAddPhoto;
    private final View[] photoSlots = new View[3];
    private final ImageView[] photoImages = new ImageView[3];
    private final List<Uri> newImageUris = new ArrayList<>();
    private final List<CitizenRescueDetailState.AttachmentItem> existingAttachments = new ArrayList<>();
    private int peopleCount = 1;
    private String selectedPriority = "HIGH";

    private final ActivityResultLauncher<String[]> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null || uris.isEmpty()) return;
                int remaining = MAX_IMAGES - (existingAttachments.size() + newImageUris.size());
                if (remaining <= 0) return;
                int added = 0;
                for (Uri uri : uris) {
                    if (added >= remaining) break;
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    newImageUris.add(uri);
                    added++;
                }
                renderPhotos();
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_rescue_update);

        rescueRepository = new CitizenRescueRepository(this);
        requestId = getIntent().getLongExtra(EXTRA_REQUEST_ID, -1L);
        if (requestId <= 0L) {
            finish();
            return;
        }

        bindViews();
        bindActions();
        loadDetail();
    }

    private void bindViews() {
        layoutDescription = findViewById(R.id.layoutUpdateDescription);
        layoutAddress = findViewById(R.id.layoutUpdateAddress);
        editDescription = findViewById(R.id.editUpdateDescription);
        editAddress = findViewById(R.id.editUpdateAddress);
        textPeopleCount = findViewById(R.id.textUpdatePeopleCount);
        buttonPriorityHigh = findViewById(R.id.buttonUpdatePriorityHigh);
        buttonPriorityMedium = findViewById(R.id.buttonUpdatePriorityMedium);
        buttonPriorityLow = findViewById(R.id.buttonUpdatePriorityLow);
        cardAddPhoto = findViewById(R.id.cardUpdateAddPhoto);
        photoSlots[0] = findViewById(R.id.updatePhotoSlot1);
        photoSlots[1] = findViewById(R.id.updatePhotoSlot2);
        photoSlots[2] = findViewById(R.id.updatePhotoSlot3);
        photoImages[0] = findViewById(R.id.updateImagePhoto1);
        photoImages[1] = findViewById(R.id.updateImagePhoto2);
        photoImages[2] = findViewById(R.id.updateImagePhoto3);
    }

    private void bindActions() {
        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonCancelUpdate).setOnClickListener(v -> finish());
        findViewById(R.id.buttonSaveUpdate).setOnClickListener(v -> submitUpdate());
        findViewById(R.id.buttonDecreasePeople).setOnClickListener(v -> {
            if (peopleCount > 1) {
                peopleCount--;
                renderPeopleCount();
            }
        });
        findViewById(R.id.buttonIncreasePeople).setOnClickListener(v -> {
            peopleCount++;
            renderPeopleCount();
        });
        buttonPriorityHigh.setOnClickListener(v -> selectPriority("HIGH"));
        buttonPriorityMedium.setOnClickListener(v -> selectPriority("MEDIUM"));
        buttonPriorityLow.setOnClickListener(v -> selectPriority("LOW"));
        cardAddPhoto.setOnClickListener(v -> pickImagesLauncher.launch(new String[]{"image/*"}));
        findViewById(R.id.buttonRemovePhoto1).setOnClickListener(v -> removePhoto(0));
        findViewById(R.id.buttonRemovePhoto2).setOnClickListener(v -> removePhoto(1));
        findViewById(R.id.buttonRemovePhoto3).setOnClickListener(v -> removePhoto(2));
    }

    private void loadDetail() {
        showLoading(true);
        rescueRepository.getRescueRequestDetail(requestId, new RepositoryCallback<CitizenRescueDetailState>() {
            @Override
            public void onSuccess(CitizenRescueDetailState data) {
                runOnUiThread(() -> {
                    showLoading(false);
                    bindDetail(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showShortToast(message);
                    finish();
                });
            }
        });
    }

    private void bindDetail(CitizenRescueDetailState state) {
        peopleCount = Math.max(state.getAffectedPeopleCount(), 1);
        selectedPriority = normalize(state.getPriority()).isEmpty() ? "HIGH" : normalize(state.getPriority());
        editDescription.setText(state.getDescription());
        editAddress.setText(state.getAddressText());

        existingAttachments.clear();
        if (state.getAttachments() != null) {
            existingAttachments.addAll(state.getAttachments());
        }

        renderPeopleCount();
        renderPriority();
        renderPhotos();
    }

    private void renderPeopleCount() {
        textPeopleCount.setText(String.valueOf(peopleCount));
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

    private void renderPhotos() {
        int total = existingAttachments.size() + newImageUris.size();
        for (int i = 0; i < photoSlots.length; i++) {
            if (i < total) {
                photoSlots[i].setVisibility(View.VISIBLE);
                if (i < existingAttachments.size()) {
                    RemoteImageLoader.load(photoImages[i], buildAttachmentUrl(existingAttachments.get(i).getFileUrl()));
                } else {
                    int localIndex = i - existingAttachments.size();
                    photoImages[i].setImageURI(newImageUris.get(localIndex));
                }
            } else {
                photoSlots[i].setVisibility(View.GONE);
                photoImages[i].setImageDrawable(null);
            }
        }
        cardAddPhoto.setVisibility(total >= MAX_IMAGES ? View.GONE : View.VISIBLE);
    }

    private void removePhoto(int index) {
        int totalExisting = existingAttachments.size();
        if (index < totalExisting) {
            existingAttachments.remove(index);
        } else {
            int localIndex = index - totalExisting;
            if (localIndex >= 0 && localIndex < newImageUris.size()) {
                newImageUris.remove(localIndex);
            }
        }
        renderPhotos();
    }

    private void submitUpdate() {
        clearErrors();
        String description = textOf(editDescription);
        String address = textOf(editAddress);
        if (peopleCount <= 0) {
            showShortToast(getString(R.string.citizen_detail_edit_people_error));
            return;
        }
        if (isBlank(description)) {
            layoutDescription.setError(getString(R.string.citizen_detail_edit_description_error));
            return;
        }
        if (isBlank(address)) {
            layoutAddress.setError(getString(R.string.citizen_detail_edit_address_error));
            return;
        }

        showLoading(true);
        if (newImageUris.isEmpty()) {
            submitUpdateWithAttachments(buildAttachmentPayloads(existingAttachments));
            return;
        }

        rescueRepository.uploadAttachments(newImageUris, new RepositoryCallback<List<AttachmentUploadResponse>>() {
            @Override
            public void onSuccess(List<AttachmentUploadResponse> data) {
                runOnUiThread(() -> {
                    List<CitizenRescueDetailState.AttachmentItem> merged = new ArrayList<>(existingAttachments);
                    if (data != null) {
                        for (AttachmentUploadResponse upload : data) {
                            merged.add(new CitizenRescueDetailState.AttachmentItem(
                                    -1L,
                                    upload.getFileUrl(),
                                    upload.getFileType(),
                                    ""
                            ));
                        }
                    }
                    submitUpdateWithAttachments(buildAttachmentPayloads(merged));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showShortToast(message);
                });
            }
        });
    }

    private void submitUpdateWithAttachments(List<CitizenRescueUpdateRequest.AttachmentPayload> attachments) {
        rescueRepository.updateRescueRequest(
                requestId,
                new CitizenRescueUpdateRequest(
                        peopleCount,
                        textOf(editDescription),
                        textOf(editAddress),
                        selectedPriority,
                        attachments
                ),
                new RepositoryCallback<CitizenRescueDetailState>() {
                    @Override
                    public void onSuccess(CitizenRescueDetailState data) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showShortToast(getString(R.string.citizen_detail_edit_success));
                            Intent intent = new Intent(CitizenRescueUpdateActivity.this, CitizenRescueDetailActivity.class);
                            intent.putExtra(CitizenRescueDetailActivity.EXTRA_REQUEST_ID, data.getId());
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showShortToast(message);
                        });
                    }
                }
        );
    }

    private List<CitizenRescueUpdateRequest.AttachmentPayload> buildAttachmentPayloads(List<CitizenRescueDetailState.AttachmentItem> attachments) {
        List<CitizenRescueUpdateRequest.AttachmentPayload> list = new ArrayList<>();
        if (attachments == null) return list;
        for (CitizenRescueDetailState.AttachmentItem item : attachments) {
            if (isBlank(item.getFileUrl())) continue;
            list.add(new CitizenRescueUpdateRequest.AttachmentPayload(
                    item.getFileUrl(),
                    item.getFileType()
            ));
        }
        return list;
    }

    private void clearErrors() {
        layoutDescription.setError(null);
        layoutAddress.setError(null);
    }

    private void showLoading(boolean loading) {
        findViewById(R.id.progressUpdate).setVisibility(loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.buttonSaveUpdate).setEnabled(!loading);
        findViewById(R.id.buttonCancelUpdate).setEnabled(!loading);
    }

    private String buildAttachmentUrl(String fileUrl) {
        String raw = safe(fileUrl);
        if (raw.startsWith("http://") || raw.startsWith("https://")) return raw;
        String base = BuildConfig.BASE_URL.endsWith("/")
                ? BuildConfig.BASE_URL.substring(0, BuildConfig.BASE_URL.length() - 1)
                : BuildConfig.BASE_URL;
        if (!raw.startsWith("/")) raw = "/" + raw;
        return base + raw;
    }

    private String textOf(TextInputEditText editText) {
        if (editText.getText() == null) return "";
        return editText.getText().toString().trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return TextUtils.isEmpty(value == null ? null : value.trim());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
