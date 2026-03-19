package com.floodrescue.mobile.ui.role.manager.relief.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;

import java.util.ArrayList;
import java.util.List;

public class ManagerReliefLineAdapter extends RecyclerView.Adapter<ManagerReliefLineAdapter.ViewHolder> {

    private final List<ManagerReliefDetailState.LineItem> items = new ArrayList<>();

    public void submit(List<ManagerReliefDetailState.LineItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_relief_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerReliefDetailState.LineItem item = items.get(position);
        holder.name.setText(item.getItemName());
        holder.code.setText(item.getItemCode());
        holder.qty.setText(item.getQty() + (item.getUnit() == null || item.getUnit().trim().isEmpty() ? "" : " " + item.getUnit()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView code;
        final TextView qty;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textLineName);
            code = itemView.findViewById(R.id.textLineCode);
            qty = itemView.findViewById(R.id.textLineQty);
        }
    }
}
