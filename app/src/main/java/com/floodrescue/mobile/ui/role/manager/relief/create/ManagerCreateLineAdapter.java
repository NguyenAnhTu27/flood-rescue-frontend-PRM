package com.floodrescue.mobile.ui.role.manager.relief.create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.ui.ItemCategoryOption;

import java.util.ArrayList;
import java.util.List;

public class ManagerCreateLineAdapter extends RecyclerView.Adapter<ManagerCreateLineAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(int position);
    }

    public static class LineDraft {
        private final ItemCategoryOption option;
        private final String qty;

        public LineDraft(ItemCategoryOption option, String qty) {
            this.option = option;
            this.qty = qty;
        }

        public ItemCategoryOption getOption() { return option; }
        public String getQty() { return qty; }
    }

    private final List<LineDraft> items = new ArrayList<>();
    private final OnRemoveListener onRemoveListener;

    public ManagerCreateLineAdapter(OnRemoveListener onRemoveListener) {
        this.onRemoveListener = onRemoveListener;
    }

    public void submit(List<LineDraft> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_create_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LineDraft item = items.get(position);
        holder.name.setText(item.getOption().toString());
        holder.meta.setText("Số lượng: " + item.getQty() + " " + item.getOption().getUnit());
        holder.remove.setOnClickListener(v -> onRemoveListener.onRemove(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView meta;
        final ImageButton remove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textCreateLineName);
            meta = itemView.findViewById(R.id.textCreateLineMeta);
            remove = itemView.findViewById(R.id.buttonRemoveLine);
        }
    }
}
