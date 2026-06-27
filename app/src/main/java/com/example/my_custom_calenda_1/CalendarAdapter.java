/*
//*package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
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
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DAY = 0;
    private static final int TYPE_DETAIL = 1;

    // (예시) 5주 치 데이터가 있다고 가정
    private int weekCount = 5;

    private final ArrayList<Object> CopyItems;
    public ArrayList<Event> eventList;
    private final My_OnItemListener MyAL_onItemListener1;

    public ArrayList<LocalDate> seldate;
    public LocalDate oneSelDate;
    public int clickNum;
    private int selectionStep = 0; // 1, 2, 3, 4 단계 순환을 위한 변수

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
            if (dayHolder.eventLayout != null) {
                dayHolder.eventLayout.removeAllViews();
                // If this is the start of a week (Sunday), we can inflate or setup the WeekEventView
                if (position % 7 == 0 && sSCModel != null) {
                    WeekEventView weekEventView;
                    if (dayHolder.eventLayout.getChildCount() > 0 && dayHolder.eventLayout.getChildAt(0) instanceof WeekEventView) {
                        weekEventView = (WeekEventView) dayHolder.eventLayout.getChildAt(0);
                    } else {
                        weekEventView = new WeekEventView(dayHolder.itemView.getContext());
                    dayHolder.eventLayout.addView(weekEventView);

                    List<LocalDate> weekDays = new ArrayList<>();
                    for (int i = 0; i < 7 && (position + i) < CopyItems.size(); i++) {
                        Object item = CopyItems.get(position + i);
                        if (item instanceof LocalDate) {
                            weekDays.add((LocalDate) item);
                        }
                    }

                    if (!weekDays.isEmpty()) {
                        weekEventView.setWeekData(weekDays, sSCModel);
                    }
                }
            }
            dayHolder.itemView.setBackgroundColor(Color.WHITE);

            if (date == null) {
                dayHolder.dayText.setText("");
                dayHolder.itemView.setOnClickListener(null);
            } else {
                for (LocalDate seldate1 : seldate) {
                    if (date.equals(seldate1)) {
                        // 1. GradientDrawable 객체 생성
                        GradientDrawable borderDrawable = new GradientDrawable();

// 2. 배경색은 투명하게 (테두리만 필요하니까)
                        borderDrawable.setColor(Color.TRANSPARENT);

// 3. 테두리 설정 (두께 5px, 빨간색)
                        borderDrawable.setStroke(5, Color.BLACK);

// 4. (선택사항) 모서리를 둥글게 하고 싶다면 (10dp 정도)
                        borderDrawable.setCornerRadius(1f);

// 5. 뷰에 적용
                        dayHolder.itemView.setBackground(borderDrawable);
                    }
                }
                dayHolder.dayText.setText(String.valueOf(date.getDayOfMonth()));

                ArrayList<SelectSendCalenderModel> eventsOnDay = new ArrayList<>();
                ArrayList<Integer> eventsIndex = new ArrayList<>();
                // 이 날짜(해당 셀)에 '종료되는 일정'이 몇 개인지 세는 카운터 (반복문 밖에서 0으로 초기화)

                int i = 0;
                if (sSCModel != null) {
                    //싱글스케줄 컬러 및 카운트 역할
                    ArrayList<Integer> singsColor = new ArrayList<Integer>();
                    // 1. [싱글 일정]일정 뷰(막대기) 생성 및 사이즈 설정 (기존과 동일)
                    View singleHighlighter = new View(dayHolder.itemView.getContext());
                    LinearLayout.LayoutParams singleparams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 25); // 높이 25
                    singleparams.setMargins(0, 2, 0, 2);
                    singleHighlighter.setLayoutParams(singleparams);

                    dayHolder.highlighterLayout.addView(singleHighlighter);

                    //싱글스케줄 컬러 및 카운트 역할
                    ArrayList<Integer> endsColor = new ArrayList<Integer>();

                    for (SelectSendCalenderModel model : sSCModel) {
                        int swicher=model.isWithin(date);
                        if (swicher!=0) {
                            eventsIndex.add(i);
                            eventsOnDay.add(model);

                            // 1. 일정 뷰(막대기) 생성 및 사이즈 설정 (기존과 동일)
                            View highlighter = new View(dayHolder.itemView.getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 15); // 높이 25
                            params.setMargins(0, 2, 0, 2);
                            highlighter.setLayoutParams(params);

                            // 3. 조건에 맞춰 배경 다르게 그리기
                            if (swicher==3) {
                                // 단일 일정은 꽉 찬 네모 (혹은 이전 답변의 동그라미 적용 가능)

                                singsColor.add(model.getColor());

                            } else if (swicher==2) {
                                // [핵심] 종료일인 경우: 커스텀 대각선 객체를 씌워줍니다.
                                // endEventCount를 같이 넘겨주어 옆으로 밀리게 만들고, 카운트를 증가시킵니다.


                                endsColor.add(model.getColor());


                            } else if (swicher==1) {
                                // 시작일인 경우에 대한 처리를 추가할 수 있습니다.
                                highlighter.setBackgroundColor(model.getColor());
                                // 4. 레이아웃에 뷰 추가
                                dayHolder.highlighterLayout.addView(highlighter);
                            }else {
                                // 시작일이거나 중간에 낀 날짜인 경우: 꽉 찬 네모 막대기
                                highlighter.setBackgroundColor(model.getColor());
                                // 4. 레이아웃에 뷰 추가
                                dayHolder.highlighterLayout.addView(highlighter);
                            }



                        }

                        i++;
                    }
                    if (!singsColor.isEmpty()) {

                        // 2. 개수만큼 Drawable 객체 생성
                        Drawable[] layers = new Drawable[singsColor.size()];
                        for (int j = 0; j < singsColor.size(); j++) {
                            layers[j] = new CircleDrawable(singsColor.get(j));
                        }
                        // 3. LayerDrawable로 묶기
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
// 4. 위치 조정 (setLayerInset을 사용하면 겹치지 않게 옆으로 밀 수 있음)
// 각 동그라미의 위치를 오른쪽으로 조금씩 밀어줍니다.

                        int spacing = 20;
                        for (int j = 0; j < layers.length; j++) {
                            // setLayerInset(index, 좌, 상, 우, 하)
                            layerDrawable.setLayerInset(j, j * spacing, 0, 0, 0);
                        }


                        View highlighterView = dayHolder.highlighterLayout.getChildAt(0);
                        if (highlighterView != null) {
                            // 3. 뷰의 배경을 즉시 교체합니다.
                            highlighterView.setBackground(layerDrawable);
                        }







                    }

                    if (!endsColor.isEmpty()) {


                        // 1. [싱글 일정]일정 뷰(막대기) 생성 및 사이즈 설정 (기존과 동일)
                        View endHighlighter = new View(dayHolder.itemView.getContext());
                        LinearLayout.LayoutParams endparams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 25); // 높이 25
                        endparams.setMargins(0, 2, 0, 2);
                        endHighlighter.setLayoutParams(endparams);


                        // 2. 개수만큼 Drawable 객체 생성
                        Drawable[] layers = new Drawable[endsColor.size()];
                        for (int j = 0; j < endsColor.size(); j++) {
                            layers[j] = new MultiDiagonalLineDrawable(endsColor.get(j));
                        }
                        // 3. LayerDrawable로 묶기
                        LayerDrawable endlayerDrawable = new LayerDrawable(layers);
// 4. 위치 조정 (setLayerInset을 사용하면 겹치지 않게 옆으로 밀 수 있음)
// 각 동그라미의 위치를 오른쪽으로 조금씩 밀어줍니다.

                        int spacing = 20;
                        for (int j = 0; j < layers.length; j++) {
                            // setLayerInset(index, 좌, 상, 우, 하)
                            endlayerDrawable.setLayerInset(j, j * spacing, 0, 0, 0);
                        }
                        endHighlighter.setBackground(endlayerDrawable);
                        int count = dayHolder.highlighterLayout.getChildCount();
                        if (count == 0) {
                            dayHolder.highlighterLayout.addView(endHighlighter);
                        }else if(count < 9) {
                            dayHolder.highlighterLayout.addView(endHighlighter, count - 1);
                        }else{
                            dayHolder.highlighterLayout.addView(endHighlighter, 8);

                        }

                    }

                }

                // ===== 여기까지 =====

                dayHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalDate clickedDate = (LocalDate) CopyItems.get(dayHolder.getAdapterPosition());
                        
                        // 변경 전 상태의 날짜들을 기록 (UI 갱신용)
                        ArrayList<LocalDate> datesToNotify = new ArrayList<>(seldate);
                        if (!datesToNotify.contains(clickedDate)) {
                            datesToNotify.add(clickedDate);
                        }

                        // 1-2-3-4 단계 로직 수행
                        selectionStep = (selectionStep % 4) + 1;
                        
                        if (selectionStep == 1) {
                            // 1번째 클릭: 첫 번째 선택 추가
                            seldate.clear();
                            seldate.add(clickedDate); // [시작일]
                        } else if (selectionStep == 2) {
                            // 2번째 클릭: 두 번째 선택 추가
                            if (seldate.isEmpty()) seldate.add(null); // 혹시 시작일이 없으면 공간 확보
                            seldate.add(clickedDate); // [시작일, 종료일]
                        } else if (selectionStep == 3) {
                            // 3번째 클릭: 1번(시작일)만 null로 설정
                            if (seldate.size() >= 1) {
                                seldate.set(0, null); // [null, 종료일]
                            }
                        } else {
                            // 4번째 클릭: 모두 제거
                            seldate.clear();
                            selectionStep = 0; // 단계 초기화
                        }

                        // 변경된 날짜들의 아이템 뷰 갱신 (기존 + 현재 선택된 날짜들 모두)
                        for (LocalDate d : datesToNotify) {
                            if (d != null) {
                                int index = CopyItems.indexOf(d);
                                if (index != -1) notifyItemChanged(index);
                            }
                        }
                        // 현재 seldate에 있는 날짜들도 갱신
                        for (LocalDate d : seldate) {
                            if (d != null) {
                                int index = CopyItems.indexOf(d);
                                if (index != -1) notifyItemChanged(index);
                            }
                        }

                        // 추가적인 클릭 정보 업데이트
                        if (oneSelDate != null && oneSelDate.equals(clickedDate)) {
                            clickNum++;
                        } else {
                            clickNum = 1;
                        }
                        oneSelDate = clickedDate;
                        
                        if (MyAL_onItemListener1 != null) {
                            Log.d("CalendarAdapter", "eventsIndex: " + eventsIndex.toString());


                            MyAL_onItemListener1.My_OnItemClick(date, eventsOnDay, dayHolder.getAdapterPosition(), eventsIndex, clickNum);
                            showDetail(dayHolder.getAdapterPosition(), eventsOnDay);
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
        LinearLayout eventLayout;


        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            highlighterLayout = itemView.findViewById(R.id.highlighterLayout);
            eventLayout = itemView.findViewById(R.id.eventLayout);

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
//**/
