package com.hardik.remember.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.hardik.remember.R
import com.hardik.remember.databinding.ActivityMainBinding
import com.hardik.remember.databinding.ItemAlertDialogSpellingBinding
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.BlogRepositoryInstance
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.ui.blog.BlogViewModel
import com.hardik.remember.ui.blog.BlogViewModelProviderFactory
import com.hardik.remember.ui.word.WordViewModel
import com.hardik.remember.ui.word.WordViewModelProviderFactory
import com.hardik.remember.util.CsvWriterUtil
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var appBarLayout: AppBarLayout
    lateinit var my_toolbar: Toolbar
    lateinit var bottomNavView: BottomNavigationView
    var isToolbarVisible = true
    var isBottomNavigationViewVisible = true

    lateinit var wordViewModel: WordViewModel
    lateinit var blogViewModel: BlogViewModel
    lateinit var dataList: List<SpellingResponseItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get viewModel
        val spellingRepositoryInstance = SpellingRepositoryInstance(DBInstance(this))
        val wordViewModelProviderFactory = WordViewModelProviderFactory(
            applicationContext as Application,
            spellingRepositoryInstance
        )
        wordViewModel =
            ViewModelProvider(this, wordViewModelProviderFactory).get(WordViewModel::class.java)

        val blogRepositoryInstance = BlogRepositoryInstance(DBInstance(this))
        val blogViewModelProviderFactory = BlogViewModelProviderFactory(
            applicationContext as Application,
            blogRepositoryInstance
        )
        blogViewModel = ViewModelProvider(this, blogViewModelProviderFactory).get(BlogViewModel::class.java)

        wordViewModel.getAllSpelling().observe(this) { it ->
                it?.let {
                    dataList = it.toList()
                }
            }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appBarLayout = binding.appBarMain.appBarLayout
        my_toolbar = binding.appBarMain.toolbar
        bottomNavView = binding.appBarMain.bottomNavView

        setSupportActionBar(my_toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

            when (navHostFragment.navController.currentDestination?.id) {
                R.id.nav_word -> {
//                    Snackbar.make(view, "your own action Home", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                    showCustomAlertDialog()
                }
                R.id.nav_blog -> {
//                    Snackbar.make(view, "your own action Gallery", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                    navHostFragment.navController.navigate(R.id.blogInsertFragment)
                }
                R.id.nav_slideshow -> {
//                    Snackbar.make(view, "your own action Slideshow", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
                else -> {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
            }
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_word, R.id.nav_blog, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarMain.bottomNavView.setupWithNavController(navController)

        // Handle the scroll change to show/hide the Toolbar and BottomNavigationView
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

            if (percentage >= 0.7f && isToolbarVisible) {
                // Toolbar is fully collapsed, hide it
                hideToolbarAndBottomNavigationView()
            } else if (percentage < 0.7f && !isToolbarVisible) {
                // Toolbar is not fully collapsed, show it
                showToolbarAndBottomNavigationView()
            }
        })
        // Add a destination changed listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_word ->{
                    binding.appBarMain.fab.visibility = View.VISIBLE
                    binding.appBarMain.fab.setImageResource(R.drawable.icon_add)
                }
                R.id.nav_blog ->{
                    binding.appBarMain.fab.visibility = View.VISIBLE
                    binding.appBarMain.fab.setImageResource(R.drawable.icon_post_add)
                }
                R.id.blogReadScrollingFragment -> {
                    binding.appBarMain.fab.visibility = View.GONE
                    binding.appBarMain.fab.setImageResource(R.drawable.icon_edit_note)
                }
                else -> {
                    binding.appBarMain.fab.visibility = View.GONE
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val itemSettings = menu.findItem(R.id.action_settings)
        val itemExportWord = menu.findItem(R.id.action_export_word)
        val itemImportWord = menu.findItem(R.id.action_import_word)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return when (navHostFragment.navController.currentDestination?.id) {
            R.id.nav_word -> {
                itemExportWord.isVisible = true
                itemImportWord.isVisible = true
                itemSettings.isVisible = true
                true
            }
            R.id.nav_blog -> {
                itemExportWord.isVisible = false
                itemImportWord.isVisible = false
                itemSettings.isVisible = true
                true
            }
            else -> {
                itemSettings.isVisible = false
                itemExportWord.isVisible = false
                itemImportWord.isVisible = false
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            R.id.action_export_word -> {
                // User selected "Export Database" menu item
                if (checkStoragePermissions()) {
                    // Permission already granted, call export function
                    exportDatabase()
                    Log.e("TAG", "onOptionsItemSelected: exportDatabase", )
                } else {
                    // Permission not granted, request it
                    Log.e("TAG", "onOptionsItemSelected: exportDatabase Request", )
                    requestForStoragePermissions()
                }
                return true
            }
            R.id.action_import_word -> {
                // User selected "Import Database" menu item
                if (checkStoragePermissions()) {
                    // Permission already granted, call export function
                    importDatabase()
                    Log.e("TAG", "onOptionsItemSelected: importDatabase", )
                } else {
                    // Permission not granted, request it
                    requestForStoragePermissions()
                    Log.e("TAG", "onOptionsItemSelected: importDatabase Request", )
                }
                return true
            }
            // Handle other menu items if needed

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun hideToolbarAndBottomNavigationView() {
        my_toolbar.animate().translationY(-my_toolbar.height.toFloat()).setDuration(200).start()
        bottomNavView.animate().translationY(bottomNavView.height.toFloat()).setDuration(200)
            .start()
        isToolbarVisible = false
        isBottomNavigationViewVisible = false
    }

    private fun showToolbarAndBottomNavigationView() {
        my_toolbar.animate().translationY(0f).setDuration(200).start()
        bottomNavView.animate().translationY(0f).setDuration(200).start()
        isToolbarVisible = true
        isBottomNavigationViewVisible = true
    }


    var moreLess : Boolean = false
    private var adBinding : ItemAlertDialogSpellingBinding? = null
    var likeDisLike = false
    @SuppressLint("SetTextI18n")
    private fun showCustomAlertDialog() {
        val dialogView = layoutInflater.inflate(R.layout.item_alert_dialog_spelling, null)
        adBinding = ItemAlertDialogSpellingBinding.bind(dialogView)

//        val dialog = Dialog(requireContext())
//        dialog.setContentView(adBinding.root)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        //dialog.window?.setBackgroundDrawableResource(android.R.drawable.screen_background_light_transparent) // Set your background drawable here
//        dialog.show()

        val builder = AlertDialog.Builder(this)
        builder.setView(adBinding!!.root)
        val alertDialog = builder.create()
//        alertDialog.window?.setBackgroundDrawableResource(android.R.drawable.screen_background_light_transparent) // Set your background drawable here
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set your background drawable here
        alertDialog.setCancelable(true)
        alertDialog.show()

        val spellingRepositoryInstance = SpellingRepositoryInstance(DBInstance(this))
        val wordViewModelProviderFactory = WordViewModelProviderFactory(applicationContext as Application, spellingRepositoryInstance)
        val wordViewModel = ViewModelProvider(this,wordViewModelProviderFactory).get(WordViewModel::class.java)
//        homeViewModel.saveSpelling(SpellingResponseItem(0,"Spelling","Spelling","","Spelling",false))
        setMoreLess()
        adBinding!!.paginationProgressBar.visibility = View.INVISIBLE

        adBinding?.also {adb->
            adb.sivMoreLess.setOnClickListener { setMoreLess() }
            adb.tInEdtSpelling.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s!!.isEmpty()){
                        handleNotContainsSpelling()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    val inputText = s.toString()
                    if (inputText.isNotBlank() && inputText.isNotEmpty()) {
                        wordViewModel.containsSpellingsIgnoreCase(inputText).observe(this@MainActivity) { isContainsSpelling ->
                            if (isContainsSpelling) {
                                showHideProgressBar()
                                handleContainsSpelling(inputText)
                            } else {
                                handleNotContainsSpelling()
                            }
                        }
                    }else{
                        handleNotContainsSpelling()
                    }
                }

                private fun handleContainsSpelling(inputText: String) {
                    adb.tvAlreadyInList.visibility = View.VISIBLE
                    adb.tvAlreadyInList.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))

                    wordViewModel.isLike(inputText).observe(this@MainActivity) { isLike ->
                        if (isLike) {
                            likeDisLike = true
                            adb.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.like_in_light), PorterDuff.Mode.SRC_IN)
                        } else {
                            likeDisLike = false
                            adb.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
                        }
                    }
                }

                private fun handleNotContainsSpelling() {
                    adb.tvAlreadyInList.visibility = View.GONE
                    adb.tvAlreadyInList.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
                    likeDisLike = false
                    adb.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
                }
            })

            adb.sivLikeDislike.setOnClickListener{
                if (!likeDisLike){
                    likeDisLike = true
                    adb.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.like_in_light), PorterDuff.Mode.SRC_IN)
                }else{
                    likeDisLike = false
                    adb.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
                }
            }

            adb.mbSubmit.setOnClickListener {
                val spelling = adb.tInEdtSpelling.text.toString()
                val meaning = adb.tInEdtMeaning.text.toString()
                val pronounce = adb.tInEdtPronounce.text.toString()
                val type = adb.tInEdtType.text.toString()
                val isLike = likeDisLike

                showHideProgressBar()
                if (spelling.isEmpty()) {
                    Snackbar.make(adb.tInEdtSpelling, "Please enter spelling", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                } else {
                    wordViewModel.saveSpelling(SpellingResponseItem(0,spelling, meaning ?: "",pronounce?:"",type?:"",isLike))
//                    Snackbar.make(adb.tInEdtSpelling, "Spelling Saved", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
                resetAllFields()
            }

        }
    }

    private fun resetAllFields() {
        adBinding?.apply {
            this.tInEdtSpelling.setText("")
            this.tInEdtMeaning.setText("")
            this.tInEdtPronounce.setText("")
            this.tInEdtType.setText("")
            this.tvAlreadyInList.visibility = View.GONE
            this.sivLikeDislike.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.unlike_in_light), PorterDuff.Mode.SRC_IN)
            likeDisLike = false
        }
    }

    private fun setMoreLess(){
        if (moreLess){
            moreLess = false
            adBinding?.also {
                it.sivMoreLess.setImageResource(R.drawable.icon_arrow_drop_up)
                it.scrollItem.visibility = View.VISIBLE
                it.tInLayPronounce.visibility = View.VISIBLE
                it.tInLayMeaning.visibility = View.VISIBLE
                it.tInLayType.visibility = View.VISIBLE
            }
        }else{
            moreLess = true
            adBinding?.also {
                it.sivMoreLess.setImageResource(R.drawable.icon_arrow_drop_down)
                it.scrollItem.visibility = View.GONE
                it.tInLayPronounce.visibility = View.GONE
                it.tInLayMeaning.visibility = View.GONE
                it.tInLayType.visibility = View.GONE
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private fun showHideProgressBar() {
        synchronized(this) {
//            handler.removeCallbacksAndMessages(null) // Remove any existing callbacks
            adBinding!!.paginationProgressBar.visibility = View.VISIBLE
            handler.postDelayed({
                if (adBinding!!.paginationProgressBar.visibility == View.VISIBLE) {
                    adBinding!!.paginationProgressBar.visibility = View.INVISIBLE
                }
            }, 1000) // 1000 milliseconds (1 second) delay, adjust as needed
        }
    }

    // Check permission status
    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            // Below Android 11
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }
    // Request storage permission
    private val STORAGE_PERMISSION_CODE = 42 // or any other unique identifier

    // Request storage permission
    private fun requestForStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                storageActivityResultLauncher.launch(intent)
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        } else {
            // Check if the user should be shown a rationale for the permission
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show a rationale to the user and request permission again if needed
                // You can display a dialog or some user-friendly explanation here
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                storageActivityResultLauncher.launch(intent)
            } else {
                storageActivityResultLauncher.launch(intent)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        }
    }

    private val storageActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                // The user accepted the request to manage external storage permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // Manage External Storage Permissions Granted
                        Log.e("TAG", "Manage External Storage Permissions Granted")
                    } else {
                        Toast.makeText(this@MainActivity, "Storage Permissions Denied", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // The user did not accept the request
                Toast.makeText(this@MainActivity, "Storage Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE ->{
                // If request is cancelled, the result arrays are empty.
                if(grantResults.isNotEmpty()){
                    val writeGranted = grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED
                    val readGranted = grantResults.getOrNull(1) == PackageManager.PERMISSION_GRANTED
                    if (readGranted && writeGranted) {
                        // Permission granted, call export function
//                        exportDatabase("database.db")
                        Toast.makeText(this@MainActivity, "Storage Permissions Granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Storage Permissions Denied", Toast.LENGTH_SHORT).show()
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CSV_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { fileUri ->
                // File picked, you can now read its contents
                val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
                inputStream?.use { stream ->
                    val content = stream.bufferedReader().use { it.readText() }
                    // Use 'content' as needed
                    Log.d("FileContent", content)
                    importedDataInsertInDatabase(content)
                }
            }
        }
    }

    private fun importedDataInsertInDatabase(content: String) {
        val csvReader = CSVReader(StringReader(content))
        val dataList = mutableListOf<SpellingResponseItem>()

        var record: Array<String>?
        while (csvReader.readNext().also { record = it } != null) {
            // Process each row of the CSV file
            // 'record' array contains the values of each column in the current row
            record?.let {
                if(it.size >= 6){// Ensure there are at least three columns
                    val id = it[0].toIntOrNull() ?: 0
                    val word = it[1]
                    val meaning = it[2]
                    val pronounce = it[3]
                    val type = it[4]
                    val isLike = it[5].toBoolean()

                    // Create SpellingResponseItem object and add to the list
                    val spellingResponseItem = SpellingResponseItem(0, word, meaning, pronounce, type, isLike)
                    dataList.add(spellingResponseItem)
                }
            }
        }
        csvReader.close()
        // Insert data in database
        if (dataList.isNotEmpty()) {
            dataList.removeAt(0)
        }
        wordViewModel.saveSpelling(dataList)

        for ((index, data) in dataList.withIndex()) {
            println("Index: $index, Data: $data")
        }
    }

    // Function to export Room database
    @SuppressLint("SimpleDateFormat")
    private fun exportDatabase() {
        try {
            val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss"))
            } else {
                (SimpleDateFormat("MM/dd/yyyy hh:mm:ss") as SimpleDateFormat).format(Calendar.getInstance().time)
            }
            val modifiedTimeFormat = currentTime.replace("/", "").replace(" ","_").replace(":", "")
            Log.i("TAG", "onOptionsItemSelected: $modifiedTimeFormat")


            val file = File(Environment.getExternalStorageDirectory(), "Remember")
//                                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Remember")
            if (!file.exists()) {
                file.mkdirs()
            }

            val csvFileName = "word_${modifiedTimeFormat.trim()}.csv"
            val csv = File(file, csvFileName)
            val csvWriter = CSVWriter(FileWriter(csv, true))

            // Write header if the file is empty
            if (!csv.exists() || csv.length() == 0L) {
                val header = arrayOf("id", "word", "meaning", "pronounce", "type", "is_like")
                csvWriter.writeNext(header)
            }

            // Assuming dataList is a List<SpellingResponseItem>
//          val dataList: List<SpellingResponseItem> =

            // Write data
            dataList.forEach { item ->
                val row = arrayOf(
                    item.id.toString(),
                    item.word,
                    item.meaning,
                    item.pronounce,
                    item.type,
                    item.is_like.toString()
                )
                csvWriter.writeNext(row)
            }

            csvWriter.close()

            Toast.makeText(this, "Export Successful", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
        }
    }

    private val PICK_CSV_FILE_REQUEST_CODE = 12
    private fun importDatabase() {
        // Launch file picker intent
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // You can specify the type of files you want to pick here
        startActivityForResult(intent, PICK_CSV_FILE_REQUEST_CODE)
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "text/csv" // Set the MIME type based on your file type
//        }
//        startActivityForResult(intent, PICK_CSV_FILE_REQUEST_CODE)
    }

}