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

public class ManagerTransactionAdapter extends RecyclerView.Adapter<ManagerTransactionAdapter.ViewHolder> {

    private final List<ManagerDashboardState.TransactionItem> items = new ArrayList<>();

    public void submit(List<ManagerDashboardState.TransactionItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDashboardState.TransactionItem item = items.get(position);
        ManagerUi.styleTag(holder.type, item.getTypeLabel(), item.getTypeColor(), false);
        ManagerUi.styleTag(holder.status, ManagerUi.documentStatusLabel(item.getStatusLabel()), item.getStatusColor(), false);
        holder.code.setText(item.getCode());
        holder.destination.setText(item.getDestination());
        holder.time.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView type;
        final TextView status;
        final TextView code;
        final TextView destination;
        final TextView time;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.textTransactionType);
            status = itemView.findViewById(R.id.textTransactionStatus);
            code = itemView.findViewById(R.id.textTransactionCode);
            destination = itemView.findViewById(R.id.textTransactionDestination);
            time = itemView.findViewById(R.id.textTransactionTime);
        }
    }
}
