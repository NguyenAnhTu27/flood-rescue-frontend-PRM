package com.floodrescue.mobile.ui.role.admin.user.list;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.AdminRoleOption;
import com.floodrescue.mobile.data.model.ui.AdminUserItem;
import com.floodrescue.mobile.data.model.ui.AdminUserPage;
import com.floodrescue.mobile.data.repository.AdminUserRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.admin.permission.AdminPermissionActivity;
import com.floodrescue.mobile.ui.role.admin.user.form.AdminUserFormActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminUserListActivity extends BaseActivity implements AdminUserAdapter.Listener {

    private static final int PAGE_SIZE = 10;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = () -> {
        currentPage = 0;
        loadUsers();
    };

    private AdminUserRepository repository;
    private AdminUserAdapter adapter;

    private EditText editAdminUserSearch;
    private LinearLayout containerAdminRoleFilters;
    private TextView textAdminUserCount;
    private TextView textAdminUserUpdated;
    private TextView textAdminUsersEmpty;
    private TextView textAdminUsersError;
    private TextView buttonAdminUsersPrev;
    private TextView buttonAdminUsersNext;
    private TextView textAdminUsersPage;
    private View progressAdminUsers;

    private final List<AdminRoleOption> roles = new ArrayList<>();
    private Integer selectedRoleId = null;
    private String selectedStatus = "ALL";
    private int currentPage = 0;
    private int totalPages = 1;
    private boolean firstResume = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_admin_user_list);

        repository = new AdminUserRepository(this);
        bindViews();
        bindActions();
        setupList();
        renderRoleFilters();
        loadRoles();
        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
            return;
        }
        loadUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchHandler.removeCallbacks(searchRunnable);
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackAdminUsers);
        editAdminUserSearch = findViewById(R.id.editAdminUserSearch);
        containerAdminRoleFilters = findViewById(R.id.containerAdminRoleFilters);
        textAdminUserCount = findViewById(R.id.textAdminUserCount);
        textAdminUserUpdated = findViewById(R.id.textAdminUserUpdated);
        textAdminUsersEmpty = findViewById(R.id.textAdminUsersEmpty);
        textAdminUsersError = findViewById(R.id.textAdminUsersError);
        buttonAdminUsersPrev = findViewById(R.id.buttonAdminUsersPrev);
        buttonAdminUsersNext = findViewById(R.id.buttonAdminUsersNext);
        textAdminUsersPage = findViewById(R.id.textAdminUsersPage);
        progressAdminUsers = findViewById(R.id.progressAdminUsers);
    }

    private void bindActions() {
        editAdminUserSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchHandler.postDelayed(searchRunnable, 350);
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        findViewById(R.id.buttonAdminUserFilter).setOnClickListener(v -> showStatusFilterDialog());
        findViewById(R.id.buttonAdminUsersPermission).setOnClickListener(v -> startActivity(new Intent(this, AdminPermissionActivity.class)));
        findViewById(R.id.fabAdminCreateUser).setOnClickListener(v -> startActivity(AdminUserFormActivity.createIntent(this)));
        buttonAdminUsersPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadUsers();
            }
        });
        buttonAdminUsersNext.setOnClickListener(v -> {
            if (currentPage + 1 < totalPages) {
                currentPage++;
                loadUsers();
            }
        });
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerAdminUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadRoles() {
        repository.loadRoles(new RepositoryCallback<List<AdminRoleOption>>() {
            @Override
            public void onSuccess(List<AdminRoleOption> data) {
                roles.clear();
                if (data != null) {
                    roles.addAll(data);
                }
                renderRoleFilters();
            }

            @Override
            public void onError(String message) {
                renderRoleFilters();
                showShortToast(message == null ? getString(R.string.admin_users_error) : message);
            }
        });
    }

    private void loadUsers() {
        setLoading(true);
        String keyword = editAdminUserSearch.getText() == null
                ? null
                : editAdminUserSearch.getText().toString().trim();
        repository.loadUsers(currentPage, PAGE_SIZE, keyword, selectedRoleId, new RepositoryCallback<AdminUserPage>() {
            @Override
            public void onSuccess(AdminUserPage data) {
                setLoading(false);
                List<AdminUserItem> display = applyStatusFilter(data == null ? null : data.getUsers());
                adapter.submit(display);
                totalPages = data == null ? 1 : Math.max(1, data.getTotalPages());
                currentPage = data == null ? 0 : data.getPage();
                textAdminUsersPage.setText(String.format(Locale.getDefault(), "%d / %d", currentPage + 1, totalPages));
                buttonAdminUsersPrev.setAlpha(currentPage > 0 ? 1f : 0.45f);
                buttonAdminUsersNext.setAlpha(currentPage + 1 < totalPages ? 1f : 0.45f);
                textAdminUserUpdated.setText(getString(
                        R.string.admin_users_updated,
                        new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date())
                ));
                int count = selectedStatus.equals("ALL")
                        ? (data == null ? 0 : (int) data.getTotalUsers())
                        : display.size();
                textAdminUserCount.setText(getString(R.string.admin_users_count, count));
                textAdminUsersEmpty.setVisibility(display.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                textAdminUsersError.setVisibility(View.VISIBLE);
                textAdminUsersError.setText(message == null ? getString(R.string.admin_users_error) : message);
                adapter.submit(new ArrayList<>());
                textAdminUsersEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<AdminUserItem> applyStatusFilter(List<AdminUserItem> source) {
        List<AdminUserItem> result = new ArrayList<>();
        if (source == null) {
            return result;
        }
        for (AdminUserItem item : source) {
            if ("ALL".equalsIgnoreCase(selectedStatus) || selectedStatus.equalsIgnoreCase(item.getStatus())) {
                result.add(item);
            }
        }
        return result;
    }

    private void renderRoleFilters() {
        containerAdminRoleFilters.removeAllViews();
        addRoleChip(getString(R.string.admin_users_role_all), null, selectedRoleId == null);
        for (AdminRoleOption role : roles) {
            addRoleChip(role.getCode(), role.getId(), selectedRoleId != null && selectedRoleId == role.getId());
        }
    }

    private void addRoleChip(String label, Integer roleId, boolean selected) {
        TextView chip = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(36)
        );
        params.rightMargin = dp(8);
        chip.setLayoutParams(params);
        chip.setGravity(Gravity.CENTER);
        chip.setMinWidth(dp(64));
        chip.setPadding(dp(14), 0, dp(14), 0);
        chip.setText(label);
        chip.setTextSize(12f);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_secondary, null));
        chip.setBackgroundResource(selected ? R.drawable.bg_admin_filter_active : R.drawable.bg_admin_filter_inactive);
        chip.setOnClickListener(v -> {
            selectedRoleId = roleId;
            currentPage = 0;
            renderRoleFilters();
            loadUsers();
        });
        containerAdminRoleFilters.addView(chip);
    }

    private void showStatusFilterDialog() {
        String[] items = {
                getString(R.string.admin_users_status_all),
                getString(R.string.admin_users_status_active),
                getString(R.string.admin_users_status_locked)
        };
        int checked = "ACTIVE".equals(selectedStatus) ? 1 : "LOCKED".equals(selectedStatus) ? 2 : 0;
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_users_advanced_title)
                .setSingleChoiceItems(items, checked, (dialog, which) -> {
                    selectedStatus = which == 1 ? "ACTIVE" : which == 2 ? "LOCKED" : "ALL";
                    dialog.dismiss();
                    currentPage = 0;
                    loadUsers();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setLoading(boolean loading) {
        progressAdminUsers.setVisibility(loading ? View.VISIBLE : View.GONE);
        textAdminUsersError.setVisibility(View.GONE);
        if (loading) {
            textAdminUsersEmpty.setVisibility(View.GONE);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onEdit(AdminUserItem item) {
        startActivity(AdminUserFormActivity.editIntent(this, item));
    }

    @Override
    public void onToggleStatus(AdminUserItem item) {
        String targetStatus = "ACTIVE".equalsIgnoreCase(item.getStatus()) ? "LOCKED" : "ACTIVE";
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.admin_users_status_confirm, targetStatus))
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        repository.updateUserStatus(item.getId(), targetStatus, refreshCallback()))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onResetPassword(AdminUserItem item) {
        EditText input = new EditText(this);
        input.setHint(R.string.admin_users_reset_hint);
        input.setMinHeight(dp(48));
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_users_reset_title)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String password = input.getText() == null ? "" : input.getText().toString().trim();
                    if (password.isEmpty()) {
                        showShortToast(getString(R.string.admin_users_reset_hint));
                        return;
                    }
                    repository.resetPassword(item.getId(), password, refreshCallback());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onDelete(AdminUserItem item) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.admin_users_delete_confirm)
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        repository.deleteUser(item.getId(), refreshCallback()))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private RepositoryCallback<String> refreshCallback() {
        return new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                showShortToast(data);
                loadUsers();
            }

            @Override
            public void onError(String message) {
                showShortToast(message == null ? getString(R.string.admin_users_error) : message);
            }
        };
    }
}
