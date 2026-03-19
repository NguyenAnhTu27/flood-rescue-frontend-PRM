package com.floodrescue.mobile.ui.role.admin.user.form;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.AdminRoleOption;
import com.floodrescue.mobile.data.model.ui.AdminTeamOption;
import com.floodrescue.mobile.data.model.ui.AdminUserItem;
import com.floodrescue.mobile.data.repository.AdminUserRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminUserFormActivity extends BaseActivity {

    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_FULL_NAME = "extra_full_name";
    private static final String EXTRA_EMAIL = "extra_email";
    private static final String EXTRA_PHONE = "extra_phone";
    private static final String EXTRA_ROLE_ID = "extra_role_id";
    private static final String EXTRA_ROLE_CODE = "extra_role_code";
    private static final String EXTRA_STATUS = "extra_status";

    private AdminUserRepository repository;

    private long userId = -1L;
    private int preselectedRoleId = 0;
    private String preselectedRoleCode = "";
    private String preselectedStatus = "ACTIVE";

    private TextView textTitle;
    private TextView textSummaryBadge;
    private TextView textSummaryFlag;
    private TextView textSummaryName;
    private TextView textSummaryStatus;
    private TextView textTeamHint;
    private TextView textSecurityNote;
    private TextView buttonResetPassword;
    private TextView buttonDelete;
    private TextView buttonSave;
    private TextView textError;
    private View progressView;
    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editPassword;
    private View inputLayoutPassword;
    private Spinner spinnerRole;
    private Spinner spinnerTeam;
    private RadioButton radioActive;
    private RadioButton radioLocked;

    private final List<AdminRoleOption> roles = new ArrayList<>();
    private final List<AdminTeamOption> teams = new ArrayList<>();

    public static Intent createIntent(Context context) {
        return new Intent(context, AdminUserFormActivity.class);
    }

    public static Intent editIntent(Context context, AdminUserItem item) {
        Intent intent = new Intent(context, AdminUserFormActivity.class);
        intent.putExtra(EXTRA_USER_ID, item.getId());
        intent.putExtra(EXTRA_FULL_NAME, item.getFullName());
        intent.putExtra(EXTRA_EMAIL, item.getEmail());
        intent.putExtra(EXTRA_PHONE, item.getPhone());
        intent.putExtra(EXTRA_ROLE_ID, item.getRoleId());
        intent.putExtra(EXTRA_ROLE_CODE, item.getRoleCode());
        intent.putExtra(EXTRA_STATUS, item.getStatus());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_admin_user_form);

        repository = new AdminUserRepository(this);
        readExtras();
        bindViews();
        bindStaticState();
        bindActions();
        loadRoles();
        loadTeams();
    }

    private void readExtras() {
        Intent intent = getIntent();
        userId = intent.getLongExtra(EXTRA_USER_ID, -1L);
        preselectedRoleId = intent.getIntExtra(EXTRA_ROLE_ID, 0);
        preselectedRoleCode = safe(intent.getStringExtra(EXTRA_ROLE_CODE));
        preselectedStatus = safe(intent.getStringExtra(EXTRA_STATUS)).isEmpty() ? "ACTIVE" : safe(intent.getStringExtra(EXTRA_STATUS));
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackAdminUserForm);
        textTitle = findViewById(R.id.textAdminUserFormTitle);
        textSummaryBadge = findViewById(R.id.textAdminFormSummaryBadge);
        textSummaryFlag = findViewById(R.id.textAdminFormSummaryFlag);
        textSummaryName = findViewById(R.id.textAdminFormSummaryName);
        textSummaryStatus = findViewById(R.id.textAdminFormSummaryStatus);
        textTeamHint = findViewById(R.id.textAdminFormTeamHint);
        textSecurityNote = findViewById(R.id.textAdminFormSecurityNote);
        buttonResetPassword = findViewById(R.id.buttonAdminFormResetPassword);
        buttonDelete = findViewById(R.id.buttonAdminFormDelete);
        buttonSave = findViewById(R.id.buttonAdminFormSave);
        textError = findViewById(R.id.textAdminUserFormError);
        progressView = findViewById(R.id.progressAdminUserForm);
        editName = findViewById(R.id.editAdminFormName);
        editEmail = findViewById(R.id.editAdminFormEmail);
        editPhone = findViewById(R.id.editAdminFormPhone);
        editPassword = findViewById(R.id.editAdminFormPassword);
        inputLayoutPassword = findViewById(R.id.inputLayoutAdminFormPassword);
        spinnerRole = findViewById(R.id.spinnerAdminFormRole);
        spinnerTeam = findViewById(R.id.spinnerAdminFormTeam);
        radioActive = findViewById(R.id.radioAdminFormActive);
        radioLocked = findViewById(R.id.radioAdminFormLocked);

        editName.setText(getIntent().getStringExtra(EXTRA_FULL_NAME));
        editEmail.setText(getIntent().getStringExtra(EXTRA_EMAIL));
        editPhone.setText(getIntent().getStringExtra(EXTRA_PHONE));
        radioActive.setChecked(!"LOCKED".equalsIgnoreCase(preselectedStatus));
        radioLocked.setChecked("LOCKED".equalsIgnoreCase(preselectedStatus));
    }

    private void bindStaticState() {
        boolean editMode = isEditMode();
        textTitle.setText(editMode ? R.string.admin_user_form_edit_title : R.string.admin_user_form_create_title);
        textSummaryFlag.setText(editMode ? R.string.admin_user_form_summary_existing : R.string.admin_user_form_summary_new);
        textSecurityNote.setText(editMode ? R.string.admin_user_form_security_note_edit : R.string.admin_user_form_security_note_create);
        buttonSave.setText(editMode ? R.string.admin_user_form_save : R.string.admin_user_form_create);
        inputLayoutPassword.setVisibility(editMode ? View.GONE : View.VISIBLE);
        buttonDelete.setVisibility(editMode ? View.VISIBLE : View.GONE);
        buttonResetPassword.setVisibility(editMode ? View.VISIBLE : View.GONE);
        updateSummary();
    }

    private void bindActions() {
        editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateSummary(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTeamState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateTeamState();
            }
        });

        findViewById(R.id.buttonAdminFormCancel).setOnClickListener(v -> finish());
        buttonSave.setOnClickListener(v -> submitForm());
        buttonDelete.setOnClickListener(v -> confirmDelete());
        buttonResetPassword.setOnClickListener(v -> showResetPasswordDialog());
        radioActive.setOnClickListener(v -> updateSummary());
        radioLocked.setOnClickListener(v -> updateSummary());
    }

    private void loadRoles() {
        repository.loadRoles(new RepositoryCallback<List<AdminRoleOption>>() {
            @Override
            public void onSuccess(List<AdminRoleOption> data) {
                roles.clear();
                if (data != null) {
                    roles.addAll(data);
                }
                List<String> labels = new ArrayList<>();
                labels.add(getString(R.string.admin_user_form_role_label));
                for (AdminRoleOption role : roles) {
                    labels.add(role.getCode().toUpperCase(Locale.ROOT));
                }
                spinnerRole.setAdapter(new ArrayAdapter<>(AdminUserFormActivity.this, android.R.layout.simple_spinner_dropdown_item, labels));
                if (preselectedRoleId > 0) {
                    for (int i = 0; i < roles.size(); i++) {
                        if (roles.get(i).getId() == preselectedRoleId) {
                            spinnerRole.setSelection(i + 1);
                            break;
                        }
                    }
                }
                updateTeamState();
            }

            @Override
            public void onError(String message) {
                showShortToast(message == null ? getString(R.string.admin_user_form_save_error) : message);
            }
        });
    }

    private void loadTeams() {
        repository.loadTeams(new RepositoryCallback<List<AdminTeamOption>>() {
            @Override
            public void onSuccess(List<AdminTeamOption> data) {
                teams.clear();
                if (data != null) {
                    teams.addAll(data);
                }
                List<String> labels = new ArrayList<>();
                labels.add(getString(R.string.admin_user_form_team_label));
                for (AdminTeamOption team : teams) {
                    labels.add(team.getName());
                }
                spinnerTeam.setAdapter(new ArrayAdapter<>(AdminUserFormActivity.this, android.R.layout.simple_spinner_dropdown_item, labels));
                spinnerTeam.setSelection(0);
                updateTeamState();
            }

            @Override
            public void onError(String message) {
                textTeamHint.setText(message == null ? getString(R.string.admin_user_form_team_empty) : message);
            }
        });
    }

    private void updateSummary() {
        String name = safe(editName.getText() == null ? "" : editName.getText().toString());
        if (name.isEmpty()) {
            name = getString(R.string.admin_users_role_unknown);
        }
        textSummaryName.setText(name);
        textSummaryBadge.setText(initials(name));
        boolean active = radioActive.isChecked();
        textSummaryStatus.setText(active ? R.string.admin_user_form_status_active : R.string.admin_user_form_status_locked);
        textSummaryStatus.setBackgroundResource(active ? R.drawable.bg_chip_success : R.drawable.bg_chip_danger);
        textSummaryStatus.setTextColor(getResources().getColor(active ? R.color.success : R.color.danger, null));
    }

    private void updateTeamState() {
        AdminRoleOption role = getSelectedRole();
        boolean requiresTeam = role != null && ("RESCUER".equalsIgnoreCase(role.getCode()) || "COORDINATOR".equalsIgnoreCase(role.getCode()));
        boolean canEditTeam = requiresTeam && !isEditMode();
        spinnerTeam.setEnabled(canEditTeam);
        if (!requiresTeam) {
            textTeamHint.setText(R.string.admin_user_form_team_disabled);
        } else if (isEditMode()) {
            textTeamHint.setText(R.string.admin_user_form_team_edit_note);
        } else if (teams.isEmpty()) {
            textTeamHint.setText(R.string.admin_user_form_team_empty);
        } else {
            textTeamHint.setText(R.string.admin_user_form_team_select_note);
        }
    }

    private void submitForm() {
        String fullName = safe(editName.getText() == null ? "" : editName.getText().toString());
        String email = safe(editEmail.getText() == null ? "" : editEmail.getText().toString());
        String phone = safe(editPhone.getText() == null ? "" : editPhone.getText().toString());
        String password = safe(editPassword.getText() == null ? "" : editPassword.getText().toString());
        AdminRoleOption role = getSelectedRole();
        if (fullName.isEmpty()) {
            showShortToast(getString(R.string.admin_user_form_missing_name));
            return;
        }
        if (role == null) {
            showShortToast(getString(R.string.admin_user_form_missing_role));
            return;
        }
        if (!isEditMode() && password.isEmpty()) {
            showShortToast(getString(R.string.admin_user_form_missing_password));
            return;
        }

        String status = radioLocked.isChecked() ? "LOCKED" : "ACTIVE";
        setLoading(true);
        if (isEditMode()) {
            repository.updateUser(userId, fullName, email, phone, role.getId(), status, saveCallback());
        } else {
            repository.createUser(fullName, email, phone, password, role.getId(), getSelectedTeamId(role), saveCallback());
        }
    }

    private RepositoryCallback<String> saveCallback() {
        return new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                setLoading(false);
                showShortToast(data);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText(message == null ? getString(R.string.admin_user_form_save_error) : message);
            }
        };
    }

    private void showResetPasswordDialog() {
        if (!isEditMode()) {
            return;
        }
        EditText input = new EditText(this);
        input.setHint(R.string.admin_users_reset_hint);
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_users_reset_title)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String password = safe(input.getText() == null ? "" : input.getText().toString());
                    if (password.isEmpty()) {
                        showShortToast(getString(R.string.admin_users_reset_hint));
                        return;
                    }
                    repository.resetPassword(userId, password, new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            showShortToast(data);
                        }

                        @Override
                        public void onError(String message) {
                            showShortToast(message == null ? getString(R.string.admin_user_form_save_error) : message);
                        }
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void confirmDelete() {
        if (!isEditMode()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.admin_user_form_delete_confirm)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    setLoading(true);
                    repository.deleteUser(userId, new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            setLoading(false);
                            showShortToast(data);
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            setLoading(false);
                            textError.setVisibility(View.VISIBLE);
                            textError.setText(message == null ? getString(R.string.admin_user_form_save_error) : message);
                        }
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        buttonSave.setEnabled(!loading);
        buttonDelete.setEnabled(!loading);
        buttonResetPassword.setEnabled(!loading);
    }

    private boolean isEditMode() {
        return userId > 0L;
    }

    private AdminRoleOption getSelectedRole() {
        int position = spinnerRole.getSelectedItemPosition() - 1;
        if (position < 0 || position >= roles.size()) {
            return null;
        }
        return roles.get(position);
    }

    private Long getSelectedTeamId(AdminRoleOption role) {
        if (role == null) {
            return null;
        }
        boolean requiresTeam = "RESCUER".equalsIgnoreCase(role.getCode()) || "COORDINATOR".equalsIgnoreCase(role.getCode());
        if (!requiresTeam || isEditMode()) {
            return null;
        }
        int position = spinnerTeam.getSelectedItemPosition() - 1;
        if (position < 0 || position >= teams.size()) {
            return null;
        }
        return teams.get(position).getId();
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "ND";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
