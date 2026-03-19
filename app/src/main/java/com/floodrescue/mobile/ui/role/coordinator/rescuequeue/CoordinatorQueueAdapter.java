package com.floodrescue.mobile.ui.role.coordinator.rescuequeue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorQueueItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoordinatorQueueAdapter extends RecyclerView.Adapter<CoordinatorQueueAdapter.QueueViewHolder> {

    public interface Listener {
        void onSelectionChanged(int count);
        void onOpenDetail(CoordinatorQueueItem item);
    }

    private final Listener listener;
    private final List<CoordinatorQueueItem> items = new ArrayList<>();
    private final Set<Long> selectedIds = new HashSet<>();

    public CoordinatorQueueAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<CoordinatorQueueItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        selectedIds.retainAll(extractIds(items));
        notifyDataSetChanged();
        listener.onSelectionChanged(selectedIds.size());
    }

    public List<Long> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_queue_request, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        CoordinatorQueueItem item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textCitizenName.setText(item.getCitizenName());
        holder.textPhone.setText(item.getPhoneNumber());
        holder.textPeople.setText(holder.itemView.getContext().getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(item.getPeopleCount(), 0),
                Math.max(item.getPeopleCount(), 0)
        ));
        holder.textAddress.setText(item.getAddress());
        holder.textVerified.setText(item.isLocationVerified()
                ? holder.itemView.getContext().getString(R.string.coordinator_queue_verified)
                : holder.itemView.getContext().getString(R.string.coordinator_queue_unverified));
        holder.textVerified.setBackgroundResource(item.isLocationVerified() ? R.drawable.bg_chip_success : R.drawable.bg_chip_soft);
        holder.textVerified.setTextColor(holder.itemView.getContext().getColor(item.isLocationVerified() ? R.color.success : R.color.text_secondary));
        holder.textPriority.setText(mapPriority(item.getPriority(), holder.itemView));
        applyPriorityStyle(holder.textPriority, item.getPriority(), holder.itemView);
        holder.textMeta.setText(buildMeta(item));

        holder.checkSelect.setOnCheckedChangeListener(null);
        holder.checkSelect.setChecked(selectedIds.contains(item.getId()));
        holder.checkSelect.setOnCheckedChangeListener((buttonView, isChecked) -> toggleSelection(item.getId(), isChecked));
        holder.itemView.setOnClickListener(v -> listener.onOpenDetail(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void toggleSelection(long id, boolean selected) {
        if (selected) {
            selectedIds.add(id);
        } else {
            selectedIds.remove(id);
        }
        listener.onSelectionChanged(selectedIds.size());
    }

    private Set<Long> extractIds(List<CoordinatorQueueItem> source) {
        Set<Long> ids = new HashSet<>();
        for (CoordinatorQueueItem item : source) {
            ids.add(item.getId());
        }
        return ids;
    }

    private String formatCode(String code) {
        String safe = code == null ? "" : code.trim();
        if (safe.startsWith("#")) {
            return safe;
        }
        return "#" + safe;
    }

    private String buildMeta(CoordinatorQueueItem item) {
        String teamName = item.getTeamName() == null ? "" : item.getTeamName().trim();
        if (!teamName.isEmpty()) {
            return teamName.toUpperCase(Locale.ROOT);
        }
        String status = item.getStatus() == null ? "" : item.getStatus().trim().toUpperCase(Locale.ROOT);
        if ("ASSIGNED".equals(status) || "VERIFIED".equals(status)) {
            return "DA DUOC DIEU PHOI";
        }
        if ("COMPLETED".equals(status)) {
            return "DA HOAN THANH";
        }
        return "DANG CHO DOI CUU HO";
    }

    private String mapPriority(String priority, View view) {
        String safe = priority == null ? "" : priority.trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(safe)) {
            return view.getContext().getString(R.string.coordinator_queue_priority_high);
        }
        if ("LOW".equals(safe)) {
            return view.getContext().getString(R.string.coordinator_queue_priority_low);
        }
        return view.getContext().getString(R.string.coordinator_queue_priority_medium);
    }

    private void applyPriorityStyle(TextView view, String priority, View root) {
        String safe = priority == null ? "" : priority.trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(root.getContext().getColor(R.color.danger));
            return;
        }
        if ("LOW".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(root.getContext().getColor(R.color.accent_dark));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(root.getContext().getColor(R.color.warning));
    }

    static class QueueViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkSelect;
        final TextView textCode;
        final TextView textPriority;
        final TextView textCitizenName;
        final TextView textPhone;
        final TextView textPeople;
        final TextView textVerified;
        final TextView textAddress;
        final TextView textMeta;

        QueueViewHolder(@NonNull View itemView) {
            super(itemView);
            checkSelect = itemView.findViewById(R.id.checkSelectRequest);
            textCode = itemView.findViewById(R.id.textQueueCode);
            textPriority = itemView.findViewById(R.id.textQueuePriority);
            textCitizenName = itemView.findViewById(R.id.textQueueCitizenName);
            textPhone = itemView.findViewById(R.id.textQueuePhone);
            textPeople = itemView.findViewById(R.id.textQueuePeople);
            textVerified = itemView.findViewById(R.id.textQueueVerified);
            textAddress = itemView.findViewById(R.id.textQueueAddress);
            textMeta = itemView.findViewById(R.id.textQueueMeta);
        }
    }
}
