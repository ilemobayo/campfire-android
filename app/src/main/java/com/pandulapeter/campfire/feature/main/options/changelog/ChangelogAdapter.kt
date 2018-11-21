package com.pandulapeter.campfire.feature.main.options.changelog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.data.model.local.ChangelogItem
import com.pandulapeter.campfire.databinding.ItemChangelogBinding

class ChangelogAdapter(private val items: List<ChangelogItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<ChangelogAdapter.ChangelogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChangelogViewHolder.create(parent)

    override fun onBindViewHolder(holder: ChangelogViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    class ChangelogViewHolder(private val binding: ItemChangelogBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(changelogItem: ChangelogItem) {
            binding.model = changelogItem
            binding.executePendingBindings()
        }

        companion object {
            fun create(parent: ViewGroup) = ChangelogViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_changelog, parent, false))
        }
    }
}