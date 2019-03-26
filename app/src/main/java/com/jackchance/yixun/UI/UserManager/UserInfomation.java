package com.jackchance.yixun.UI.UserManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.NetWorkUtils;

import org.w3c.dom.Text;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserInfomation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);
        UserBean currentUser = BmobUser.getCurrentUser(UserBean.class);
        final TextView nameText = (TextView)findViewById(R.id.editText_username);
        final TextView phoneText = (TextView)findViewById(R.id.editText_phone);
        final TextView passwordText = (TextView)findViewById(R.id.editText_password);
        final TextView signText = (TextView)findViewById(R.id.editText_sign);
        //设置界面展示
        Button submitButton = (Button)findViewById(R.id.submit_button);
        nameText.setText(currentUser.getNickname());
        phoneText.setText(currentUser.getMobilePhoneNumber());
        passwordText.setText("");
        signText.setText(currentUser.getSignature());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString().trim();
                String phone = phoneText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                String sign = signText.getText().toString().trim();
                if(name.isEmpty()){
                    CommonUtils.showToast(UserInfomation.this,"用户名不能为空");
                    return;
                }
                if(phone.isEmpty()){
                    CommonUtils.showToast(UserInfomation.this,"手机号不能为空");
                    return;
                }
                //提交数据库
                UserBean userBean = new UserBean();
                userBean.setNickname(name);
                userBean.setMobilePhoneNumber(phone);
                if (!password.isEmpty()){
                    userBean.setPassword(password);
                }
                if(!sign.isEmpty()){
                    userBean.setSignature(sign);
                }
                //检查网络是否可用
                NetWorkUtils.networkStateTips(UserInfomation.this);
                //发送到数据库
                sentToUserDataBase(userBean);
            }
        });

    }

    /**
     * 更新用户信息
     * @param newUser
     */
    void sentToUserDataBase(UserBean newUser) {
        newUser.update(BmobUser.getCurrentUser(UserBean.class).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    CommonUtils.showToast(UserInfomation.this,"更新用户信息成功");

                } else {
                    CommonUtils.showToast(UserInfomation.this,"更新用户信息失败");
                }
            }
        });

    }
}
