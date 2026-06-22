package com.example.my_custom_calenda_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_custom_calenda_1.CalendarAdapter;
import com.example.my_custom_calenda_1.Event;
import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {
    private CalendarAdapter adapter;

    // native) android.widget.TextView: 텍스트를 화면에 표시하는 안드로이드 기본 뷰 클래스입니다.
    TextView find_yearmonthText;
    // native) java.time.LocalDate: 현재 선택된 월 정보를 담는 자바 네이티브 날짜 클래스입니다.
    LocalDate local_selectedDate;

    // native) androidx.recyclerview.widget.RecyclerView: 대량의 데이터 세트를 효율적으로 표시하기 위한 안드로이드 핵심 컴포넌트입니다.
    RecyclerView find_cal_num_recyclerView;
    // native) java.util.ArrayList: 동적 배열을 구현한 자바 표준 컬렉션 프레임워크 클래스입니다.
    ArrayList<Event> eventList;

    TextView find_pre_view;

    ImageButton preBtn;
    ImageButton nextBtn;

    EditText find_schedule_editText;

    Button find_save_btn;

    Button find_remove_btn;

    int romove;

    int nums;
    // 기존 MainActivity에 있던 TextView, RecyclerView 등 변수 선언

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_calendar.xml을 인플레이트
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        //--------- 기존 MainActivity의 onCreate()에 있던 초기화 로직을 여기서 수행
        //View객체 id와 묶기
        myFindViewByIdSet(view);
        make_EventList();// eventlist array 만들고 안에 행사 넣음

        // native) LocalDate.now(): 현재 시스템의 날짜 정보를 가져오는 정적 함수입니다.
        local_selectedDate = LocalDate.now();
        setRecyclerView();

        // native) View.OnClickListener: 뷰 클릭 이벤트를 감지하는 안드로이드 인터페이스입니다.
        preBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // native) LocalDate#minusMonths: 월 단위로 날짜를 빼는 불변(Immutable) 함수입니다.
                local_selectedDate = local_selectedDate.minusMonths(1);
                setRecyclerView();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // native) LocalDate#plusMonths: 월 단위로 날짜를 더하는 불변 함수입니다.
                local_selectedDate = local_selectedDate.plusMonths(1);
                setRecyclerView();
            }
        });

        find_save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String scheduleText = find_schedule_editText.getText().toString();
                // 🚀 [수정] 날짜가 2개가 아니어도 (하나만 있거나 없어도) 텍스트가 있으면 저장 가능하게 변경
                if (!scheduleText.isEmpty()) {
                    LocalDate startDate = (adapter.seldate.size() > 0) ? adapter.seldate.get(0) : null;
                    LocalDate endDate = (adapter.seldate.size() > 1) ? adapter.seldate.get(1) : null;
                    
                    Event newEvent = new Event(scheduleText, startDate, endDate);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(requireContext()).eventDao().insert(newEvent);
                        requireActivity().runOnUiThread(() -> {
                            eventList.add(newEvent);
                            find_schedule_editText.setText("");
                            
                            // 저장 후 선택 해제 (원할 경우 주석 해제)
                            // adapter.seldate.clear(); 
                            
                            setRecyclerView();
                            Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    Toast.makeText(requireContext(), "일정 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        find_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (romove >= 0 && romove < eventList.size()) {
                    Event eventToDelete = eventList.get(romove);
                    if (eventToDelete != null) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase.getDatabase(requireContext()).eventDao().delete(eventToDelete);
                            requireActivity().runOnUiThread(() -> {
                                eventList.remove(romove);
                                romove = -1;
                                find_pre_view.setText("Selected Date Info");
                                setRecyclerView();
                            });
                        });
                    } else {
                        Toast.makeText(requireContext(), "삭제할 항목이 없습니다."           , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }


    private void myFindViewByIdSet(View view) {
        find_yearmonthText = view.findViewById(R.id.yearmonthText);
        find_cal_num_recyclerView = view.findViewById(R.id.cal_num_recyclerView);
        find_pre_view = view.findViewById(R.id.pre_view);
        preBtn = view.findViewById(R.id.pre_btn);
        nextBtn = view.findViewById(R.id.next_btn);
        find_schedule_editText = view.findViewById(R.id.schedule_editText);
        find_save_btn = view.findViewById(R.id.save_btn);
        find_remove_btn = view.findViewById(R.id.remove_btn);
    }


    private void make_EventList() {
        eventList = new ArrayList<>();

        // 데이터베이스 인스턴스 준비
        AppDatabase db = AppDatabase.getDatabase(requireContext());

        // 1. 백그라운드 스레드에서 데이터 불러오기 예시
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Event> savedEvents = db.eventDao().getAllEvents();

                // UI 변경은 다시 메인 스레드에서 처리해야 합니다!
                if (getActivity() == null) return;
                requireActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        eventList.clear();
                        eventList.addAll(savedEvents);
                        setRecyclerView(); // 달력 갱신
                    }
                });
            }
        });
    }



    private String localdate_fomat_tostring(LocalDate local_date){
        // native) java.time.format.DateTimeFormatter:  원래는 문자열이 아니기 때문에
        // 날짜를 원하는 문자열 형식으로 포맷팅하는 클래스입니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-M");
        return local_date.format(formatter);
    }





    private void setRecyclerView(){//리사이클뷰어 객체연결완성     //1. 캘린더어뎁터객체생성
        find_yearmonthText.setText(localdate_fomat_tostring(local_selectedDate));// XML TEXT에 글자를 값을 넣는다
        ArrayList<LocalDate> Local_Cal_ArrayDayList = Create_aLocalArray_ofDays_fortheCalendar(local_selectedDate);//달력array만들기

        //CalendarAdapter의 객체생성
        //3번째 파라미터는 해당 클래스 객체의 리스너를 final로 따로 저장됨
        // 객체생성시작
        adapter = new CalendarAdapter(Local_Cal_ArrayDayList, eventList, new CalendarAdapter.My_OnItemListener() {

            //온아이템클릭 함수의 date 파라메터는 안씀
            //리스너 구현
            @Override
            public void My_OnItemClick(LocalDate date, ArrayList<SelectSendCalenderModel> events, int position, ArrayList<Integer> eventsindex, int clicknums) {
                // 1. 이벤트 인덱스 리스트가 유효한지 먼저 확인
                if (eventsindex != null && !eventsindex.isEmpty()) {

                    int targetIndex = (clicknums-1) % eventsindex.size();

                    // 3. eventsindex에서 실제 eventList의 위치를 꺼냄
                    romove = eventsindex.get(targetIndex);

                    // 4. romove가 유효한 인덱스(0 이상)인지 확인 후 UI 업데이트
                    if (romove >= 0 && romove < eventList.size()) {
                        find_pre_view.setText(eventList.get(romove).getName());
                    } else {
                        find_pre_view.setText("No Event");
                        romove = -1;
                    }
                } else {
                    // 이벤트가 없는 날짜 클릭 시 초기화
                    find_pre_view.setText("No Event");
                    romove = -1;
                }


                // 커스텀 어댑터 내의 showDetail 함수를 호출하여 동적으로 뷰를 조작합니다.
                //1. 리사이클러뷰의 어뎁터함수를 가지고 어뎁터를 뽑아낸다.(이것이 의미하는 바가 뭘까?)
                // 2. 리사이클러뷰 -> 어뎁터.캘린더어뎁터로 형변환
                ((CalendarAdapter)find_cal_num_recyclerView.getAdapter()).showDetail(position, events);
            }
        });//캘린더 어뎁터 객체생성 완료

        //2. 그리드 매니저 객체생성 및 어뎁터연결
        // native) GridLayoutManager: 리사이클러뷰를 그리드(바둑판) 형태로 배치하는 레이아웃 매니저입니다.
        GridLayoutManager manager = new GridLayoutManager(requireContext(), 7);
        // native) SpanSizeLookup: 특정 위치의 아이템이 몇 개의 열을 차지할지 결정하는 클래스입니다.
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 상세 정보 칸(TYPE_DETAIL)인 경우 가로 7칸 전체를 사용하게 하여 중간 삽입 효과를 줍니다.
                return adapter.getItemViewType(position) == 1 ? 7 : 1;
            }
        });
        //리사이클뷰와 매니저 및 어뎁터 연결
        find_cal_num_recyclerView.setLayoutManager(manager);
        find_cal_num_recyclerView.setAdapter(adapter);
    }//setmonthview 끝






    //cal_daylist라는 array를 만들고 달력칸의 순서를 만든다
    private ArrayList<LocalDate> Create_aLocalArray_ofDays_fortheCalendar(LocalDate date){
        ArrayList<LocalDate> local_cal_dayList = new ArrayList<>();
        // native) java.time.YearMonth: 월의 길이(말일)를 계산하기 위한 자바 네이티브 클래스입니다.
        YearMonth yearMonth = YearMonth.from(date);
        int lastDate = yearMonth.lengthOfMonth();
        // native) LocalDate#withDayOfMonth: 특정 날짜로 변경(여기서는 1일로 설정)하는 함수입니다.
        LocalDate firstDay = date.withDayOfMonth(1);//2026-6-1
        // native) LocalDate#getDayOfWeek: 해당 날짜의 요일 객체를 반환합니다.
        int dayofweek = firstDay.getDayOfWeek().getValue();

        int offset = dayofweek % 7;// 숫자로 무슨요일에 시작하는지 알 수있음

        for(int i = 0; i < 42; i++){
            if(i < offset || i >= lastDate + offset) {//1일보다 전의 칸, 말일보다 뒤에 있는칸 여부
                local_cal_dayList.add(null);//빈공간 만들기
            } else {
                local_cal_dayList.add(LocalDate.of(date.getYear(), date.getMonth(), i - offset + 1));//달력 채우기
            }
        }
        return local_cal_dayList;
    }


}