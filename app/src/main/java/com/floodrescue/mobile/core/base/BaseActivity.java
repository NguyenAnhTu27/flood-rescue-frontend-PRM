package com.floodrescue.mobile.core.base;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Shared activity helpers for the new role-based screen structure.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void bindBackButton(@IdRes int viewId) {
        View backView = findViewById(viewId);
        if (backView != null) {
            backView.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    protected void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
