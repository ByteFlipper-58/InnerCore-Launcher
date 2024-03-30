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
import com.byteflipper.iclauncher.manager.PackManager;
import com.byteflipper.iclauncher.model.ModDescription;
import com.byteflipper.iclauncher.ui.dialog.PackSelectionDialog;
import com.byteflipper.iclauncher.utils.AnimationsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ModInfoFragment extends Fragment {

    private FragmentModInfoBinding binding;
    private ScreenshotsAdapter screenshotsAdapter;
    private ArrayList<String> arrayList;

    private boolean isExpanded = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentModInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle finalBundle = getArguments();
        assert finalBundle != null;

        // Получаем описание мода
        ModDescriptionFetcher fetcher = new ModDescriptionFetcher();
        fetcher.fetchModDescription(finalBundle.getInt("mod_id"), "ru", new ModDescriptionFetcher.Callback() {
            @Override
            public void onSuccess(ModDescription modDescription) {
                // Успешно получено описание мода
                loadModDescription(modDescription); // Загружаем описание мода в UI
                Log.d("SUCCESS", "DONE");
            }

            @Override
            public void onError(Exception e) {
                // Обработка ошибки получения описания мода
                Log.d("ERRORRR: ", e.getMessage());
            }
        });

        // Создаем адаптер для списка скриншотов
        screenshotsAdapter = new ScreenshotsAdapter(requireContext(), arrayList);
        binding.screenshotsRecview.setAdapter(screenshotsAdapter);

        // Скрываем кнопку cancelOrDeleteButton при запуске фрагмента
        binding.cancelOrDeleteButton.setVisibility(View.GONE);

        // Обработчик клика на кнопке действия
        binding.actionButton.setOnClickListener(v -> {
            // Получаем менеджер паков
            PackManager packManager = new PackManager(requireActivity());
            // Показываем диалог выбора пака с доступными паками
            PackSelectionDialog.show(requireActivity(), packManager.checkForPacks());

            // Разворачиваем кнопки, если они не развернуты
            if (!isExpanded) {
                expandButtons();
            }
        });

        // Обработчик клика на кнопке отмены или удаления
        binding.cancelOrDeleteButton.setOnClickListener(v -> {
            // Сворачиваем кнопки, если они развернуты
            if (isExpanded) {
                collapseButtons();
            }
        });
    }

    // Метод для загрузки описания мода в UI
    private void loadModDescription(ModDescription modDescription) {
        Picasso.get().load("https://icmods.mineprogramming.org/api/img/" + modDescription.getIcon_full()).into(binding.modIcon);
        binding.modName.setText(modDescription.getTitle());
        binding.modDeveloper.setText(modDescription.getAuthor_name());
        binding.modVersion.setText(String.valueOf(modDescription.getVersion_name()));
        binding.likesCount.setText(String.valueOf(modDescription.getLikes()));
        binding.downloadsCount.setText(String.valueOf(modDescription.getDownloads()));
        binding.modDescription.setText(modDescription.getDescription_full());

        arrayList = new ArrayList<>(modDescription.getScreenshots());
        screenshotsAdapter.setScreenshotsList(arrayList);
        screenshotsAdapter.notifyDataSetChanged();
    }

    // Метод для разворачивания кнопок
    private void expandButtons() {
        isExpanded = true;
        AnimationsUtils.expand(binding.actionButton, requireContext());
        AnimationsUtils.expand(binding.cancelOrDeleteButton, requireContext());
        setButtonMargins(true);
    }

    // Метод для сворачивания кнопок
    private void collapseButtons() {
        isExpanded = false;
        AnimationsUtils.collapse(binding.actionButton, requireContext());
        AnimationsUtils.collapse(binding.cancelOrDeleteButton, requireContext());
        setButtonMargins(false);
    }

    // Метод для установки отступов кнопок
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