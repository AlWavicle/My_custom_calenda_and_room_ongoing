package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // native) android.widget.TextView: 텍스트를 화면에 표시하는 안드로이드 기본 뷰 클래스입니다.
    TextView find_yearmonthText;
    // native) java.time.LocalDate: 현재 선택된 월 정보를 담는 자바 네이티브 날짜 클래스입니다.
    LocalDate local_selectedDate;

    // native) androidx.recyclerview.widget.RecyclerView: 대량의 데이터 세트를 효율적으로 표시하기 위한 안드로이드 핵심 컴포넌트입니다.
    RecyclerView find_cal_num_recyclerView;
    // native) java.util.ArrayList: 동적 배열을 구현한 자바 표준 컬렉션 프레임워크 클래스입니다.
    ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // native) setContentView: XML 레이아웃 리소스를 인플레이트하여 액티비티의 UI로 설정하는 함수입니다.
        //LayoutInflater가 XML 리소스를 읽고 요소들 new TextView, new editText 등을 생성해냄
        //그다음에 findViewById를 써야 new객체와 연결시켜줄 수 있음(즉, new editText등을 한 객체를 만든 참조값을 리턴해줌)
        setContentView(R.layout.activity_main);

        // native) findViewById: 레이아웃에서 정의된 ID를 통해 뷰 객체를 찾는 함수입니다.
        find_yearmonthText = findViewById(R.id.yearmonthText);
        ImageButton preBtn = findViewById(R.id.pre_btn);
        ImageButton nextBtn = findViewById(R.id.next_btn);
        find_cal_num_recyclerView = findViewById(R.id.cal_num_recyclerView);

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
        Log.d("오호리", "Selected Date: " + local_selectedDate+"끝");
        Toast.makeText(MainActivity.this, "끝났다~~", Toast.LENGTH_LONG).show();

    }




    private void make_EventList() {
        eventList = new ArrayList<>();

        
        // native) android.graphics.Color: 안드로이드에서 제공하는 색상 상수 및 조작 클래스입니다.
        eventList.add(new Event("공부", 
                LocalDate.of(LocalDate.now().getYear(), 6, 10),
                LocalDate.of(LocalDate.now().getYear(), 6, 15),
                Color.YELLOW));
        
        eventList.add(new Event("영상촬영", 
                LocalDate.of(LocalDate.now().getYear(), 6, 13),
                LocalDate.of(LocalDate.now().getYear(), 6, 19),
                Color.CYAN));
        
        eventList.add(new Event("복무점검기간", 
                LocalDate.of(2026, 7, 20),
                LocalDate.of(LocalDate.now().getYear(), 7, 24),
                Color.GREEN));
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
        CalendarAdapter adapter = new CalendarAdapter(Local_Cal_ArrayDayList, eventList, new CalendarAdapter.My_OnItemListener() {

            //온아이템클릭 함수의 date 파라메터는 안씀
            //리스너 구현
            @Override
            public void My_OnItemClick(LocalDate wow, ArrayList<Event> events, int position) {
                // 커스텀 어댑터 내의 showDetail 함수를 호출하여 동적으로 뷰를 조작합니다.
                //1. 리사이클러뷰의 어뎁터함수를 가지고 어뎁터를 뽑아낸다.(이것이 의미하는 바가 뭘까?)
                // 2. 리사이클러뷰 -> 어뎁터.캘린더어뎁터로 형변환
                ((CalendarAdapter)find_cal_num_recyclerView.getAdapter()).showDetail(position, events);
            }
        });//캘린더 어뎁터 객체생성 완료

        //2. 그리드 매니저 객체생성 및 어뎁터연결
        // native) GridLayoutManager: 리사이클러뷰를 그리드(바둑판) 형태로 배치하는 레이아웃 매니저입니다.
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);
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