package com.floodrescue.mobile.ui.role.rescuer.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerDashboardState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerAssetAdapter extends RecyclerView.Adapter<RescuerAssetAdapter.AssetViewHolder> {

    private final List<RescuerDashboardState.AssetItem> items = new ArrayList<>();

    public void submit(List<RescuerDashboardState.AssetItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_asset_summary, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        RescuerDashboardState.AssetItem item = items.get(position);
        holder.textBadge.setText(resolveBadge(item));
        holder.textName.setText(resolveName(item));
        holder.textMeta.setText(holder.itemView.getContext().getString(
                R.string.rescuer_dashboard_asset_type_template,
                resolveType(item)
        ));
        bindStatus(holder.textStatus, item.getStatus(), holder.itemView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String resolveBadge(RescuerDashboardState.AssetItem item) {
        String code = item.getCode();
        if (code != null && !code.trim().isEmpty()) {
            return code.trim().substring(0, 1).toUpperCase(Locale.ROOT);
        }
        String type = resolveType(item);
        return type.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    private String resolveName(RescuerDashboardState.AssetItem item) {
        if (item.getName() != null && !item.getName().trim().isEmpty()) {
            return item.getName().trim();
        }
        if (item.getCode() != null && !item.getCode().trim().isEmpty()) {
            return item.getCode().trim();
        }
        return "Thiết bị cứu hộ";
    }

    private String resolveType(RescuerDashboardState.AssetItem item) {
        String raw = item.getAssetType() == null ? "" : item.getAssetType().trim().toUpperCase(Locale.ROOT);
        if ("BOAT".equals(raw) || "CANOE".equals(raw)) {
            return "Phương tiện thủy";
        }
        if ("RADIO".equals(raw) || "COMMUNICATION".equals(raw)) {
            return "Liên lạc";
        }
        if ("MEDICAL".equals(raw) || "MEDICINE".equals(raw)) {
            return "Y tế";
        }
        if (raw.isEmpty()) {
            return "Thiết bị";
        }
        return raw.replace('_', ' ');
    }

    private void bindStatus(TextView view, String status, View root) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if ("AVAILABLE".equals(normalized) || "READY".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_asset_ready);
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(root.getContext().getColor(R.color.success));
            return;
        }
        if ("INACTIVE".equals(normalized) || "OFFLINE".equals(normalized)) {
            view.setText(R.string.rescuer_dashboard_asset_offline);
            view.setBackgroundResource(R.drawable.bg_chip_soft);
            view.setTextColor(root.getContext().getColor(R.color.text_secondary));
            return;
        }
        view.setText(R.string.rescuer_dashboard_asset_busy);
        view.setBackgroundResource(R.drawable.bg_chip_info);
        view.setTextColor(root.getContext().getColor(R.color.accent_dark));
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        final TextView textBadge;
        final TextView textName;
        final TextView textMeta;
        final TextView textStatus;

        AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            textBadge = itemView.findViewById(R.id.textAssetBadge);
            textName = itemView.findViewById(R.id.textAssetName);
            textMeta = itemView.findViewById(R.id.textAssetMeta);
            textStatus = itemView.findViewById(R.id.textAssetStatus);
        }
    }
}
