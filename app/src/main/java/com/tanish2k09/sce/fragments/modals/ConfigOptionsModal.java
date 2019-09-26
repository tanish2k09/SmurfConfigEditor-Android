package com.tanish2k09.sce.fragments.modals;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tanish2k09.sce.R;
import com.tanish2k09.sce.utils.StringValClass;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ConfigOptionsModal.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link ConfigOptionsModal.Listener}.</p>
 */
public class ConfigOptionsModal extends BottomSheetDialogFragment {

    private static StringValClass svc;
    private static int color;
    private Listener mListener;

    public static ConfigOptionsModal newInstance(StringValClass svc_arg, int accentColor) {
        final ConfigOptionsModal fragment = new ConfigOptionsModal();
        svc = svc_arg;
        color = accentColor;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_config_options_dialog, container, false);
        TextView modalTitle = v.findViewById(R.id.modalTitle);
        CardView modalTitleCard = v.findViewById(R.id.modalTopTitleCard);

        modalTitle.setText(getString(R.string.modalTitle, svc.getName(), svc.getActiveVal()));
        modalTitleCard.setCardBackgroundColor(color);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(svc.getNumOptions()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onItemClickedGov(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text, serial;
        final View tapView;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_list_item_text, parent, false));
            text = itemView.findViewById(R.id.text);
            serial = itemView.findViewById(R.id.serial);
            tapView = itemView.findViewById(R.id.tapView);
            tapView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClickedGov(getAdapterPosition());
                    dismiss();
                }
            });
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;
        private int currentNum = 1;

        ItemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.text.setText(svc.getOption(currentNum-1));
            holder.serial.setText(getString(R.string.numSerial,position+1));
            currentNum++;
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
