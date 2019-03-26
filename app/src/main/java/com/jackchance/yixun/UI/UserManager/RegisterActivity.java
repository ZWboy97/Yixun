package com.jackchance.yixun.UI.UserManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.NetWorkUtils;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 应用的用户注册与密码找回模块，提供手机号短信验证注册以及短信密码找回
 * @author 蚍蜉
 * @version 1.0
 * 2017/08/22
 */
public class RegisterActivity extends Activity {

    private EditText mPhoneET;              //手机号
    private EditText mCodeET;               //短信验证码
    private EditText mPasswordET;           //密码
    private Button mResetBtn;               //重置
    private Button mSendBtn;                //发送短信按钮
    private Button mRegBtn;                 //点击注册按钮
    private ImageButton mBackBtn;           //返回按钮
    private TextView mBackTV;               //返回Text
    private TextView mTitleTV;              //标题Title
    private TextView mProtocolTV;           //软件协议
    private LinearLayout mProtocolLayout;   //协议展示
    private CheckBox mPasswordCB;           //密码复选框
    private CheckBox mProtocolCB;           //协议复选框

    private TimeCount time;                 //接收验证码倒计时
    private String areaStr = null;          //
    private boolean isForgetPass = false;   //是否是找回密码模式
    final private int LOAD_DATA_SUCCESS = 0;//常量，代表注册消息，用户handle消息处理

    private Handler mHandler;               //Handle用户处理消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /****************************获取实例************************************/
        mPhoneET = (EditText) findViewById(R.id.reg_phone_et);
        mCodeET = (EditText) findViewById(R.id.id_code_et);
        mPasswordET = (EditText) findViewById(R.id.id_password_et);
        mResetBtn = (Button) findViewById(R.id.id_reset_btn);
        mSendBtn = (Button) findViewById(R.id.id_send_btn);
        mRegBtn = (Button) findViewById(R.id.id_reg_btn);
        mBackBtn = (ImageButton) findViewById(R.id.common_back_btn);
        mBackTV = (TextView) findViewById(R.id.common_back_tv);
        mTitleTV = (TextView) findViewById(R.id.regsiter_title);
        mProtocolTV = (TextView) findViewById(R.id.register_protocol_tv);
        mProtocolLayout = (LinearLayout) findViewById(R.id.register_protocol_checkbox_layout);
        mPasswordCB = (CheckBox) findViewById(R.id.chat_register_password_checkbox);
        mProtocolCB = (CheckBox) findViewById(R.id.register_protocol_checkbox);
        isForgetPass = this.getIntent().getBooleanExtra("resetpass", false);//是否是找回密码模式


        /********************************初始化界面按钮逻辑*********************************/
        //找回密码模式与新用户注册模式界面初始化
        mBackBtn.setVisibility(View.VISIBLE);
        mBackTV.setVisibility(View.GONE);
        if (isForgetPass) {
            mProtocolLayout.setVisibility(View.GONE);
            mRegBtn.setVisibility(View.GONE);
            mResetBtn.setVisibility(View.VISIBLE);
            mTitleTV.setText("找回密码");
        } else {
            mRegBtn.setVisibility(View.VISIBLE);
            mResetBtn.setVisibility(View.GONE);
            mTitleTV.setText("注册");
        }
        //后退按钮逻辑
        mBackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LandActivity.class));
                finish();
            }
        });
        //后退图形按钮逻辑
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //软件协议复选按钮
        mProtocolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //软件协议
                if (mProtocolCB.isChecked()) {
                    mProtocolCB.setChecked(false);
                    mProtocolTV.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.grey));
                } else {
                    mProtocolCB.setChecked(true);
                    mProtocolTV.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
                }
            }
        });

        /********************************Handler机制处理消息***************************/
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case LOAD_DATA_SUCCESS:
                        /************组装User对象信息************/
                        final String phone = mPhoneET.getText().toString().trim();
                        final String password = mPasswordET.getText().toString().trim();
                        final String code = mCodeET.getText().toString().trim();
                        UserBean userBean = new UserBean();
                        userBean.setPassword(password);
                        userBean.setUsername(phone);
                        userBean.setCode(Integer.parseInt(code));
                        userBean.setArea(areaStr);
                        userBean.setAvatar("http://file.bmob.cn/M02/79/0B/oYYBAFawXZOALjy5AAACPGs5RvU833.png");
                        userBean.setBgurl("http://file.bmob.cn/M02/79/0B/oYYBAFawXXqAAX_OAAhPzr3vBNo866.png");
                        userBean.setGender(1);
                        userBean.setNickname("");
                        userBean.setRid(0);
                        userBean.setSignature("开启一段美好旅程吧！");

                        /*************向用户数据库提交新用户信息***********/
                        userBean.signUp(new SaveListener<UserBean>() {
                            @Override
                            public void done(UserBean userBean, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    if (e.getErrorCode() == 202) {
                                        CommonUtils.showToast(RegisterActivity.this, "该用户已注册");
                                    } else {
                                        CommonUtils.showToast(RegisterActivity.this, "注册失败");
                                    }
                                }
                            }
                        });
                        break;
                }
            }
        };

        /***********************************发送短信验证码逻辑**********************************/
        time = new TimeCount(60000, 1000);      //CountDownr对象，用于接收验证码倒计时
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.networkStateTips(RegisterActivity.this);   //网络状态检查

                //读取手机号
                final String mPhoneStr = mPhoneET.getText().toString().trim()
                        .replaceAll(" ", "");
                if (mPhoneStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                } else {
                    /************************请求手机短信验证码************************/
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);//查询mobile叫mPhoneStr的数据
                    bmobQuery.findObjects(new FindListener<UserBean>() {
                        @Override
                        public void done(List<UserBean> list, BmobException e) {
                            if (e == null) {
                                    /**************手机已注册过了***************/
                                if (list.size() > 0) {
                                    Toast.makeText(RegisterActivity.this, "该手机号已注册，请直接登录",
                                            Toast.LENGTH_SHORT).show();
                                    mCodeET.setText("");
                                    mPasswordET.setText("");
                                } else {
                                    /**************未注册，发送短信***************/
                                    mSendBtn.setText("获取中...");
                                    BmobSMS.requestSMSCode(mPhoneET.getText().toString().trim().replaceAll(" ", ""),
                                            getResources().getString(R.string.app_name), new QueryListener<Integer>() {
                                        @Override
                                        public void done(Integer smsId, BmobException ex) {
                                            if (ex == null) {       //验证码发送成功
                                                time.start();       //开始倒计时
                                                CommonUtils.showToast(RegisterActivity.this, "验证码已经发送");
                                                mPhoneET.setTextColor(getResources().getColor(R.color.grey));
                                                mPhoneET.setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        /***********************************密码是否可见动态设置**********************************/
        mPasswordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置密码可见
                    mPasswordCB.setChecked(true);
                    mPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPasswordCB.setChecked(false);
                    mPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPasswordET.setSelection(mPasswordET.getText().toString().length());//设置光标位置在文本框末尾
            }
        });

        /***********************************注册模式下，注册按钮逻辑**********************************/
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.networkStateTips(RegisterActivity.this);   //网络状态检查
                /****************读取注册信息，并进行差错判断*******************/
                final String mPhoneStr = mPhoneET.getText().toString().trim().replaceAll(" ", "");
                final String mPasswordStr = mPasswordET.getText().toString().trim();
                final String mCodeStr = mCodeET.getText().toString().trim();
                if (mPhoneStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                }  else if (mPasswordStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "密码不能为空");
                } else if (mCodeStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "验证码不能为空");
                } else if (!mProtocolCB.isChecked()) {
                    CommonUtils.showToast(RegisterActivity.this, "您还未同意用户协议");
                } else {
                    CommonUtils.showProgressDialog(RegisterActivity.this, "正在注册");

                    /****************注册信息提交用户数据库*******************/
                    //先查询手机号是否已注册
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);
                    bmobQuery.findObjects(new FindListener<UserBean>() {
                        @Override
                        public void done(List<UserBean> list, BmobException e) {
                            if (e == null) {    //已经注册过，直接登陆
                                if (list.size() > 0) {
                                    CommonUtils.hideProgressDialog();
                                    Toast.makeText(RegisterActivity.this, "该手机号已注册，请直接登录",
                                            Toast.LENGTH_SHORT).show();
                                    mCodeET.setText("");
                                    mPasswordET.setText("");
                                } else {        //未注册过，发送验证码
                                    BmobSMS.verifySmsCode(mPhoneET.getText().toString().
                                            replaceAll(" ", ""), mCodeStr, new UpdateListener() {
                                        @Override
                                        public void done(BmobException ex) {
                                            CommonUtils.hideProgressDialog();
                                            if (ex == null) {   //短信验证码已验证成功
                                                onRegister();
                                            } else {
                                                CommonUtils.showToast(RegisterActivity.this, "验证码验证失败，请检查后重试");
                                            }
                                        }
                                    });
                                }
                            } else {
                                CommonUtils.hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        /***********************************找回模式下，找回密码按钮逻辑**********************************/
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.networkStateTips(RegisterActivity.this);   //网络状态检查
                /***************获取密码找回信息，并差错检查*****************/
                final String mPhoneStr = mPhoneET.getText().toString().trim().replaceAll(" ", "");
                final String mPasswordStr = mPasswordET.getText().toString().trim();
                final String mCodeStr = mCodeET.getText().toString().trim();
                if (mPhoneStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                } else if (mPasswordStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "密码不能为空");
                } else if (mCodeStr.isEmpty()) {
                    CommonUtils.showToast(RegisterActivity.this, "验证码不能为空");
                } else {
                    /****************将重置密码信息提交到用户数据库*******************/
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);
                    bmobQuery.findObjects(new FindListener<UserBean>() {
                        @Override
                        public void done(List<UserBean> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 1) {     //手机号存在
                                    String mPasswordStr = mPasswordET.getText().toString().trim();
                                    UserBean userBean = new UserBean();
                                    userBean.setPassword(mPasswordStr);
                                    userBean.update(list.get(0).getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(RegisterActivity.this, "重置密码成功",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "重置密码失败，请稍后重试",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {            //该用户不存在
                                    Toast.makeText(RegisterActivity.this, "该用户名不存在，请稍后重试",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "密码重置出现错误，请稍后重试",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 自定义方法，处理注册，发送注册请求。
     */
    private void onRegister() {
        final String phone = mPhoneET.getText().toString().trim()
                .replaceAll(" ", "");
        // 获取手机号码归属地作为地区值
        new Thread(new Runnable() {
            @Override
            public void run() {
                areaStr = "北京";
                mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);       //将注册消息发送到handler进行处理
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 倒计时的内部类，用户接收验证码时候的倒计时
     */
    class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture    总时间
         * @param countDownInterval 计时间隔
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            mSendBtn.setClickable(true);
            mSendBtn.setText("重新获取");       //更改验证码按钮显示
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            mSendBtn.setClickable(false);
            mSendBtn.setText(millisUntilFinished / 1000 + "s"); //展示到界面
        }
    }
}
