package com.floodrescue.mobile.ui.role.coordinator.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordinatorTaskGroupAdapter extends RecyclerView.Adapter<CoordinatorTaskGroupAdapter.TaskVH> {

    private final Context context;
    private final List<CoordinatorTaskGroupItem> data = new ArrayList<>();

    public CoordinatorTaskGroupAdapter(Context context) {
        this.context = context;
    }

    public void submit(List<CoordinatorTaskGroupItem> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_task_group, parent, false);
        return new TaskVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        CoordinatorTaskGroupItem item = data.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription().isEmpty() ? context.getString(R.string.not_available) : item.getDescription());
        holder.status.setText(mapStatus(item.getStatus()));
        styleStatus(holder.status, item.getStatus());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TaskVH extends RecyclerView.ViewHolder {
        TextView name, desc, status;

        TaskVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textTaskGroupName);
            desc = itemView.findViewById(R.id.textTaskGroupDesc);
            status = itemView.findViewById(R.id.textTaskGroupStatus);
        }
    }

    private String mapStatus(String status) {
        String s = safe(status).toUpperCase(Locale.ROOT);
        switch (s) {
            case "ONLINE":
            case "ACTIVE":
                return context.getString(R.string.status_online);
            case "OFFLINE":
            case "INACTIVE":
                return context.getString(R.string.status_offline);
            default:
                return context.getString(R.string.status_unknown);
        }
    }

    private void styleStatus(TextView view, String status) {
        String s = safe(status).toUpperCase(Locale.ROOT);
        if ("ONLINE".equals(s) || "ACTIVE".equals(s)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(context.getColor(R.color.success));
        } else if ("OFFLINE".equals(s) || "INACTIVE".equals(s)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(context.getColor(R.color.danger));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(context.getColor(R.color.warning));
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
