package com.example.my_custom_calenda_1;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_custom_calenda_and_room.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryFragment extends Fragment {

    // 백그라운드 스레드 및 UI 업데이트용 핸들러
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    // 데이터베이스 이름 정의
    private static final String DB_NAME = "my_database.db";
    private SharedViewModel viewModel;
    private EditText queryEditText;
    private Button find_selectquery_btn;
    private Button find_insertquery_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        queryEditText = view.findViewById(R.id.queryEditText);
        find_insertquery_btn = view.findViewById(R.id.insertquery_btn);
        find_selectquery_btn = view.findViewById(R.id.selectquery_btn);

        // Activity 범위의 SharedViewModel 가져오기 (3개 페이지가 공유함)
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
                                    Toast.makeText(requireContext(), "데이터 삽입(INSERT) 완료!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
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
                                while (cursor.moveToNext()) {
                                    int nameIndex = cursor.getColumnIndex("name"); // 필요에 따라 컬럼명 수정
                                    if (nameIndex != -1) {
                                        String name = cursor.getString(nameIndex);
                                        resultBuilder.append(name).append("\n"); // 줄바꿈으로 유저 구분
                                    }
                                }
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
                                    // ==========================================
                                    // 🚀 [수정된 부분] 뷰페이저용 데이터 전달 (Fragment Result API)
                                    // ==========================================

                                    // 1. 택배 상자(Bundle) 생성 및 데이터 넣기
                                    Bundle bundle = new Bundle();
                                    bundle.putString("QUERY_DATA", finalResult);

                                    // 2. 부모 관리자를 통해 "DATA_KEY"라는 주소로 상자 던지기
                                    getParentFragmentManager().setFragmentResult("DATA_KEY", bundle);

                                    // 3. (선택사항) 조회가 끝났다는 알림 띄우기
                                    // Toast.makeText(requireContext(), "조회 완료! 옆 화면에서 확인하세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null && !cursor.isClosed()) cursor.close();
                            if (db != null && db.isOpen()) db.close();
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

