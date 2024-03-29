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
    private MutableLiveData<List<ModItem>> modItemList;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    public ModItemViewModel() {
        modItemList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public MutableLiveData<List<ModItem>> getModItemList() {
        return modItemList;
    }

    public MutableLiveData<Boolean> isLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadModItemList(String sort, String lang, int page) {
        isLoading.setValue(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ModItem>> call = apiService.getList(sort, lang, page);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ModItem>> call, Response<List<ModItem>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    List<ModItem> items = response.body();
                    if (items != null && items.size() > 1) {
                        items.remove(0);
                        modItemList.setValue(items);
                    } else {
                        errorMessage.setValue("Ошибка: список модов пустой или содержит только один элемент.");
                    }
                } else {
                    errorMessage.setValue("Ошибка: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<List<ModItem>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Ошибка загрузки: " + t.getMessage());
            }
        });
    }
}