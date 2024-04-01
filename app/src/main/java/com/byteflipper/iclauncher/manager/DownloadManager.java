package com.byteflipper.iclauncher.manager;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.byteflipper.iclauncher.api.client.ApiClient;
import com.byteflipper.iclauncher.api.service.DownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadManager {

    private static final int REQUEST_CODE_WRITE_STORAGE = 100;

    public interface DownloadCallback {
        void onStart();
        void onProgress(int progress);
        void onSuccess(File file);
        void onError(String message);
    }

    public static void downloadModFile(Context context, int modId, String packName, String modName, DownloadCallback callback) {
        if (callback != null) {
            callback.onStart();
        }

        // Разрешение на запись уже предоставлено или устройство работает на версии SDK ниже 23
        executeDownload(context, modId, packName, modName, callback);
    }

    private static void executeDownload(Context context, int modId, String packName, String modName, DownloadCallback callback) {
        DownloadService downloadService = ApiClient.getClient().create(DownloadService.class);

        Call<ResponseBody> call = downloadService.downloadMod(modId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        saveFileToStorage(context, responseBody, packName, modName, callback);
                    } else {
                        if (callback != null) {
                            callback.onError("Ошибка: получено пустое тело ответа");
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onError("Ошибка при скачивании файла");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (callback != null) {
                    callback.onError("Ошибка при выполнении запроса: " + t.getMessage());
                }
            }
        });
    }

    private static void saveFileToStorage(Context context, ResponseBody body, String packName, String modName, DownloadCallback callback) {
        try {
            File directory = new File(context.getExternalFilesDir(null), "games/horizon/packs/" + packName + "/innercore/mods");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = getFileName(modName);

            File file = new File(directory, fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    if (callback != null) {
                        callback.onProgress((int) ((fileSizeDownloaded * 100) / fileSize));
                    }
                }

                outputStream.flush();

                if (callback != null) {
                    callback.onSuccess(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Ошибка при сохранении файла: " + e.getMessage());
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError("Ошибка при сохранении файла: " + e.getMessage());
            }
        }
    }

    private static String getFileName(String modName) {
        if (modName == null || modName.isEmpty()) {
            return "mod_" + System.currentTimeMillis() + ".zip";
        } else {
            return modName;
        }
    }
}