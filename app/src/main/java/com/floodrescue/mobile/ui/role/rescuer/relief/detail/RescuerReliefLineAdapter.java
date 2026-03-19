package com.floodrescue.mobile.ui.role.rescuer.relief.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerReliefDetailState;

import java.util.ArrayList;
import java.util.List;

public class RescuerReliefLineAdapter extends RecyclerView.Adapter<RescuerReliefLineAdapter.LineViewHolder> {

    private final List<RescuerReliefDetailState.LineItem> items = new ArrayList<>();

    public void submit(List<RescuerReliefDetailState.LineItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_relief_line, parent, false);
        return new LineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        RescuerReliefDetailState.LineItem item = items.get(position);
        holder.textName.setText(isBlank(item.getItemName())
                ? holder.itemView.getContext().getString(R.string.rescuer_relief_detail_item_fallback)
                : item.getItemName().trim());
        holder.textCode.setText(isBlank(item.getItemCode()) ? "--" : item.getItemCode().trim());
        holder.textQty.setText(holder.itemView.getContext().getString(
                R.string.rescuer_relief_detail_qty,
                item.getQuantityLabel(),
                isBlank(item.getUnit()) ? "" : item.getUnit().trim()
        ));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class LineViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textCode;
        final TextView textQty;

        LineViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRescuerReliefItemName);
            textCode = itemView.findViewById(R.id.textRescuerReliefItemCode);
            textQty = itemView.findViewById(R.id.textRescuerReliefItemQty);
        }
    }
}
