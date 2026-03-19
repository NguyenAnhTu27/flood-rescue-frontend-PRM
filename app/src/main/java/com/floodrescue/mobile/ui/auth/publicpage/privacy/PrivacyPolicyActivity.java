package com.floodrescue.mobile.ui.auth.publicpage.privacy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.response.PublicContentPageResponse;
import com.floodrescue.mobile.data.repository.PublicContentRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;

public class PrivacyPolicyActivity extends BaseActivity {

    public static Intent newIntent(Context context, String source) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        intent.putExtra(TermsOfUseSource.EXTRA_SOURCE, source);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_public_privacy);

        bindBackButton(R.id.buttonBack);
        findViewById(R.id.buttonPrivacyBack).setOnClickListener(v -> finish());

        PublicContentRepository repository = new PublicContentRepository(getApplicationContext());
        repository.getContentPage("privacy", new RepositoryCallback<PublicContentPageResponse>() {
            @Override
            public void onSuccess(PublicContentPageResponse data) {
                if (data == null) {
                    return;
                }
                if (!data.getTitle().isEmpty()) {
                    ((TextView) findViewById(R.id.textPrivacyToolbarTitle)).setText(data.getTitle());
                }
                if (!data.getContent().isEmpty()) {
                    TextView content = findViewById(R.id.textPrivacyBackendContent);
                    content.setText(data.getContent());
                    content.setVisibility(android.view.View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                // Keep fallback content if backend page is empty or unavailable.
            }
        });
    }

    private static final class TermsOfUseSource {
        private static final String EXTRA_SOURCE = "source";
    }
}
