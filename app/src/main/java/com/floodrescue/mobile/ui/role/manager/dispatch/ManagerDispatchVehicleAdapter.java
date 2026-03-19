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

import java.util.ArrayList;
import java.util.List;

public class ManagerDispatchVehicleAdapter extends RecyclerView.Adapter<ManagerDispatchVehicleAdapter.ViewHolder> {

    private final List<ManagerDispatchState.VehicleItem> items = new ArrayList<>();

    public void submit(List<ManagerDispatchState.VehicleItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_dispatch_vehicle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDispatchState.VehicleItem item = items.get(position);
        holder.name.setText(item.getCode() + " • " + item.getName());
        StringBuilder meta = new StringBuilder();
        meta.append(item.getType());
        if (item.getCapacity() != null) meta.append(" • ").append(item.getCapacity()).append(" tải");
        if (item.getLocation() != null && !item.getLocation().trim().isEmpty()) meta.append(" • ").append(item.getLocation());
        holder.meta.setText(meta.toString());
        ManagerUi.styleTag(holder.status, item.isOnline() ? ManagerUi.prettify(item.getStatus()) : "Offline", item.isOnline() ? ManagerUi.colorForStatus(item.getStatus()) : "red", false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView meta;
        final TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textDispatchVehicleName);
            meta = itemView.findViewById(R.id.textDispatchVehicleMeta);
            status = itemView.findViewById(R.id.textDispatchVehicleStatus);
        }
    }
}
