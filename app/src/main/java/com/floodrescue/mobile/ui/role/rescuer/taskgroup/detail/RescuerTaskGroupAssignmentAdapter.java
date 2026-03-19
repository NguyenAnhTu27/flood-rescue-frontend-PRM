package com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupDetailState;

import java.util.ArrayList;
import java.util.List;

public class RescuerTaskGroupAssignmentAdapter extends RecyclerView.Adapter<RescuerTaskGroupAssignmentAdapter.AssignmentViewHolder> {

    private final List<RescuerTaskGroupDetailState.AssignmentItem> items = new ArrayList<>();

    public void submit(List<RescuerTaskGroupDetailState.AssignmentItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_group_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        RescuerTaskGroupDetailState.AssignmentItem item = items.get(position);
        holder.textTeam.setText(isBlank(item.getTeamName()) ? "Đội cứu hộ" : item.getTeamName().trim());
        holder.textAsset.setText(holder.itemView.getContext().getString(
                R.string.rescuer_task_group_detail_assignment_asset,
                isBlank(item.getAssetCode()) ? "--" : item.getAssetCode().trim(),
                isBlank(item.getAssetName()) ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_assignment_none) : item.getAssetName().trim()
        ));
        holder.textMeta.setText(holder.itemView.getContext().getString(
                R.string.rescuer_task_group_detail_assignment_meta,
                isBlank(item.getAssignedByName()) ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_actor_default) : item.getAssignedByName().trim(),
                formatTime(item.getAssignedAt())
        ));
        holder.textState.setText(item.isActive()
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_assignment_active)
                : holder.itemView.getContext().getString(R.string.status_offline));
        holder.textState.setBackgroundResource(item.isActive() ? R.drawable.bg_chip_success : R.drawable.bg_chip_soft);
        holder.textState.setTextColor(holder.itemView.getContext().getColor(item.isActive() ? R.color.success : R.color.text_secondary));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatTime(String raw) {
        if (isBlank(raw)) {
            return "vừa xong";
        }
        return raw.trim().replace('T', ' ');
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        final TextView textTeam;
        final TextView textState;
        final TextView textAsset;
        final TextView textMeta;

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textTeam = itemView.findViewById(R.id.textGroupAssignmentTeam);
            textState = itemView.findViewById(R.id.textGroupAssignmentState);
            textAsset = itemView.findViewById(R.id.textGroupAssignmentAsset);
            textMeta = itemView.findViewById(R.id.textGroupAssignmentMeta);
        }
    }
}
