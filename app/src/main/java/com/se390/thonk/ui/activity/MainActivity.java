package com.se390.thonk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.se390.thonk.services.NotificationService;
import com.se390.thonk.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceRunning() {
        try {
            String names = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
            String entry = getPackageName() + "/" + NotificationService.class.getName();
            System.out.println(entry);
            String[] entries = names.split(":");
            return Arrays.asList(entries).contains(entry);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showSettingsPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, R.string.service_name_enable, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_history, R.id.navigation_rules)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if(!isServiceRunning()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.perm_request_content)
                    .setTitle(R.string.perm_request_title)
                    .setNeutralButton(android.R.string.yes, (dialog, which) -> showSettingsPermission())
                    .show();
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Intent intent = new Intent(this, EditRuleActivity.class);
        startActivity(intent);
    }
}