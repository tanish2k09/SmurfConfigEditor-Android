package com.tanish2k09.sce.fragments.containerFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    CardView catCard;

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

        TextView catText = v.findViewById(R.id.categoryTitle);
        catText.setText(categoryName);

        catCard = v.findViewById(R.id.catCard);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = catCard.getContext().getSharedPreferences("settings",MODE_PRIVATE);
        int accent = Color.parseColor(sp.getString("accentCol", "#00bfa5"));
        catCard.setCardBackgroundColor(accent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            catCard.setOutlineAmbientShadowColor(accent);
            catCard.setOutlineSpotShadowColor(accent);
        }
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
