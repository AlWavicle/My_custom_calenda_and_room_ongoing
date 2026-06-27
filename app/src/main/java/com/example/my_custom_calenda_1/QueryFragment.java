package com.example.my_custom_calenda_1;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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



    // 데이터베이스 이름 정의 (Room에서 사용하는 이름과 동일하게 맞춤)
    private static final String DB_NAME = "calendar_database";

    public static ArrayList<Object> sendArrayListResult = new ArrayList<>();

    public static ArrayList<String> stringEvent = new ArrayList<String>();

    private SharedViewModel viewModel;
    private EditText queryEditText, dateconversionEditText;
    private Button find_selectquery_btn, find_insertquery_btn, prequery_btn, nextquery_btn, convertDateBtn;
    
    // 날짜 변환용 포맷터
    // 2자리 년도(yy)와 4자리 년도(yyyy)를 모두 지원하도록 개선
    private static final DateTimeFormatter dateformatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
            .appendOptional(DateTimeFormatter.ofPattern("yy-MM-dd"))   // 26-11-01 형식 지원
            .appendOptional(DateTimeFormatter.ofPattern("yy-M-d"))     // 26-11-1 형식 지원
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d"))
            .appendOptional(DateTimeFormatter.ofPattern("yy/M/d"))     // 26/11/1 형식 지원
            .appendOptional(DateTimeFormatter.ofPattern("yyyy년MM월dd일"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy년M월d일"))
            .appendOptional(DateTimeFormatter.ofPattern("yy년MM월dd일")) // 26년11월01일 형식 지원
            .appendOptional(DateTimeFormatter.ofPattern("yy년M월d일"))   // 26년11월1일 형식 지원
            .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyMMdd"))
            .toFormatter();

    private Button equel_btn, open_parenthesis_btn, close_parenthesis_btn, Percent_btn, Asterisk_btn, Apostrophe_btn;
    private Button Lessthansign_btn, greaterthan_btn, underscore_btn, plus_btn, minus_btn, slesh_btn, past_btn;

    private TextView find_columview;
    private Button savequery_btn;
    private Button removequery_btn;
    private RecyclerView savedquery_recyclerView;
    private SavedQueryAdapter savedQueryAdapter;

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
        greaterthan_btn = view.findViewById(R.id.openparenthesis_btn); // XML ID가 openparenthesis_btn임
        underscore_btn = view.findViewById(R.id.underscore_btn);
        plus_btn = view.findViewById(R.id.plus_btn);
        minus_btn = view.findViewById(R.id.minus_btn);
        slesh_btn = view.findViewById(R.id.slesh_btn);
        past_btn = view.findViewById(R.id.past_btn);

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

        // SavedQuery 리사이클러뷰 설정
        savedQueryAdapter = new SavedQueryAdapter(new SavedQueryAdapter.OnQueryClickListener() {
            @Override
            public void onQueryClick(SavedQuery savedQuery) {
                queryEditText.setText(savedQuery.getQuery());
            }

            @Override
            public void onQueryLongClick(String query) {
                // 🚀 롱클릭 시 클립보드에 복사
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Saved Query", query);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "쿼리가 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        savedquery_recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        savedquery_recyclerView.setAdapter(savedQueryAdapter);
        refreshSavedQueries();

        // savequery 버튼 클릭 처리
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

        // removequery 버튼 클릭 처리
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

        // 1. 날짜 변환 에딧텍스트 엔터 처리
        if (dateconversionEditText != null) {
            // 엔터 시 줄바꿈 방지를 위해 싱글라인 설정 및 키보드 엔터 액션 설정
            dateconversionEditText.setSingleLine(true);
            dateconversionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

            dateconversionEditText.setOnKeyListener((v, keyCode, event) -> {
                // 엔터 키를 눌렀을 때 실행 (ACTION_DOWN)
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String input = dateconversionEditText.getText().toString().trim();
                    
                    // "202 6 년 5월 6 일 " 같이 띄어쓰기가 섞인 경우를 대비해 
                    // 숫자/년/월/일/기호 외의 모든 공백을 제거
                    String cleanInput = input.replaceAll(" ", "");
                    
                    try {
                        // 텍스트를 LocalDate로 변환 시도
                        LocalDate parsedDate = LocalDate.parse(cleanInput, dateformatter);
                        // 다시 표준 포맷(yyyy-MM-dd)의 문자열로 변환
                        String result = parsedDate.toString();
                        // 에딧텍스트 업데이트
                        dateconversionEditText.setText(result);
                        dateconversionEditText.setSelection(result.length()); // 커서를 끝으로
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "날짜 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return true; // 이벤트 소비 (줄바꿈 방지)
                }
                return false;
            });
        }

        // removequery 버튼 클릭 처리
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

        // 2. 컨버트 버튼 클릭 처리 (현재 커서 위치에 붙여넣기)
        if (convertDateBtn != null) {
            convertDateBtn.setOnClickListener(v -> {
                String dateStr = dateconversionEditText.getText().toString().trim();
                if (!dateStr.isEmpty()) {
                    int start = Math.max(queryEditText.getSelectionStart(), 0);
                    int end = Math.max(queryEditText.getSelectionEnd(), 0);
                    queryEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                            dateStr, 0, dateStr.length());
                }
            });
        }

        // removequery 버튼 클릭 처리
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

        // 붙여넣기(Paste) 버튼 기능 구현
        if (past_btn != null) {
            past_btn.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null && clipboard.hasPrimaryClip()) {
                    ClipData clip = clipboard.getPrimaryClip();
                    if (clip != null && clip.getItemCount() > 0) {
                        CharSequence pasteData = clip.getItemAt(0).getText();
                        if (pasteData != null) {
                            int start = Math.max(queryEditText.getSelectionStart(), 0);
                            int end = Math.max(queryEditText.getSelectionEnd(), 0);
                            queryEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                                    pasteData, 0, pasteData.length());
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "클립보드가 비어있습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // removequery 버튼 클릭 처리
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

        // Activity 범위의 SharedViewModel 가져오기 (3개 페이지가 공유함)
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 이전 쿼리 버튼 클릭
        prequery_btn.setOnClickListener(v -> {
            String preQuery = viewModel.getPreviousQuery();
            if (preQuery != null) {
                queryEditText.setText(preQuery);
            } else {
                Toast.makeText(requireContext(), "이전 기록이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        // 다음 쿼리 버튼 클릭
        nextquery_btn.setOnClickListener(v -> {
            String nextQuery = viewModel.getNextQuery();
            if (nextQuery != null) {
                queryEditText.setText(nextQuery);
            }
        });

        // ViewModel에 저장된 값을 가져와서 EditText에 복원 (화면 스와이프 복귀 시)
        viewModel.getQueryText().observe(getViewLifecycleOwner(), text -> {
            if (!queryEditText.getText().toString().equals(text)) {
                queryEditText.setText(text);
            }
        });
        //String query = queryEditText.getText().toString();


        find_insertquery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 실행할 날것의 인서트 쿼리문
                String query = queryEditText.getText().toString();
                final String rawInsertQuery = query;

                // 쿼리 기록에 추가
                viewModel.addQueryToHistory(rawInsertQuery);

                viewModel.executor.execute(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            // 🚀 [수정] ViewModel의 헬퍼 메서드 사용 (이미 열려있으면 그대로 사용)
                            viewModel.getOrOpenDatabase(requireContext(), DB_NAME);

                            // 1. 트랜잭션 시작
                            viewModel.mDb.execSQL("BEGIN TRANSACTION;");

                            // 쿼리 실행
                            viewModel.mDb.execSQL(rawInsertQuery);

                            // 🚀 [수정] 자동 COMMIT 제거 (ResultFragment에서 수동 커밋하도록 유도)
                            // viewModel.mDb.execSQL("COMMIT;");

                            viewModel.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "쿼리 실행 완료! 결과 페이지에서 [커밋]을 눌러야 저장됩니다.", Toast.LENGTH_SHORT).show();
                                    // 🚀 [수정] ResultFragment로 데이터 전달 (Bundle에 쿼리문 담기)
                                    viewModel.setResultText("쿼리가 성공적으로 실행되었습니다(트랜잭션 대기 중).\n실행 쿼리: " + rawInsertQuery);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("QUERY_DATA", rawInsertQuery);
                                    getParentFragmentManager().setFragmentResult("DATA_KEY", bundle);

                                    getParentFragmentManager().setFragmentResult("QUERY_EXECUTED", new Bundle());
                                }
                            });
                        } catch (Exception e) {
                            final String errorMessage = e.getMessage();
                            viewModel.handler.post(() -> {
                                viewModel.setResultText("에러 발생: " + errorMessage);
                                Toast.makeText(requireContext(), "쿼리 실패!", Toast.LENGTH_SHORT).show();
                            });
                            e.printStackTrace();
                        } finally {
                            // 🚀 [수정] 여기서 DB를 닫지 않습니다! (ResultFragment에서 커밋/롤백 후 닫아야 함)
                            // if (viewModel.mDb != null && viewModel.mDb.isOpen()) viewModel.mDb.close();
                        }
                    }
                });
            }
        });

        find_selectquery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 실행할 날것의 셀렉트 쿼리문
                String query = queryEditText.getText().toString();
                final String rawSelectQuery = query;

                // 쿼리 기록에 추가
                viewModel.addQueryToHistory(rawSelectQuery);

                viewModel.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = null;
                        try {
                            // 🚀 [수정] ViewModel의 헬퍼 메서드 사용
                            viewModel.getOrOpenDatabase(requireContext(), DB_NAME);
                            cursor = viewModel.mDb.rawQuery(rawSelectQuery, null);

                            final StringBuilder resultBuilder = new StringBuilder();
                            if (cursor != null) {
                                // 모든 컬럼 이름을 가져와서 상단에 표시
                                sendArrayListResult.clear();//이전 내용 지우기
                                stringEvent.clear();
                                String[] columnNames = cursor.getColumnNames();
                                for (String col : columnNames) {
                                    resultBuilder.append("[     ").append(col).append("     ] ");
                                    stringEvent.add(col);
                                }


                                sendArrayListResult.add(new ArrayList<>(stringEvent));
                                Log.d("오호리", sendArrayListResult.toString());
                                resultBuilder.append("\n------------------------------\n");

                                while (cursor.moveToNext()) {
                                    stringEvent.clear();
                                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                                        resultBuilder.append(cursor.getString(i)).append(" | ");
                                        stringEvent.add(cursor.getString(i));
                                    }
                                    resultBuilder.append("\n");
                                    sendArrayListResult.add(new ArrayList<>(stringEvent));
                                }
                                Log.d("오호리", sendArrayListResult.toString());




                            }

                            // 최종 결과 문자열 생성
                            String tempResult = resultBuilder.toString().trim();
                            if (tempResult.isEmpty()) {
                                tempResult = "조회된 데이터가 없습니다.";
                            }

                            // 안드로이드에서 익명 클래스 내부에 변수를 전달하기 위해 final 선언
                            final String finalResult = tempResult;

                            viewModel.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 🚀 [수정] ViewModel에 결과 저장 (ResultFragment가 실시간으로 감지)
                                    find_columview.setText(sendArrayListResult.get(0).toString());
                                    viewModel.setResultText(finalResult);
                                    // 기존 방식: Fragment Result API로 전달
                                    Bundle bundle = new Bundle();
                                    bundle.putString("QUERY_DATA", finalResult);
                                    getParentFragmentManager().setFragmentResult("DATA_KEY", bundle);
                                }
                            });
                        } catch (Exception e) {
                            final String errorMessage = e.getMessage();
                            viewModel.handler.post(() -> {
                                viewModel.setResultText("조회 에러: " + errorMessage);
                            });
                            e.printStackTrace();
                        } finally {
                            // 1. 커서는 무조건 닫아줍니다. (메모리 누수 방지)
                            if (cursor != null && !cursor.isClosed()) cursor.close();
                            // 2. DB가 열려있고, 트랜잭션 중이 아닐 때만 닫아줍니다!
                            if (viewModel.mDb != null && viewModel.mDb.isOpen()) {
                                if (!viewModel.mDb.inTransaction()) { // 🚀 현재 트랜잭션 중이 아니라면? (자물쇠가 풀려있다면)
                                    viewModel.mDb.close();
                                    viewModel.mDb = null;
                                } else {
                                    Log.d("DB_TEST", "현재 트랜잭션 대기 중이므로 DB를 닫지 않고 유지합니다.");
                                }
                            }


                            //____
                            if (sendArrayListResult != null && !sendArrayListResult.isEmpty()) {
                                int i = 0;
                                int idCol = -1;
                                int nameCol = -1;
                                int startDateCol = -1;
                                int endDateCol = -1;
                                int colorCol = -1;
                                int startTimeCol = -1;
                                int endTimeCol = -1;
                                int memoCol = -1;
                                int isAllDayCol = -1;
                                int category1Col = -1;
                                int category2Col = -1;
                                int createdAtCol = -1;
                                for (String col : (ArrayList<String>)sendArrayListResult.get(0)) {
                                    switch (col){
                                        case "id":
                                            idCol=i;
                                            break;
                                        case "name":
                                            nameCol=i;
                                            break;
                                        case "startDate":
                                            startDateCol=i;
                                            break;
                                        case "endDate":
                                            endDateCol=i;
                                            break;
                                        case "color":
                                            colorCol=i;
                                            break;
                                        case "startTime":
                                            startTimeCol=i;
                                            break;
                                        case "endTime":
                                            endTimeCol=i;
                                            break;
                                        case "memo":
                                            memoCol=i;
                                            break;
                                        case "isAllDay":
                                            isAllDayCol=i;
                                            break;
                                        case "category1":
                                            category1Col=i;
                                            break;
                                        case "category2":
                                            category2Col=i;
                                            break;
                                        case "createdAt":
                                            createdAtCol=i;
                                            break;
                                    }
                                    i++;
                                }

                                OuterCalendarAdapter.sSCModel = new ArrayList<SelectSendCalenderModel>();
                                for (int j = 1; j < sendArrayListResult.size(); j++) {
                                    ArrayList<String> selevent = (ArrayList<String>)sendArrayListResult.get(j);
                                    SelectSendCalenderModel model = new SelectSendCalenderModel.Builder()
                                            .setId(idCol != -1 ? selevent.get(idCol) : null)
                                            .setName(nameCol != -1 ? selevent.get(nameCol) : null)
                                            .setStartDate(startDateCol != -1 && selevent.get(startDateCol) != null ? LocalDate.parse(selevent.get(startDateCol)) : (LocalDate)null)
                                            .setEndDate(endDateCol != -1 && selevent.get(endDateCol) != null ? LocalDate.parse(selevent.get(endDateCol)) : (LocalDate)null)
                                            .setColor(colorCol != -1 && selevent.get(colorCol) != null ? Integer.parseInt(selevent.get(colorCol)) : 0)
                                            .setStartTime(startTimeCol != -1 ? selevent.get(startTimeCol) : null)
                                            .setEndTime(endTimeCol != -1 ? selevent.get(endTimeCol) : null)
                                            .setMemo(memoCol != -1 ? selevent.get(memoCol) : null)
                                            .setIsAllDay(isAllDayCol != -1 && selevent.get(isAllDayCol) != null ? Boolean.parseBoolean(selevent.get(isAllDayCol)) : false)
                                            .setCategory1(category1Col != -1 ? selevent.get(category1Col) : null)
                                            .setCategory2(category2Col != -1 ? selevent.get(category2Col) : null)
                                            .setCreatedAt(createdAtCol != -1 ? selevent.get(createdAtCol) : null)
                                            .build();
                                    OuterCalendarAdapter.sSCModel.add(model);
                                }
                            }
                            //__




                        }
                    }
                });
            }
        });

        return view;
    }

    private void setupSymbolButton(Button button, String symbol) {
        if (button != null) {
            button.setOnClickListener(v -> {
                int start = Math.max(queryEditText.getSelectionStart(), 0);
                int end = Math.max(queryEditText.getSelectionEnd(), 0);
                queryEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                        symbol, 0, symbol.length());
            });
        }

        // removequery 버튼 클릭 처리
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
    }

    private void refreshSavedQueries() {
        Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<SavedQuery> savedQueries = AppDatabase.getDatabase(requireContext()).savedQueryDao().getAllSavedQueries();
            requireActivity().runOnUiThread(() -> {
                if (savedQueryAdapter != null) {
                    savedQueryAdapter.setQueries(savedQueries);
                }
            });
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (queryEditText != null) {
            // 화면을 벗어날 때 (스와이프 할 때) 현재 적힌 글자를 ViewModel에 저장
            viewModel.setQueryText(queryEditText.getText().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queryEditText != null) {
            viewModel.setQueryText(queryEditText.getText().toString());
        }
    }
}

