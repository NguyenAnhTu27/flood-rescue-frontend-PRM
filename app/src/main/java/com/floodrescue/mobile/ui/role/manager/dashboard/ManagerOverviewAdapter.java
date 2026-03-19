package com.floodrescue.mobile.ui.role.manager.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.ManagerDashboardState;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ManagerOverviewAdapter extends RecyclerView.Adapter<ManagerOverviewAdapter.ViewHolder> {

    private final List<ManagerDashboardState.OverviewItem> items = new ArrayList<>();

    public void submit(List<ManagerDashboardState.OverviewItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_overview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDashboardState.OverviewItem item = items.get(position);
        holder.label.setText(item.getLabel());
        holder.value.setText(item.getValue());
        holder.unit.setText(item.getUnit());
        holder.sub.setText(item.getSub() == null ? "" : item.getSub());
        holder.sub.setTextColor(ManagerUi.resolveColor(holder.itemView.getContext(), item.getColor()));
        holder.sub.setVisibility(item.getSub() == null || item.getSub().trim().isEmpty() ? View.GONE : View.VISIBLE);
        holder.card.setCardBackgroundColor(item.isHighlighted()
                ? holder.itemView.getContext().getColor(R.color.info_soft)
                : holder.itemView.getContext().getColor(R.color.white));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView label;
        final TextView value;
        final TextView unit;
        final TextView sub;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            label = itemView.findViewById(R.id.textOverviewLabel);
            value = itemView.findViewById(R.id.textOverviewValue);
            unit = itemView.findViewById(R.id.textOverviewUnit);
            sub = itemView.findViewById(R.id.textOverviewSub);
        }
    }
}
