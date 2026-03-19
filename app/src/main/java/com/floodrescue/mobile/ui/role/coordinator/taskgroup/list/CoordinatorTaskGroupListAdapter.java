package com.floodrescue.mobile.ui.role.coordinator.taskgroup.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordinatorTaskGroupListAdapter extends RecyclerView.Adapter<CoordinatorTaskGroupListAdapter.GroupViewHolder> {

    public interface Listener {
        void onOpenDetail(CoordinatorTaskGroupListItem item);
    }

    private final Listener listener;
    private final List<CoordinatorTaskGroupListItem> items = new ArrayList<>();

    public CoordinatorTaskGroupListAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<CoordinatorTaskGroupListItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_task_group_list, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        CoordinatorTaskGroupListItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textTitle.setText(isBlank(item.getAssignedTeamName()) ? item.getCode() : item.getAssignedTeamName());
        holder.textTeam.setText(holder.itemView.getContext().getString(
                R.string.coordinator_task_group_team,
                isBlank(item.getAssignedTeamName()) ? "Chưa phân công" : item.getAssignedTeamName()
        ));
        holder.textCreator.setText(holder.itemView.getContext().getString(
                R.string.coordinator_task_group_creator,
                isBlank(item.getCreatedByName()) ? "Hệ thống" : item.getCreatedByName()
        ));
        holder.textUpdated.setText(holder.itemView.getContext().getString(
                R.string.coordinator_task_group_updated,
                formatTime(item.getUpdatedAt(), item.getCreatedAt())
        ));
        holder.textNote.setText(isBlank(item.getNote())
                ? holder.itemView.getContext().getString(R.string.coordinator_task_group_note_empty)
                : item.getNote());
        bindStatus(holder.textStatus, item.getStatus(), holder.itemView);
        holder.buttonView.setOnClickListener(v -> listener.onOpenDetail(item));
        holder.itemView.setOnClickListener(v -> listener.onOpenDetail(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindStatus(TextView view, String status, View root) {
        String normalized = normalize(status);
        if ("DONE".equals(normalized)) {
            view.setText(R.string.coordinator_task_group_status_done);
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        if ("ASSIGNED".equals(normalized) || "IN_PROGRESS".equals(normalized)) {
            view.setText("IN_PROGRESS".equals(normalized)
                    ? R.string.coordinator_task_group_status_in_progress
                    : R.string.coordinator_task_group_status_assigned);
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(root.getContext().getColor(R.color.warning));
            return;
        }
        if ("CANCELLED".equals(normalized)) {
            view.setText(R.string.coordinator_task_group_status_cancelled);
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        view.setText(R.string.coordinator_task_group_status_new);
        view.setBackgroundResource(R.drawable.bg_chip_info);
        view.setTextColor(root.getContext().getColor(R.color.accent_dark));
    }

    private String formatCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "#TG";
        }
        return code.startsWith("#") ? code : "#" + code;
    }

    private String formatTime(String updatedAt, String createdAt) {
        if (!isBlank(updatedAt)) {
            return updatedAt.trim().replace('T', ' ');
        }
        if (!isBlank(createdAt)) {
            return createdAt.trim().replace('T', ' ');
        }
        return "Chưa cập nhật";
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
        final TextView textTitle;
        final TextView textTeam;
        final TextView textCreator;
        final TextView textUpdated;
        final TextView textNote;
        final TextView buttonView;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textTaskGroupCode);
            textStatus = itemView.findViewById(R.id.textTaskGroupStatusChip);
            textTitle = itemView.findViewById(R.id.textTaskGroupTitle);
            textTeam = itemView.findViewById(R.id.textTaskGroupTeam);
            textCreator = itemView.findViewById(R.id.textTaskGroupCreator);
            textUpdated = itemView.findViewById(R.id.textTaskGroupUpdated);
            textNote = itemView.findViewById(R.id.textTaskGroupNote);
            buttonView = itemView.findViewById(R.id.buttonTaskGroupView);
        }
    }
}
