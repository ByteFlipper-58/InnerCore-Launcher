package com.byteflipper.iclauncher;

import android.os.Handler;
import android.os.Looper;

import com.byteflipper.iclauncher.model.ModDescription;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class ModDescriptionFetcher {
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Интерфейс обратного вызова для обработки успешного получения описания мода или ошибки
    public interface Callback {
        void onSuccess(ModDescription modDescription); // Метод вызывается при успешном получении описания мода
        void onError(Exception e); // Метод вызывается при возникновении ошибки
    }

    // Метод для получения описания мода по его идентификатору и языку
    public void fetchModDescription(int id, String lang, Callback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String requestUrl = "https://icmods.mineprogramming.org/api/description?id=" + id + "&lang=" + lang;
            try {
                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                ModDescription modDescription = gson.fromJson(response.toString(), ModDescription.class);

                handler.post(() -> callback.onSuccess(modDescription)); // Успешный результат передается через обработчик в основной поток
            } catch (Exception e) {
                handler.post(() -> callback.onError(e)); // Ошибка передается через обработчик в основной поток
            }
        });
    }
}