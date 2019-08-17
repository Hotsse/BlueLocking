package com.example.atrue.bluelocking_final;



import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BlueLogin extends Activity {

    private Button btnLogin, btnAcc;
    private EditText editID, editPW;
    private CheckBox checkRememID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);


        //위젯 뷰 값 획득
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnAcc = (Button) findViewById(R.id.btnAcc);
        editID = (EditText) findViewById(R.id.editID);
        editPW = (EditText) findViewById(R.id.editPW);
        checkRememID = (CheckBox) findViewById(R.id.checkRememID);

        editID.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_OUT);
        editID.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        editPW.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

        //아이디 저장 체크 확인 및 아이디 획득
        SharedPreferences pref = getSharedPreferences("LoginInfo", 0);
        String remID = pref.getString("RememberID", null);
        if (remID != null) {
            editID.setText(remID);
            checkRememID.setChecked(true);
            editPW.requestFocus();
        } else editID.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //아이디 저장 체크 버튼 이벤트 핸들러
        checkRememID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.getId() == R.id.checkRememID) {

                    SharedPreferences pref = getSharedPreferences("LoginInfo", 0);
                    SharedPreferences.Editor edit = pref.edit();

                    if (isChecked) {
                        edit.putString("RememberID", editID.getText().toString());
                        Toast.makeText(getApplicationContext(), "아이디 저장을 설정했습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        edit.putString("RememberID", null);
                        Toast.makeText(getApplicationContext(), "아이디 저장을 해제했습니다", Toast.LENGTH_SHORT).show();
                    }
                    edit.commit();
                }
            }
        });

        //로그인 버튼 이벤트 핸들러
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("LoginInfo", 0);
                String ID = pref.getString("ID", null);
                String PW = pref.getString("PW", null);

                String InputedID = editID.getText().toString();
                String InputedPW = editPW.getText().toString();

                if (ID == null || PW == null) {
                    Toast.makeText(getApplicationContext(), "계정이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                } else if (ID.equals(InputedID) && PW.equals(InputedPW)) {

                    InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    Toast.makeText(getApplicationContext(), "로그인에 성공했습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), BlueTryConnecting.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //회원가입 버튼 이벤트 핸들러
        btnAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //프리퍼런스의 기존 값 획득
                SharedPreferences pref = getSharedPreferences("LoginInfo", 0);
                String ID = pref.getString("ID", null);
                String PW = pref.getString("PW", null);

                //참 == 디폴트 null 이 반환 됬으므로 아직 계정이 존재하지 않는 상태
                if (ID == null || PW == null) {

                    //회원가입 다이얼로그 얼럿 생성
                    final LinearLayout linear = (LinearLayout) View.inflate(getApplicationContext(), R.layout.activity_account_dialog, null);

                    AlertDialog dlg = new AlertDialog.Builder(BlueLogin.this)
                            .setTitle("회원가입")
                            .setView(linear)
                            .setPositiveButton("가입", new DialogInterface.OnClickListener() { // 가입
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    EditText dialogID, dialogPW;

                                    dialogID = (EditText) linear.findViewById(R.id.dialogID);
                                    dialogPW = (EditText) linear.findViewById(R.id.dialogPW);

                                    SharedPreferences pref = getSharedPreferences("LoginInfo", 0);

                                    String InputedID = dialogID.getText().toString();
                                    String InputedPW = dialogPW.getText().toString();

                                    if (InputedID.length() <= 0) {
                                        Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show();
                                    } else if (InputedPW.length() <= 0) {
                                        Toast.makeText(getApplicationContext(), "패스워드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                                    } else {
                                        SharedPreferences.Editor edit = pref.edit();
                                        edit.putString("ID", InputedID);
                                        edit.putString("PW", InputedPW);
                                        edit.commit();

                                        Toast.makeText(getApplicationContext(), "계정이 생성되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() { // 취소
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();

                    Button nbtn = dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbtn.setTextColor(Color.BLACK);
                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbtn.setTextColor(Color.BLACK);

                } else {
                    Toast.makeText(getApplicationContext(), "이미 계정이 존재합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}