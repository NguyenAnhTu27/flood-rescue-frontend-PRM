package com.floodrescue.mobile.ui.role.admin.user.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.AdminUserItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface Listener {
        void onEdit(AdminUserItem item);
        void onToggleStatus(AdminUserItem item);
        void onResetPassword(AdminUserItem item);
        void onDelete(AdminUserItem item);
    }

    private final Listener listener;
    private final List<AdminUserItem> items = new ArrayList<>();

    public AdminUserAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<AdminUserItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminUserItem item = items.get(position);
        holder.textInitial.setText(initials(item.getFullName()));
        holder.textName.setText(item.getFullName());
        holder.textEmail.setText(emptyFallback(item.getEmail(), holder.itemView.getContext().getString(R.string.admin_users_no_email)));
        holder.textPhone.setText(emptyFallback(item.getPhone(), holder.itemView.getContext().getString(R.string.admin_users_no_phone)));
        holder.textRole.setText(emptyFallback(item.getRoleCode(), holder.itemView.getContext().getString(R.string.admin_users_role_unknown)).toUpperCase(Locale.ROOT));
        holder.textCreated.setText(holder.itemView.getContext().getString(
                R.string.admin_users_created,
                formatDate(item.getCreatedAt())
        ));

        boolean active = "ACTIVE".equalsIgnoreCase(item.getStatus());
        holder.textStatus.setText(active
                ? holder.itemView.getContext().getString(R.string.admin_users_status_active).toUpperCase(Locale.ROOT)
                : holder.itemView.getContext().getString(R.string.admin_users_status_locked).toUpperCase(Locale.ROOT));
        holder.textStatus.setBackgroundResource(active ? R.drawable.bg_chip_success : R.drawable.bg_chip_danger);
        holder.textStatus.setTextColor(holder.itemView.getResources().getColor(active ? R.color.success : R.color.danger, null));

        holder.buttonToggle.setText(active
                ? holder.itemView.getContext().getString(R.string.admin_users_action_lock)
                : holder.itemView.getContext().getString(R.string.admin_users_action_unlock));
        holder.buttonToggle.setBackgroundResource(active ? R.drawable.bg_button_danger_outline : R.drawable.bg_button_success_outline);
        holder.buttonToggle.setTextColor(holder.itemView.getResources().getColor(active ? R.color.danger : R.color.success, null));

        holder.buttonEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.buttonToggle.setOnClickListener(v -> listener.onToggleStatus(item));
        holder.buttonReset.setOnClickListener(v -> listener.onResetPassword(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    private String emptyFallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String formatDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "--";
        }
        String value = raw.trim().replace('T', ' ');
        int dotIndex = value.indexOf('.');
        if (dotIndex > 0) {
            value = value.substring(0, dotIndex);
        }
        return value.length() > 16 ? value.substring(0, 16) : value;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView textInitial;
        final TextView textName;
        final TextView textEmail;
        final TextView textPhone;
        final TextView textRole;
        final TextView textStatus;
        final TextView textCreated;
        final TextView buttonEdit;
        final TextView buttonToggle;
        final TextView buttonReset;
        final TextView buttonDelete;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitial = itemView.findViewById(R.id.textAdminUserInitial);
            textName = itemView.findViewById(R.id.textAdminUserName);
            textEmail = itemView.findViewById(R.id.textAdminUserEmail);
            textPhone = itemView.findViewById(R.id.textAdminUserPhone);
            textRole = itemView.findViewById(R.id.textAdminUserRole);
            textStatus = itemView.findViewById(R.id.textAdminUserStatus);
            textCreated = itemView.findViewById(R.id.textAdminUserCreated);
            buttonEdit = itemView.findViewById(R.id.buttonAdminUserEdit);
            buttonToggle = itemView.findViewById(R.id.buttonAdminUserToggleStatus);
            buttonReset = itemView.findViewById(R.id.buttonAdminUserReset);
            buttonDelete = itemView.findViewById(R.id.buttonAdminUserDelete);
        }
    }
}
