package com.byteflipper.iclauncher.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byteflipper.iclauncher.ModItem;
import com.byteflipper.iclauncher.R;
import com.byteflipper.iclauncher.adapter.ModItemAdapter;
import com.byteflipper.iclauncher.databinding.FragmentFirstBinding;
import com.byteflipper.iclauncher.utils.SharedPreferencesUtils;
import com.byteflipper.iclauncher.viewmodel.ModItemViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ModItemAdapter adapter;
    private ModItemViewModel viewModel;

    private ArrayAdapter<String> categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recview.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ModItemAdapter(new ArrayList<>());
        binding.recview.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ModItemViewModel.class);
        viewModel.getModItemList().observe(getViewLifecycleOwner(), modItems -> adapter.setModItemList(modItems));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.d("Error: ", errorMessage);
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                // TODO: Показать индикатор загрузки
            } else {
                // TODO: Скрыть индикатор загрузки
            }
        });

        String[] categoryArray = getResources().getStringArray(R.array.category_options);
        List<String> categoryList = Arrays.asList(categoryArray);
        categoryAdapter = new ArrayAdapter<>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, categoryList);
        binding.categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        String[] sort = {"new", "updated", "popular", "redaction"};
        int sortType = SharedPreferencesUtils.getInteger(requireActivity(), "sort", 0);
        viewModel.loadModItemList(sort[sortType], "ru", 2);

        binding.categoryAutoCompleteTextView.setText(categoryAdapter.getItem(sortType), false);

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