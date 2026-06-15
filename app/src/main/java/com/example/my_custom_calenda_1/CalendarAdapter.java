package com.example.my_custom_calenda_1;

import android.graphics.Color;
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

/**
 * 1. 코드 구성 및 핵심 구조 설명:
 * 이 어댑터는 'Multi-ViewType' 전략을 사용하여 두 가지 형태의 레이아웃을 하나의 리사이클러뷰에서 관리합니다.
 * - TYPE_DAY (0): 개별 날짜 셀
 * - TYPE_DETAIL (1): 클릭 시 삽입되는 가로형 상세 정보 칸
 *
 * 사용자가 날짜를 클릭하면 해당 날짜가 포함된 행의 끝 인덱스를 계산하여 데이터 리스트(items)에
 * 상세 정보를 삽입(notifyItemInserted)하거나 삭제(notifyItemRemoved)함으로써 동적 레이아웃 변화를 구현합니다.
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DAY = 0;
    private static final int TYPE_DETAIL = 1;

    // native) ArrayList<Object>: 다양한 타입(LocalDate 및 Event 리스트)을 하나의 리스트에서 관리하기 위한 객체 지향형 구조입니다.

    //생성자로 연결 시켜주는 변수들 시작
    private final ArrayList<Object> CopyItems;
    public final ArrayList<Event> eventList;
    private final My_OnItemListener MyAL_onItemListener1;


    public ArrayList<LocalDate> seldate;

    public LocalDate oneSelDate;

    public int clickNum;


    //여기까지가 생성자 만들시 연결시켜주는 변수들

    private int detailPosition = -1;

    public interface My_OnItemListener {
        void My_OnItemClick(LocalDate date, ArrayList<Event> events, int position, ArrayList<Integer> eventsindex,int clicknums);
    }

    public CalendarAdapter(ArrayList<LocalDate> dayList, ArrayList<Event> eventList, My_OnItemListener par_my_onItemListener) {
        this.CopyItems = new ArrayList<>(dayList);//그대로 복사 this.items=daylist 하면 참조값만 받음
        this.eventList = eventList;
        this.MyAL_onItemListener1 = par_my_onItemListener;
        this.seldate = new ArrayList<LocalDate>();
    }


    //콜백이라함(생명주기같은건 아닌듯) 그리기전 날짜?/상세칸?
    //리턴값을 onCreateViewHolder 콜백 시 (@NonNull ViewGroup parent, int viewType) {의 int 인수로 너어줌
    @Override
    public int getItemViewType(int position) {
        // native) instanceof: 객체의 실제 타입을 확인하여 날짜인지 상세 정보인지 판별합니다.
        if (CopyItems.get(position) instanceof LocalDate || CopyItems.get(position) == null) {
            return TYPE_DAY;
        } else {
            return TYPE_DETAIL;
        }
    }


    //변수의 값을 넣기전까지의 연결작업
    // argument는 getItemViewType리턴 값이 들어옴
    //처음으로 부가 레이아웃을 연결시킴. 대신 setcontentview가 아닌 Layoutlnflater로
    @NonNull
    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // native) LayoutInflater: XML 레이아웃 파일을 실제 View 객체로 메모리에 로드하는 안드로이드 핵심 클래스입니다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DAY) {
            View view = inflater.inflate(R.layout.calendar_cell, parent, false);//LayoutInflater가 함수의 리턴값을 View로 뱉어냄
            return new CalendarViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.event_detail_item, parent, false);
            return new DetailViewHolder(view);
        }
    }

    @Override//실제 데이터를 뷰홀더의 아이템에 바인딩(연결)하는 작업
    //리사이클러 뷰는 한 행씩이 아닌 아이템 1개단위로 이함수를 호출함
    // 화면에 보이는 만큼만 만든 뒤 재사용(Recycle)합니다.
    //1. 리사이클 뷰어에 빈칸이 발생했을때에만 호출된다
    //2. notifyDataSetChanged(),notifyItemInserted()같은 함수에도 호출된다
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {//int parameter 는 생성할 몇번째 빈칸을 의미
        if (holder instanceof CalendarViewHolder) {
            CalendarViewHolder dayHolder = (CalendarViewHolder) holder;
            LocalDate date = (LocalDate) CopyItems.get(position);
            // native) removeAllViews: 기존에 추가되었던 형광펜 뷰들을 초기화하여 뷰 재사용 시 발생할 수 있는 문제를 방지합니다.
            dayHolder.highlighterLayout.removeAllViews();

            // 1. 일단 무조건 기본 색상으로 초기화 (이게 없으면 재활용 버그 발생!)
            dayHolder.itemView.setBackgroundColor(Color.WHITE);



            if (date == null) {
                dayHolder.dayText.setText("");
                dayHolder.itemView.setOnClickListener(null);
            } else {
                //셀랙트된 날짜 색상 넣기
                for(LocalDate seldate1 : seldate){
                    if(date.equals(seldate1)){
                        dayHolder.itemView.setBackgroundColor(Color.RED);
                    }
                }
                dayHolder.dayText.setText(String.valueOf(date.getDayOfMonth()));










                ArrayList<Event> eventsOnDay = new ArrayList<>();
                ArrayList<Integer> eventsIndex = new ArrayList<>();
                int i = 0;
                for (Event event : eventList) {

                    if (event.isWithin(date)) {// 이벤트들이 날짜에 포함되어잇는지 확인
                        eventsIndex.add(i);
                        eventsOnDay.add(event);// 하루에 포함된 이벤트 담기 2개가 겹칠수 있고 등등 겹칠수 있음

                        // native) android.view.View: 형광펜 효과를 주기 위해 빈 뷰 객체를 동적으로 생성합니다.
                        View highlighter = new View(dayHolder.itemView.getContext());
                        // native) LinearLayout.LayoutParams: 부모 뷰 내에서의 배치 정보(너비, 높이, 마진 등)를 설정합니다.
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 25);
                        params.setMargins(0, 2, 0, 2);
                        highlighter.setLayoutParams(params);
                        highlighter.setBackgroundColor(event.getColor());
                        // native) addView: 생성한 형광펜 뷰를 레이아웃에 동적으로 추가합니다.
                        dayHolder.highlighterLayout.addView(highlighter);
                    }
                    i++;
                }
                //setOnClickListener()를 꼭 onBindViewHolder()에 넣을 필요없음 .
                //숙련된 개발자들은 onCreateViewHolder()에 넣음 한번만  리스너를 설치할 수 있으니까
                // setOnClickListener(new View.OnClickListener() {는 한번 어디서든 뷰가 생성되었다면 설치할 수 있음
                // 인플레이터 하고 부터 setOnClickListener(new View.OnClickListener() {설치 가능함
                dayHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.getAdapterPosition()는 현재 어댑터 내 위치(인덱스)를 반환합니다.
                        //holder는 밖에 함수가 호출되었을때의 아규먼트임.
                        //임시 클래스를 만들었으니 클래스소멸전까지 그 아규먼트를 저장하고 있음






//---------------------------------선택날짜
                        int existingSizeOfSeldate=seldate.size();
                        switch (existingSizeOfSeldate) {

                            case 0:
                                seldate.add((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()));
                                break;

                            case 1:
                                if (seldate.get(0).equals((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()))) {
                                    for(LocalDate date : seldate){
                                        notifyItemChanged(CopyItems.indexOf(date));
                                    }
                                    seldate.clear();
                                    break;
                                }else {
                                    seldate.add((LocalDate) CopyItems.get(dayHolder.getAdapterPosition()));
                                }
                                break;

                            default:
                                for(LocalDate date : seldate){
                                    notifyItemChanged(CopyItems.indexOf(date));
                                }
                                seldate.clear();
                                break;
                        }
                        if(existingSizeOfSeldate !=seldate.size()) {
                            for(LocalDate date : seldate){
                                notifyItemChanged(CopyItems.indexOf(date));
                            }
                        }
//---------------------------------------------------








                        //Log.d("오호리", "Selected Date: " + CopyItems.get(dayHolder.getAdapterPosition()).toString()+"끝");
                        //Toast.makeText(dayHolder.itemView.getContext(), CopyItems.get(dayHolder.getAdapterPosition()).toString()+"/", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(dayHolder.itemView.getContext(), "date"+date.toString(), Toast.LENGTH_SHORT).show();

                        if(oneSelDate != null && oneSelDate.equals(CopyItems.get(dayHolder.getAdapterPosition()))){
                            clickNum++;
                        }
                        else {clickNum=1;}
                        oneSelDate = (LocalDate) CopyItems.get(dayHolder.getAdapterPosition());
                        Toast.makeText(dayHolder.itemView.getContext(), "clickNum: "+clickNum+"/eventlist.size(): "+eventList.size(), Toast.LENGTH_SHORT).show();




                        if (MyAL_onItemListener1 != null) {
                            //getAdapterPosition()는 ""어, 나 지금 14번 인덱스(15일째) 보여주고 있어!""
                            MyAL_onItemListener1.My_OnItemClick(date, eventsOnDay, holder.getAdapterPosition(), eventsIndex, clickNum);
                        }

                    }
                });
            }
        } else if (holder instanceof DetailViewHolder) {
            DetailViewHolder detailHolder = (DetailViewHolder) holder;
            ArrayList<Event> events = (ArrayList<Event>) CopyItems.get(position);
            
            // native) StringBuilder: 여러 문자열을 효율적으로 합치기 위한 자바 네이티브 클래스입니다.
            StringBuilder sb = new StringBuilder();
            for (Event e : events) {
                sb.append(e.getName()).append("\n");
            }
            detailHolder.detailTitle.setText("선택한 날짜의 일정");
            detailHolder.detailContent.setText(sb.toString().trim());
        }
    }

    /**
     * 상세 정보 칸을 중간에 삽입/삭제하는 핵심 함수
     */
    public void showDetail(int dayPosition, ArrayList<Event> events) {

        if (detailPosition != -1) {
            // native) items.remove: 리스트에서 기존 상세 칸 데이터를 제거합니다.
            CopyItems.remove(detailPosition);
            // native) notifyItemRemoved: 특정 위치의 아이템이 삭제되었음을 알리고 애니메이션을 트리거합니다.
            notifyItemRemoved(detailPosition);
            if (dayPosition > detailPosition) {
                dayPosition--;
            }
            detailPosition = -1;
        }

        if (events == null || events.isEmpty()) {
            // native) notifyDataSetChanged: 리스트 전체를 다시 그리게 하여 UI를 갱신합니다.
            //notifyDataSetChanged();
            return;
        }

        int row = dayPosition / 7;
        int insertPos = (row + 1) * 7;
        if (insertPos > CopyItems.size()) insertPos = CopyItems.size();

        // native) items.add(index, object): 특정 위치에 데이터를 삽입합니다.
        CopyItems.add(insertPos, events);
        detailPosition = insertPos;
        // native) notifyItemInserted: 새로운 아이템이 삽입되었음을 알리고 칸을 밀어내는 애니메이션을 수행합니다.
        notifyItemInserted(detailPosition);
    }

    @Override
    public int getItemCount() {
        return CopyItems.size();
    }

    // native) RecyclerView.ViewHolder: 각 아이템의 뷰 객체를 보관하여 findViewById 호출 횟수를 줄이는 최적화 클래스입니다.
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
