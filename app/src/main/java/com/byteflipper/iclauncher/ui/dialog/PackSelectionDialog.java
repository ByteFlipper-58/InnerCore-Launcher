package com.byteflipper.iclauncher.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Класс для отображения диалогового окна выбора пака.
 */
public class PackSelectionDialog {

    /**
     * Показывает диалоговое окно выбора пака.
     *
     * @param context        Контекст активности или фрагмента, откуда вызывается диалог.
     * @param availablePacks Список доступных паков для выбора.
     */
    public static void show(@NonNull Context context, @NonNull List<String> availablePacks) {
        // Проверка, есть ли доступные паки
        if (availablePacks.isEmpty()) {
            // Если нет доступных паков, показываем короткое сообщение об этом и завершаем метод
            Toast.makeText(context, "Нет доступных паков", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем адаптер для списка доступных паков
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice, availablePacks);

        // Создаем диалоговое окно с помощью MaterialAlertDialogBuilder
        new MaterialAlertDialogBuilder(context)
                // Устанавливаем заголовок диалога
                .setTitle("Выберите пак")
                // Устанавливаем адаптер для отображения списка паков в диалоге и обработчик для выбора пака
                .setAdapter(adapter, (dialog, which) -> {
                    // Получаем выбранный пак по его позиции в списке
                    String selectedPack = availablePacks.get(which);
                    // Показываем короткое уведомление о выбранном паке
                    Toast.makeText(context, "Выбран пак: " + selectedPack, Toast.LENGTH_SHORT).show();
                })
                // Устанавливаем кнопку "Отмена"
                .setNegativeButton("Отмена", null)
                // Устанавливаем кнопку "Скачать" с обработчиком клика
                .setPositiveButton("Скачать", (dialog, which) -> Toast.makeText(context, "Скачивание...", Toast.LENGTH_SHORT).show())
                // Устанавливаем кнопку "Запомнить выбор" с обработчиком клика
                .setNeutralButton("Запомнить выбор", (dialog, which) -> Toast.makeText(context, "Выбор запомнен", Toast.LENGTH_SHORT).show())
                // Показываем диалоговое окно
                .show();
    }
}