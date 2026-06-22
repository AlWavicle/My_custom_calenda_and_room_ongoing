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
                // 이 날짜(해당 셀)에 '종료되는 일정'이 몇 개인지 세는 카운터 (반복문 밖에서 0으로 초기화)
                int endEventCount = 1;
                int i = 0;
                if (sSCModel != null) {
                    for (SelectSendCalenderModel model : sSCModel) {
                        if (model.isWithin(date)) {
                            eventsIndex.add(i);
                            eventsOnDay.add(model);

                            // 1. 일정 뷰(막대기) 생성 및 사이즈 설정 (기존과 동일)
                            View highlighter = new View(dayHolder.itemView.getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 15); // 높이 25
                            params.setMargins(0, 2, 0, 2);
                            highlighter.setLayoutParams(params);

                            // 2. 이 일정이 오늘 끝나는 일정인지 확인
                            boolean isStart = model.getStartDate() != null && date.isEqual(model.getStartDate());
                            boolean isEnd = model.getStartDate() == null && model.getEndDate() != null && date.isEqual(model.getEndDate());
                            boolean startisEnd = model.getStartDate() != null &&model.getEndDate() != null && date.isEqual(model.getEndDate());
                            boolean isSingleDay = isStart && isEnd; // 하루짜리 단일 일정인지 판단

                            // 3. 조건에 맞춰 배경 다르게 그리기
                            if (isSingleDay) {
                                // 단일 일정은 꽉 찬 네모 (혹은 이전 답변의 동그라미 적용 가능)
                                highlighter.setBackground(new CircleDrawable(model.getColor()));

                            } else if (isEnd) {
                                // [핵심] 종료일인 경우: 커스텀 대각선 객체를 씌워줍니다.
                                // endEventCount를 같이 넘겨주어 옆으로 밀리게 만들고, 카운트를 증가시킵니다.
                                highlighter.setBackground(new MultiDiagonalLineDrawable(model.getColor(), endEventCount ));


                            } else if (startisEnd) {
                                // 시작일인 경우에 대한 처리를 추가할 수 있습니다.
                                highlighter.setBackgroundColor(model.getColor());

                            } else if (isStart) {
                                // 시작일인 경우에 대한 처리를 추가할 수 있습니다.
                                highlighter.setBackgroundColor(model.getColor());

                            }else {
                                // 시작일이거나 중간에 낀 날짜인 경우: 꽉 찬 네모 막대기
                                highlighter.setBackgroundColor(model.getColor());
                            }

                            // 4. 레이아웃에 뷰 추가
                            dayHolder.highlighterLayout.addView(highlighter);
                            i++;
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
