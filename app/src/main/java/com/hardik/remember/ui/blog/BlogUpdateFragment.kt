package com.hardik.remember.ui.blog

import android.app.Application
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hardik.remember.R
import com.hardik.remember.databinding.FragmentBlogInsertBinding
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.BlogResponseItem
import com.hardik.remember.repository.BlogRepositoryInstance
import com.hardik.remember.ui.MainActivity
import com.hardik.remember.util.Constants
import kotlin.properties.Delegates

class BlogUpdateFragment : Fragment() {
    private val TAG = BlogUpdateFragment::class.java.simpleName

    private var _binding: FragmentBlogInsertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object { fun newInstance() = BlogUpdateFragment() }

    private lateinit var viewModel: BlogViewModel

    private var blogId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            blogId = it.getInt(Constants.PARAM_BLOG_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_content, container, false)
        Log.d(TAG, "onCreateView: ")
        _binding = FragmentBlogInsertBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root

    }
    var likeDisLike = false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
        
//        val blogRepositoryInstance = BlogRepositoryInstance(DBInstance(requireContext()))
//        val blogViewModelProviderFactory = BlogViewModelProviderFactory(requireContext().applicationContext as Application, blogRepositoryInstance)
//        viewModel = ViewModelProvider(this,blogViewModelProviderFactory).get(BlogViewModel::class.java)
        viewModel = (activity as MainActivity).blogViewModel

        viewModel.getBlog(blogId).observe(viewLifecycleOwner){
            it?.let {
                binding.tInEdtType.setText(it.type)
                binding.tInEdtTitle.setText(it.title)
                binding.tInEdtContent.setText(it.content)
                binding.sivLikeDislike.setColorFilter(if(it.is_like){ContextCompat.getColor(requireContext(),R.color.like_in_light)}else{ContextCompat.getColor(requireContext(),R.color.unlike_in_light)}, PorterDuff.Mode.SRC_IN)
                likeDisLike = it.is_like
            }
        }

        binding.tInEdtTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()){
                    handleNotContainsContent()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                if (inputText.isNotBlank() && inputText.isNotEmpty()) {
                    viewModel.containsBlogsIgnoreCase(inputText).observe(viewLifecycleOwner) { isContainsContent ->
                       if (isContainsContent){
                           showHideProgressBar()
                           handleContainsContent(inputText)
                       }else{
                           handleNotContainsContent()
                       }
                    }
                }else{
                    handleNotContainsContent()
                }
            }

            private fun handleContainsContent(inputText: String) {
//                binding.tvAlreadyInList.visibility = View.VISIBLE
//                binding.tvAlreadyInList.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))

                viewModel.isLike(inputText).observe(viewLifecycleOwner) { isLike ->
                    if (isLike) {
                        likeDisLike = true
                        binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.like_in_light), PorterDuff.Mode.SRC_IN)
                    } else {
                        likeDisLike = false
                        binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            private fun handleNotContainsContent() {
//                binding.tvAlreadyInList.visibility = View.GONE
//                binding.tvAlreadyInList.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                likeDisLike = false
                binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
            }
        })

        binding.sivLikeDislike.setOnClickListener{
            if (!likeDisLike){
                likeDisLike = true
                binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(),R.color.like_in_light), PorterDuff.Mode.SRC_IN)
            }else{
                likeDisLike = false
                binding.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(),R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
            }
        }

        binding.mbSubmit.text = resources.getString(R.string.update)

        binding.mbSubmit.setOnClickListener {
            val title = binding.tInEdtTitle.text.toString()
            val content = binding.tInEdtContent.text.toString()
            val type = binding.tInEdtType.text.toString()
            val isLike = likeDisLike
//            binding.mbSubmit.visibility = View.GONE

            showHideProgressBar()
            if (title.isEmpty()) {
                Snackbar.make(binding.tInEdtTitle, "Please enter title", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            } else {
                viewModel.saveBlog(BlogResponseItem(blogId,title, content ?: "",type?:"",isLike))
//                Snackbar.make(binding.root, "Spelling Saved", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                findNavController().popBackStack()
            }
            resetAllFields()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
    }

    private fun resetAllFields() {
        binding.apply {
            this.tInEdtTitle.setText("")
            this.tInEdtContent.setText("")
            this.tInEdtType.setText("")
//            this.tvAlreadyInList.visibility = View.GONE
//            this.mbSubmit.visibility = View.VISIBLE
            this.sivLikeDislike.setColorFilter(ContextCompat.getColor(requireContext(),R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
            likeDisLike = false
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private fun showHideProgressBar() {
        synchronized(this) {
//            handler.removeCallbacksAndMessages(null) // Remove any existing callbacks
            binding.paginationProgressBar.visibility = View.VISIBLE
            handler.postDelayed({
                if (binding.paginationProgressBar.visibility == View.VISIBLE) {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                }
            }, 1000) // 1000 milliseconds (1 second) delay, adjust as needed
        }
    }
}

