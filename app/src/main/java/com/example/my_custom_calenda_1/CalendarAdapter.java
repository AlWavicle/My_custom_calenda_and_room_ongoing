package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DAY = 0;
    private static final int TYPE_DETAIL = 1;

    private final ArrayList<Object> CopyItems;
    public ArrayList<Event> eventList;
    private final My_OnItemListener MyAL_onItemListener1;

    public ArrayList<LocalDate> seldate;
    public LocalDate oneSelDate;
    public int clickNum;

    public static ArrayList<SelectSendCalenderModel> sSCModel;

    private int detailPosition = -1;

    public interface My_OnItemListener {
        void My_OnItemClick(LocalDate date, ArrayList<SelectSendCalenderModel> events, int position, ArrayList<Integer> eventsindex, int clicknums);
    }

    public CalendarAdapter(ArrayList<LocalDate> dayList, ArrayList<Event> eventList, My_OnItemListener par_my_onItemListener) {
        this.CopyItems = new ArrayList<>(dayList);
        this.eventList = eventList;
        this.MyAL_onItemListener1 = par_my_onItemListener;
        this.seldate = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (CopyItems.get(position) instanceof LocalDate || CopyItems.get(position) == null) {
            return TYPE_DAY;
        } else {
            return TYPE_DETAIL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DAY) {
            View view = inflater.inflate(R.layout.calendar_cell, parent, false);
            return new CalendarViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.event_detail_item, parent, false);
            return new DetailViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CalendarViewHolder) {
            CalendarViewHolder dayHolder = (CalendarViewHolder) holder;
            LocalDate date = (LocalDate) CopyItems.get(position);
            dayHolder.highlighterLayout.removeAllViews();
            dayHolder.itemView.setBackgroundColor(Color.WHITE);

            if (date == null) {
                dayHolder.dayText.setText("");
                dayHolder.itemView.setOnClickListener(null);
            } else {
                for (LocalDate seldate1 : seldate) {
                    if (date.equals(seldate1)) {
                        dayHolder.itemView.setBackgroundColor(Color.RED);
                    }
                }
                dayHolder.dayText.setText(String.valueOf(date.getDayOfMonth()));

                ArrayList<SelectSendCalenderModel> eventsOnDay = new ArrayList<>();
                ArrayList<Integer> eventsIndex = new ArrayList<>();
                int i = 0;
                if (sSCModel != null) {
                    for (SelectSendCalenderModel model : sSCModel) {
                        if (model.isWithin(date)) {
                            eventsIndex.add(i);
                            eventsOnDay.add(model);

                            View highlighter = new View(dayHolder.itemView.getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 25);
                            params.setMargins(0, 2, 0, 2);
                            highlighter.setLayoutParams(params);
                            highlighter.setBackgroundColor(model.getColor());
                            dayHolder.highlighterLayout.addView(highlighter);
                        }
                        i++;
                    }
                }

                dayHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int existingSizeOfSeldate = seldate.size();
                        switch (existingSizeOfSeldate) {
                            case 0:
                                seldate.add((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()));
                                break;
                            case 1:
                                if (seldate.get(0).equals((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()))) {
                                    for (LocalDate date : seldate) {
                                        notifyItemChanged(CopyItems.indexOf(date));
                                    }
                                    seldate.clear();
                                    break;
                                } else {
                                    seldate.add((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()));
                                }
                                break;
                            default:
                                for (LocalDate date : seldate) {
                                    notifyItemChanged(CopyItems.indexOf(date));
                                }
                                seldate.clear();
                                break;
                        }
                        if (existingSizeOfSeldate != seldate.size()) {
                            for (LocalDate date : seldate) {
                                notifyItemChanged(CopyItems.indexOf(date));
                            }
                        }

                        if (oneSelDate != null && oneSelDate.equals(CopyItems.get(dayHolder.getAdapterPosition()))) {
                            clickNum++;
                        } else {
                            clickNum = 1;
                        }
                        oneSelDate = (LocalDate) CopyItems.get(dayHolder.getAdapterPosition());
                        Toast.makeText(dayHolder.itemView.getContext(), "clickNum: " + clickNum + "/eventlist.size(): " + eventList.size(), Toast.LENGTH_SHORT).show();

                        if (MyAL_onItemListener1 != null) {
                            MyAL_onItemListener1.My_OnItemClick(date, eventsOnDay, holder.getAdapterPosition(), eventsIndex, clickNum);
                            showDetail(holder.getAdapterPosition(), eventsOnDay);
                        }
                    }
                });
            }
        } else if (holder instanceof DetailViewHolder) {
            DetailViewHolder detailHolder = (DetailViewHolder) holder;
            ArrayList<SelectSendCalenderModel> events = (ArrayList<SelectSendCalenderModel>) CopyItems.get(position);
            StringBuilder sb = new StringBuilder();
            for (SelectSendCalenderModel e : events) {
                sb.append(e.getName()).append("\n");
            }
            detailHolder.detailTitle.setText("선택한 날짜의 일정");
            detailHolder.detailContent.setText(sb.toString().trim());
        }
    }

    public void showDetail(int dayPosition, ArrayList<SelectSendCalenderModel> events) {
        if (detailPosition != -1) {
            CopyItems.remove(detailPosition);
            notifyItemRemoved(detailPosition);
            if (dayPosition > detailPosition) {
                dayPosition--;
            }
            detailPosition = -1;
        }

        if (events == null || events.isEmpty()) {
            return;
        }

        int row = dayPosition / 7;
        int insertPos = (row + 1) * 7;
        if (insertPos > CopyItems.size()) insertPos = CopyItems.size();

        CopyItems.add(insertPos, events);
        detailPosition = insertPos;
        notifyItemInserted(detailPosition);
    }

    @Override
    public int getItemCount() {
        return CopyItems.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        LinearLayout highlighterLayout;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            highlighterLayout = itemView.findViewById(R.id.highlighterLayout);
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView detailTitle;
        TextView detailContent;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            detailTitle = itemView.findViewById(R.id.detailTitle);
            detailContent = itemView.findViewById(R.id.detailContent);
        }
    }
}
