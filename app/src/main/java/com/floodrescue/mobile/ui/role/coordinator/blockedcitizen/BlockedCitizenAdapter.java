package com.floodrescue.mobile.ui.role.coordinator.blockedcitizen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.BlockedCitizenItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockedCitizenAdapter extends RecyclerView.Adapter<BlockedCitizenAdapter.BlockedCitizenViewHolder> {

    public interface Listener {
        void onUnblock(BlockedCitizenItem item);
    }

    private final Listener listener;
    private final List<BlockedCitizenItem> items = new ArrayList<>();

    public BlockedCitizenAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<BlockedCitizenItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BlockedCitizenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocked_citizen, parent, false);
        return new BlockedCitizenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedCitizenViewHolder holder, int position) {
        BlockedCitizenItem item = items.get(position);
        holder.textInitial.setText(initials(item.getFullName()));
        holder.textName.setText(item.getFullName());
        holder.textPhone.setText(item.getPhone());
        holder.textEmail.setText(item.getEmail());
        holder.textReason.setText(item.getBlockedReason());
        holder.buttonUnblock.setOnClickListener(v -> listener.onUnblock(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "CD";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    static class BlockedCitizenViewHolder extends RecyclerView.ViewHolder {
        final TextView textInitial;
        final TextView textName;
        final TextView textPhone;
        final TextView textEmail;
        final TextView textReason;
        final TextView buttonUnblock;

        BlockedCitizenViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitial = itemView.findViewById(R.id.textBlockedCitizenInitial);
            textName = itemView.findViewById(R.id.textBlockedCitizenName);
            textPhone = itemView.findViewById(R.id.textBlockedCitizenPhone);
            textEmail = itemView.findViewById(R.id.textBlockedCitizenEmail);
            textReason = itemView.findViewById(R.id.textBlockedCitizenReason);
            buttonUnblock = itemView.findViewById(R.id.buttonUnblockCitizen);
        }
    }
}
