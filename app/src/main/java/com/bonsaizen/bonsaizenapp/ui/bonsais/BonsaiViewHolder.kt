package com.bonsaizen.bonsaizenapp.ui.bonsais


import androidx.recyclerview.widget.RecyclerView
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.databinding.ItemListBonsaisBinding
import com.squareup.picasso.Picasso

class BonsaiViewHolder(
    private val binding: ItemListBonsaisBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        bonsai: Bonsai,
        onBonsaiClicked: (Bonsai) -> Unit
    ) {
        binding.apply {
            tvBonsaiName.text = bonsai.name
            tvDate.text = bonsai.dateAdquisition

            if (bonsai.images.isNotEmpty()) {
                Picasso.get()
                    .load(bonsai.images.first())
                    .resize(200, 200)
                    .rotate(90f)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(binding.ivBonsai)
            } else {
                binding.ivBonsai.setImageResource(R.drawable.ic_image)
            }

            itemView.setOnClickListener { onBonsaiClicked(bonsai) }
        }
    }
}