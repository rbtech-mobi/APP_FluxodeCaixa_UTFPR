package com.caixaapp.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caixaapp.R
import com.caixaapp.databinding.ItemStatementBinding
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.caixaapp.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

class StatementAdapter(
    private var items: List<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<StatementAdapter.StatementViewHolder>() {

    fun update(newItems: List<Transaction>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {
        val binding = ItemStatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatementViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class StatementViewHolder(
        private val binding: ItemStatementBinding,
        private val onDeleteClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun bind(item: Transaction) {
            val context = binding.root.context
            
            binding.itemDate.text = DateUtils.formatDate(item.data)
            binding.itemDescription.text = item.descricao
            
            val sign = if (item.tipo == TransactionType.CREDITO) "+" else "-"
            val formattedValue = formatter.format(item.valor)
            binding.itemValue.text = "$sign $formattedValue"

            // Style based on type
            if (item.tipo == TransactionType.CREDITO) {
                binding.itemValue.setTextColor(ContextCompat.getColor(context, R.color.income))
                binding.itemTypeIcon.setImageResource(R.drawable.ic_arrow_up)
                binding.itemTypeIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.income))
                binding.viewIconBg.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.income).withAlpha(30)
                )
            } else {
                binding.itemValue.setTextColor(ContextCompat.getColor(context, R.color.expense))
                binding.itemTypeIcon.setImageResource(R.drawable.ic_arrow_down)
                binding.itemTypeIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.expense))
                binding.viewIconBg.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.expense).withAlpha(30)
                )
            }

            // Logic for Delete Button
            binding.buttonDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.dialog_button_yes) { _, _ -> onDeleteClick(item) }
                    .setNegativeButton(R.string.dialog_button_no, null)
                    .show()
            }
        }

        private fun Int.withAlpha(alpha: Int): Int {
            return (this and 0x00FFFFFF) or (alpha shl 24)
        }
    }
}
