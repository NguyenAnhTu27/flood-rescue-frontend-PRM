package com.floodrescue.mobile.ui.role.rescuer.teamlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerTeamLocationState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTeamAssetAdapter extends RecyclerView.Adapter<RescuerTeamAssetAdapter.AssetViewHolder> {

    private final List<RescuerTeamLocationState.AssetItem> items = new ArrayList<>();

    public void submit(List<RescuerTeamLocationState.AssetItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_team_asset, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        RescuerTeamLocationState.AssetItem item = items.get(position);
        holder.textCode.setText(isBlank(item.getCode()) ? "--" : item.getCode().trim());
        holder.textName.setText(isBlank(item.getName())
                ? holder.itemView.getContext().getString(R.string.rescuer_team_location_asset_fallback)
                : item.getName().trim());
        holder.textType.setText(holder.itemView.getContext().getString(
                R.string.rescuer_team_location_asset_type,
                isBlank(item.getAssetType()) ? holder.itemView.getContext().getString(R.string.rescuer_team_location_asset_type_unknown) : item.getAssetType().trim()
        ));
        bindStatus(holder.textStatus, item.getStatus(), holder.itemView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindStatus(TextView view, String status, View root) {
        String safe = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if ("IN_USE".equals(safe) || "ASSIGNED".equals(safe)) {
            view.setText(R.string.rescuer_dashboard_asset_busy);
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(root.getContext().getColor(R.color.warning));
            return;
        }
        if ("READY".equals(safe) || "AVAILABLE".equals(safe)) {
            view.setText(R.string.rescuer_dashboard_asset_ready);
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        view.setText(R.string.status_offline);
        view.setBackgroundResource(R.drawable.bg_chip_soft);
        view.setTextColor(root.getContext().getColor(R.color.text_secondary));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        final TextView textCode;
        final TextView textStatus;
        final TextView textName;
        final TextView textType;

        AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            textCode = itemView.findViewById(R.id.textRescuerTeamAssetCode);
            textStatus = itemView.findViewById(R.id.textRescuerTeamAssetStatus);
            textName = itemView.findViewById(R.id.textRescuerTeamAssetName);
            textType = itemView.findViewById(R.id.textRescuerTeamAssetType);
        }
    }
}
