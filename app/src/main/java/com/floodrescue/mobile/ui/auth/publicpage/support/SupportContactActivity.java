package com.floodrescue.mobile.ui.auth.publicpage.support;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.response.PublicContentPageResponse;
import com.floodrescue.mobile.data.repository.PublicContentRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;

import java.util.Map;

public class SupportContactActivity extends BaseActivity {

    public static final String EXTRA_SOURCE = "source";

    public static Intent newIntent(Context context, String source) {
        Intent intent = new Intent(context, SupportContactActivity.class);
        intent.putExtra(EXTRA_SOURCE, source);
        return intent;
    }

    private String hotlineValue = "1900 6789";
    private String supportEmail = "support@cuuho.gov.vn";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_public_support);

        bindBackButton(R.id.buttonBack);
        bindExpandableSection(R.id.textSupportHeader1, R.id.textSupportBody1);
        bindExpandableSection(R.id.textSupportHeader2, R.id.textSupportBody2);
        bindExpandableSection(R.id.textSupportHeader3, R.id.textSupportBody3);
        findViewById(R.id.buttonSupportBackLogin).setOnClickListener(v -> finish());
        findViewById(R.id.buttonCallHotline).setOnClickListener(v -> openDialer());
        findViewById(R.id.buttonEmailSupport).setOnClickListener(v -> openEmailApp());

        PublicContentRepository repository = new PublicContentRepository(getApplicationContext());
        repository.getRuntimeSettings(new RepositoryCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                if (data == null) {
                    return;
                }
                String hotline = valueOf(data.get("hotline"));
                String email = valueOf(data.get("footerSupportEmail"));
                String title = valueOf(data.get("footerSupportLabel"));

                if (!hotline.isEmpty()) {
                    hotlineValue = hotline.replace("-", " ");
                    ((TextView) findViewById(R.id.textSupportHotline)).setText(hotlineValue);
                }
                if (!email.isEmpty()) {
                    supportEmail = email;
                    ((TextView) findViewById(R.id.textSupportEmail)).setText(email);
                }
                if (!title.isEmpty()) {
                    ((TextView) findViewById(R.id.textSupportToolbarTitle)).setText(title);
                }
            }

            @Override
            public void onError(String message) {
                // Repository already falls back to empty map.
            }
        });

        repository.getContentPage("support", new RepositoryCallback<PublicContentPageResponse>() {
            @Override
            public void onSuccess(PublicContentPageResponse data) {
                if (data == null) {
                    return;
                }
                if (!data.getTitle().isEmpty()) {
                    ((TextView) findViewById(R.id.textSupportToolbarTitle)).setText(data.getTitle());
                }
                if (!data.getContent().isEmpty()) {
                    TextView content = findViewById(R.id.textSupportBackendContent);
                    content.setText(data.getContent());
                    content.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                // Keep fallback support content.
            }
        });
    }

    private void bindExpandableSection(int headerId, int bodyId) {
        TextView header = findViewById(headerId);
        TextView body = findViewById(bodyId);
        header.setOnClickListener(v -> body.setVisibility(body.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
    }

    private void openDialer() {
        String digits = hotlineValue.replaceAll("[^\\d+]", "");
        if (TextUtils.isEmpty(digits)) {
            showShortToast("Chua co hotline kha dung.");
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + digits)));
        } catch (ActivityNotFoundException exception) {
            showShortToast("Khong mo duoc ung dung goi dien.");
        }
    }

    private void openEmailApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + supportEmail));
            startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            showShortToast("Khong mo duoc ung dung email.");
        }
    }

    private String valueOf(String value) {
        return value == null ? "" : value.trim();
    }
}
