package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my_custom_calenda_and_room.R;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InnerDayAdapter extends RecyclerView.Adapter<InnerDayAdapter.DayViewHolder> {
    private WeekRow weekRow;
    private int parentWeekPosition;
    private List<LocalDate> seldate; // 🚀 추가
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(LocalDate date, int parentWeekPosition,
                        ArrayList<SelectSendCalenderModel> eventsOnDay,
                        ArrayList<Integer> eventsIndex);
    }

    public InnerDayAdapter(WeekRow weekRow, int parentWeekPosition, List<LocalDate> seldate, OnDayClickListener listener) {
        this.weekRow = weekRow;
        this.parentWeekPosition = parentWeekPosition;
        this.seldate = seldate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_cell, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        LocalDate date = weekRow.days.get(position);
        holder.highlighterLayout.removeAllViews();

        if (date != null) {
            holder.dayText.setText(String.valueOf(date.getDayOfMonth()));

            // 1. [사용자 선택 하이라이트] 빨간색 배경 추가
            if (seldate != null && seldate.contains(date)) {
                View redView = new View(holder.itemView.getContext());
                redView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 
                        ViewGroup.LayoutParams.MATCH_PARENT));
                redView.setBackgroundColor(Color.parseColor("#44FF0000")); // 투명한 빨간색
                holder.highlighterLayout.addView(redView);
            }

            // 2. [일정 마커] 기존 로직
            ArrayList<SelectSendCalenderModel> eventsOnDay = new ArrayList<>();
            ArrayList<Integer> eventsIndex = new ArrayList<>();

            if (OuterCalendarAdapter.sSCModel != null) {
                int i = 0;
                for (SelectSendCalenderModel model : OuterCalendarAdapter.sSCModel) {
                    int swicher = model.isWithin(date);
                    if (swicher != 0) {
                        eventsIndex.add(i);
                        eventsOnDay.add(model);
/*                        //
                        // 간단한 점 마커 예시 (나중에 커스텀 가능)
                        View dot = new View(holder.itemView.getContext());
                        dot.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
                        dot.setBackgroundColor(model.getColor());
                        holder.highlighterLayout.addView(dot);
                        //***/
                    }
                    i++;
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDayClick(date, parentWeekPosition, eventsOnDay, eventsIndex);
                }
            });

        } else {
            holder.dayText.setText("");
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() { return 7; }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        LinearLayout highlighterLayout;
        public DayViewHolder(View v) {
            super(v);
            dayText = v.findViewById(R.id.dayText);
            highlighterLayout = v.findViewById(R.id.highlighterLayout);
        }
    }
}
