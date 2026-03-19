package com.floodrescue.mobile.ui.role.rescuer.task.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerTaskItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskAdapter extends RecyclerView.Adapter<RescuerTaskAdapter.TaskViewHolder> {

    public interface Listener {
        void onOpenTask(RescuerTaskItem item);
    }

    private final Listener listener;
    private final List<RescuerTaskItem> items = new ArrayList<>();

    public RescuerTaskAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RescuerTaskItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        RescuerTaskItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getRequestCode()));
        holder.textPriority.setText(mapPriority(holder.itemView, item));
        applyPriorityStyle(holder.textPriority, holder.itemView, item);
        holder.textCitizen.setText(resolveHeadline(item));
        holder.textTime.setText(formatTime(item.getUpdatedAt()));
        holder.textAddress.setText(isBlank(item.getAddress())
                ? holder.itemView.getContext().getString(R.string.citizen_dashboard_address_placeholder)
                : item.getAddress().trim());
        holder.textDescription.setText(isBlank(item.getDescription())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_list_description_placeholder)
                : item.getDescription().trim());
        holder.textPeople.setText(holder.itemView.getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(item.getPeopleCount(), 0),
                Math.max(item.getPeopleCount(), 0)
        ));
        holder.textVerified.setText(item.isLocationVerified()
                ? holder.itemView.getContext().getString(R.string.rescuer_task_list_verified)
                : holder.itemView.getContext().getString(R.string.rescuer_task_list_unverified));
        holder.textVerified.setBackgroundResource(item.isLocationVerified() ? R.drawable.bg_chip_success : R.drawable.bg_chip_soft);
        holder.textVerified.setTextColor(holder.itemView.getContext().getColor(
                item.isLocationVerified() ? R.color.success : R.color.text_secondary
        ));

        holder.textStatus.setText(mapStatus(holder.itemView, item.getStatus()));
        applyStatusStyle(holder.textStatus, holder.itemView, item.getStatus());
        holder.textGroupMeta.setText(buildGroupMeta(holder.itemView, item));
        holder.itemView.setOnClickListener(v -> listener.onOpenTask(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String resolveHeadline(RescuerTaskItem item) {
        if (!isBlank(item.getDescription())) {
            return item.getDescription().trim();
        }
        if (!isBlank(item.getCitizenName())) {
            return item.getCitizenName().trim();
        }
        return "Nhiệm vụ cứu hộ";
    }

    private String buildGroupMeta(View view, RescuerTaskItem item) {
        String groupCode = isBlank(item.getTaskGroupCode()) ? "TG" : item.getTaskGroupCode().trim();
        String citizen = isBlank(item.getCitizenName()) ? item.getCitizenPhone() : item.getCitizenName().trim();
        if (isBlank(citizen)) {
            return view.getContext().getString(R.string.rescuer_task_list_group_meta, groupCode);
        }
        return view.getContext().getString(R.string.rescuer_task_list_group_meta_with_citizen, groupCode, citizen);
    }

    private String mapPriority(View view, RescuerTaskItem item) {
        if (item.isEmergency()) {
            return view.getContext().getString(R.string.rescuer_task_list_quick_emergency);
        }
        String priority = item.getPriority() == null ? "" : item.getPriority().trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(priority)) {
            return view.getContext().getString(R.string.rescuer_task_list_priority_high);
        }
        if ("LOW".equals(priority)) {
            return view.getContext().getString(R.string.rescuer_task_list_priority_low);
        }
        return view.getContext().getString(R.string.rescuer_task_list_priority_medium);
    }

    private void applyPriorityStyle(TextView view, View root, RescuerTaskItem item) {
        if (item.isEmergency()) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        String priority = item.getPriority() == null ? "" : item.getPriority().trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(priority)) {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(root.getContext().getColor(R.color.warning));
            return;
        }
        if ("LOW".equals(priority)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(root.getContext().getColor(R.color.accent_dark));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_soft);
        view.setTextColor(root.getContext().getColor(R.color.text_secondary));
    }

    private String mapStatus(View view, String status) {
        String safe = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if ("IN_PROGRESS".equals(safe)) {
            return view.getContext().getString(R.string.rescuer_task_list_status_progress);
        }
        if ("COMPLETED".equals(safe)) {
            return view.getContext().getString(R.string.rescuer_task_list_status_done);
        }
        if ("CANCELLED".equals(safe)) {
            return view.getContext().getString(R.string.citizen_request_status_cancelled);
        }
        return view.getContext().getString(R.string.rescuer_task_list_status_pending);
    }

    private void applyStatusStyle(TextView view, View root, String status) {
        String safe = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if ("IN_PROGRESS".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(root.getContext().getColor(R.color.accent_dark));
            return;
        }
        if ("COMPLETED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        if ("CANCELLED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(root.getContext().getColor(R.color.warning));
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#RESC";
        }
        String safe = code.trim();
        return safe.startsWith("#") ? safe : ("#" + safe);
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

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textPriority;
        final TextView textCitizen;
        final TextView textTime;
        final TextView textAddress;
        final TextView textDescription;
        final TextView textPeople;
        final TextView textVerified;
        final TextView textStatus;
        final TextView textGroupMeta;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textRescuerTaskCode);
            textPriority = itemView.findViewById(R.id.textRescuerTaskPriority);
            textCitizen = itemView.findViewById(R.id.textRescuerTaskCitizen);
            textTime = itemView.findViewById(R.id.textRescuerTaskTime);
            textAddress = itemView.findViewById(R.id.textRescuerTaskAddress);
            textDescription = itemView.findViewById(R.id.textRescuerTaskDescription);
            textPeople = itemView.findViewById(R.id.textRescuerTaskPeople);
            textVerified = itemView.findViewById(R.id.textRescuerTaskVerified);
            textStatus = itemView.findViewById(R.id.textRescuerTaskStatus);
            textGroupMeta = itemView.findViewById(R.id.textRescuerTaskGroupMeta);
        }
    }
}
