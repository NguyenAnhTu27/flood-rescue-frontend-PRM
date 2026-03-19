package com.floodrescue.mobile.ui.role.coordinator.taskgroup.create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.CoordinatorRescueDetailState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectedRescueRequestAdapter extends RecyclerView.Adapter<SelectedRescueRequestAdapter.RequestViewHolder> {

    public interface Listener {
        void onRemoveRequest(CoordinatorRescueDetailState item);
    }

    private final Listener listener;
    private final List<CoordinatorRescueDetailState> items = new ArrayList<>();

    public SelectedRescueRequestAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<CoordinatorRescueDetailState> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator_selected_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        CoordinatorRescueDetailState item = items.get(position);
        holder.textCode.setText(formatCode(item.getCode()));
        holder.textAddress.setText(isBlank(item.getAddress())
                ? holder.itemView.getContext().getString(R.string.coordinator_rescue_detail_address_unknown)
                : item.getAddress());
        holder.textMeta.setText(holder.itemView.getContext().getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(item.getPeopleCount(), 0),
                Math.max(item.getPeopleCount(), 0)
        ));
        holder.textMeta.append(" • " + mapPriority(item.getPriority()));
        holder.buttonRemove.setOnClickListener(v -> listener.onRemoveRequest(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "#RESC";
        }
        return code.startsWith("#") ? code : "#" + code;
    }

    private String mapPriority(String priority) {
        String value = normalize(priority);
        if ("HIGH".equals(value)) {
            return "KHẨN CẤP";
        }
        if ("LOW".equals(value)) {
            return "THẤP";
        }
        return "TRUNG BÌNH";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textAddress;
        final TextView textMeta;
        final TextView buttonRemove;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textSelectedRequestCode);
            textAddress = itemView.findViewById(R.id.textSelectedRequestAddress);
            textMeta = itemView.findViewById(R.id.textSelectedRequestMeta);
            buttonRemove = itemView.findViewById(R.id.buttonRemoveSelectedRequest);
        }
    }
}
