package com.tanish2k09.sce.fragments.containerFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanish2k09.sce.R;


import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    private String categoryName = "Not categorized";
    private View dividerTop, dividerBottom;
    private TextView catText;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public void setInstanceCategory(String category) {
        categoryName = category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);

        catText = v.findViewById(R.id.categoryTitle);
        catText.setText(categoryName);

        dividerTop = v.findViewById(R.id.catDivTop);
        dividerBottom = v.findViewById(R.id.catDivBottom);

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = catText.getContext().getSharedPreferences("settings",MODE_PRIVATE);
        int accent = Color.parseColor(sp.getString("accentCol", "#00bfa5"));
        catText.setTextColor(accent);
        dividerBottom.setBackgroundColor(accent);
        dividerTop.setBackgroundColor(accent);
    }

    public void pushConfigVarFragment(fConfigVar var, int index) {
        getChildFragmentManager().beginTransaction().add(R.id.categoryList, var, "configCard"+index).commit();
    }

    public boolean popFragment(int index) {
        getChildFragmentManager().beginTransaction()
                                    .remove(Objects.requireNonNull(getChildFragmentManager()
                                        .findFragmentByTag("configCard" + index)))
                                    .commitNow();
        return getChildFragmentManager().getFragments().size() == 0;
    }
}
