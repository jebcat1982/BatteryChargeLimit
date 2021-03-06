package com.slash.batterychargelimit.settings

import android.content.Context
import android.preference.DialogPreference
import android.support.annotation.Keep
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.slash.batterychargelimit.ControlFile
import com.slash.batterychargelimit.R
import com.slash.batterychargelimit.SharedMethods

import java.util.Collections

@Keep
class ControlFilePreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private var ctrlFiles = emptyList<ControlFile>()

    init {
        positiveButtonText = null
        ctrlFiles = SharedMethods.getCtrlFiles(context)
    }

    inner class ControlFileAdapter internal constructor(private val data: List<ControlFile>, private val pContext: Context)
        : ArrayAdapter<ControlFile>(context, R.layout.cf_row, data) {
        private val holder: ViewHolder? = null

        private inner class ViewHolder {
            internal var label: RadioButton? = null
            internal var details: TextView? = null
            internal var experimental: TextView? = null
        }

        override fun getView(position: Int, pConvertView: View?, parent: ViewGroup): View {
            var convertView: View? = pConvertView
            val h: ViewHolder
            val cf = data[position]

            if (convertView == null) {
                h = ViewHolder()
                val inflater = LayoutInflater.from(getContext())
                convertView = inflater.inflate(R.layout.cf_row, parent, false)
                convertView!!.setOnClickListener { h.label!!.performClick() }
                h.label = convertView.findViewById(R.id.cf_label) as RadioButton
                h.label!!.setOnClickListener { v ->
                    if (v.isEnabled) {
                        SharedMethods.setCtrlFile(getContext(), v.tag as ControlFile)
                        this@ControlFilePreference.dialog.dismiss()
                    }
                }
                h.details = convertView.findViewById(R.id.cf_details) as TextView
                h.experimental = convertView.findViewById(R.id.cf_experimental) as TextView
                convertView.tag = h
            } else {
                h = convertView.tag as ViewHolder
            }

            h.label!!.isEnabled = cf.isValid
            h.label!!.text = cf.label
            h.label!!.tag = cf
            h.label!!.isChecked = cf.file == getPersistedString(null)
            h.details!!.text = cf.details
            h.experimental!!.visibility = if (cf.isExperimental) View.VISIBLE else View.INVISIBLE

            return convertView
        }
    }

    override fun onCreateDialogView(): View {
        val v = ListView(context)
        v.adapter = ControlFileAdapter(ctrlFiles, context)
        return v
    }
}