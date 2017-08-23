package com.example.mezereon.Login;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mezereon.Component.DaggerAppComponent;
import com.example.mezereon.Home.HomeActivity;
import com.example.mezereon.MyApp;
import com.example.mezereon.R;
import com.example.mezereon.Tool.API;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.jakewharton.rxbinding.view.RxView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 嚴 on 2017/8/17.
 */

public class SignActivity extends AppCompatActivity {
    @Bind(R.id.textInputEditText)
    EditText et_id;
    @Bind(R.id.textInputEditText2)
    EditText et_phone;
    @Bind(R.id.textInputEditText3)
    EditText et_wnumber;
    @Bind(R.id.appCompatButton)
    Button btn_sign;
    @Bind(R.id.textInputLayout)
    TextInputLayout til;
    @Bind(R.id.textInputLayout2)
    TextInputLayout til2;
    @Bind(R.id.textInputLayout3)
    TextInputLayout til3;
    @Bind(R.id.radioButton1)
    RadioButton bt_man;
    @Bind(R.id.radioButton2)
    RadioButton bt_woman;
    @Bind(R.id.radiogroup)
    RadioGroup bt_group;

    @Inject
    Retrofit retrofit;

    boolean phoneIsRight = false;
    boolean nameIsRight = false;

    public interface RegistService {
        @GET("addUser.php")
        Observable<Void> regist(@Query("phone") String phone,
                                    @Query("name") String name,
                                    @Query("sex") String sex,
                                    @Query("number") String number);
    }

    final Intent intent=new Intent();
    private SharedPreferences hp;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog = null;
    private static final int MESSAGETYPE_02 = 0x0002;
    private  boolean isLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_sign);
        DaggerAppComponent.builder().build().inject(this);
        setTheStateBarAndInitEM();
        hp = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
        progressDialog=new ProgressDialog(SignActivity.this);
        editor = hp.edit();
        ButterKnife.bind(this);

        RxView.clicks(btn_sign).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                checkTheName();
                checkThePhone();
                checkTheNumberAndRegist();
            }
        });

    }

    private void checkTheNumberAndRegist() {
        if(phoneIsRight && isWnumber(et_wnumber.getText().toString())){
            int buttonId = bt_group.getCheckedRadioButtonId();
            String sex = "";
            sex = buttonId == R.id.radioButton1?"1":"0";
            editor.putString("NAME",et_id.getText().toString());
            editor.putString("PHONE",et_phone.getText().toString());
            editor.putString("WNUMBER",et_wnumber.getText().toString());
            editor.putString("SEX",sex);
            editor.commit();
            progressDialog.setMessage("注册中");
            progressDialog.show();
            regist(et_id.getText().toString(),et_phone.getText().toString(),et_wnumber.getText().toString(),sex);
        }else{
            til3.setErrorEnabled(true);
            til3.setError("请输入正确的工号");
        }
    }

    private void checkThePhone() {
        if(nameIsRight && isMobileNO(et_phone.getText().toString())){
            phoneIsRight = true;
        }else{
            til2.setErrorEnabled(true);
            til2.setError("请输入正确的手机号码");
        }
    }

    private void checkTheName() {
        if(!et_id.getText().equals("") ){
            nameIsRight = true;
        }else{
            til.setErrorEnabled(true);
            til.setError("请输入正确的名字");
        }
    }

    private void regist(final String name, final String phone, final String number, final String sex) {
        RegistService registService = retrofit.create(RegistService.class);
        Subscription subscription = registService.regist(phone,name,sex,number)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            loginAndTurnToHome();
                                        }
                                    });
    }

    private void loginAndTurnToHome() {
        if (progressDialog.isShowing()) {
            Log.d("phone",hp.getString("PHONE", "none"));
            EMClient.getInstance().login(hp.getString("PHONE", "none"),
                    hp.getString("PHONE", "none"), new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    closeTheDialogAndTurnToHome();
                }

                @Override
                public void onProgress(int progress, String status) {
                    Log.d("progress", progress + "");
                }

                @Override
                public void onError(int code, String message) {
                    Log.d("TAG", "登录服务器失败");
                }
            });
        }
    }

    private void closeTheDialogAndTurnToHome() {
        progressDialog.dismiss();
        isLogined = true;
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        //Toast.makeText(SignActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
        getWindow().setExitTransition(new Explode());
        intent.setClass(SignActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    public  boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    private void setTheStateBarAndInitEM() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //初始化EMClient
        EMOptions options = new EMOptions();
        EMClient.getInstance().init(new MyApp(), options);
    }

    public boolean isWnumber(String wnumber){
        String numRegex = "[0-9]{6,10}";
        if (TextUtils.isEmpty(wnumber.trim())) return false;
        else return wnumber.matches(numRegex);

    }


}
