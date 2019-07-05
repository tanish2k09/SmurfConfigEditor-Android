package com.tanish2k09.sce.fragments.containerFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tanish2k09.sce.R;
import com.tanish2k09.sce.fragments.modals.ConfigOptionsModal;
import com.tanish2k09.sce.utils.ConfigCacheClass;
import com.tanish2k09.sce.utils.StringValClass;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class fConfigVar extends Fragment implements ConfigOptionsModal.Listener, View.OnClickListener {

    private TextView curVal, title;
    private StringValClass svc;
    private LinearLayout ll_topCard;
    private int accentCol;
    public int index = -1;

    public boolean setupCardInfo(int index) {
        svc = ConfigCacheClass.getStringVal(index);
        this.index = index;

        return (svc != null);
    }

    public fConfigVar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_config_var, container, false);

        title = v.findViewById(R.id.title);
        curVal = v.findViewById(R.id.curVal);

        title.setText(svc.getName().toUpperCase());
        curVal.setText(svc.getActiveVal());

        ll_topCard = v.findViewById(R.id.ll_topCard);
        ll_topCard.setOnClickListener(this);

        ImageButton infoButton = v.findViewById(R.id.infoButtonConfig);
        infoButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = curVal.getContext().getSharedPreferences("settings",MODE_PRIVATE);
        accentCol = Color.parseColor(sp.getString("accentCol", "#00bfa5"));
        curVal.setTextColor(accentCol);

        if (sp.getBoolean("useBlackNotDark", true))
            ll_topCard.setBackground(getResources().getDrawable(R.drawable.card_border, ll_topCard.getContext().getTheme()));
        else
            ll_topCard.setBackground(getResources().getDrawable(R.drawable.card_border_dark, ll_topCard.getContext().getTheme()));

        if (sp.getBoolean("useTitlesOnCards", false)) {
            title.setText(svc.getTitle());
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            curVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            curVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setText(svc.getName().toUpperCase());
            title.setTypeface(Typeface.MONOSPACE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClickedGov(int position) {
        svc.setActiveVal(svc.getOption(position));
        curVal.setText(svc.getActiveVal());
    }

    private void showInfoDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.dialogCustomStyle);

        builder.setPositiveButton("Okay", (dialog, which) -> dialog.dismiss());

        builder.setTitle(svc.getTitle())
                .setMessage(svc.getDescriptionString())
                .setCancelable(true)
                .create()
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_topCard)
            ConfigOptionsModal.newInstance(svc, accentCol).show(getChildFragmentManager(), svc.getName() + " Selector");
        else
            showInfoDialog(v);
    }
}
