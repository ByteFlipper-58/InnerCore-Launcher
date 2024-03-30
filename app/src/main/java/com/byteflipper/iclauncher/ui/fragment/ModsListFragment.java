package com.byteflipper.iclauncher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byteflipper.iclauncher.R;
import com.byteflipper.iclauncher.adapter.ModItemAdapter;
import com.byteflipper.iclauncher.databinding.FragmentModsListBinding;
import com.byteflipper.iclauncher.utils.SharedPreferencesUtils;
import com.byteflipper.iclauncher.viewmodel.ModItemViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModsListFragment extends Fragment {

    private FragmentModsListBinding binding;
    private ModItemAdapter adapter;
    private ModItemViewModel viewModel;

    private ArrayAdapter<String> categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentModsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Устанавливаем менеджер компоновки и адаптер для RecyclerView
        binding.recview.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ModItemAdapter(this, new ArrayList<>());
        binding.recview.setAdapter(adapter);

        // Получаем экземпляр ViewModel
        viewModel = new ViewModelProvider(this).get(ModItemViewModel.class);

        // Наблюдаем за списком модов и обновляем адаптер при изменениях
        viewModel.getModItemList().observe(getViewLifecycleOwner(), modItems -> adapter.setModItemList(modItems));

        // Наблюдаем за сообщениями об ошибке
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.d("Error: ", errorMessage);
        });

        // Наблюдаем за состоянием загрузки и показываем или скрываем индикатор загрузки
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                // TODO: Показать индикатор загрузки
            } else {
                // TODO: Скрыть индикатор загрузки
            }
        });

        // Устанавливаем адаптер для выпадающего списка категорий модов
        String[] categoryArray = getResources().getStringArray(R.array.category_options);
        List<String> categoryList = Arrays.asList(categoryArray);
        categoryAdapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, categoryList);
        binding.categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        // Получаем текущий выбор категории и загружаем соответствующий список модов
        String[] sort = {"new", "updated", "popular", "redaction"};
        int sortType = SharedPreferencesUtils.getInteger(requireActivity(), "sort", 1);
        viewModel.loadModItemList(sort[sortType], "ru", 2);

        // Устанавливаем текущую категорию в поле ввода
        binding.categoryAutoCompleteTextView.setText(categoryAdapter.getItem(sortType), false);

        // Обработчик выбора категории из выпадающего списка
        binding.categoryAutoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCategory = categoryAdapter.getItem(position);
            viewModel.loadModItemList(sort[position], "ru", 2);
            SharedPreferencesUtils.writeInteger(requireActivity(), "sort", position);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}