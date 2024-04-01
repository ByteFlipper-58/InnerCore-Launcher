package com.byteflipper.iclauncher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.byteflipper.iclauncher.ModDescriptionFetcher;
import com.byteflipper.iclauncher.R;
import com.byteflipper.iclauncher.adapter.ScreenshotsAdapter;
import com.byteflipper.iclauncher.databinding.FragmentModInfoBinding;
import com.byteflipper.iclauncher.manager.DownloadManager;
import com.byteflipper.iclauncher.manager.PackManager;
import com.byteflipper.iclauncher.model.ModDescription;
import com.byteflipper.iclauncher.ui.dialog.PackSelectionDialog;
import com.byteflipper.iclauncher.utils.AnimationsUtils;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ModInfoFragment extends Fragment {

    private FragmentModInfoBinding binding;
    private ScreenshotsAdapter screenshotsAdapter;
    private ArrayList<String> arrayList;

    private boolean isExpanded = false;
    private String selectedPackName;
    private String mod_name;
    private int mod_id;

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
                loadModDescription(modDescription);
                Log.d("SUCCESS", "DONE");
            }

            @Override
            public void onError(Exception e) {
                Log.d("ERRORRR: ", e.getMessage());
            }
        });

        screenshotsAdapter = new ScreenshotsAdapter(requireContext(), arrayList);
        binding.screenshotsRecview.setAdapter(screenshotsAdapter);

        binding.cancelOrDeleteButton.setVisibility(View.GONE);

        binding.actionButton.setOnClickListener(v -> {
            PackManager packManager = new PackManager(requireActivity());
            ArrayList<String> availablePacks = (ArrayList<String>) packManager.checkForPacks();
            if (!availablePacks.isEmpty()) {
                PackSelectionDialog.show(requireActivity(), availablePacks, new PackSelectionDialog.PackSelectionCallback() {
                    @Override
                    public void onPackSelected(String packName) {
                        selectedPackName = packName;
                        packManager.saveSelectedPack(packName); // Сохраняем выбранный пак
                    }
                });
            } else {
                // Handle case where no packs are available
                Log.e("ModInfoFragment", "No packs available");
            }

            if (!isExpanded) {
                expandButtons();
            }
        });

        binding.cancelOrDeleteButton.setOnClickListener(v -> {
            if (isExpanded) {
                collapseButtons();
            }
        });

        binding.actionButton.setOnClickListener(v -> {
            if (selectedPackName != null) {
                int modId = finalBundle.getInt("mod_id");
                String packDirectoryPath = new PackManager(requireActivity()).getSavedPack(); // Получаем полный путь к месту для скачивания файла
                DownloadManager.downloadModFile(requireContext(), modId, packDirectoryPath, mod_name, new DownloadManager.DownloadCallback() {
                    @Override
                    public void onStart() {
                        binding.progressIndicator.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress) {
                        binding.progressIndicator.setProgressCompat(progress, true);
                    }

                    @Override
                    public void onSuccess(File file) {
                        binding.progressIndicator.setVisibility(View.GONE);
                        // Handle download success
                    }

                    @Override
                    public void onError(String message) {
                        binding.progressIndicator.setVisibility(View.GONE);
                        // Handle download error
                    }
                });
            } else {
                // Handle case where no pack is selected
                Log.e("ModInfoFragment", "No pack selected");
            }
        });
    }

    private void loadModDescription(ModDescription modDescription) {
        Picasso.get().load("https://icmods.mineprogramming.org/api/img/" + modDescription.getIcon_full()).into(binding.modIcon);
        binding.modName.setText(modDescription.getTitle());
        binding.modDeveloper.setText(modDescription.getAuthor_name());
        binding.modVersion.setText(String.valueOf(modDescription.getVersion_name()));
        binding.likesCount.setText(String.valueOf(modDescription.getLikes()));
        binding.downloadsCount.setText(String.valueOf(modDescription.getDownloads()));
        binding.modDescription.setText(modDescription.getDescription_full());
        mod_id = modDescription.getId();
        mod_name = modDescription.getFilename();

        arrayList = new ArrayList<>(modDescription.getScreenshots());
        screenshotsAdapter.setScreenshotsList(arrayList);
        screenshotsAdapter.notifyDataSetChanged();
    }

    private void expandButtons() {
        isExpanded = true;
        AnimationsUtils.expand(binding.actionButton, requireContext());
        AnimationsUtils.expand(binding.cancelOrDeleteButton, requireContext());
        setButtonMargins(true);
    }

    private void collapseButtons() {
        isExpanded = false;
        AnimationsUtils.collapse(binding.actionButton, requireContext());
        AnimationsUtils.collapse(binding.cancelOrDeleteButton, requireContext());
        setButtonMargins(false);
    }

    private void setButtonMargins(boolean isExpanded) {
        int startMargin = getResources().getDimensionPixelSize(isExpanded ? R.dimen.button_margin_expanded : R.dimen.button_margin_default);
        int endMargin = getResources().getDimensionPixelSize(isExpanded ? R.dimen.button_margin_expanded : R.dimen.button_margin_default);

        ViewGroup.MarginLayoutParams paramsActionButton = (ViewGroup.MarginLayoutParams) binding.actionButton.getLayoutParams();
        paramsActionButton.setMarginStart(startMargin);

        ViewGroup.MarginLayoutParams paramsCancelButton = (ViewGroup.MarginLayoutParams) binding.cancelOrDeleteButton.getLayoutParams();
        paramsCancelButton.setMarginEnd(endMargin);

        binding.actionButton.setLayoutParams(paramsActionButton);
        binding.cancelOrDeleteButton.setLayoutParams(paramsCancelButton);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}