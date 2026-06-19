package com.example.my_custom_calenda_1;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryFragment extends Fragment {

    // 백그라운드 스레드 및 UI 업데이트용 핸들러
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    // 데이터베이스 이름 정의 (Room에서 사용하는 이름과 동일하게 맞춤)
    private static final String DB_NAME = "calendar_database";

    public static ArrayList<Object> sendArrayListResult = new ArrayList<>();

    public static ArrayList<String> stringEvent = new ArrayList<String>();

    private SharedViewModel viewModel;
    private EditText queryEditText;
    private Button find_selectquery_btn;
    private Button find_insertquery_btn;
    private Button prequery_btn;
    private Button nextquery_btn;

    private TextView find_columview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        queryEditText = view.findViewById(R.id.queryEditText);
        find_insertquery_btn = view.findViewById(R.id.insertquery_btn);
        find_selectquery_btn = view.findViewById(R.id.selectquery_btn);
        prequery_btn = view.findViewById(R.id.prequery_btn);
        nextquery_btn = view.findViewById(R.id.nextquery_btn);
        find_columview = view.findViewById(R.id.columview);

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

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase db = null;
                        try {
                            db = requireContext().openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

                            // 결과 테이블이 없는 형태이므로 execSQL 사용
                            db.execSQL(rawInsertQuery);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "쿼리 실행 완료!", Toast.LENGTH_SHORT).show();
                                    // 🚀 추가: 결과 페이지에 성공 메시지 전달
                                    viewModel.setResultText("쿼리가 성공적으로 실행되었습니다.\n실행 쿼리: " + rawInsertQuery);
                                }
                            });
                        } catch (Exception e) {
                            final String errorMessage = e.getMessage();
                            handler.post(() -> {
                                viewModel.setResultText("에러 발생: " + errorMessage);
                                Toast.makeText(requireContext(), "쿼리 실패!", Toast.LENGTH_SHORT).show();
                            });
                            e.printStackTrace();
                        } finally {
                            if (db != null && db.isOpen()) db.close();
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

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase db = null;
                        Cursor cursor = null;
                        try {
                            db = requireContext().openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
                            cursor = db.rawQuery(rawSelectQuery, null);

                            final StringBuilder resultBuilder = new StringBuilder();
                            if (cursor != null) {
                                // 모든 컬럼 이름을 가져와서 상단에 표시
                                sendArrayListResult.clear();//이전 내용 지우기
                                stringEvent.clear();
                                String[] columnNames = cursor.getColumnNames();
                                for (String col : columnNames) {
                                    resultBuilder.append("[").append(col).append("] ");
                                    stringEvent.add(col);
                                }
                                find_columview.setText(stringEvent.toString());
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

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // 🚀 [수정] ViewModel에 결과 저장 (ResultFragment가 실시간으로 감지)
                                    viewModel.setResultText(finalResult);

                                    // 기존 방식: Fragment Result API로 전달
                                    Bundle bundle = new Bundle();
                                    bundle.putString("QUERY_DATA", finalResult);
                                    getParentFragmentManager().setFragmentResult("DATA_KEY", bundle);
                                }
                            });
                        } catch (Exception e) {
                            final String errorMessage = e.getMessage();
                            handler.post(() -> {
                                viewModel.setResultText("조회 에러: " + errorMessage);
                            });
                            e.printStackTrace();
                        } finally {
                            if (cursor != null && !cursor.isClosed()) cursor.close();
                            if (db != null && db.isOpen()) db.close();


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
                                    }
                                    i++;
                                }

                                CalendarAdapter.sSCModel = new ArrayList<SelectSendCalenderModel>();
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
                                            .build();
                                    CalendarAdapter.sSCModel.add(model);
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

    @Override
    public void onPause() {
        super.onPause();
        // 화면을 벗어날 때 (스와이프 할 때) 현재 적힌 글자를 ViewModel에 저장
        viewModel.setQueryText(queryEditText.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}

