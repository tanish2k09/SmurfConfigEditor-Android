package com.tanish2k09.sce.fragments.modals

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.config.ConfigVar
import com.tanish2k09.sce.databinding.FragmentConfigOptionsDialogBinding
import com.tanish2k09.sce.databinding.FragmentListItemTextBinding
import com.tanish2k09.sce.interfaces.ISelectedItemCallback

class ConfigOptionsModal(private val configVar: ConfigVar, private val accent: String, private val _listener: ISelectedItemCallback) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentConfigOptionsDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentConfigOptionsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.valuePickerList.layoutManager = LinearLayoutManager(view.context)
        binding.valuePickerList.adapter = OptionsAdapter()
        binding.modalTitle.text = getString(R.string.modalTitle, configVar.code, configVar.activeValue)
        binding.modalTopTitleCard.setCardBackgroundColor(Color.parseColor(accent))
    }

    inner class OptionsViewHolder(private val itemBinding: FragmentListItemTextBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            val option = configVar.options[position]
            itemBinding.optionText.text = option
            itemBinding.serial.text = (position + 1).toString().plus(')')
            itemBinding.tapView.setOnClickListener {
                _listener.onItemClickedGov(position)
                dismiss()
            }

            if (option == configVar.activeValue) {
                itemBinding.optionText.setTextColor(binding.root.context.getColor(R.color.optionActive))
                itemBinding.serial.setTextColor(binding.root.context.getColor(R.color.optionActive))
            } else if (option == configVar.originalActiveValue) {
                itemBinding.optionText.setTextColor(binding.root.context.getColor(R.color.optionOriginal))
                itemBinding.serial.setTextColor(binding.root.context.getColor(R.color.optionOriginal))
            }
        }
    }

    inner class OptionsAdapter : RecyclerView.Adapter<OptionsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
            return OptionsViewHolder(FragmentListItemTextBinding.inflate(LayoutInflater.from(parent.context)))
        }

        override fun getItemCount(): Int {
            return configVar.options.size
        }

        override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
            holder.bind(position)
        }
    }
}