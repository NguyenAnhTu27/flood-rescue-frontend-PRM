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

import java.util.ArrayList;
import java.util.List;

public class ManagerInventorySummaryAdapter extends RecyclerView.Adapter<ManagerInventorySummaryAdapter.ViewHolder> {

    private final List<ManagerDashboardState.InventorySummaryItem> items = new ArrayList<>();

    public void submit(List<ManagerDashboardState.InventorySummaryItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_inventory_summary, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDashboardState.InventorySummaryItem item = items.get(position);
        holder.label.setText(item.getLabel());
        holder.value.setText(item.getValue());
        holder.value.setTextColor(ManagerUi.resolveColor(holder.itemView.getContext(), item.getColor()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView label;
        final TextView value;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.textSummaryLabel);
            value = itemView.findViewById(R.id.textSummaryValue);
        }
    }
}
