package com.hardik.remember.ui.word

import android.app.Application
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hardik.remember.R
import com.hardik.remember.adapter.SpellingAdapter
import com.hardik.remember.databinding.FragmentWordBinding
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.ui.MainActivity

class WordFragment : Fragment() {
    private val TAG = WordFragment::class.java.simpleName

    private var _binding: FragmentWordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var wordViewModel: WordViewModel
    lateinit var spellingAdapter: SpellingAdapter


    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val itemSettings = menu.findItem(R.id.action_settings)
        val itemExportWord = menu.findItem(R.id.action_export_word)

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return when (navHostFragment.navController.currentDestination?.id) {
            R.id.nav_word -> {
                itemExportWord.isVisible = true
                itemSettings.isVisible = true
            }
            R.id.nav_blog -> {
                itemExportWord.isVisible = false
                itemSettings.isVisible = true
            }
            else -> {
                itemSettings.isVisible = false
                itemExportWord.isVisible = false
            }
        }
    }*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView: ")
        _binding = FragmentWordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
//        val spellingRepositoryInstance = SpellingRepositoryInstance(DBInstance(requireContext()))
//        val wordViewModelProviderFactory = WordViewModelProviderFactory(requireContext().applicationContext as Application, spellingRepositoryInstance)
//        wordViewModel = ViewModelProvider(this, wordViewModelProviderFactory).get(WordViewModel::class.java)
        wordViewModel = (activity as MainActivity).wordViewModel

        setUpRecyclerView()

        wordViewModel.getAllSpelling().observe(viewLifecycleOwner) {
            // if you want to  use search filter
            spellingAdapter.setOriginalList(it.toList())
            // if you want to load list in adapter
//            spellingAdapter.differ.submitList(it.toList())
            if (it.isNotEmpty()){
//                showHideProgressBar()
            }
            binding.recyclerview.setPadding(0, 0, 0, 0)
        }

        val searchIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        // Set icon color programmatically
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
        // Set text size programmatically
        val searchAutoComplete = binding.searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.textSize = resources.getDimension(com.intuit.ssp.R.dimen._6ssp) // Set your desired text size
        // Set text color programmatically
        searchAutoComplete.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // if query is done then enter the search
//                if (query!= null){
//                    spellingAdapter.filter.filter(query)
//                }else{
//                    spellingAdapter.filter.filter("")
//                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               // if query is add char then the search
                spellingAdapter.filter.filter(newText)
//                if (newText.isNullOrBlank()) {
//                    // Show the search icon when the query is empty
//                    searchIcon.visibility = View.VISIBLE
//                } else {
//                    // Hide the search icon when there is text in the query
//                    searchIcon.visibility = View.GONE
//                }
                return true
            }

        })
        spellingAdapter.setOnItemClickListener { Log.e(TAG, "onViewCreated: $it", )
//            spellingAdapter.filter.filter("a")
        }
        spellingAdapter.setOnItemIsLikeClickListener { wordViewModel.saveSpelling(SpellingResponseItem(it.id,it.word, it.meaning ?: "",it.pronounce?:"",it.type?:"", !it.is_like)) }

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val spelling = spellingAdapter.differ.currentList[position]
                wordViewModel.deleteSpelling(spelling)
                Snackbar.make(view,"Successfully deleted article!", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        wordViewModel.saveSpelling(spelling)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.recyclerview)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotABeginning = firstVisibleItemPosition >= 0
//            val totalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotABeginning && isScrolling //&& totalMoreThanVisible
            if (shouldPaginate) {
//                homeViewModel.getSpelling(COUNTRY_CODE)
                isScrolling = false
            }

        }
    }

    private fun setUpRecyclerView() {
        spellingAdapter = SpellingAdapter()
        binding.recyclerview.apply {
            adapter = spellingAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)

            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            addItemDecoration(dividerItemDecoration)
//            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

            addOnScrollListener(this@WordFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        _binding = null
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