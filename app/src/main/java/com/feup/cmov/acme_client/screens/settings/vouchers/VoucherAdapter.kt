package com.feup.cmov.acme_client.screens.settings.vouchers

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.feup.cmov.acme_client.AcmeApplication
import com.feup.cmov.acme_client.R
import com.feup.cmov.acme_client.database.models.Voucher
import com.feup.cmov.acme_client.database.models.composed_models.VoucherWithOrder


class VoucherAdapter: RecyclerView.Adapter<VoucherAdapter.ViewHolder>() {

    var data = listOf<VoucherWithOrder>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voucher = data[position]
        holder.bind(voucher)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val voucherType: TextView = itemView.findViewById(R.id.voucher_type)
        private val voucherCaption: TextView = itemView.findViewById(R.id.voucher_caption)
        private val usedCaption: TextView = itemView.findViewById(R.id.used_caption)
        private val usedBar: View = itemView.findViewById(R.id.used_bar)
        private val imageView: ImageView =  itemView.findViewById(R.id.voucher_image)

        fun bind(item: VoucherWithOrder) {
            when (item.voucher.voucherType) {
                "discount" -> {
                    imageView.setImageResource(R.drawable.voucher_discount)
                    voucherType.text = itemView.resources.getString(R.string.discount)
                    voucherCaption.text = itemView.resources.getString(R.string.discount_5)
                }
                "free_coffee" ->  {
                    imageView.setImageResource(R.drawable.voucher_free_item)
                    voucherType.text = itemView.resources.getString(R.string.free_item)
                    voucherCaption.text = itemView.resources.getString(R.string.free_item_coffee)
                }
            }

            when (item.voucher.hasBeenUsed()) {
                true -> {
                    usedBar.setBackgroundColor(getColor(AcmeApplication.getAppContext(), R.color.red_500))
                    usedCaption.text = "Used on " + item.order!!.formatCompletedDate().split(' ')[0]
                }
                false -> {
                    usedBar.setBackgroundColor(getColor(AcmeApplication.getAppContext(), R.color.green_500))
                    usedCaption.text = "No expiration date"
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.voucher_row, parent, false)

                return ViewHolder(view)
            }
        }
    }
}