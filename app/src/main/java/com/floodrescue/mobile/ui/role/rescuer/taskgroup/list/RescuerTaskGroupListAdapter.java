package com.floodrescue.mobile.ui.role.rescuer.taskgroup.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskGroupListAdapter extends RecyclerView.Adapter<RescuerTaskGroupListAdapter.GroupViewHolder> {

    public interface Listener {
        void onOpenDetail(RescuerTaskGroupListItem item);
    }

    private final Listener listener;
    private final List<RescuerTaskGroupListItem> items = new ArrayList<>();

    public RescuerTaskGroupListAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RescuerTaskGroupListItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_task_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        RescuerTaskGroupListItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textTeamName.setText(isBlank(item.getTeamName())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_list_team_fallback)
                : item.getTeamName().trim());
        holder.textNote.setText(isBlank(item.getNote())
                ? holder.itemView.getContext().getString(R.string.rescuer_dashboard_task_note_empty)
                : item.getNote().trim());
        holder.textRequestCount.setText(String.format(Locale.getDefault(), "%02d", Math.max(item.getRequestCount(), 0)));
        holder.textAssignmentCount.setText(String.format(Locale.getDefault(), "%02d", Math.max(item.getActiveAssignments(), 0)));
        holder.textUpdated.setText(formatTime(item.getUpdatedAt(), item.getCreatedAt()));
        bindStatus(holder.textStatus, item.getStatus(), holder.itemView);
        holder.buttonView.setOnClickListener(v -> listener.onOpenDetail(item));
        holder.itemView.setOnClickListener(v -> listener.onOpenDetail(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindStatus(TextView view, String status, View root) {
        String safe = normalize(status);
        if ("IN_PROGRESS".equals(safe) || "ASSIGNED".equals(safe)) {
            view.setText(R.string.rescuer_task_group_list_status_active);
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        if ("DONE".equals(safe) || "COMPLETED".equals(safe)) {
            view.setText(R.string.rescuer_task_group_list_status_done);
            view.setBackgroundResource(R.drawable.bg_chip_soft);
            view.setTextColor(root.getContext().getColor(R.color.text_secondary));
            return;
        }
        if ("CANCELLED".equals(safe)) {
            view.setText(R.string.citizen_request_status_cancelled);
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        view.setText(R.string.rescuer_task_group_list_status_pending);
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(root.getContext().getColor(R.color.warning));
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#TG";
        }
        String safe = code.trim();
        return safe.startsWith("#") ? safe : ("#" + safe);
    }

    private String formatTime(String updatedAt, String createdAt) {
        String value = !isBlank(updatedAt) ? updatedAt : createdAt;
        if (isBlank(value)) {
            return "vừa xong";
        }
        return value.trim().replace('T', ' ');
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textStatus;
        final TextView textTeamName;
        final TextView textNote;
        final TextView textRequestCount;
        final TextView textAssignmentCount;
        final TextView textUpdated;
        final TextView buttonView;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textRescuerTaskGroupCode);
            textStatus = itemView.findViewById(R.id.textRescuerTaskGroupStatus);
            textTeamName = itemView.findViewById(R.id.textRescuerTaskGroupName);
            textNote = itemView.findViewById(R.id.textRescuerTaskGroupNote);
            textRequestCount = itemView.findViewById(R.id.textRescuerTaskGroupRequestCount);
            textAssignmentCount = itemView.findViewById(R.id.textRescuerTaskGroupAssignmentCount);
            textUpdated = itemView.findViewById(R.id.textRescuerTaskGroupUpdated);
            buttonView = itemView.findViewById(R.id.buttonRescuerTaskGroupView);
        }
    }
}
