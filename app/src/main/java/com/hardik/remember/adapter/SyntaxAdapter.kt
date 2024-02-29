package com.hardik.remember.adapter

import android.graphics.PorterDuff
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hardik.remember.R
import com.hardik.remember.databinding.ItemSpellingPreviewBinding
import com.hardik.remember.databinding.ItemSyntaxPreviewBinding
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.models.SyntaxResponseItem

class SyntaxAdapter : RecyclerView.Adapter<SyntaxAdapter.SyntaxViewHolder>(), Filterable {
    inner class SyntaxViewHolder(var binding: ItemSyntaxPreviewBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SyntaxResponseItem>(){
        override fun areItemsTheSame(oldItem: SyntaxResponseItem, newItem: SyntaxResponseItem): Boolean {
            return oldItem.sentenceType == newItem.sentenceType
        }

        override fun areContentsTheSame(oldItem: SyntaxResponseItem, newItem: SyntaxResponseItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this@SyntaxAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyntaxViewHolder {
        return SyntaxViewHolder(ItemSyntaxPreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SyntaxViewHolder, position: Int) {
       val syntax = differ.currentList[position]

        // Apply fade-in animations
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()

        holder.itemView.apply {
            holder.binding.mtvType.text = syntax.sentenceType
            holder.binding.mtvType.visibility = if (syntax.sentenceType.isEmpty()) View.GONE else View.VISIBLE
//            var pronunciation = if (syntax.pronounce.isNotEmpty()){" <font color=\"${ContextCompat.getColor(context, R.color.unlike_in_dark)}\"><small><small>(${spelling.pronounce})</small></small></font><br>"}else{""}
//            var meaning = if (syntax.meaning.isNotEmpty()){"<font color=\"${ContextCompat.getColor(context, R.color.unlike_in_dark)}\"><small><small><small>${spelling.meaning}</small><small></small></font>"}else{""}

            var spellingWordHtmlString = syntax.example//.word + pronunciation + meaning

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.mtvSyntax.text = Html.fromHtml(spellingWordHtmlString, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                holder.binding.mtvSyntax.text = HtmlCompat.fromHtml(spellingWordHtmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            setOnClickListener{
                onItemClickListener?.let {
                    it(
                        SyntaxResponseItem(
                            id = syntax.id,
                            data = syntax.data,
                            example = syntax.example,
                            sentenceType = syntax.sentenceType,
                        )
                    )
                }
            }
        }
    }

    private var onItemClickListener: ((SyntaxResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (SyntaxResponseItem) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemIsLikeClickListener: ((SyntaxResponseItem) -> Unit)? = null

    fun setOnItemIsLikeClickListener(listener: (SyntaxResponseItem) -> Unit) {
        onItemIsLikeClickListener = listener
    }


    private var originalList: List<SyntaxResponseItem> = emptyList()

    // Function to set the original list
    fun setOriginalList(list: List<SyntaxResponseItem>) {
        originalList = list
        differ.submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter { spelling ->
                        spelling.sentenceType.contains(constraint, true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as List<SyntaxResponseItem>?)
            }
        }
    }
}