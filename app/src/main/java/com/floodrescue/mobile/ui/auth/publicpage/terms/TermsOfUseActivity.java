package com.floodrescue.mobile.ui.auth.publicpage.terms;

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

public class TermsOfUseActivity extends BaseActivity {

    public static final String EXTRA_SOURCE = "source";
    public static final String SOURCE_LOGIN = "login";
    public static final String SOURCE_REGISTER = "register";

    public static Intent newIntent(Context context, String source) {
        Intent intent = new Intent(context, TermsOfUseActivity.class);
        intent.putExtra(EXTRA_SOURCE, source);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_public_terms);

        bindBackButton(R.id.buttonBack);

        TextView secondaryButton = findViewById(R.id.buttonTermsBackRegister);
        String source = getIntent().getStringExtra(EXTRA_SOURCE);
        if (SOURCE_LOGIN.equals(source)) {
            secondaryButton.setText(R.string.public_support_back_login);
        }
        secondaryButton.setOnClickListener(v -> finish());
        findViewById(R.id.buttonTermsAccept).setOnClickListener(v -> finish());

        PublicContentRepository repository = new PublicContentRepository(getApplicationContext());
        repository.getContentPage("terms", new RepositoryCallback<PublicContentPageResponse>() {
            @Override
            public void onSuccess(PublicContentPageResponse data) {
                if (data == null) {
                    return;
                }
                if (!data.getTitle().isEmpty()) {
                    ((TextView) findViewById(R.id.textTermsToolbarTitle)).setText(data.getTitle());
                    ((TextView) findViewById(R.id.textTermsTitle)).setText(data.getTitle());
                }
                if (!data.getContent().isEmpty()) {
                    TextView content = findViewById(R.id.textTermsBackendContent);
                    content.setText(data.getContent());
                    content.setVisibility(android.view.View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                // Keep fallback content already rendered in XML.
            }
        });
    }
}
