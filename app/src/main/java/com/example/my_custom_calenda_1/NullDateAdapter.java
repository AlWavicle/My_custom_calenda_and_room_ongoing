package com.example.my_custom_calenda_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my_custom_calenda_and_room.R;
import java.util.ArrayList;
import java.util.List;

public class NullDateAdapter extends RecyclerView.Adapter<NullDateAdapter.ViewHolder> {
    private List<Event> events = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event);
        void onItemLongClick(Event event);
    }

    public NullDateAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_null_date_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvName.setText(event.getName());
        holder.tvComment.setText(event.getComment());
        holder.tvContent.setText(event.getContent());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(event);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onItemLongClick(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvComment, tvContent;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_null_name);
            tvComment = v.findViewById(R.id.tv_null_comment);
            tvContent = v.findViewById(R.id.tv_null_content);
        }
    }
}
