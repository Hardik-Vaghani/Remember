package com.hardik.remember.ui.blog

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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hardik.remember.R
import com.hardik.remember.databinding.FragmentBlogInsertBinding
import com.hardik.remember.models.BlogResponseItem
import com.hardik.remember.ui.MainActivity

class BlogInsertFragment : Fragment() {
    private val TAG = BlogInsertFragment::class.java.simpleName

    private var _binding: FragmentBlogInsertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object { fun newInstance() = BlogInsertFragment() }

    private lateinit var viewModel: BlogViewModel

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
                viewModel.saveBlog(BlogResponseItem(0,title, content ?: "",type?:"",isLike))
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

