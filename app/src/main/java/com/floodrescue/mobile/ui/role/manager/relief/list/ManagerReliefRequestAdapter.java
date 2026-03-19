package com.floodrescue.mobile.ui.role.manager.relief.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.ManagerReliefRequestItem;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ManagerReliefRequestAdapter extends RecyclerView.Adapter<ManagerReliefRequestAdapter.ViewHolder> {

    public interface OnRequestClickListener {
        void onRequestClick(ManagerReliefRequestItem item);
    }

    private final List<ManagerReliefRequestItem> items = new ArrayList<>();
    private final OnRequestClickListener listener;

    public ManagerReliefRequestAdapter(OnRequestClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<ManagerReliefRequestItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_relief_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerReliefRequestItem item = items.get(position);
        holder.code.setText(item.getCode());
        holder.area.setText(item.getTargetArea());
        holder.meta.setText(item.getCreatedByName() + " • " + item.getLineCount() + " items • " + ManagerUi.prettyDate(item.getUpdatedAt()));
        holder.address.setText(item.getAddress() == null || item.getAddress().trim().isEmpty() ? "Chưa có địa chỉ nhận hàng" : item.getAddress());
        ManagerUi.styleTag(holder.status, ManagerUi.documentStatusLabel(item.getStatus()), ManagerUi.colorForStatus(item.getStatus()), false);
        ManagerUi.styleTag(holder.delivery, ManagerUi.deliveryStatusLabel(item.getDeliveryStatus()), ManagerUi.colorForStatus(item.getDeliveryStatus()), false);
        holder.action.setOnClickListener(v -> listener.onRequestClick(item));
        holder.itemView.setOnClickListener(v -> listener.onRequestClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView code;
        final TextView area;
        final TextView status;
        final TextView meta;
        final TextView address;
        final TextView delivery;
        final MaterialButton action;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.textRequestCode);
            area = itemView.findViewById(R.id.textRequestArea);
            status = itemView.findViewById(R.id.textRequestStatus);
            meta = itemView.findViewById(R.id.textRequestMeta);
            address = itemView.findViewById(R.id.textRequestAddress);
            delivery = itemView.findViewById(R.id.textRequestDelivery);
            action = itemView.findViewById(R.id.buttonRequestAction);
        }
    }
}
