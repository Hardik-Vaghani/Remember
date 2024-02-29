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
import com.hardik.remember.databinding.ItemBlogPreviewBinding
import com.hardik.remember.models.BlogResponseItem

class BlogAdapter : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>(), Filterable {
    inner class BlogViewHolder(var binding: ItemBlogPreviewBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<BlogResponseItem>(){
        override fun areItemsTheSame(oldItem: BlogResponseItem, newItem: BlogResponseItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BlogResponseItem, newItem: BlogResponseItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this@BlogAdapter, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        return BlogViewHolder(ItemBlogPreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
       val blog = differ.currentList[position]

        // Apply fade-in animations
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()

        holder.itemView.apply {
            holder.binding.mtvType.text = blog.type
            holder.binding.mtvType.visibility = if (blog.type.isEmpty()) View.GONE else View.VISIBLE
//            val datestamp = if (blog.datestamp.isNotEmpty()){"<br>  <font color=\"grey\"><small><small><small>${blog.datestamp}</small></small></small></font>"}else{""}
//            val timestamp = if (blog.timestamp.isNotEmpty()){"<font color=\"grey\"><small><small><small>${blog.timestamp}</small></small></small></font>"}else{""}

            val blogTitleHtmlString = blog.title// + datestamp +" <small>|</small> "+ timestamp

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.mtvTitle.text = Html.fromHtml(blogTitleHtmlString, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                holder.binding.mtvTitle.text = HtmlCompat.fromHtml(blogTitleHtmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

//            holder.binding.mtvContent.visibility = if (blog.content.isEmpty()) View.GONE else View.VISIBLE
//            holder.binding.mtvContent.text = if(blog.content.isNotEmpty()){ blog.content} else{""}

            if(blog.is_like){
                holder.binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(context,R.color.like_in_dark), PorterDuff.Mode.SRC_IN)
            }else{
                holder.binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(context, R.color.unlike_in_dark), PorterDuff.Mode.SRC_IN)
            }
            setOnClickListener{
                onItemClickListener?.let {
                    it(
                        BlogResponseItem(
                            id = blog.id,
                            title = blog.title,
                            type = blog.type,
                            is_like = blog.is_like,
                            datestamp = blog.datestamp,
                            timestamp = blog.timestamp
                        )
                    )
                }
            }
        }
        holder.binding.sivLikeDislike.apply {
            setOnClickListener{
                onItemIsLikeClickListener?.let {
                    it(
                        BlogResponseItem(
                            id = blog.id,
                            title = blog.title,
                            type = blog.type,
                            is_like = blog.is_like,
                            datestamp = blog.datestamp,
                            timestamp = blog.timestamp
                        )
                    )
                }
            }
        }
    }

    private var onItemClickListener: ((BlogResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (BlogResponseItem) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemIsLikeClickListener: ((BlogResponseItem) -> Unit)? = null

    fun setOnItemIsLikeClickListener(listener: (BlogResponseItem) -> Unit) {
        onItemIsLikeClickListener = listener
    }

    private var originalList: List<BlogResponseItem> = emptyList()

    // Function to set the original list
    fun setOriginalList(list: List<BlogResponseItem>) {
        originalList = list
        differ.submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter { blog ->
                        blog.title.contains(constraint, true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as List<BlogResponseItem>?)
            }
        }
    }
}