package com.floodrescue.mobile.ui.role.manager.dispatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.ManagerDispatchState;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ManagerDispatchTeamAdapter extends RecyclerView.Adapter<ManagerDispatchTeamAdapter.ViewHolder> {

    public interface OnTeamSelectedListener {
        void onSelected(ManagerDispatchState.TeamItem item);
    }

    private final List<ManagerDispatchState.TeamItem> items = new ArrayList<>();
    private final OnTeamSelectedListener listener;
    private long selectedId;

    public ManagerDispatchTeamAdapter(OnTeamSelectedListener listener) {
        this.listener = listener;
    }

    public void submit(List<ManagerDispatchState.TeamItem> data, long selectedId) {
        items.clear();
        if (data != null) items.addAll(data);
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setSelectedId(long selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_dispatch_team, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDispatchState.TeamItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.area.setText(item.getArea() == null || item.getArea().trim().isEmpty() ? "Khu vực chưa cập nhật" : item.getArea());
        String meta = (item.getDistance() == null ? "Khoảng cách chưa rõ" : String.format(java.util.Locale.US, "%.1f km", item.getDistance()))
                + " • " + (item.getLastUpdate() == null || item.getLastUpdate().trim().isEmpty() ? "Vừa cập nhật" : item.getLastUpdate());
        holder.meta.setText(meta);
        String statusLabel = item.isOnline() ? ("AVAILABLE".equalsIgnoreCase(item.getStatus()) ? "Sẵn sàng" : "Đang bận") : "Offline";
        String colorKey = item.isOnline() ? ("AVAILABLE".equalsIgnoreCase(item.getStatus()) ? "green" : "orange") : "red";
        ManagerUi.styleTag(holder.status, statusLabel, colorKey, false);
        ManagerUi.styleSelectableCard(holder.card, item.getId() == selectedId);
        holder.itemView.setOnClickListener(v -> listener.onSelected(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView name;
        final TextView area;
        final TextView meta;
        final TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardDispatchTeam);
            name = itemView.findViewById(R.id.textDispatchTeamName);
            area = itemView.findViewById(R.id.textDispatchTeamArea);
            meta = itemView.findViewById(R.id.textDispatchTeamMeta);
            status = itemView.findViewById(R.id.textDispatchTeamStatus);
        }
    }
}
