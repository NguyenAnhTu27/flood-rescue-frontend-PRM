package com.floodrescue.mobile.ui.role.admin.permission;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.AdminPermissionMatrixState;
import com.floodrescue.mobile.data.model.ui.AdminPermissionOption;
import com.floodrescue.mobile.data.model.ui.AdminRoleOption;
import com.floodrescue.mobile.data.repository.AdminPermissionRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminPermissionActivity extends BaseActivity {

    private AdminPermissionRepository repository;
    private LinearLayout containerRoles;
    private LinearLayout containerModules;
    private TextView textError;
    private TextView textEmpty;
    private View progressView;
    private TextView buttonSave;

    private AdminPermissionMatrixState matrixState;
    private AdminRoleOption selectedRole;
    private final LinkedHashSet<String> workingPermissions = new LinkedHashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_admin_permission);

        repository = new AdminPermissionRepository(this);
        containerRoles = findViewById(R.id.containerAdminPermissionRoles);
        containerModules = findViewById(R.id.containerAdminPermissionModules);
        textError = findViewById(R.id.textAdminPermissionError);
        textEmpty = findViewById(R.id.textAdminPermissionEmpty);
        progressView = findViewById(R.id.progressAdminPermission);
        buttonSave = findViewById(R.id.buttonAdminPermissionSave);

        bindBackButton(R.id.buttonBackAdminPermissions);
        buttonSave.setOnClickListener(v -> savePermissions());
        loadMatrix();
    }

    private void loadMatrix() {
        setLoading(true);
        repository.loadMatrix(new RepositoryCallback<AdminPermissionMatrixState>() {
            @Override
            public void onSuccess(AdminPermissionMatrixState data) {
                setLoading(false);
                matrixState = data;
                if (data == null || data.getRoles().isEmpty() || data.getPermissions().isEmpty()) {
                    textEmpty.setVisibility(View.VISIBLE);
                    return;
                }
                selectedRole = data.getRoles().get(0);
                syncWorkingPermissions();
                renderRoleTabs();
                renderModules();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText(message == null ? getString(R.string.admin_permission_error) : message);
            }
        });
    }

    private void renderRoleTabs() {
        containerRoles.removeAllViews();
        if (matrixState == null) {
            return;
        }
        for (AdminRoleOption role : matrixState.getRoles()) {
            boolean selected = selectedRole != null && selectedRole.getCode().equalsIgnoreCase(role.getCode());
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    dp(38)
            );
            params.rightMargin = dp(8);
            chip.setLayoutParams(params);
            chip.setGravity(Gravity.CENTER);
            chip.setMinWidth(dp(72));
            chip.setPadding(dp(14), 0, dp(14), 0);
            chip.setBackgroundResource(selected ? R.drawable.bg_admin_filter_active : R.drawable.bg_admin_filter_inactive);
            chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_secondary, null));
            chip.setText(role.getCode().toUpperCase(Locale.ROOT));
            chip.setTextSize(12f);
            chip.setOnClickListener(v -> {
                selectedRole = role;
                syncWorkingPermissions();
                renderRoleTabs();
                renderModules();
            });
            containerRoles.addView(chip);
        }
    }

    private void renderModules() {
        containerModules.removeAllViews();
        if (matrixState == null || selectedRole == null) {
            textEmpty.setVisibility(View.VISIBLE);
            return;
        }

        Map<String, List<AdminPermissionOption>> grouped = new LinkedHashMap<>();
        for (AdminPermissionOption option : matrixState.getPermissions()) {
            String module = option.getModule() == null ? "OTHER" : option.getModule().trim().toUpperCase(Locale.ROOT);
            if (!grouped.containsKey(module)) {
                grouped.put(module, new ArrayList<>());
            }
            grouped.get(module).add(option);
        }

        if (grouped.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map.Entry<String, List<AdminPermissionOption>> entry : grouped.entrySet()) {
            View sectionView = inflater.inflate(R.layout.item_admin_permission_module, containerModules, false);
            TextView title = sectionView.findViewById(R.id.textAdminPermissionModuleTitle);
            TextView count = sectionView.findViewById(R.id.textAdminPermissionModuleCount);
            LinearLayout rows = sectionView.findViewById(R.id.containerAdminPermissionRows);

            title.setText(mapModuleTitle(entry.getKey()));
            count.setText(getString(R.string.admin_permission_module_count, entry.getValue().size()));
            for (AdminPermissionOption option : entry.getValue()) {
                View row = inflater.inflate(R.layout.item_admin_permission_toggle, rows, false);
                TextView name = row.findViewById(R.id.textAdminPermissionName);
                TextView code = row.findViewById(R.id.textAdminPermissionCode);
                SwitchMaterial toggle = row.findViewById(R.id.switchAdminPermission);
                name.setText(option.getName());
                code.setText(option.getCode());
                toggle.setChecked(workingPermissions.contains(option.getCode()));
                toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        workingPermissions.add(option.getCode());
                    } else {
                        workingPermissions.remove(option.getCode());
                    }
                });
                rows.addView(row);
            }
            containerModules.addView(sectionView);
        }
    }

    private void savePermissions() {
        if (selectedRole == null) {
            return;
        }
        setLoading(true);
        repository.updateRolePermissions(selectedRole.getCode(), new ArrayList<>(workingPermissions), new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                setLoading(false);
                if (matrixState != null) {
                    matrixState.getRolePermissions().put(selectedRole.getCode(), new ArrayList<>(workingPermissions));
                }
                showShortToast(data == null ? getString(R.string.admin_permission_saved) : data);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText(message == null ? getString(R.string.admin_permission_error) : message);
            }
        });
    }

    private void syncWorkingPermissions() {
        workingPermissions.clear();
        if (matrixState == null || selectedRole == null) {
            return;
        }
        List<String> existing = matrixState.getRolePermissions().get(selectedRole.getCode());
        if (existing != null) {
            workingPermissions.addAll(existing);
        }
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        if (loading) {
            textEmpty.setVisibility(View.GONE);
        }
        buttonSave.setEnabled(!loading);
    }

    private String mapModuleTitle(String module) {
        switch (module) {
            case "USER":
                return getString(R.string.admin_permission_module_user);
            case "RESCUE":
                return getString(R.string.admin_permission_module_rescue);
            case "INVENTORY":
                return getString(R.string.admin_permission_module_inventory);
            case "SYSTEM":
                return getString(R.string.admin_permission_module_system);
            case "RELIEF":
                return getString(R.string.admin_permission_module_relief);
            case "FEEDBACK":
                return getString(R.string.admin_permission_module_feedback);
            case "TEAM":
                return getString(R.string.admin_permission_module_team);
            default:
                return getString(R.string.admin_permission_module_other);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
