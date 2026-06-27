package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my_custom_calenda_and_room.R;
import java.util.ArrayList;
import java.util.List;

public class SavedQueryAdapter extends RecyclerView.Adapter<SavedQueryAdapter.ViewHolder> {
    private List<SavedQuery> queries = new ArrayList<>();
    private OnQueryClickListener listener;
    private int selectedPosition = -1;

    public interface OnQueryClickListener {
        void onQueryClick(SavedQuery savedQuery);
        void onQueryLongClick(String query);
    }

    public SavedQueryAdapter(OnQueryClickListener listener) {
        this.listener = listener;
    }

    public void setQueries(List<SavedQuery> queries) {
        this.queries = queries;
        this.selectedPosition = -1; // 데이터 갱신 시 선택 해제
        notifyDataSetChanged();
    }

    public SavedQuery getSelectedQuery() {
        if (selectedPosition != -1 && selectedPosition < queries.size()) {
            return queries.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (80 * parent.getContext().getResources().getDisplayMetrics().density));
        } else {
            params.height = (int) (80 * parent.getContext().getResources().getDisplayMetrics().density);
        }
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedQuery savedQuery = queries.get(position);
        holder.textView.setText(savedQuery.getQuery());

        // 선택 표시 테두리 설정
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT);
        if (selectedPosition == position) {
            border.setStroke(5, Color.BLACK); // 선택 시 검은색 테두리
        } else {
            border.setStroke(0, Color.TRANSPARENT);
        }
        holder.itemView.setBackground(border);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onQueryClick(savedQuery);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onQueryLongClick(savedQuery.getQuery());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return queries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
