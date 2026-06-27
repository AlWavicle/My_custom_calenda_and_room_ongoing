package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
            if (date.equals(LocalDate.now())) {
                View todayIndicator = new View(holder.itemView.getContext());
                todayIndicator.setLayoutParams(new FrameLayout.LayoutParams(80, 80));
                todayIndicator.setBackgroundResource(R.drawable.selectcell_view); // Assuming a distinct drawable for today
                holder.highlighterLayout.addView(todayIndicator);
            }


            if (date.equals(OuterCalendarAdapter.clicksel)){
                // [레이어 2] 그 위에 검은 테두리(selectcell_view)를 얹어서 중첩시킵니다.
                View selectView = new View(holder.itemView.getContext());
                selectView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                selectView.setBackgroundResource(R.drawable.selectcell_view);
                holder.highlighterLayout.addView(selectView); // 빨간 배경 위에 겹쳐서 추가됨
            }



// 🚀 효과 중첩 구현 영역
            if (seldate != null && seldate.contains(date)) {

                // [레이어 1] 먼저 투명한 빨간색 배경을 밑에 깝니다.
                View redView = new View(holder.itemView.getContext());
                redView.setLayoutParams(new FrameLayout.LayoutParams( // LinearLayout에서 FrameLayout으로 변경
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                redView.setBackgroundColor(Color.parseColor("#44FF0000"));
                holder.highlighterLayout.addView(redView); // 바닥층에 추가됨

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
        FrameLayout highlighterLayout;
        public DayViewHolder(View v) {
            super(v);
            dayText = v.findViewById(R.id.dayText);
            highlighterLayout = v.findViewById(R.id.highlighterLayout);
        }
    }
}
