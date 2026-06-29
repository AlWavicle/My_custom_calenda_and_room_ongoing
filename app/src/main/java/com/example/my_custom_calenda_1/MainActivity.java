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
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        // 🚀 Index 2 (CalendarFragment)에서 시작하도록 설정
        viewPager.setCurrentItem(2, false);

        SharedViewModel viewModel = new androidx.lifecycle.ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.getNavigateToPage().observe(this, pageIndex -> {
            if (pageIndex != null) {
                viewPager.setCurrentItem(pageIndex, true);
            }
        });
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {
        public MainPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new NullDateFragment(); // 🚀 Index 0
                case 1:
                    return new ContentFragment();  // 🚀 Index 1
                case 2:
                    return new CalendarFragment(); // 🚀 Index 2 (시작 화면)
                case 3:
                    return new QueryFragment();
                case 4:
                    return new ResultFragment();
                default:
                    return new CalendarFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}
