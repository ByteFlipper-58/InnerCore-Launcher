package com.byteflipper.iclauncher.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.byteflipper.iclauncher.R;
import com.byteflipper.iclauncher.databinding.ActivityMainBinding;
import com.byteflipper.iclauncher.manager.PackManager;
import com.byteflipper.iclauncher.ui.dialog.PackSelectionDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private PackManager packManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        packManager = new PackManager(this);

        // Проверяем, выбран ли пак
        String selectedPack = packManager.loadSelectedPack();
        if (selectedPack.isEmpty()) {
            // Если пак не выбран, отображаем диалог выбора пака
            showPackSelectionDialog();
        } else {
            // Пак уже выбран, можно выполнять операции с файлами
            // Например, загрузка файла
            // DownloadManager.downloadModFile(this, modId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Отображает диалог выбора пака.
     */
    private void showPackSelectionDialog() {
        List<String> availablePacks = packManager.checkForPacks();
        PackSelectionDialog.show(this, availablePacks, packName -> {
            // Сохраняем выбранный пак
            packManager.saveSelectedPack(packName);
        });
    }
}