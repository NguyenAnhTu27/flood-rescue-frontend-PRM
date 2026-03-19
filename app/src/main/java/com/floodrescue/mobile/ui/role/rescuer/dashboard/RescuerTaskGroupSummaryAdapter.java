package com.floodrescue.mobile.ui.role.rescuer.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerDashboardState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskGroupSummaryAdapter extends RecyclerView.Adapter<RescuerTaskGroupSummaryAdapter.TaskGroupViewHolder> {

    public interface Listener {
        void onOpenTaskGroup(RescuerDashboardState.TaskGroupItem item);
    }

    private final Listener listener;
    private final List<RescuerDashboardState.TaskGroupItem> items = new ArrayList<>();

    public RescuerTaskGroupSummaryAdapter() {
        this(null);
    }

    public RescuerTaskGroupSummaryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RescuerDashboardState.TaskGroupItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_task_group_summary, parent, false);
        return new TaskGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskGroupViewHolder holder, int position) {
        RescuerDashboardState.TaskGroupItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textNote.setText(isBlank(item.getNote())
                ? holder.itemView.getContext().getString(R.string.rescuer_dashboard_task_note_empty)
                : item.getNote().trim());
        holder.textUpdated.setText(holder.itemView.getContext().getString(
                R.string.rescuer_dashboard_task_updated,
                formatUpdated(item.getUpdatedAt())
        ));
        bindStatus(holder.textStatus, item.getStatus(), holder.itemView);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenTaskGroup(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "TG";
        }
        return code.trim();
    }

    private String formatUpdated(String updatedAt) {
        if (updatedAt == null || updatedAt.trim().isEmpty()) {
            return "vừa xong";
        }
        return updatedAt.trim().replace('T', ' ');
    }

    private void bindStatus(TextView view, String status, View root) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if ("DONE".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_task_done);
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        if ("ASSIGNED".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_task_assigned);
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(root.getContext().getColor(R.color.accent_dark));
            return;
        }
        if ("IN_PROGRESS".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_task_progress);
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(root.getContext().getColor(R.color.warning));
            return;
        }
        if ("CANCELLED".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_task_cancelled);
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        view.setText(R.string.rescuer_dashboard_task_new);
        view.setBackgroundResource(R.drawable.bg_chip_soft);
        view.setTextColor(root.getContext().getColor(R.color.text_secondary));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class TaskGroupViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textNote;
        final TextView textUpdated;
        final TextView textStatus;

        TaskGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textTaskGroupCode);
            textNote = itemView.findViewById(R.id.textTaskGroupNote);
            textUpdated = itemView.findViewById(R.id.textTaskGroupUpdated);
            textStatus = itemView.findViewById(R.id.textTaskGroupStatus);
        }
    }
}
