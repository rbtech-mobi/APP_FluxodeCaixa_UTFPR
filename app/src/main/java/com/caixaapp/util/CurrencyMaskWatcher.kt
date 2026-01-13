package com.caixaapp.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class CurrencyMaskWatcher(private val editText: EditText) : TextWatcher {
    private var isUpdating = false
    private val locale = Locale("pt", "BR")
    private val nf = NumberFormat.getCurrencyInstance(locale)

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isUpdating) {
            isUpdating = false
            return
        }

        isUpdating = true

        var str = s.toString().replace("[R$\u00A0,\\.]".toRegex(), "")
        if (str.isEmpty()) str = "0"
        
        try {
            val parsed = str.toDouble() / 100
            val formatted = nf.format(parsed)
            
            editText.setText(formatted)
            editText.setSelection(formatted.length)
        } catch (e: NumberFormatException) {
            str = ""
        }
    }

    override fun afterTextChanged(s: Editable?) {}
}
