package com.byteflipper.iclauncher.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackManager {
    private final File packsDir;
    private final SharedPreferences sharedPreferences;
    private static final String SELECTED_PACK_KEY = "selected_pack";

    public PackManager(Context context) {
        // Путь к внутреннему хранилищу (/storage/emulated/0/games/horizon/packs)
        this.packsDir = new File(Environment.getExternalStorageDirectory(), "games/horizon/packs");
        // Инициализация SharedPreferences для сохранения выбранного пака
        this.sharedPreferences = context.getSharedPreferences("PackPrefs", Context.MODE_PRIVATE);
    }

    /**
     * Проверяет наличие паков в директории.
     *
     * @return список доступных паков или пустой список, если паков нет.
     */
    public List<String> checkForPacks() {
        List<String> availablePacks = new ArrayList<>();

        // Проверяем, существует ли директория
        if (packsDir.exists() && packsDir.isDirectory()) {
            // Получаем список файлов/директорий внутри
            File[] files = packsDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Добавляем имя директории в список доступных паков
                        availablePacks.add(file.getName());
                    }
                }
            }
        }

        return availablePacks;
    }

    /**
     * Сохраняет выбранный пак.
     *
     * @param packName имя выбранного пака для сохранения.
     */
    public void saveSelectedPack(String packName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SELECTED_PACK_KEY, packName);
        editor.apply();
    }

    /**
     * Загружает выбранный пак, если он доступен.
     *
     * @return имя доступного выбранного пака или пустую строку, если пак не доступен.
     */
    public String loadSelectedPack() {
        String selectedPackName = sharedPreferences.getString(SELECTED_PACK_KEY, "");
        if (!selectedPackName.isEmpty() && packExists(selectedPackName)) {
            return selectedPackName;
        } else {
            // Если сохраненный пак недоступен, очищаем его из SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SELECTED_PACK_KEY);
            editor.apply();
            return "";
        }
    }

    /**
     * Получает полный путь к месту, куда нужно скачать файл для выбранного пака.
     *
     * @param packName имя выбранного пака.
     * @return полный путь к месту, куда нужно скачать файл.
     */
    public String getDownloadPathForPack(String packName) {
        return packsDir.getAbsolutePath() + "/" + packName + "/innercore/mods";
    }

    /**
     * Получает имя сохраненного пака.
     *
     * @return имя сохраненного пака или пустую строку, если пак не был сохранен.
     */
    public String getSavedPack() {
        return sharedPreferences.getString(SELECTED_PACK_KEY, "");
    }

    /**
     * Проверяет, существует ли пак с заданным именем.
     *
     * @param packName имя пака для проверки.
     * @return true, если пак существует, в противном случае - false.
     */
    private boolean packExists(String packName) {
        File packDir = new File(packsDir, packName);
        return packDir.exists() && packDir.isDirectory();
    }
}