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

public class ManagerDispatchRequestAdapter extends RecyclerView.Adapter<ManagerDispatchRequestAdapter.ViewHolder> {

    public interface OnRequestSelectedListener {
        void onSelected(ManagerDispatchState.QueueItem item);
    }

    private final List<ManagerDispatchState.QueueItem> items = new ArrayList<>();
    private final OnRequestSelectedListener listener;
    private long selectedId;

    public ManagerDispatchRequestAdapter(OnRequestSelectedListener listener) {
        this.listener = listener;
    }

    public void submit(List<ManagerDispatchState.QueueItem> data, long selectedId) {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_dispatch_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagerDispatchState.QueueItem item = items.get(position);
        ManagerUi.styleTag(holder.priority, ManagerUi.priorityLabel(item.getPriority()), ManagerUi.colorForStatus(item.getPriority()), false);
        ManagerUi.styleTag(holder.status, ManagerUi.documentStatusLabel(item.getStatus()), ManagerUi.colorForStatus(item.getStatus()), false);
        holder.code.setText(item.getCode());
        holder.meta.setText(item.getPeopleCount() + " người • " + item.getTimeAgo());
        ManagerUi.styleSelectableCard(holder.card, item.getId() == selectedId);
        holder.itemView.setOnClickListener(v -> listener.onSelected(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView priority;
        final TextView status;
        final TextView code;
        final TextView meta;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardDispatchRequest);
            priority = itemView.findViewById(R.id.textDispatchRequestPriority);
            status = itemView.findViewById(R.id.textDispatchRequestStatus);
            code = itemView.findViewById(R.id.textDispatchRequestCode);
            meta = itemView.findViewById(R.id.textDispatchRequestMeta);
        }
    }
}
