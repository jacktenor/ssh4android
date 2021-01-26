package com.sativa.ssh4android;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity2 extends Activity implements View.OnClickListener {

    private EditText address;
    private EditText user;
    private EditText pass;
    private EditText command;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        address = findViewById(R.id.address);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        command = findViewById(R.id.command);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {

            String value = address.getText().toString();
            String value2 = user.getText().toString();
            String value3 = pass.getText().toString();
            String value4 = command.getText().toString();

            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            prop.put("PreferredAuthentications", "password");

            JSch jsch = new JSch();
            Session session = null;
            try {
                session = jsch.getSession(value2, value, 22);
            } catch (JSchException e) {
                e.printStackTrace();
            }
            if (session != null) {
                session.setPassword(value3);
            }
            if (session != null) {
                session.setConfig(prop);
            }
            try {
                if (session != null) {
                    session.connect();
                }
            } catch (JSchException e) {
                e.printStackTrace();
            }
            StringBuilder outputBuffer = null;
            if (session != null) {
                if (session.isConnected()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection successful.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection failed.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                }

                ChannelExec channel = null;
                try {
                    channel = (ChannelExec) session.openChannel("exec");
                } catch (JSchException e) {
                    e.printStackTrace();
                }

                assert channel != null;
                channel.setCommand(value4);
                ;
                try {
                    InputStream commandOutput = channel.getExtInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                outputBuffer = new StringBuilder();
                StringBuilder errorBuffer = new StringBuilder();

                InputStream in = null;
                try {
                    in = channel.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream err = null;
                try {
                    err = channel.getExtInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    channel.connect();
                } catch (JSchException e) {
                    e.printStackTrace();
                }

                byte[] tmp = new byte[1024];
                while (true) {
                    while (true) {
                        try {
                            if (in != null && !(in.available() > 0)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int i = 0;
                        try {
                            if (in != null) {
                                i = in.read(tmp, 0, 1024);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (i < 0) break;
                        outputBuffer.append(new String(tmp, 0, i));
                    }
                    while (true) {
                        try {
                            if (err != null && !(err.available() > 0)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int i = 0;
                        try {
                            if (err != null) {
                                i = err.read(tmp, 0, 1024);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (i < 0) break;
                        errorBuffer.append(new String(tmp, 0, i));
                    }
                    if (channel.isClosed()) {
                        try {
                            if ((in.available() > 0) || (err.available() > 0)) continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "exit-status: " + channel.getExitStatus(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300);
                        toast.show();
                        break;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                new AlertDialog.Builder(this).setTitle("Output:")
                        .setMessage(outputBuffer.toString())
                        .setPositiveButton("OK", null).create().show();

                channel.disconnect();
            }
        }
    }
}