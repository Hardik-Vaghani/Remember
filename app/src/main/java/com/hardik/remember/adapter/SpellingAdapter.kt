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
import com.hardik.remember.models.SpellingResponseItem

class SpellingAdapter : RecyclerView.Adapter<SpellingAdapter.SpellingViewHolder>(), Filterable {
    inner class SpellingViewHolder(var binding: ItemSpellingPreviewBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SpellingResponseItem>(){
        override fun areItemsTheSame(oldItem: SpellingResponseItem, newItem: SpellingResponseItem): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: SpellingResponseItem, newItem: SpellingResponseItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this@SpellingAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellingViewHolder {
        return SpellingViewHolder(ItemSpellingPreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpellingViewHolder, position: Int) {
       val spelling = differ.currentList[position]

        // Apply fade-in animations
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()

        holder.itemView.apply {
            holder.binding.mtvType.text = spelling.type
            holder.binding.mtvType.visibility = if (spelling.type.isEmpty()) View.GONE else View.VISIBLE
            var pronunciation = if (spelling.pronounce.isNotEmpty()){" <font color=\"${ContextCompat.getColor(context, R.color.unlike_in_dark)}\"><small><small>(${spelling.pronounce})</small></small></font><br>"}else{""}
            var meaning = if (spelling.meaning.isNotEmpty()){"<font color=\"${ContextCompat.getColor(context, R.color.unlike_in_dark)}\"><small><small><small>${spelling.meaning}</small><small></small></font>"}else{""}

            var spellingWordHtmlString = spelling.word + pronunciation + meaning

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.mtvSpelling.text = Html.fromHtml(spellingWordHtmlString, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                holder.binding.mtvSpelling.text = HtmlCompat.fromHtml(spellingWordHtmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

//            holder.binding.mtvSpelling.text = spelling.word
            if(spelling.is_like){
                holder.binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(context, R.color.like_in_dark), PorterDuff.Mode.SRC_IN)
            }else{
                holder.binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(context,R.color.unlike_in_dark), PorterDuff.Mode.SRC_IN)
            }
            setOnClickListener{
                onItemClickListener?.let {
                    it(
                        SpellingResponseItem(
                            spelling.id,
                            spelling.word,
                            spelling.meaning,
                            spelling.pronounce,
                            spelling.type,
                            spelling.is_like
                        )
                    )
                }
            }
        }
        holder.binding.sivLikeDislike.apply {
            setOnClickListener{
                onItemIsLikeClickListener?.let {
                    it(
                        SpellingResponseItem(
                            spelling.id,
                            spelling.word,
                            spelling.meaning,
                            spelling.pronounce,
                            spelling.type,
                            spelling.is_like
                        )
                    )
                }
            }
        }
    }

    private var onItemClickListener: ((SpellingResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (SpellingResponseItem) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemIsLikeClickListener: ((SpellingResponseItem) -> Unit)? = null

    fun setOnItemIsLikeClickListener(listener: (SpellingResponseItem) -> Unit) {
        onItemIsLikeClickListener = listener
    }


    private var originalList: List<SpellingResponseItem> = emptyList()

    // Function to set the original list
    fun setOriginalList(list: List<SpellingResponseItem>) {
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
                        spelling.word.contains(constraint, true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as List<SpellingResponseItem>?)
            }
        }
    }
}