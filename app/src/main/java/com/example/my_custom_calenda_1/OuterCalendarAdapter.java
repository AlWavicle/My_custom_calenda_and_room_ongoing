package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my_custom_calenda_and_room.R;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OuterCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_WEEK_ROW = 0;
    private static final int TYPE_DETAIL = 1;

    public List<Object> items; // WeekRow와 Detail Data 혼합 리스트
    private InnerDayAdapter.OnDayClickListener dayClickListener;

    public static ArrayList<SelectSendCalenderModel> sSCModel;
    public List<LocalDate> seldate = new ArrayList<>(); // 🚀 선택된 날짜 관리 추가
    public int selectionStep = 0; // 🚀 4단계 순환을 위한 변수 추가

    public static LocalDate clicksel = null;

    public OuterCalendarAdapter(List<Object> items, InnerDayAdapter.OnDayClickListener listener) {
        this.items = items;
        this.dayClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof WeekRow ? TYPE_WEEK_ROW : TYPE_DETAIL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_WEEK_ROW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_row, parent, false);
            return new WeekRowViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_item, parent, false);
            return new DetailViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeekRowViewHolder) {
            WeekRowViewHolder weekHolder = (WeekRowViewHolder) holder;
            WeekRow weekRow = (WeekRow) items.get(position);

            // 1. 내부 7일 리사이클러뷰 세팅
            InnerDayAdapter innerAdapter = new InnerDayAdapter(weekRow, position, seldate, dayClickListener); // 🚀 seldate 넘겨줌
            weekHolder.innerRecyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 7));
            weekHolder.innerRecyclerView.setAdapter(innerAdapter);

            // 2. 캔버스에 긴 막대 데이터 넘겨주기
            weekHolder.rowEventLayer.setEvents(weekRow.rowEvents);

        } else if (holder instanceof DetailViewHolder) {
            DetailViewHolder detailHolder = (DetailViewHolder) holder;
            ArrayList<SelectSendCalenderModel> events = (ArrayList<SelectSendCalenderModel>) items.get(position);

            if (events != null && !events.isEmpty()) {
                SpannableStringBuilder ssb = new SpannableStringBuilder();

                for (int i = 0; i < events.size(); i++) {
                    SelectSendCalenderModel event = events.get(i);
                    String tag = "";
                    int type = event.isWithin(OuterCalendarAdapter.clicksel);

                    if (type == 1) tag = "[시작]";
                    else if (type == 2) tag = "[기한]";
                    else if (type == 3) tag = "[일정]";
                    else tag = "[기간]";

                    int start = ssb.length();
                    ssb.append(tag);
                    int end = ssb.length();

                    // [] 태그에 배경색 하이라이트 (이벤트 컬러) 및 글자색 설정
                    int eventColor = event.getColor();
                    if (eventColor == 0) eventColor = Color.GRAY;
                    
                    ssb.setSpan(new BackgroundColorSpan(eventColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // 배경색에 따라 글자색 반전 (간단히 흰색)
                    ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    ssb.append(" ID: ").append(String.valueOf(event.getId()))
                       .append("  ").append(event.getName());
                    
                    if (i < events.size() - 1) {
                        ssb.append("\n");
                    }
                }

                detailHolder.detailTitle.setText(ssb);
                detailHolder.detailContent.setText(""); // contentBuilder 내용은 title에 합침
            } else {
                detailHolder.detailTitle.setText("일정이 없습니다.");
                detailHolder.detailContent.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // --- 🚀 디테일 뷰 삽입 로직 (핵심!) ---
    private int detailPosition = -1;
    public void showDetail(int weekRowPosition, ArrayList<SelectSendCalenderModel> events) {
        // 이미 열려있는 디테일 뷰가 있다면 제거
        if (detailPosition != -1) {
            int oldPos = detailPosition;
            items.remove(oldPos);
            detailPosition = -1;
            notifyItemRemoved(oldPos);
            // 만약 새로 열려는 위치가 제거된 위치보다 뒤라면 인덱스 조정
            if (weekRowPosition >= oldPos) {
                weekRowPosition--;
            }
        }

        // 🚀 일정이 없는 빈 곳을 클릭했을 경우, 위에서 기존 뷰를 제거했으므로 그대로 종료(접기)
        if (events == null || events.isEmpty()) return;

        // 선택한 행(WeekRow) 바로 다음(+1)에 상세 뷰 삽입
        int insertPos = weekRowPosition + 1;
        items.add(insertPos, events);
        detailPosition = insertPos;
        notifyItemInserted(detailPosition);
    }

    static class WeekRowViewHolder extends RecyclerView.ViewHolder {
        RecyclerView innerRecyclerView;
        WeekEventView rowEventLayer;
        public WeekRowViewHolder(View v) {
            super(v);
            innerRecyclerView = v.findViewById(R.id.inner_day_recyclerView);
            rowEventLayer = v.findViewById(R.id.rowEventLayer);
        }
    }
    static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView detailTitle;
        TextView detailContent;
        public DetailViewHolder(View v) {
            super(v);
            detailTitle = v.findViewById(R.id.detailTitle);
            detailContent = v.findViewById(R.id.detailContent);
        }
    }
}
