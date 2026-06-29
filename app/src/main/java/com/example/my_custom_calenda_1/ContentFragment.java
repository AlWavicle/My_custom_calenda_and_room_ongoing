package com.example.my_custom_calenda_1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;

public class ContentFragment extends Fragment {

    private SharedViewModel viewModel;
    private TextView tvDateRange, tvName, tvComment, tvContent;
    private EditText etName, etComment, etContent;
    private Button btnSave;
    private SelectSendCalenderModel currentModel;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content, container, false);

        tvDateRange = view.findViewById(R.id.tv_date_range);
        tvName = view.findViewById(R.id.tv_name);
        tvComment = view.findViewById(R.id.tv_comment);
        tvContent = view.findViewById(R.id.tv_content);
        etName = view.findViewById(R.id.et_name);
        etComment = view.findViewById(R.id.et_comment);
        etContent = view.findViewById(R.id.et_content);
        btnSave = view.findViewById(R.id.btn_save_content);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getSelectedEvent().observe(getViewLifecycleOwner(), model -> {
            if (model != null) {
                currentModel = model;
                updateUI();
            }
        });

        // 🚀 달력에서 선택된 날짜 관찰 및 적용
        viewModel.getCalendarSelection().observe(getViewLifecycleOwner(), selection -> {
            if (isEditMode && currentModel != null && selection != null && !selection.isEmpty()) {
                LocalDate start = selection.get(0);
                LocalDate end = (selection.size() > 1) ? selection.get(1) : null;
                
                // 시작일이 종료일보다 뒤면 교체
                if (start != null && end != null && end.isBefore(start)) {
                    LocalDate temp = start;
                    start = end;
                    end = temp;
                }

                currentModel.setStartDate(start);
                currentModel.setEndDate(end);
                updateDateText(); // 날짜 텍스트 즉시 갱신
            }
        });

        // 날짜 텍스트 클릭 시 달력 화면으로 이동하도록 유도 (편의성)
        tvDateRange.setOnClickListener(v -> {
            if (isEditMode) {
                Toast.makeText(requireContext(), "달력에서 날짜를 선택한 후 돌아오세요.", Toast.LENGTH_SHORT).show();
                viewModel.navigateTo(2); // 달력(Index 2)으로 이동
            }
        });

        View.OnLongClickListener longClickListener = v -> {
            toggleEditMode(true);
            return true;
        };

        tvName.setOnLongClickListener(longClickListener);
        tvComment.setOnLongClickListener(longClickListener);
        tvContent.setOnLongClickListener(longClickListener);
        tvDateRange.setOnLongClickListener(longClickListener);

        btnSave.setOnClickListener(v -> {
            if (currentModel != null) {
                currentModel.setName(etName.getText().toString());
                currentModel.setComment(etComment.getText().toString());
                currentModel.setContent(etContent.getText().toString());

                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase db = AppDatabase.getDatabase(requireContext());
                    Event event = new Event(currentModel.getId(), currentModel.isChecked(), currentModel.getOne(),
                            currentModel.getTwo(), currentModel.getThree(), currentModel.getName(),
                            currentModel.getComment(), currentModel.getContent(), currentModel.getStartDate(),
                            currentModel.getEndDate(), currentModel.getColor(), currentModel.getCreatedAt());
                    db.eventDao().update(event);
                    
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        toggleEditMode(false);
                        updateUI();
                        Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        return view;
    }

    private void updateUI() {
        if (currentModel != null) {
            tvName.setText(currentModel.getName());
            tvComment.setText(currentModel.getComment());
            tvContent.setText(currentModel.getContent());
            etName.setText(currentModel.getName());
            etComment.setText(currentModel.getComment());
            etContent.setText(currentModel.getContent());
            updateDateText();
        }
    }

    private void updateDateText() {
        if (currentModel != null) {
            String start = (currentModel.getStartDate() != null) ? currentModel.getStartDate().toString() : "null";
            String end = (currentModel.getEndDate() != null) ? currentModel.getEndDate().toString() : "null";
            tvDateRange.setText(start + " ~ " + end);
            
            // 편집 모드일 때 강조 효과
            if (isEditMode) {
                tvDateRange.setBackgroundColor(Color.parseColor("#FFFFE0B2")); // 연한 주황색 (편집 알림)
            } else {
                tvDateRange.setBackgroundColor(Color.parseColor("#FFF0F0")); // 기본 배경
            }
        }
    }

    private void toggleEditMode(boolean isEditing) {
        this.isEditMode = isEditing;
        tvName.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        tvComment.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        tvContent.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        etName.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        etComment.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        etContent.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        updateDateText();
    }
}
