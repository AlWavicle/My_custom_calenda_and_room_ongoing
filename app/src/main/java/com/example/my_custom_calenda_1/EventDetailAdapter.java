package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my_custom_calenda_and_room.R;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventDetailAdapter extends RecyclerView.Adapter<EventDetailAdapter.ViewHolder> {
    private List<SelectSendCalenderModel> events;
    private OnEventEditListener listener;
    private LocalDate selectedDate;

    public interface OnEventEditListener {
        void onEventEdit(SelectSendCalenderModel event);
        void onCheckChanged(SelectSendCalenderModel event, boolean isChecked);
        void onEventSelected(SelectSendCalenderModel event);
    }

    public EventDetailAdapter(List<SelectSendCalenderModel> events, LocalDate selectedDate, OnEventEditListener listener) {
        this.events = events;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_detail_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectSendCalenderModel event = events.get(position);

        // 테두리 설정
        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setColor(Color.WHITE);

        if (OuterCalendarAdapter.selectedEventId != -1 && OuterCalendarAdapter.selectedEventId == event.getId()) {
            int strokeColor = event.getColor();
            if (strokeColor == 0 || strokeColor == Color.WHITE) strokeColor = Color.BLUE;
            border.setStroke(10, strokeColor);
        } else {
            border.setStroke(2, Color.LTGRAY);
        }
        holder.itemView.setBackground(border);

        View.OnClickListener clickListener = v -> {
            OuterCalendarAdapter.selectedEventId = event.getId();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onEventSelected(event);
            }
        };
        holder.itemView.setOnClickListener(clickListener);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(event.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckChanged(event, isChecked);
            }
        });

        // 태그 처리
        String tag = "";
        int isWithinType = event.isWithin(selectedDate);
        if (isWithinType == 1) tag = "[시작]";
        else if (isWithinType == 2) tag = "[기한]";
        else if (isWithinType == 3) tag = "[일정]";
        else tag = "[기간]";

        SpannableStringBuilder ssb = new SpannableStringBuilder(tag);
        int eventColor = event.getColor();
        if (eventColor == 0) eventColor = Color.GRAY;
        ssb.setSpan(new BackgroundColorSpan(eventColor), 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tagText.setText(ssb);

        // daydiff 계산
        if (selectedDate != null) {
            LocalDate start = event.getStartDate();
            LocalDate end = event.getEndDate();
            String diffStr = "";
            if (start != null && end == null) {
                diffStr = String.valueOf(ChronoUnit.DAYS.between(selectedDate, start));
            } else if (start == null && end == null) {
                diffStr = "null";
            } else {
                // 종료일이 있는 경우 (end != null)
                if (end != null) {
                    diffStr = String.valueOf(ChronoUnit.DAYS.between(selectedDate, end));
                } else {
                    diffStr = "null";
                }
            }
            holder.dayDiffText.setText(diffStr);
        }

        holder.oneText.setText(event.getOne());
        holder.twoText.setText(event.getTwo());
        holder.threeText.setText(event.getThree());
        holder.nameText.setText(event.getName());
        holder.commentText.setText(event.getComment());

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEventEdit(event);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tagText, dayDiffText, oneText, twoText, threeText, nameText, commentText;

        ViewHolder(View v) {
            super(v);
            checkBox = v.findViewById(R.id.detail_check);
            tagText = v.findViewById(R.id.detail_tag);
            dayDiffText = v.findViewById(R.id.detail_daydiff);
            oneText = v.findViewById(R.id.detail_one);
            twoText = v.findViewById(R.id.detail_two);
            threeText = v.findViewById(R.id.detail_three);
            nameText = v.findViewById(R.id.detail_name);
            commentText = v.findViewById(R.id.detail_comment);
        }
    }
}
