package com.sativa.ssh4android;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.util.Properties;


public class MainActivity4 extends Activity implements View.OnClickListener {

    private EditText address;
    private EditText user;
    private EditText pass;
    private EditText file;
    private EditText file2;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        address = findViewById(R.id.address);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        file = findViewById(R.id.file);
        file2 = findViewById(R.id.file2);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        final int WRITE_EXTERNAL_STORAGE = 0;
        final int REQUEST_PERMISSION = 0;

        int permissionCheckStorage = ContextCompat.checkSelfPermission(MainActivity4.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity4.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity4.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity4.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {

            String value = address.getText().toString();
            String value2 = user.getText().toString();
            String value3 = pass.getText().toString();
            String value4 = file.getText().toString();
            String value5 = file2.getText().toString();

            Session session;
            try {
                JSch jsch = new JSch();
                session = jsch.getSession("" + value2, "" + value, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("PreferredAuthentications", "password");
                session.setPassword(value3);
                session.connect();

                Channel channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                sftpChannel.put("" + value4, "" + value5);
                session.disconnect();
            } catch (JSchException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }
    }
}
