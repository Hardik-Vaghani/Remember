package com.hardik.remember.ui.blog

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hardik.remember.R
import com.hardik.remember.databinding.FragmentBlogReadScrollingBinding
import com.hardik.remember.models.BlogResponseItem
import com.hardik.remember.ui.MainActivity
import com.hardik.remember.util.Constants.Companion.PARAM_BLOG_ID
import kotlin.properties.Delegates

class BlogReadScrollingFragment : Fragment() {
    private val TAG = BlogReadScrollingFragment::class.java.simpleName

    private var _binding: FragmentBlogReadScrollingBinding? = null
    private val binding get() = _binding!!

    private lateinit var blogViewModel: BlogViewModel
    var likeDisLike = false
    lateinit var blog: BlogResponseItem

    private var blogId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            blogId = it.getInt(PARAM_BLOG_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_blog_read_scrolling, container, false)
        _binding = FragmentBlogReadScrollingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blogViewModel = (activity as MainActivity).blogViewModel

        blogViewModel.getBlog(blogId).observe(viewLifecycleOwner){
            it?.let {
                blog = BlogResponseItem(id=it.id, title = it.title, content = it.content, type = it.type, is_like = it.is_like)
                binding.tvType.text= it.type
                binding.tvDateTime.text = it.datestamp+" | "+it.timestamp
                binding.tvTitle.text = it.title
                binding.tvContent.text = it.content
//                binding.tvContent.text = resources.getString(R.string.large_text)
            }
        }

        // click on like and dislike button
        binding.sivLikeDislike.setOnClickListener{
            if (!likeDisLike){
                likeDisLike = true
                blog.is_like = true
                binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(),R.color.like_in_light), PorterDuff.Mode.SRC_IN)
            }else{
                likeDisLike = false
                blog.is_like = false
                binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(),R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
            }
            blogViewModel.saveBlog(blog)
        }

        blogViewModel.isLike(blogId).observe(viewLifecycleOwner) { isLike ->
            isLike?.let {
                if (it) {
                    likeDisLike = true
                    binding.sivLikeDislike.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.like_in_light
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    likeDisLike = false
                    binding.sivLikeDislike.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.unlike_in_light
                        ), PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

        binding.iBtnEditNotes.setOnClickListener {
            val b = Bundle()
            b.putInt(PARAM_BLOG_ID, blogId)
            findNavController().navigate(R.id.blogUpdateFragment,b)
        }
    }
}