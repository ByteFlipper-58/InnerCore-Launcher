package com.byteflipper.iclauncher.api.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DownloadService {
    @GET("download")
    Call<ResponseBody> downloadMod(@Query("id") int modId);
}