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
    private static final int REQUEST_CODE_WRITE_STORAGE = 100;
    private ActivityResultLauncher<Intent> storageActivityResultLauncher;

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

        // Инициализация ActivityResultLauncher для запроса разрешений на доступ к хранилищу
        storageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Android 11 (R) или выше
                        if (Environment.isExternalStorageManager()) {
                            // Разрешения на управление внешним хранилищем предоставлены
                            Toast.makeText(MainActivity.this, "Разрешения на управление внешним хранилищем предоставлены", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Разрешения на доступ к хранилищу отклонены", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Ниже Android 11
                        // Дополнительная обработка, если необходимо
                    }
                });

        // Проверяем разрешения при запуске активности
        checkStoragePermissions();
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
        PackSelectionDialog.show(this, availablePacks, packName -> {
            // Сохраняем выбранный пак
            packManager.saveSelectedPack(packName);
        });
    }

    /**
     * Проверяет разрешения на доступ к хранилищу.
     */
    private void checkStoragePermissions() {
        // Android 11 (R) или выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Проверяем, предоставлены ли разрешения на управление внешним хранилищем
            if (Environment.isExternalStorageManager()) {
                // Разрешения уже предоставлены
                Toast.makeText(this, "Разрешения на доступ к хранилищу уже предоставлены", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешения еще не предоставлены, запрашиваем их у пользователя
                requestForStoragePermissions();
            }
        } else {
            // Ниже Android 11
            // Проверяем, предоставлены ли разрешения на чтение и запись в хранилище
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Разрешения уже предоставлены
                Toast.makeText(this, "Разрешения на доступ к хранилищу уже предоставлены", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешения еще не предоставлены, запрашиваем их у пользователя
                requestForStoragePermissions();
            }
        }
    }

    /**
     * Запрашивает разрешения на доступ к хранилищу.
     */
    private void requestForStoragePermissions() {
        // Android 11 (R) или выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            // Ниже Android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE_WRITE_STORAGE
            );
        }
    }
}