package com.example.my_custom_calenda_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {

    WeekEventView weekEventLayer;
    TextView find_yearmonthText;
    LocalDate local_selectedDate;
    RecyclerView find_cal_num_recyclerView;
    ArrayList<Event> eventList;
    TextView find_pre_view;
    Button preBtn, nextBtn;
    EditText find_schedule_editText;
    Button find_save_btn, find_remove_btn, find_refresh_btn;
    int romove;
    int nums;

    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        myFindViewByIdSet(view);
        make_EventList();
        local_selectedDate = LocalDate.now();
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setRecyclerView();

        preBtn.setOnClickListener(v -> {
            local_selectedDate = local_selectedDate.minusMonths(1);
            setRecyclerView();
        });

        nextBtn.setOnClickListener(v -> {
            local_selectedDate = local_selectedDate.plusMonths(1);
            setRecyclerView();
        });

        find_save_btn.setOnClickListener(v -> {
            String scheduleText = find_schedule_editText.getText().toString();
            if (!scheduleText.isEmpty()) {
                OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
                LocalDate startDate = null, endDate = null;
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
                        make_EventList();
                        Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                });
            } else {
                Toast.makeText(requireContext(), "일정 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        find_remove_btn.setOnClickListener(v -> {
            if (OuterCalendarAdapter.sSCModel != null && romove >= 0 && romove < OuterCalendarAdapter.sSCModel.size()) {
                int removeIdNum = OuterCalendarAdapter.sSCModel.get(romove).getId();
                if (removeIdNum != -1) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(requireContext()).eventDao().deleteById(removeIdNum);
                        requireActivity().runOnUiThread(() -> {
                            romove = -1;
                            find_pre_view.setText("Selected Date Info");
                            make_EventList();
                            Toast.makeText(requireContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    Toast.makeText(requireContext(), "삭제할 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "삭제할 일정을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        find_refresh_btn.setOnClickListener(v -> {
            make_EventList();
            Toast.makeText(requireContext(), "데이터를 새로고침했습니다.", Toast.LENGTH_SHORT).show();
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
        find_refresh_btn = view.findViewById(R.id.reset_btn);
        weekEventLayer = view.findViewById(R.id.weekEventView);
    }

    private void make_EventList() {
        eventList = new ArrayList<>();
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Event> savedEvents = db.eventDao().getAllEvents();
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> {
                eventList.clear();
                eventList.addAll(savedEvents);
                setRecyclerView();
            });
        });
    }

    private String localdate_fomat_tostring(LocalDate local_date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-M");
        return local_date.format(formatter);
    }

    private void showEditDialog(SelectSendCalenderModel model) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("일정 수정");
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        EditText nameEdit = createEditText("이름", model.getName());
        EditText oneEdit = createEditText("One", model.getOne());
        EditText twoEdit = createEditText("Two", model.getTwo());
        EditText threeEdit = createEditText("Three", model.getThree());
        EditText commentEdit = createEditText("Comment", model.getComment());
        EditText contentEdit = createEditText("Content", model.getContent());

        layout.addView(nameEdit); layout.addView(oneEdit); layout.addView(twoEdit);
        layout.addView(threeEdit); layout.addView(commentEdit); layout.addView(contentEdit);

        builder.setView(layout);
        builder.setPositiveButton("저장", (dialog, which) -> {
            model.setName(nameEdit.getText().toString());
            model.setOne(oneEdit.getText().toString());
            model.setTwo(twoEdit.getText().toString());
            model.setThree(threeEdit.getText().toString());
            model.setComment(commentEdit.getText().toString());
            model.setContent(contentEdit.getText().toString());

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                Event event = new Event(model.getId(), model.isChecked(), model.getOne(), model.getTwo(), model.getThree(),
                        model.getName(), model.getComment(), model.getContent(), model.getStartDate(), model.getEndDate(), 
                        model.getColor(), model.getCreatedAt());
                db.eventDao().update(event);
                requireActivity().runOnUiThread(() -> {
                    OuterCalendarAdapter adapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
                    if (adapter != null) adapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                });
            });
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private EditText createEditText(String hint, String text) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setText(text);
        return et;
    }

    private void setRecyclerView() {
        find_yearmonthText.setText(localdate_fomat_tostring(local_selectedDate));
        ArrayList<LocalDate> Local_Cal_ArrayDayList = Create_aLocalArray_ofDays_fortheCalendar(local_selectedDate);
        OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
        List<LocalDate> selDates = (currentAdapter != null) ? currentAdapter.seldate : new ArrayList<>();
        List<Object> weekRowItems = createWeekRowData(Local_Cal_ArrayDayList, eventList, selDates);

        OuterCalendarAdapter adapter = new OuterCalendarAdapter(weekRowItems, new InnerDayAdapter.OnDayClickListener() {
            @Override
            public void onDayClick(LocalDate date, int parentWeekPosition, ArrayList<SelectSendCalenderModel> eventsOnDay, ArrayList<Integer> eventsIndex) {
                if (local_selectedDate != null && local_selectedDate.equals(date)) nums++;
                else nums = 1;
                local_selectedDate = date;
                OuterCalendarAdapter.clicksel = date;
                OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter();
                if (currentAdapter != null) {
                    currentAdapter.selectionStep = (currentAdapter.selectionStep % 4) + 1;
                    if (currentAdapter.selectionStep == 1) { currentAdapter.seldate.clear(); currentAdapter.seldate.add(date); }
                    else if (currentAdapter.selectionStep == 2) { if (currentAdapter.seldate.isEmpty()) currentAdapter.seldate.add(null); currentAdapter.seldate.add(date); }
                    else if (currentAdapter.selectionStep == 3) { if (!currentAdapter.seldate.isEmpty()) currentAdapter.seldate.set(0, null); }
                    else { currentAdapter.seldate.clear(); currentAdapter.selectionStep = 0; }
                    // 🚀 선택된 날짜 범위를 ViewModel에 전달 (ContentFragment와 연동)
                    viewModel.setCalendarSelection(new ArrayList<>(currentAdapter.seldate));
                    currentAdapter.notifyDataSetChanged(); 
                }
                if (currentAdapter != null) {
                    LocalDate start = (currentAdapter.seldate.size() > 0) ? currentAdapter.seldate.get(0) : null;
                    LocalDate end = (currentAdapter.seldate.size() > 1) ? currentAdapter.seldate.get(1) : null;
                    String dateInfo = "시작: " + (start != null ? start : "null") + " / 종료: " + (end != null ? end : "null");
                    if (eventsIndex != null && !eventsIndex.isEmpty()) {
                        int targetIndex = (nums - 1) % eventsIndex.size();
                        romove = eventsIndex.get(targetIndex);
                        if (romove >= 0 && romove < OuterCalendarAdapter.sSCModel.size()) {
                            find_pre_view.setText("id: "+OuterCalendarAdapter.sSCModel.get(romove).getId()+"  "+dateInfo + "\n(일정: " + OuterCalendarAdapter.sSCModel.get(romove).getName() + ")");
                        } else { find_pre_view.setText(dateInfo); romove = -1; }
                    } else { find_pre_view.setText(dateInfo); romove = -1; }
                }
                ((OuterCalendarAdapter) find_cal_num_recyclerView.getAdapter()).showDetail(parentWeekPosition, eventsOnDay);
            }
            @Override public void onEventEdit(SelectSendCalenderModel event) { showEditDialog(event); }
            @Override public void onCheckChanged(SelectSendCalenderModel event, boolean isChecked) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase db = AppDatabase.getDatabase(requireContext());
                    db.eventDao().updateCheckStatus(event.getId(), isChecked);
                    requireActivity().runOnUiThread(() -> {
                        event.setChecked(isChecked);
                        Toast.makeText(requireContext(), "체크 상태가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                });
            }
            @Override public void onEventSelected(SelectSendCalenderModel event) {
                viewModel.setSelectedEvent(event);
            }
        });
        find_cal_num_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        find_cal_num_recyclerView.setAdapter(adapter);
        find_cal_num_recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child == null) {
                        OuterCalendarAdapter currentAdapter = (OuterCalendarAdapter) rv.getAdapter();
                        if (currentAdapter != null) currentAdapter.showDetail(-1, null);
                    }
                }
                return false;
            }
            @Override public void onTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    private List<WeekEvent> getWeekEventsForLayer(ArrayList<LocalDate> calendarDays, ArrayList<SelectSendCalenderModel> allEvents) {
        List<WeekEvent> weekEvents = new ArrayList<>();
        for (SelectSendCalenderModel event : allEvents) {
            LocalDate startDate = event.getStartDate(), endDate = event.getEndDate();
            int type = 0;
            if (startDate != null && endDate != null) { if (startDate.equals(endDate)) type = 3; else type = 0; }
            else if (startDate != null) { type = 1; endDate = startDate; }
            else if (endDate != null) { type = 2; startDate = endDate; }
            else continue;
            int startIndex = getDayIndex(startDate, calendarDays, true);
            int endIndex = getDayIndex(endDate, calendarDays, false);
            if (startIndex != -1 && endIndex != -1 && startIndex <= endIndex) {
                WeekEvent we = new WeekEvent(startIndex, endIndex, event.getColor(), event.getName());
                we.type = type; we.id = event.getId();
                weekEvents.add(we);
            }
        }
        return weekEvents;
    }

    private int getDayIndex(LocalDate targetDate, ArrayList<LocalDate> calendarDays, boolean isStart) {
        if (targetDate == null) return -1;
        LocalDate firstDateOfMonth = null, lastDateOfMonth = null;
        int firstDateIndex = -1, lastDateIndex = -1;
        for (int i = 0; i < calendarDays.size(); i++) {
            if (calendarDays.get(i) != null) {
                if (firstDateOfMonth == null) { firstDateOfMonth = calendarDays.get(i); firstDateIndex = i; }
                lastDateOfMonth = calendarDays.get(i); lastDateIndex = i;
            }
        }
        if (isStart && targetDate.isBefore(firstDateOfMonth)) return firstDateIndex;
        if (!isStart && targetDate.isAfter(lastDateOfMonth)) return lastDateIndex;
        for (int i = 0; i < calendarDays.size(); i++) {
            if (calendarDays.get(i) != null && calendarDays.get(i).equals(targetDate)) return i;
        }
        return -1;
    }

    private ArrayList<LocalDate> Create_aLocalArray_ofDays_fortheCalendar(LocalDate date){
        ArrayList<LocalDate> local_cal_dayList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int lastDate = yearMonth.lengthOfMonth();
        LocalDate firstDay = date.withDayOfMonth(1);
        int dayofweek = firstDay.getDayOfWeek().getValue();
        int offset = dayofweek % 7;
        for(int i = 0; i < 42; i++){
            if(i < offset || i >= lastDate + offset) local_cal_dayList.add(null);
            else local_cal_dayList.add(LocalDate.of(date.getYear(), date.getMonth(), i - offset + 1));
        }
        return local_cal_dayList;
    }

    private List<Object> createWeekRowData(ArrayList<LocalDate> dayList, ArrayList<Event> allEvents, List<LocalDate> selDates) {
        List<Object> weekRows = new ArrayList<>();
        ArrayList<SelectSendCalenderModel> sscEvents = new ArrayList<>();
        if (OuterCalendarAdapter.sSCModel != null && !OuterCalendarAdapter.sSCModel.isEmpty()) {
            sscEvents.addAll(OuterCalendarAdapter.sSCModel);
        }
        List<WeekEvent> allWeekEvents = getWeekEventsForLayer(dayList, sscEvents);
        for (int i = 0; i < 42; i += 7) {
            WeekRow row = new WeekRow();
            for (int j = 0; j < 7; j++) row.days.add(dayList.get(i + j));
            List<Integer> lanesOccupiedUntil = new ArrayList<>();
            for (WeekEvent we : allWeekEvents) {
                int overlapStart = Math.max(we.startDayIndex, i), overlapEnd = Math.min(we.endDayIndex, i + 6);
                if (overlapStart <= overlapEnd) {
                    int startInWeek = overlapStart - i, endInWeek = overlapEnd - i;
                    int targetLane = -1;
                    for (int l = 0; l < lanesOccupiedUntil.size(); l++) { if (lanesOccupiedUntil.get(l) < startInWeek) { targetLane = l; break; } }
                    if (targetLane == -1) { targetLane = lanesOccupiedUntil.size(); lanesOccupiedUntil.add(endInWeek); }
                    else { lanesOccupiedUntil.set(targetLane, endInWeek); }
                    row.addRowEvent(new WeekEvent(startInWeek, endInWeek, we.color, we.title, targetLane, we.type, we.id));
                }
            }
            weekRows.add(row);
        }
        return weekRows;
    }
}
