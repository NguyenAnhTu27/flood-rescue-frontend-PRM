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

public class ManagerInventoryItemAdapter extends RecyclerView.Adapter<ManagerInventoryItemAdapter.ViewHolder> {

    private final List<ManagerDashboardState.InventoryItem> items = new ArrayList<>();

    public void submit(List<ManagerDashboardState.InventoryItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_inventory_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDashboardState.InventoryItem item = items.get(position);
        holder.code.setText(item.getCode());
        holder.name.setText(item.getName());
        holder.category.setText(item.getCategoryName());
        holder.qty.setText(item.getQty() + (item.getUnit() == null || item.getUnit().trim().isEmpty() ? "" : " " + item.getUnit()));
        ManagerUi.styleTag(holder.status, ManagerUi.inventoryStatusLabel(item.getStatusLabel()), ManagerUi.colorForStatus(item.getStatusColor()), false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView code;
        final TextView name;
        final TextView category;
        final TextView qty;
        final TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.textInventoryCode);
            name = itemView.findViewById(R.id.textInventoryName);
            category = itemView.findViewById(R.id.textInventoryCategory);
            qty = itemView.findViewById(R.id.textInventoryQty);
            status = itemView.findViewById(R.id.textInventoryStatus);
        }
    }
}
