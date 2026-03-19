package com.floodrescue.mobile.ui.role.coordinator.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorRequestItem;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordinatorUrgentRequestAdapter extends RecyclerView.Adapter<CoordinatorUrgentRequestAdapter.RequestVH> {

    private final Context context;
    private final List<CoordinatorRequestItem> data = new ArrayList<>();

    public CoordinatorUrgentRequestAdapter(Context context) {
        this.context = context;
    }

    public void submit(List<CoordinatorRequestItem> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_request, parent, false);
        return new RequestVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestVH holder, int position) {
        CoordinatorRequestItem item = data.get(position);
        holder.code.setText(item.getCode());
        holder.title.setText(item.getTitle().isEmpty() ? context.getString(R.string.not_available) : item.getTitle());
        holder.people.setText(String.format(Locale.getDefault(), "%d người", item.getPeople()));
        holder.address.setText(item.getAddress().isEmpty() ? context.getString(R.string.citizen_dashboard_address_placeholder) : item.getAddress());
        holder.status.setText(mapStatus(item.getStatus()));
        styleStatus(holder.status, item.getStatus());
        stylePriority(holder.priority, item.getPriority());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CoordinatorRescueQueueActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class RequestVH extends RecyclerView.ViewHolder {
        TextView code, title, people, address, status, priority;

        RequestVH(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.textRequestCode);
            title = itemView.findViewById(R.id.textRequestTitle);
            people = itemView.findViewById(R.id.textRequestPeople);
            address = itemView.findViewById(R.id.textRequestAddress);
            status = itemView.findViewById(R.id.textRequestStatus);
            priority = itemView.findViewById(R.id.textRequestPriority);
        }
    }

    private String mapStatus(String status) {
        String s = safe(status).toUpperCase(Locale.ROOT);
        switch (s) {
            case "IN_PROGRESS":
                return context.getString(R.string.citizen_request_status_processing);
            case "ASSIGNED":
            case "VERIFIED":
                return context.getString(R.string.citizen_request_status_received);
            case "COMPLETED":
                return context.getString(R.string.citizen_request_status_completed);
            case "CANCELLED":
                return context.getString(R.string.citizen_request_status_cancelled);
            default:
                return context.getString(R.string.citizen_request_status_processing);
        }
    }

    private void styleStatus(TextView view, String status) {
        String s = safe(status).toUpperCase(Locale.ROOT);
        if ("COMPLETED".equals(s)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(context.getColor(R.color.success));
        } else if ("CANCELLED".equals(s)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(context.getColor(R.color.danger));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(context.getColor(R.color.warning));
        }
    }

    private void stylePriority(TextView view, String priority) {
        String p = safe(priority).toUpperCase(Locale.ROOT);
        if ("HIGH".equals(p)) {
            view.setText(R.string.citizen_request_priority_high);
            view.setTextColor(context.getColor(R.color.danger));
        } else if ("LOW".equals(p)) {
            view.setText(R.string.citizen_request_priority_low);
            view.setTextColor(context.getColor(R.color.success));
        } else {
            view.setText(R.string.citizen_request_priority_medium);
            view.setTextColor(context.getColor(R.color.warning));
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
