package com.example.my_custom_calenda_1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;

public class NullDateFragment extends Fragment {

    private SharedViewModel viewModel;
    private RecyclerView recyclerView;
    private NullDateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nulldate, container, false);
        recyclerView = view.findViewById(R.id.rv_null_dates);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        adapter = new NullDateAdapter(new NullDateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                // 🚀 클릭 시 ContentFragment(Index 1)로 연결
                SelectSendCalenderModel model = convertToModel(event);
                viewModel.setSelectedEvent(model);
                viewModel.navigateTo(1);
            }

            @Override
            public void onItemLongClick(Event event) {
                // 🚀 오래 클릭 시 일자 추가
                showAddDateDialog(event);
            }
        });

        recyclerView.setAdapter(adapter);
        loadNullDateEvents();

        return view;
    }

    private void loadNullDateEvents() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            List<Event> events = db.eventDao().getNullDateEvents();
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapter.setEvents(events);
            });
        });
    }

    private SelectSendCalenderModel convertToModel(Event e) {
        return new SelectSendCalenderModel.Builder()
                .setId(e.getId())
                .setChecked(e.isChecked())
                .setOne(e.getOne())
                .setTwo(e.getTwo())
                .setThree(e.getThree())
                .setName(e.getName())
                .setComment(e.getComment())
                .setContent(e.getContent())
                .setStartDate(e.getStartDate())
                .setEndDate(e.getEndDate())
                .setColor(e.getColor())
                .setCreatedAt(e.getCreatedAt())
                .build();
    }

    private void showAddDateDialog(Event event) {
        LocalDate today = LocalDate.now();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            LocalDate startDate = LocalDate.of(year, month + 1, dayOfMonth);
            
            // 시작일 선택 후 종료일 선택
            new DatePickerDialog(requireContext(), (view2, year2, month2, dayOfMonth2) -> {
                LocalDate endDate = LocalDate.of(year2, month2 + 1, dayOfMonth2);
                
                event.setStartDate(startDate);
                event.setEndDate(endDate);
                
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getDatabase(requireContext()).eventDao().update(event);
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "날짜가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        loadNullDateEvents(); // 리스트 갱신
                    });
                });
            }, year, month, dayOfMonth).show();
            
        }, today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNullDateEvents();
    }
}
