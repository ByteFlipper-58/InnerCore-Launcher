package com.byteflipper.iclauncher.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private static final int REQUEST_CODE_WRITE_STORAGE = 100;

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

    // Метод для обработки результата запроса разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение на запись на устройство предоставлено
                // Вызываем метод для выполнения операции, которая требует это разрешение
                // Например, выполняем скачивание файла
                // DownloadManager.downloadModFile(this, modId);
            } else {
                // Разрешение на запись на устройство не предоставлено
                // Обрабатываем этот случай соответственно, например, показываем диалоговое окно с объяснением или отключаем функциональность, требующую разрешения
                Toast.makeText(this, "Для скачивания файла требуется разрешение на запись на устройство", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Отображает диалог выбора пака.
     */
    private void showPackSelectionDialog() {
        List<String> availablePacks = packManager.checkForPacks();
        PackSelectionDialog.show(this, availablePacks, new PackSelectionDialog.PackSelectionCallback() {
            @Override
            public void onPackSelected(String packName) {
                // Сохраняем выбранный пак
                packManager.saveSelectedPack(packName);
            }
        });
    }
}