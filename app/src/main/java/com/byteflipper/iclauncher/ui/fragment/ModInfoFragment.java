package com.byteflipper.iclauncher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.byteflipper.iclauncher.ModDescriptionFetcher;
import com.byteflipper.iclauncher.adapter.ScreenshotsAdapter;
import com.byteflipper.iclauncher.databinding.FragmentModInfoBinding;
import com.byteflipper.iclauncher.model.ModDescription;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ModInfoFragment extends Fragment {

    private FragmentModInfoBinding binding;
    private ScreenshotsAdapter screenshotsAdapter;
    private ArrayList<String> arrayList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentModInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle finalBundle = getArguments();
        assert finalBundle != null;

        ModDescriptionFetcher fetcher = new ModDescriptionFetcher();
        fetcher.fetchModDescription(finalBundle.getInt("mod_id"), "ru", new ModDescriptionFetcher.Callback() {
            @Override
            public void onSuccess(ModDescription modDescription) {
                Picasso.get().load("https://icmods.mineprogramming.org/api/img/" + modDescription.getIcon_full()).into(binding.modIcon);
                binding.modName.setText(modDescription.getTitle());
                binding.modDeveloper.setText(modDescription.getAuthor_name());
                binding.modVersion.setText(String.valueOf(modDescription.getVersion_name()));
                binding.likesCount.setText(String.valueOf(modDescription.getLikes()));
                binding.downloadsCount.setText(String.valueOf(modDescription.getDownloads()));
                //binding.updateDate.setText(String.valueOf(modDescription.getLast_update()));
                binding.modDescription.setText(modDescription.getDescription_full());

                arrayList = new ArrayList<>(modDescription.getScreenshots());
                screenshotsAdapter.setScreenshotsList(arrayList);
                screenshotsAdapter.notifyDataSetChanged();

                Log.d("SUCCESS", "DONE");
            }

            @Override
            public void onError(Exception e) {
                Log.d("ERRORRR: ", e.getMessage());
            }
        });

        screenshotsAdapter = new ScreenshotsAdapter(requireContext(), arrayList);
        binding.screenshotsRecview.setAdapter(screenshotsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}