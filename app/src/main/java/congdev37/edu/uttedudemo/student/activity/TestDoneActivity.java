package congdev37.edu.uttedudemo.student.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import congdev37.edu.uttedudemo.HomeActivity;
import congdev37.edu.uttedudemo.R;
import congdev37.edu.uttedudemo.model.Question;
import congdev37.edu.uttedudemo.model.Student;
import congdev37.edu.uttedudemo.response.ResponseMessage;
import congdev37.edu.uttedudemo.service.ApiUtils;
import congdev37.edu.uttedudemo.service.SOService;
import congdev37.edu.uttedudemo.student.fragment.SubjectFragment;
import congdev37.edu.uttedudemo.student.slide.ScreenSlideActivity;
import congdev37.edu.uttedudemo.student.slide.ScreenSlidePageFragment;
import congdev37.edu.uttedudemo.util.ConstantKey;
import congdev37.edu.uttedudemo.util.Converter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TestDoneActivity extends AppCompatActivity {

    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvTitleToolbar)
    TextView tvTitleToolbar;
    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.txt_true)
    TextView txtTrue;
    @BindView(R.id.txt_fail)
    TextView txtFail;
    @BindView(R.id.txt_unanswered)
    TextView txtNoAnswer;
    @BindView(R.id.txt_total_point)
    TextView txtTotalPoint;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_save_point)
    Button btnSavePoint;
    private TextView txtname;
    TextView txtLevel, txtstdCode, txtSubject;

    AlertDialog.Builder builderConfirm;
    SOService mService;
    ArrayList<Student> dataStudent;
    private ArrayList<Question> listQuestion = new ArrayList<Question>();
    private int numTrue = 0, numFail = 0, numNoAns = 0, totalPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_done);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        try {
            tvTitleToolbar.setText("Kết quả");
            dataStudent = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        try {
            listQuestion = ScreenSlidePageFragment.arr_Ques;
            showPoint();
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogExit();
                }
            });
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  dialogExit();
                }
            });
            //nhấn lưu
            btnSavePoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogSaveScore();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //dialog lưu điểm
    private void showDialogSaveScore() {
        try {
            final Dialog dialog = new Dialog(TestDoneActivity.this,R.style.Theme_Dialog);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alert_dialog_saver_point);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            txtname =  dialog.findViewById(R.id.txt_name);
            txtLevel =  dialog.findViewById(R.id.tvExDay);
            txtstdCode =  dialog.findViewById(R.id.txt_student_code);
            txtSubject =  dialog.findViewById(R.id.txt_subject);
            TextView txtPoint =  dialog.findViewById(R.id.txt_point);
            Button btClose =  dialog.findViewById(R.id.btClose);
            Button btSave =  dialog.findViewById(R.id.btSave);

            final int numTotal = numTrue;
            txtPoint.setText(numTotal + "/" + listQuestion.size());
            txtLevel.setText(Converter.convertLevel(TestActivity.level) + "");
            txtname.setText(HomeActivity.stdName);
            txtstdCode.setText(HomeActivity.stdCode);
            txtSubject.setText(SubjectFragment.subName);

            btClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveTest();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //hàm tính điểm
    private void showPoint() {
        try {
            for (int i = 0; i < listQuestion.size(); i++) {
                if (listQuestion.get(i).getAnswer().equals("")) {
                    numNoAns++;
                } else if (listQuestion.get(i).getAnsCorrect().equals(listQuestion.get(i).getAnswer())) {
                    numTrue++;
                } else {
                    numFail++;
                }
            }
            txtNoAnswer.setText("" + numNoAns);
            txtTrue.setText("" + numTrue);
            txtFail.setText("" + numFail);
            totalPoint = numTrue;
            txtTotalPoint.setText("" + (totalPoint) + "/" + listQuestion.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //lưu bài test lên server
    private void saveTest() {
        try {
            mService = ApiUtils.getSOService();
            Map<String, Object> params = new HashMap<>();
            params.put("testID", ScreenSlideActivity.testID);
            params.put("Level", TestActivity.level);
            params.put("questionID", ScreenSlideActivity.questionID);
            params.put("testName", ScreenSlideActivity.testName);
            params.put("studentCode", HomeActivity.stdCode);
            params.put("subjectCode", TestActivity.subCode);
            params.put("Answer", Converter.convertAnswer(getAnswer()));
            params.put("exDay", Converter.setDate());
            params.put("Score", "" + totalPoint + "/" + listQuestion.size());
            params.put("status", 0);

            mService.saveTest(params).enqueue(new Callback<ResponseMessage>() {
                @Override
                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() == 1) {
                            Intent intentBroadCast = new Intent(ConstantKey.ACTION_NOTIFY_DATA);
                            intentBroadCast.putExtra("screen", "test_completed");
                            sendBroadcast(intentBroadCast);
                            finish();
                        } else {
                            Toast.makeText(TestDoneActivity.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        int statusCode = response.code();
                        if (statusCode == 404) {
                            Toast.makeText(TestDoneActivity.this, "Lỗi : Không thể kết nối tới máy chủ ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TestDoneActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //lấy ra câu trả lời
    private ArrayList<String> getAnswer() {
        ArrayList<String> listAns = null;
        try {
            listAns = new ArrayList<>();
            for (int i = 0; i < listQuestion.size(); i++) {
                listAns.add(listQuestion.get(i).getAnswer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listAns;
    }

    //trờ về
    @Override
    public void onBackPressed() {
        dialogExit();
    }

    //dialog thoát
    public void dialogExit(){
        try {
            builderConfirm = new AlertDialog.Builder(TestDoneActivity.this);
            builderConfirm.setMessage("Bạn có muốn thoát mà không lưu điểm");
            builderConfirm.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builderConfirm.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builderConfirm.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
