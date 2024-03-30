package com.byteflipper.iclauncher.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.byteflipper.iclauncher.model.ModItem;
import com.byteflipper.iclauncher.api.ApiClient;
import com.byteflipper.iclauncher.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModItemViewModel extends ViewModel {
    private MutableLiveData<List<ModItem>> modItemList; // LiveData для списка модов
    private MutableLiveData<Boolean> isLoading; // LiveData для индикатора загрузки
    private MutableLiveData<String> errorMessage; // LiveData для сообщений об ошибках

    // Конструктор для инициализации LiveData
    public ModItemViewModel() {
        modItemList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    // Методы для получения LiveData извне
    public MutableLiveData<List<ModItem>> getModItemList() {
        return modItemList;
    }

    public MutableLiveData<Boolean> isLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Метод для загрузки списка модов с сервера
    public void loadModItemList(String sort, String lang, int page) {
        isLoading.setValue(true); // Устанавливаем значение загрузки как true

        // Создаем экземпляр ApiService для выполнения запроса
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ModItem>> call = apiService.getList(sort, lang, page); // Создаем Call для получения списка модов

        // Асинхронно выполняем запрос
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ModItem>> call, Response<List<ModItem>> response) {
                isLoading.setValue(false); // Завершаем индикацию загрузки

                if (response.isSuccessful()) { // Если запрос успешен
                    List<ModItem> items = response.body(); // Получаем список модов
                    if (items != null && items.size() > 1) { // Проверяем, что список не пуст и содержит более одного элемента
                        items.remove(0); // Удаляем первый элемент (если это требуется)
                        modItemList.setValue(items); // Устанавливаем полученный список модов в LiveData
                    } else {
                        errorMessage.setValue("Ошибка: список модов пустой или содержит только один элемент.");
                    }
                } else {
                    errorMessage.setValue("Ошибка: " + response.code()); // Устанавливаем сообщение об ошибке
                }
            }

            @Override
            public void onFailure(Call<List<ModItem>> call, Throwable t) {
                isLoading.setValue(false); // Завершаем индикацию загрузки
                errorMessage.setValue("Ошибка загрузки: " + t.getMessage()); // Устанавливаем сообщение об ошибке
            }
        });
    }
}