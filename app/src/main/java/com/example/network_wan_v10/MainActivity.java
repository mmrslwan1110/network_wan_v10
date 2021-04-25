package com.example.network_wan_v10;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    boolean login_flag=false;
    Vector usr = new Vector();
    Vector word = new Vector();
    private Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLogin_yon(login_flag);
        usr.add("ngh");
        usr.add("wkp");
        usr.add("yxh");
        word.add("10086");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }





    public void setLogin_yon(boolean login_flag){

        if (login_flag == true) {


            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        if (login_flag == false) {
            setContentView(R.layout.login);
            this.setTitle("Communication");

            //监听用户名输入框
            EditText name = (EditText) findViewById(R.id.usrname);
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) { //屏蔽回车 中英文空格
                    String name1 = s.toString();
                    int usr_size = usr.size();
                    boolean flag = true;
                    for (int i = 0; i < usr_size; i++) {
                        if (name1.equals(usr.elementAt(i))) {
                            TextView name = (TextView) findViewById(R.id.text_usrname);
                            name.setText("Usr name √");
                            add();
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        if (!name1.isEmpty()) {
                            TextView name = (TextView) findViewById(R.id.text_usrname);
                            name.setText("illegel name ×");
                            add();
                        } else {
                            TextView name = (TextView) findViewById(R.id.text_usrname);
                            name.setText("cannot be NULL ×");
                            add();
                        }
                    }
                }
            });

            //监听密码输入
            EditText password = (EditText) findViewById(R.id.password);
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) { //屏蔽回车 中英文空格
                    String psw = s.toString();
                    int password_size = word.size();
                    boolean flag_1 = true;
                    for (int j = 0; j < password_size; j++) {
                        if (psw.equals(word.elementAt(j))) {
                            TextView name = (TextView) findViewById(R.id.text_password);
                            name.setText("Password √");
                            add();
                            flag_1 = false;
                        }
                    }
                    if (flag_1 == true) {
                        if (!psw.isEmpty()) {
                            TextView name = (TextView) findViewById(R.id.text_password);
                            name.setText("Fail ×");
                            add();
                        } else {
                            TextView name = (TextView) findViewById(R.id.text_password);
                            name.setText("NULL ×");
                            add();
                        }
                    }

                }
            });

            login=findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
                    if(bar.getProgress()==100) {
                        Toast.makeText(v.getContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        setLogin_yon(true);
                    }
                    if(bar.getProgress()<100) {
                        Toast.makeText(v.getContext(), "Login failed!", Toast.LENGTH_LONG).show();
                        setLogin_yon(false);
                    }


                }
            });


        }



    }


    public void onClick_male(View v)
    {
        TextView name= (TextView) findViewById(R.id.text_surprise);
        name.setText("hello handsome.");
        ProgressBar bar=(ProgressBar)findViewById(R.id.progressBar);
        add();
        if(bar.getProgress()==60)
            bar.setProgress(100);
    }

    public void onClick_female(View v)
    {
        TextView name= (TextView) findViewById(R.id.text_surprise);
        name.setText("hello beauty.");
        ProgressBar bar=(ProgressBar)findViewById(R.id.progressBar);
        add();
        if(bar.getProgress()==60)
            bar.setProgress(100);
    }

    public void add() {
        int sum = 0;

        TextView usrname = (TextView) findViewById(R.id.text_usrname);
        String usrname_copy = usrname.getText().toString();
        if (usrname_copy.equals("Usr name √"))
            sum += 30;

        TextView psw = (TextView) findViewById(R.id.text_password);
        String psw_copy = psw.getText().toString();
        if (psw_copy.equals("Password √"))
            sum += 30;

        TextView surprise = (TextView) findViewById(R.id.text_surprise);
        String surprise_copy = surprise.getText().toString();
        if (surprise_copy.equals("hello handsome.") || surprise_copy.equals("hello beauty."))
            sum += 40;

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setProgress(sum);
    }


}




