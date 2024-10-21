package com.bonsaizen.bonsaizenapp.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Locale

class DatePickerFragment(
    val listener: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val locale = Locale("es", "ES")
        Locale.setDefault(locale)
        val calendar = Calendar.getInstance(locale)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(requireContext(), this, year, month, day)

        return picker
    }
}