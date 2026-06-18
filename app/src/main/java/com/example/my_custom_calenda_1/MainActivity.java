package com.example.my_custom_calenda_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.my_custom_calenda_and_room.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ViewPager2 하나만 있는 레이아웃

        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // ViewPager2에 3개의 프래그먼트를 공급해주는 어댑터 설정
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 팁: 스와이프 할 때 양쪽 페이지가 메모리에서 완전히 삭제되는 것을 방지하여
        // 로딩 지연을 줄이고 스크롤 위치 등을 자연스럽게 유지해 줍니다.
        viewPager.setOffscreenPageLimit(2);
    }

    // ViewPager2 전용 어댑터 클래스 (내부 클래스로 생성)
    private static class MainPagerAdapter extends FragmentStateAdapter {
        public MainPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // 위치(position)에 따라 보여줄 페이지(Fragment)를 반환
            switch (position) {
                case 0:
                    return new CalendarFragment(); // 1페이지: 기존 달력
                case 1:
                    return new QueryFragment();    // 2페이지: 쿼리 작성
                case 2:
                    return new ResultFragment();   // 3페이지: 결과 화면
                default:
                    return new CalendarFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // 총 페이지 수
        }
    }
}