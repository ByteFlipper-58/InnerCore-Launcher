package com.byteflipper.iclauncher.api.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DownloadService {
    @GET("download?id={modId}")
    Call<ResponseBody> downloadMod(@Path("modId") int modId);
}