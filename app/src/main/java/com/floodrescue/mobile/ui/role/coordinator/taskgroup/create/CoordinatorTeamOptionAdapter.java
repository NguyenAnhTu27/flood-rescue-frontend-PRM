package com.floodrescue.mobile.ui.role.coordinator.taskgroup.create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorTeamOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordinatorTeamOptionAdapter extends RecyclerView.Adapter<CoordinatorTeamOptionAdapter.TeamViewHolder> {

    public interface Listener {
        void onSelectTeam(CoordinatorTeamOption item);
    }

    private final Listener listener;
    private final List<CoordinatorTeamOption> items = new ArrayList<>();
    private long selectedId = -1L;

    public CoordinatorTeamOptionAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<CoordinatorTeamOption> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setSelectedId(long selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public long getSelectedId() {
        return selectedId;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_team_option, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        CoordinatorTeamOption item = items.get(position);
        boolean active = item.getId() == selectedId;

        holder.cardView.setBackgroundResource(active
                ? R.drawable.bg_coordinator_selection_card_active
                : R.drawable.bg_coordinator_selection_card);
        holder.textBadge.setText(initials(item.getName()));
        holder.textName.setText(item.getName());
        holder.textArea.setText(buildSubtitle(item));
        holder.textSelected.setVisibility(active ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> listener.onSelectTeam(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildSubtitle(CoordinatorTeamOption item) {
        String status = item.isOnline() ? "Đang sẵn sàng" : "Ngoại tuyến";
        if (item.getArea() == null || item.getArea().trim().isEmpty()) {
            return status;
        }
        return status + " • " + item.getArea().trim();
    }

    private String initials(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Đ";
        }
        String[] parts = value.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        final View cardView;
        final TextView textBadge;
        final TextView textName;
        final TextView textArea;
        final TextView textSelected;

        TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardTeamOption);
            textBadge = itemView.findViewById(R.id.textTeamOptionBadge);
            textName = itemView.findViewById(R.id.textTeamOptionName);
            textArea = itemView.findViewById(R.id.textTeamOptionArea);
            textSelected = itemView.findViewById(R.id.textTeamOptionSelected);
        }
    }
}
