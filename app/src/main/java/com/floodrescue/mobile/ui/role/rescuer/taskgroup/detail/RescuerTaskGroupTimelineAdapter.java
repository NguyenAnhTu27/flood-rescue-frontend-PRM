package com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail;

import android.graphics.drawable.GradientDrawable;
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
import java.util.Locale;

public class RescuerTaskGroupTimelineAdapter extends RecyclerView.Adapter<RescuerTaskGroupTimelineAdapter.TimelineViewHolder> {

    private final List<RescuerTaskGroupDetailState.TimelineItem> items = new ArrayList<>();

    public void submit(List<RescuerTaskGroupDetailState.TimelineItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_group_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        RescuerTaskGroupDetailState.TimelineItem item = items.get(position);
        boolean isLast = position == items.size() - 1;
        holder.textTitle.setText(mapTitle(holder.itemView, item));
        holder.textTime.setText(formatTime(item.getCreatedAt()));
        holder.textActor.setText(isBlank(item.getActorName())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_actor_default)
                : item.getActorName().trim());
        holder.textNote.setText(isBlank(item.getNote())
                ? holder.itemView.getContext().getString(R.string.rescuer_task_group_detail_timeline_empty_note)
                : item.getNote().trim());
        int color = resolveColor(holder.itemView, item.getEventType());
        holder.viewDot.setBackground(makeCircle(color));
        holder.viewLine.setBackgroundColor(color);
        holder.viewLine.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String mapTitle(View view, RescuerTaskGroupDetailState.TimelineItem item) {
        String eventType = item.getEventType() == null ? "" : item.getEventType().trim().toUpperCase(Locale.ROOT);
        if ("RESCUER_STATUS_CHANGE".equals(eventType)) {
            return view.getContext().getString(R.string.rescuer_task_group_detail_timeline_status);
        }
        if ("RESCUER_EMERGENCY".equals(eventType)) {
            return view.getContext().getString(R.string.rescuer_task_group_detail_timeline_emergency);
        }
        if ("CREATE".equals(eventType) || "CREATED".equals(eventType)) {
            return view.getContext().getString(R.string.rescuer_task_group_detail_timeline_created);
        }
        return view.getContext().getString(R.string.rescuer_task_group_detail_timeline_update);
    }

    private int resolveColor(View view, String eventType) {
        String safe = eventType == null ? "" : eventType.trim().toUpperCase(Locale.ROOT);
        if ("RESCUER_EMERGENCY".equals(safe)) {
            return view.getContext().getColor(R.color.danger);
        }
        if ("RESCUER_STATUS_CHANGE".equals(safe)) {
            return view.getContext().getColor(R.color.accent_dark);
        }
        return view.getContext().getColor(R.color.success);
    }

    private GradientDrawable makeCircle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        return drawable;
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

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        final View viewDot;
        final View viewLine;
        final TextView textTitle;
        final TextView textTime;
        final TextView textActor;
        final TextView textNote;

        TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDot = itemView.findViewById(R.id.viewRescuerTimelineDot);
            viewLine = itemView.findViewById(R.id.viewRescuerTimelineLine);
            textTitle = itemView.findViewById(R.id.textGroupTimelineTitle);
            textTime = itemView.findViewById(R.id.textGroupTimelineTime);
            textActor = itemView.findViewById(R.id.textGroupTimelineActor);
            textNote = itemView.findViewById(R.id.textGroupTimelineNote);
        }
    }
}
