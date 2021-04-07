package np.com.socialize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SocializeDashboardActivity extends AppCompatActivity {
    private static final String TAG = "SocializeDashboardActiv";



    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialize_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment,new SocializeFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){

                    case R.id.swriz :

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment,new SocializeFragment())
                                .commit();

                        break;


                    case R.id.chat :


                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment,new ChatFragment())
                                .commit();

                        break;


                    case R.id.setting :

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment,new SettingFragment())
                                .commit();
                        break;





                }

                return true;

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode +", " + resultCode);
    }
}