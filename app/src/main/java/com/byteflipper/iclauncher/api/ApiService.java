package com.byteflipper.iclauncher.api;

import com.byteflipper.iclauncher.model.ModItem;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("list")
    Call<List<ModItem>> getList(
            @Query("sort") String sort,
            @Query("lang") String lang,
            @Query("page") int page
    );
}