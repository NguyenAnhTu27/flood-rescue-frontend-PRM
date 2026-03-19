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

public class RescuerTaskGroupRequestAdapter extends RecyclerView.Adapter<RescuerTaskGroupRequestAdapter.RequestViewHolder> {

    public interface Listener {
        void onOpenTaskDetail(RescuerTaskGroupDetailState.RequestItem item);
    }

    private final Listener listener;
    private final List<RescuerTaskGroupDetailState.RequestItem> items = new ArrayList<>();

    public RescuerTaskGroupRequestAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<RescuerTaskGroupDetailState.RequestItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_group_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RescuerTaskGroupDetailState.RequestItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textPriority.setText(resolvePriority(holder.itemView, item));
        applyPriorityStyle(holder.textPriority, holder.itemView, item);
        holder.textStatus.setText(mapStatus(holder.itemView, item.getStatus()));
        applyStatusStyle(holder.textStatus, holder.itemView, item.getStatus());
        holder.textHeadline.setText(isBlank(item.getDescription())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_detail_title)
                : item.getDescription().trim());
        holder.textAddress.setText(isBlank(item.getAddress())
                ? holder.itemView.getContext().getString(R.string.citizen_dashboard_address_placeholder)
                : item.getAddress().trim());
        holder.textCitizen.setText(buildCitizen(holder.itemView, item));
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
        holder.buttonView.setOnClickListener(v -> listener.onOpenTaskDetail(item));
        holder.itemView.setOnClickListener(v -> listener.onOpenTaskDetail(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildCitizen(View view, RescuerTaskGroupDetailState.RequestItem item) {
        String name = isBlank(item.getCitizenName())
                ? view.getContext().getString(R.string.coordinator_rescue_detail_citizen_unknown)
                : item.getCitizenName().trim();
        if (isBlank(item.getCitizenPhone())) {
            return name;
        }
        return name + " • " + item.getCitizenPhone().trim();
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#RESC";
        }
        String safe = code.trim();
        return safe.startsWith("#") ? safe : ("#" + safe);
    }

    private String resolvePriority(View view, RescuerTaskGroupDetailState.RequestItem item) {
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

    private void applyPriorityStyle(TextView view, View root, RescuerTaskGroupDetailState.RequestItem item) {
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textPriority;
        final TextView textStatus;
        final TextView textHeadline;
        final TextView textAddress;
        final TextView textCitizen;
        final TextView textPeople;
        final TextView textVerified;
        final TextView buttonView;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textGroupRequestCode);
            textPriority = itemView.findViewById(R.id.textGroupRequestPriority);
            textStatus = itemView.findViewById(R.id.textGroupRequestStatus);
            textHeadline = itemView.findViewById(R.id.textGroupRequestHeadline);
            textAddress = itemView.findViewById(R.id.textGroupRequestAddress);
            textCitizen = itemView.findViewById(R.id.textGroupRequestCitizen);
            textPeople = itemView.findViewById(R.id.textGroupRequestPeople);
            textVerified = itemView.findViewById(R.id.textGroupRequestVerified);
            buttonView = itemView.findViewById(R.id.buttonGroupRequestOpen);
        }
    }
}
