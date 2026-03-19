package com.floodrescue.mobile.ui.role.citizen.feedback;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.repository.CitizenFeedbackRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CitizenFeedbackActivity extends BaseActivity {

    private final ImageView[] starViews = new ImageView[5];
    private int selectedRating = 0;

    private TextInputLayout layoutContent;
    private TextInputEditText editContent;
    private SwitchMaterial switchRescued;
    private SwitchMaterial switchRelief;
    private View progressView;
    private View buttonSubmit;

    private CitizenFeedbackRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_feedback);

        repository = new CitizenFeedbackRepository(this);
        bindViews();
        bindActions();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBack);
        starViews[0] = findViewById(R.id.star1);
        starViews[1] = findViewById(R.id.star2);
        starViews[2] = findViewById(R.id.star3);
        starViews[3] = findViewById(R.id.star4);
        starViews[4] = findViewById(R.id.star5);
        layoutContent = findViewById(R.id.layoutFeedbackContent);
        editContent = findViewById(R.id.editFeedbackContent);
        switchRescued = findViewById(R.id.switchRescued);
        switchRelief = findViewById(R.id.switchRelief);
        progressView = findViewById(R.id.progressFeedback);
        buttonSubmit = findViewById(R.id.buttonSubmitFeedback);
    }

    private void bindActions() {
        for (int i = 0; i < starViews.length; i++) {
            int index = i + 1;
            starViews[i].setOnClickListener(v -> setRating(index));
        }
        buttonSubmit.setOnClickListener(v -> submitFeedback());
        findViewById(R.id.buttonSkipFeedback).setOnClickListener(v -> finish());
    }

    private void setRating(int rating) {
        selectedRating = rating;
        for (int i = 0; i < starViews.length; i++) {
            starViews[i].setImageResource(i < rating ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        }
    }

    private void submitFeedback() {
        layoutContent.setError(null);
        if (selectedRating <= 0) {
            showShortToast(getString(R.string.citizen_feedback_error_rating));
            return;
        }
        String content = editContent.getText() == null ? "" : editContent.getText().toString().trim();
        boolean rescued = switchRescued.isChecked();
        boolean relief = switchRelief.isChecked();

        setLoading(true);
        repository.submitFeedback(selectedRating, content, rescued, relief, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showShortToast(data);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showShortToast(message);
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonSubmit.setEnabled(!loading);
        ((TextView) buttonSubmit).setText(loading ? getString(R.string.common_loading) : getString(R.string.citizen_feedback_submit));
    }
}
