package com.example.my_custom_calenda_1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_custom_calenda_and_room.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class QueryFragment extends Fragment {

    private static final String DB_NAME = "calendar_database";
    public static ArrayList<Object> sendArrayListResult = new ArrayList<>();
    public static ArrayList<String> stringEvent = new ArrayList<String>();

    private SharedViewModel viewModel;
    private EditText queryEditText, dateconversionEditText;
    private Button find_selectquery_btn, find_insertquery_btn, prequery_btn, nextquery_btn, convertDateBtn;
    private TextView find_columview;
    private Button savequery_btn, removequery_btn;
    private RecyclerView savedquery_recyclerView;
    private SavedQueryAdapter savedQueryAdapter;

    private Button equel_btn, open_parenthesis_btn, close_parenthesis_btn, Percent_btn, Asterisk_btn, Apostrophe_btn;
    private Button Lessthansign_btn, greaterthan_btn, underscore_btn, plus_btn, minus_btn, slesh_btn, past_btn;

    private static final DateTimeFormatter dateformatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
            .appendOptional(DateTimeFormatter.ofPattern("yy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yy-M-d"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d"))
            .appendOptional(DateTimeFormatter.ofPattern("yy/M/d"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy년MM월dd일"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy년M월d일"))
            .appendOptional(DateTimeFormatter.ofPattern("yy년MM월dd일"))
            .appendOptional(DateTimeFormatter.ofPattern("yy년M월d일"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyMMdd"))
            .toFormatter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        queryEditText = view.findViewById(R.id.queryEditText);
        find_insertquery_btn = view.findViewById(R.id.insertquery_btn);
        find_selectquery_btn = view.findViewById(R.id.selectquery_btn);
        prequery_btn = view.findViewById(R.id.prequery_btn);
        nextquery_btn = view.findViewById(R.id.nextquery_btn);
        savequery_btn = view.findViewById(R.id.savequery_btn);
        removequery_btn = view.findViewById(R.id.removequery_btn);
        savedquery_recyclerView = view.findViewById(R.id.savedquery);
        find_columview = view.findViewById(R.id.columview);

        dateconversionEditText = view.findViewById(R.id.dateconversionEditText);
        convertDateBtn = view.findViewById(R.id.convertDateBtn);

        equel_btn = view.findViewById(R.id.equel_btn);
        open_parenthesis_btn = view.findViewById(R.id.open_parenthesis_btn);
        close_parenthesis_btn = view.findViewById(R.id.close_parenthesis_btn);
        Percent_btn = view.findViewById(R.id.Percent_btn);
        Asterisk_btn = view.findViewById(R.id.Asterisk_btn);
        Apostrophe_btn = view.findViewById(R.id.Apostrophe_btn);
        Lessthansign_btn = view.findViewById(R.id.Lessthansign);
        greaterthan_btn = view.findViewById(R.id.greaterthan_btn);
        underscore_btn = view.findViewById(R.id.underscore_btn);
        plus_btn = view.findViewById(R.id.plus_btn);
        minus_btn = view.findViewById(R.id.minus_btn);
        slesh_btn = view.findViewById(R.id.slesh_btn);
        past_btn = view.findViewById(R.id.past_btn);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupSymbolButton(equel_btn, "=");
        setupSymbolButton(open_parenthesis_btn, "(");
        setupSymbolButton(close_parenthesis_btn, ")");
        setupSymbolButton(Percent_btn, "%");
        setupSymbolButton(Asterisk_btn, "*");
        setupSymbolButton(Apostrophe_btn, "'");
        setupSymbolButton(Lessthansign_btn, "<");
        setupSymbolButton(greaterthan_btn, ">");
        setupSymbolButton(underscore_btn, "_");
        setupSymbolButton(plus_btn, "+");
        setupSymbolButton(minus_btn, "-");
        setupSymbolButton(slesh_btn, "/");

        if (past_btn != null) {
            past_btn.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null && clipboard.hasPrimaryClip()) {
                    ClipData clip = clipboard.getPrimaryClip();
                    if (clip != null && clip.getItemCount() > 0) {
                        CharSequence text = clip.getItemAt(0).getText();
                        if (text != null) insertAtCursor(text.toString());
                    }
                }
            });
        }

        if (dateconversionEditText != null) {
            dateconversionEditText.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String input = dateconversionEditText.getText().toString().trim().replaceAll(" ", "");
                    try {
                        LocalDate date = LocalDate.parse(input, dateformatter);
                        dateconversionEditText.setText(date.toString());
                        dateconversionEditText.setSelection(date.toString().length());
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "날짜 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
        }

        if (convertDateBtn != null) {
            convertDateBtn.setOnClickListener(v -> {
                String date = dateconversionEditText.getText().toString().trim();
                if (!date.isEmpty()) insertAtCursor(date);
            });
        }

        savedQueryAdapter = new SavedQueryAdapter(new SavedQueryAdapter.OnQueryClickListener() {
            @Override
            public void onQueryClick(SavedQuery savedQuery) {
                // 🚀 클릭 시 에딧텍스트에 적용하는 기능 제거 (선택 테두리 유지를 위해 리스너는 유지)
            }

            @Override
            public void onQueryLongClick(String query) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Saved Query", query);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "쿼리가 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        savedquery_recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        savedquery_recyclerView.setAdapter(savedQueryAdapter);
        refreshSavedQueries();

        if (savequery_btn != null) {
            savequery_btn.setOnClickListener(v -> {
                String currentQuery = queryEditText.getText().toString().trim();
                if (!currentQuery.isEmpty()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(requireContext()).savedQueryDao().insert(new SavedQuery(currentQuery, System.currentTimeMillis()));
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "쿼리가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            refreshSavedQueries();
                        });
                    });
                }
            });
        }

        if (removequery_btn != null) {
            removequery_btn.setOnClickListener(v -> {
                SavedQuery selected = savedQueryAdapter.getSelectedQuery();
                if (selected != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getDatabase(requireContext()).savedQueryDao().deleteById(selected.getId());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "쿼리가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            refreshSavedQueries();
                        });
                    });
                } else {
                    Toast.makeText(requireContext(), "삭제할 쿼리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        prequery_btn.setOnClickListener(v -> {
            String preQuery = viewModel.getPreviousQuery();
            if (preQuery != null) queryEditText.setText(preQuery);
            else Toast.makeText(requireContext(), "이전 기록이 없습니다.", Toast.LENGTH_SHORT).show();
        });

        nextquery_btn.setOnClickListener(v -> {
            String nextQuery = viewModel.getNextQuery();
            if (nextQuery != null) queryEditText.setText(nextQuery);
        });

        viewModel.getQueryText().observe(getViewLifecycleOwner(), text -> {
            if (!queryEditText.getText().toString().equals(text)) {
                queryEditText.setText(text);
            }
        });

        find_insertquery_btn.setOnClickListener(v -> {
            String query = queryEditText.getText().toString();
            viewModel.addQueryToHistory(query);
            viewModel.executor.execute(() -> {
                try {
                    viewModel.getOrOpenDatabase(requireContext(), DB_NAME);
                    viewModel.mDb.execSQL("BEGIN TRANSACTION;");
                    viewModel.mDb.execSQL(query);
                    viewModel.handler.post(() -> {
                        Toast.makeText(requireContext(), "쿼리 실행 완료! 커밋을 눌러주세요.", Toast.LENGTH_SHORT).show();
                        viewModel.setResultText("성공: " + query);
                    });
                } catch (Exception e) {
                    viewModel.handler.post(() -> viewModel.setResultText("에러: " + e.getMessage()));
                }
            });
        });

        find_selectquery_btn.setOnClickListener(v -> {
            String query = queryEditText.getText().toString();
            viewModel.addQueryToHistory(query);
            viewModel.executor.execute(() -> {
                Cursor cursor = null;
                try {
                    viewModel.getOrOpenDatabase(requireContext(), DB_NAME);
                    cursor = viewModel.mDb.rawQuery(query, null);
                    final StringBuilder resultBuilder = new StringBuilder();
                    if (cursor != null) {
                        sendArrayListResult.clear();
                        stringEvent.clear();
                        String[] columnNames = cursor.getColumnNames();
                        for (String col : columnNames) {
                            stringEvent.add(col);
                            resultBuilder.append("[").append(col).append("] ");
                        }
                        resultBuilder.append("\n------------------------------\n");
                        sendArrayListResult.add(new ArrayList<>(stringEvent));

                        while (cursor.moveToNext()) {
                            stringEvent.clear();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                String val = cursor.getString(i);
                                stringEvent.add(val);
                                resultBuilder.append(val).append(" | ");
                            }
                            resultBuilder.append("\n");
                            sendArrayListResult.add(new ArrayList<>(stringEvent));
                        }
                    }
                    viewModel.handler.post(() -> {
                        find_columview.setText(sendArrayListResult.get(0).toString());
                        viewModel.setResultText(resultBuilder.toString());
                        processSelectResult();
                    });
                } catch (Exception e) {
                    viewModel.handler.post(() -> viewModel.setResultText("에러: " + e.getMessage()));
                } finally {
                    if (cursor != null) cursor.close();
                }
            });
        });

        return view;
    }

    private void setupSymbolButton(Button button, String symbol) {
        if (button != null) {
            button.setOnClickListener(v -> insertAtCursor(symbol));
        }
    }

    private void insertAtCursor(String text) {
        int start = Math.max(queryEditText.getSelectionStart(), 0);
        int end = Math.max(queryEditText.getSelectionEnd(), 0);
        queryEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                text, 0, text.length());
    }

    private void processSelectResult() {
        if (sendArrayListResult == null || sendArrayListResult.isEmpty()) return;
        
        ArrayList<String> cols = (ArrayList<String>) sendArrayListResult.get(0);
        int idCol = cols.indexOf("id");
        int checkCol = cols.indexOf("isChecked");
        if (checkCol == -1) checkCol = cols.indexOf("check");
        int oneCol = cols.indexOf("one");
        int twoCol = cols.indexOf("two");
        int threeCol = cols.indexOf("three");
        int nameCol = cols.indexOf("name");
        int commentCol = cols.indexOf("comment");
        int contentCol = cols.indexOf("content");
        int startDateCol = cols.indexOf("startDate");
        int endDateCol = cols.indexOf("endDate");
        int colorCol = cols.indexOf("color");
        int createdCol = cols.indexOf("createdAt");

        OuterCalendarAdapter.sSCModel = new ArrayList<>();
        for (int j = 1; j < sendArrayListResult.size(); j++) {
            ArrayList<String> row = (ArrayList<String>) sendArrayListResult.get(j);
            SelectSendCalenderModel model = new SelectSendCalenderModel.Builder()
                    .setId(idCol != -1 ? row.get(idCol) : "0")
                    .setChecked(checkCol != -1 && (row.get(checkCol).equals("1") || Boolean.parseBoolean(row.get(checkCol))))
                    .setOne(oneCol != -1 ? row.get(oneCol) : "")
                    .setTwo(twoCol != -1 ? row.get(twoCol) : "")
                    .setThree(threeCol != -1 ? row.get(threeCol) : "")
                    .setName(nameCol != -1 ? row.get(nameCol) : "")
                    .setComment(commentCol != -1 ? row.get(commentCol) : "")
                    .setContent(contentCol != -1 ? row.get(contentCol) : "")
                    .setStartDate(startDateCol != -1 ? row.get(startDateCol) : null)
                    .setEndDate(endDateCol != -1 ? row.get(endDateCol) : null)
                    .setColor(colorCol != -1 ? Integer.parseInt(row.get(colorCol)) : 0)
                    .setCreatedAt(createdCol != -1 ? row.get(createdCol) : null)
                    .build();
            OuterCalendarAdapter.sSCModel.add(model);
        }
    }

    private void refreshSavedQueries() {
        Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<SavedQuery> savedQueries = AppDatabase.getDatabase(requireContext()).savedQueryDao().getAllSavedQueries();
            requireActivity().runOnUiThread(() -> {
                if (savedQueryAdapter != null) savedQueryAdapter.setQueries(savedQueries);
            });
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (queryEditText != null) viewModel.setQueryText(queryEditText.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queryEditText != null) viewModel.setQueryText(queryEditText.getText().toString());
    }
}
