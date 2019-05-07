package com.tanish2k09.sce.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class fConfigVar extends Fragment implements ConfigOptionsModal.Listener, View.OnClickListener {

    private TextView title, curVal;
    private StringValClass svc;
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

        LinearLayout ll_topCard = v.findViewById(R.id.ll_topCard);
        ll_topCard.setOnClickListener(this);

        ImageButton infoButton = v.findViewById(R.id.infoButtonConfig);
        infoButton.setOnClickListener(this);

        return v;
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

        builder.setPositiveButton("Okay", (dialog, which) -> {
           dialog.dismiss();
        });

        builder.setTitle(svc.getTitle())
                .setMessage(svc.getDescriptionString())
                .setCancelable(true)
                .create()
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_topCard)
            ConfigOptionsModal.newInstance(svc).show(getChildFragmentManager(), svc.getName() + " Selector");
        else
            showInfoDialog(v);
    }
}
