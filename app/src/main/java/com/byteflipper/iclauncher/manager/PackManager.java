package com.byteflipper.iclauncher.manager;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackManager {
    private final File packsDir;

    public PackManager(Context context) {
        // Путь к внутреннему хранилищу (/storage/emulated/0/games/horizon/packs)
        this.packsDir = new File(Environment.getExternalStorageDirectory(), "games/horizon/packs");
    }

    /**
     * Проверяет наличие паков в директории.
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
}