package com.jackchance.yixun.UI.UserManager;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.NetWorkUtils;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 应用的用户登陆模块，通过账号与密码登陆
 * 该模块与云服务端用户数据库交互并提供注册，以及忘记密码服务入口
 * @author 蚍蜉
 * @version 2.0
 * 2017/08/20
 */
public class LandActivity extends Activity {

    private EditText accountTextEdit;   //账号填写
    private EditText passwordTextEdit;  //密码填写
    private CheckBox rememberCheckBox;  //记住密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        /************************初始化实例*****************************************/
        accountTextEdit = (EditText)findViewById(R.id.account_et);
        passwordTextEdit = (EditText)findViewById(R.id.password_et);
        rememberCheckBox = (CheckBox)findViewById(R.id.remember_checkbox);
        rememberCheckBox.setChecked(true);

        /********************************登陆按钮逻辑*******************************/
        Button button_login = (Button)findViewById(R.id.bnt_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkUtils.networkStateTips(LandActivity.this);           //网络状态检查
                String Account = accountTextEdit.getText().toString().trim()
                        .replace(" ","");
                String Password = passwordTextEdit.getText().toString().trim();
                //进行输入检查
                if(Account.isEmpty()){
                    Toast.makeText(LandActivity.this,"账号不能为空！",Toast.LENGTH_SHORT).show();
                }else if(Password.isEmpty()){
                    Toast.makeText(LandActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                }else{
                    //账号+密码，提交数据库验证
                    CommonUtils.showProgressDialog(LandActivity.this,"正在登陆中");//开启进度条
                    /****************用户数据库查询********************/
                    UserBean login_user = new UserBean();
                    login_user.setUsername(Account);
                    login_user.setPassword(Password);
                    login_user.login(new SaveListener<UserBean>() {
                        @Override
                        public void done(UserBean userBean, BmobException e) {
                            if(e == null){
                                CommonUtils.showToast(LandActivity.this,"登陆成功!");//关闭进度条窗口
                                CommonUtils.hideProgressDialog();
                                LandActivity.this.finish();
                            }else{
                                CommonUtils.hideProgressDialog();
                                CommonUtils.showToast(LandActivity.this, "登录失败，" + e.getMessage() );
                            }
                        }
                    });
                }
            }
        });
        /****************************注册按钮点击逻辑***************************/
        TextView registerButton = (TextView) findViewById(R.id.reg_tv);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.networkStateTips(LandActivity.this);   //网络状态检查
                startActivity(new Intent(LandActivity.this, RegisterActivity.class)
                        .putExtra("resetpass", false));
            }
        });

        /*****************************找回密码按钮逻辑**************************/
        TextView resetButton = (TextView)findViewById(R.id.reset_tv);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.networkStateTips(LandActivity.this);   //网络状态检查
                startActivity(new Intent(LandActivity.this, RegisterActivity.class)
                        .putExtra("resetpass", true));      //设置非reset模式
            }
        });

    }

}
