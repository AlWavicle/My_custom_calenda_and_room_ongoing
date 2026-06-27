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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.my_custom_calenda_1.Event;
import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class CalendarFragment extends Fragment {


    // 1. 변수 선언 추가
    WeekEventView weekEventLayer;

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

    // 🚀 ViewModel 추가 (다른 공유 데이터 용도 유지 가능)
    private SharedViewModel viewModel;
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

        // 🚀 ViewModel 초기화??
        viewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
                if (!scheduleText.isEmpty()) {
                    OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
                    LocalDate startDate = null;
                    LocalDate endDate = null;
                    
                    if (currentAdapter != null) {
                        startDate = (currentAdapter.seldate.size() > 0) ? currentAdapter.seldate.get(0) : null;
                        endDate = (currentAdapter.seldate.size() > 1) ? currentAdapter.seldate.get(1) : null;
                    }
                    
                    Event newEvent = new Event(scheduleText, startDate, endDate);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(requireContext()).eventDao().insert(newEvent);
                        requireActivity().runOnUiThread(() -> {
                            eventList.add(newEvent);
                            find_schedule_editText.setText("");
                            make_EventList(); // Refresh
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
                if (OuterCalendarAdapter.sSCModel != null && romove >= 0 && romove < OuterCalendarAdapter.sSCModel.size()) {
                    int removeIdNum = OuterCalendarAdapter.sSCModel.get(romove).getId();

                    if (removeIdNum != -1) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase.getDatabase(requireContext()).eventDao().deleteById(removeIdNum);
                            requireActivity().runOnUiThread(() -> {
                                romove = -1;
                                find_pre_view.setText("Selected Date Info");
                                make_EventList(); // Refresh data from DB and update UI
                                Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                            });
                        });
                    } else {
                        Toast.makeText(requireContext(), "삭제할 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "삭제할 일정을 선택해주세요.", Toast.LENGTH_SHORT).show();
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

        // 2. 투명 뷰 찾기 추가
        weekEventLayer = view.findViewById(R.id.weekEventView);
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





    private void setRecyclerView() {
        find_yearmonthText.setText(localdate_fomat_tostring(local_selectedDate));
        ArrayList<LocalDate> Local_Cal_ArrayDayList = Create_aLocalArray_ofDays_fortheCalendar(local_selectedDate);

        // 7일씩 묶어 WeekRow 리스트 구성
        OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
        List<LocalDate> selDates = (currentAdapter != null) ? currentAdapter.seldate : new ArrayList<>();
        List<Object> weekRowItems = createWeekRowData(Local_Cal_ArrayDayList, eventList, selDates);

        // Outer 어댑터 객체 생성 및 리스너 구현
        OuterCalendarAdapter adapter = new OuterCalendarAdapter(weekRowItems, new InnerDayAdapter.OnDayClickListener() {
            @Override
            public void onDayClick(LocalDate date, int parentWeekPosition,
                                   ArrayList<SelectSendCalenderModel> eventsOnDay,
                                   ArrayList<Integer> eventsIndex) {

                OuterCalendarAdapter.clicksel=date;
                // 1. 다중 클릭(순환)을 위한 clickNum 처리
                if (local_selectedDate != null && local_selectedDate.equals(date)) {
                    nums++;
                } else {
                    nums = 1;
                }
                local_selectedDate = date;
                OuterCalendarAdapter.clicksel = date; // 🚀 클릭한 날짜 저장

                // 2. 🚀 [4단계 선택 로직] 적용
                OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
                if (currentAdapter != null) {
                    currentAdapter.selectionStep = (currentAdapter.selectionStep % 4) + 1;

                    if (currentAdapter.selectionStep == 1) {
                        currentAdapter.seldate.clear();
                        currentAdapter.seldate.add(date);
                    } else if (currentAdapter.selectionStep == 2) {
                        if (currentAdapter.seldate.isEmpty()) currentAdapter.seldate.add(null);
                        currentAdapter.seldate.add(date);
                    } else if (currentAdapter.selectionStep == 3) {
                        if (!currentAdapter.seldate.isEmpty()) currentAdapter.seldate.set(0, null);
                    } else {
                        currentAdapter.seldate.clear();
                        currentAdapter.selectionStep = 0;
                    }
                    // 🚀 원래 클릭 로직 복구: setRecyclerView() 호출 대신 notifyDataSetChanged()만 사용

                    currentAdapter.notifyDataSetChanged(); 
                }

                // 3. 상단 텍스트 정보 표시
                if (currentAdapter != null) {
                    LocalDate start = (currentAdapter.seldate.size() > 0) ? currentAdapter.seldate.get(0) : null;
                    LocalDate end = (currentAdapter.seldate.size() > 1) ? currentAdapter.seldate.get(1) : null;
                    String dateInfo = "시작: " + (start != null ? start : "null") + " / 종료: " + (end != null ? end : "null");

                    if (eventsIndex != null && !eventsIndex.isEmpty()) {
                        int targetIndex = (nums - 1) % eventsIndex.size();
                        romove = eventsIndex.get(targetIndex);
                        if (romove >= 0 && romove < OuterCalendarAdapter.sSCModel.size()) {
                            find_pre_view.setText("id: "+OuterCalendarAdapter.sSCModel.get(romove).getId()+"  "+dateInfo + "\n(일정: " + OuterCalendarAdapter.sSCModel.get(romove).getName() + ")");
                        } else {
                            find_pre_view.setText(dateInfo);
                            romove = -1;
                        }
                    } else {
                        find_pre_view.setText(dateInfo);
                        romove = -1;
                    }
                }

                // 4. 디테일 뷰 열기 호출
                ((OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter()).showDetail(parentWeekPosition, eventsOnDay);
            }
        });

        find_cal_num_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        find_cal_num_recyclerView.setAdapter(adapter);

        // 🚀 리사이클러뷰 빈 공간(배경) 클릭 시 디테일 뷰 접기
        find_cal_num_recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child == null) {
                        // 자식 뷰(아이템)가 없는 빈 공간을 터치한 경우
                        OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) rv.getAdapter();
                        if (currentAdapter != null) {
                            currentAdapter.showDetail(-1, null);
                        }
                    }
                }
                return false;
            }
            @Override public void onTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }



    // 1. (핵심) 전체 이벤트 리스트를 도화지 전용 데이터(WeekEvent)로 변환하는 함수
    private List<WeekEvent> getWeekEventsForLayer(ArrayList<LocalDate> calendarDays, ArrayList<SelectSendCalenderModel> allEvents) {
        List<WeekEvent> weekEvents = new ArrayList<>();

        // 등록된 모든 일정을 하나씩 확인
        for (SelectSendCalenderModel event : allEvents) {
            LocalDate startDate = event.getStartDate();
            LocalDate endDate = event.getEndDate();

            int type = 0; // 기본 막대
            
            if (startDate != null && endDate != null) {
                if (startDate.equals(endDate)) {
                    type = 3; // 당일 - 동그라미
                } else {
                    type = 0; // 기간 - 막대
                }
            } else if (startDate != null) {
                type = 1; // 시작만 - 왼쪽 하단 직삼각형
                endDate = startDate;
            } else if (endDate != null) {
                type = 2; // 종료만 - 오른쪽 하단 직삼각형
                startDate = endDate;
            } else {
                continue; // 둘 다 없으면 무시
            }

            int startIndex = getDayIndex(startDate, calendarDays, true);
            int endIndex = getDayIndex(endDate, calendarDays, false);

            if (startIndex != -1 && endIndex != -1 && startIndex <= endIndex) {
                int color = event.getColor();
                WeekEvent we = new WeekEvent(startIndex, endIndex, color, event.getName());
                we.type = type; // 타입 설정
                weekEvents.add(we);
            }
        }
        return weekEvents;
    }

    // 2. 특정 날짜가 42칸 중 몇 번째 칸인지 찾아주는 함수 (이전/다음 달 걸침 처리 완벽 대응)
    private int getDayIndex(LocalDate targetDate, ArrayList<LocalDate> calendarDays, boolean isStart) {
        if (targetDate == null) return -1;

        LocalDate firstDateOfMonth = null;
        int firstDateIndex = -1;
        LocalDate lastDateOfMonth = null;
        int lastDateIndex = -1;

        // 달력 42칸 중에서 이번 달 1일과 말일의 위치를 찾음 (null이 아닌 진짜 날짜 칸)
        for (int i = 0; i < calendarDays.size(); i++) {
            if (calendarDays.get(i) != null) {
                if (firstDateOfMonth == null) {
                    firstDateOfMonth = calendarDays.get(i);
                    firstDateIndex = i;
                }
                lastDateOfMonth = calendarDays.get(i);
                lastDateIndex = i;
            }
        }

        // 일정이 이번 달 1일보다 '이전'에 시작했다면 -> 달력의 가장 첫 칸(1일)부터 막대를 그리기 시작함
        if (isStart && targetDate.isBefore(firstDateOfMonth)) {
            return firstDateIndex;
        }
        // 일정이 이번 달 말일보다 '이후'에 끝난다면 -> 달력의 가장 마지막 칸(말일)까지 막대를 쭉 그림
        if (!isStart && targetDate.isAfter(lastDateOfMonth)) {
            return lastDateIndex;
        }

        // 그 외의 경우 (이번 달 안에 일정이 포함됨) -> 정확한 인덱스를 찾음
        for (int i = 0; i < calendarDays.size(); i++) {
            if (calendarDays.get(i) != null && calendarDays.get(i).equals(targetDate)) {
                return i;
            }
        }

        return -1; // 이번 달 화면에 아예 표시할 필요가 없는 경우
    }




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
    private List<Object> createWeekRowData(ArrayList<LocalDate> dayList, ArrayList<Event> allEvents, List<LocalDate> selDates) {
        List<Object> weekRows = new ArrayList<>();

        // 🚀 사용자가 "에스큐엘 셀렉트문을 할때만 달력에 표시되게 해줘"라고 했으므로
        // sSCModel이 있을 때만 데이터를 사용하고, 없으면 빈 리스트를 사용합니다.
        
        ArrayList<SelectSendCalenderModel> sscEvents = new ArrayList<>();
        
        if (OuterCalendarAdapter.sSCModel != null && !OuterCalendarAdapter.sSCModel.isEmpty()) {
            sscEvents.addAll(OuterCalendarAdapter.sSCModel);
        } else {
            // SQL 결과가 없으면 아무것도 표시하지 않음 (이전에는 Room 데이터를 넣었으나 제거)
            // OuterCalendarAdapter.sSCModel = sscEvents; // (필요시 비워줌)
        }

        // 2. 전체 42일 기준의 막대 데이터(WeekEvent)를 미리 계산
        List<WeekEvent> allWeekEvents = getWeekEventsForLayer(dayList, sscEvents);

        // 3. 42일(6주)을 7개씩 묶어서 6개의 WeekRow 객체 생성
        for (int i = 0; i < 42; i += 7) {
            WeekRow row = new WeekRow();
            int weekStartIdx = i;
            int weekEndIdx = i + 6;

            // 1) 날짜 7개 할당
            for (int j = 0; j < 7; j++) {
                row.days.add(dayList.get(weekStartIdx + j));
            }

            // 2) 이 주(Week)에 걸치는 막대기들을 잘라서 rowEvents에 담음
            List<Integer> lanesOccupiedUntil = new ArrayList<>(); // 각 레인이 언제까지 차 있는지 저장

            for (WeekEvent we : allWeekEvents) {
                // 겹치는지 확인
                int overlapStart = Math.max(we.startDayIndex, weekStartIdx);
                int overlapEnd = Math.min(we.endDayIndex, weekEndIdx);

                if (overlapStart <= overlapEnd) {
                    int startInWeek = overlapStart - weekStartIdx;
                    int endInWeek = overlapEnd - weekStartIdx;

                    // 이 주에서 사용할 레인(lane) 결정
                    int targetLane = -1;
                    for (int l = 0; l < lanesOccupiedUntil.size(); l++) {
                        if (lanesOccupiedUntil.get(l) < startInWeek) {
                            targetLane = l;
                            break;
                        }
                    }

                    if (targetLane == -1) {
                        targetLane = lanesOccupiedUntil.size();
                        lanesOccupiedUntil.add(endInWeek);
                    } else {
                        lanesOccupiedUntil.set(targetLane, endInWeek);
                    }

                    row.addRowEvent(new WeekEvent(
                            startInWeek,
                            endInWeek,
                            we.color,
                            we.title,
                            targetLane,
                            we.type // 원본 타입 유지
                    ));
                }
            }
            weekRows.add(row);
        }
        return weekRows;
    }



}