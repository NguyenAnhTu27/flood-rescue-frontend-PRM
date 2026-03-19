package com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupDetailState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskGroupEmergencyAdapter extends RecyclerView.Adapter<RescuerTaskGroupEmergencyAdapter.EmergencyViewHolder> {

    private final List<RescuerTaskGroupDetailState.EmergencyAckItem> items = new ArrayList<>();

    public void submit(List<RescuerTaskGroupDetailState.EmergencyAckItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_group_emergency, parent, false);
        return new EmergencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position) {
        RescuerTaskGroupDetailState.EmergencyAckItem item = items.get(position);
        holder.textCoordinator.setText(isBlank(item.getCoordinatorName())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_actor_default)
                : item.getCoordinatorName().trim());
        holder.textStatus.setText(mapAction(holder.itemView, item.getActionStatus()));
        holder.textStatus.setBackgroundResource(item.isRead() ? R.drawable.bg_chip_success : R.drawable.bg_chip_warning);
        holder.textStatus.setTextColor(holder.itemView.getContext().getColor(item.isRead() ? R.color.success : R.color.warning));
        holder.textRead.setText(item.isRead()
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_ack_read)
                : holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_ack_waiting));
        holder.textRead.setBackgroundResource(item.isRead() ? R.drawable.bg_chip_info : R.drawable.bg_chip_soft);
        holder.textRead.setTextColor(holder.itemView.getContext().getColor(item.isRead() ? R.color.accent_dark : R.color.text_secondary));
        holder.textNote.setText(isBlank(item.getActionNote())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_timeline_empty_note)
                : item.getActionNote().trim());
        holder.textTime.setText(formatTime(item.getAcknowledgedAt()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String mapAction(View view, String actionStatus) {
        String safe = actionStatus == null ? "" : actionStatus.trim().toUpperCase(Locale.ROOT);
        if ("ACCEPTED".equals(safe) || "HANDLED".equals(safe)) {
            return view.getContext().getString(R.string.rescuer_task_group_detail_ack_handled);
        }
        if ("REJECTED".equals(safe)) {
            return view.getContext().getString(R.string.citizen_request_status_cancelled);
        }
        return view.getContext().getString(R.string.rescuer_task_group_detail_ack_pending);
    }

    private String formatTime(String raw) {
        if (isBlank(raw)) {
            return "vừa xong";
        }
        return raw.trim().replace('T', ' ');
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        final TextView textCoordinator;
        final TextView textStatus;
        final TextView textRead;
        final TextView textNote;
        final TextView textTime;

        EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            textCoordinator = itemView.findViewById(R.id.textGroupEmergencyCoordinator);
            textStatus = itemView.findViewById(R.id.textGroupEmergencyStatus);
            textRead = itemView.findViewById(R.id.textGroupEmergencyRead);
            textNote = itemView.findViewById(R.id.textGroupEmergencyNote);
            textTime = itemView.findViewById(R.id.textGroupEmergencyTime);
        }
    }
}
