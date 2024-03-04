package com.hardik.remember.ui.web_view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.hardik.remember.R
import com.hardik.remember.databinding.FragmentWebViewFullscreenBinding

class WebViewFullscreenFragment : Fragment() {


    private var _binding: FragmentWebViewFullscreenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWebViewFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "QueryPermissionsNeeded", "SetTextI18n", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGetFileLink.text = "https://drive.google.com/drive/folders/1BSl7C9ACQL-ckRI0lL_s1ab-i2Vbd9J5"

        binding.mbOpenLink.setOnClickListener {
            // Hide the TextView and show the WebView
//                val url = "https://drive.google.com/file/d/1qPw4KLvbzJ2RSBc6BgD6uFvCDXU0bciM/view?usp=sharing"
            val url = "https://drive.google.com/drive/folders/1BSl7C9ACQL-ckRI0lL_s1ab-i2Vbd9J5"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Verify that there's an app that can handle this intent before starting it
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Handle the case where no activity can handle the intent (e.g., no browser installed)
                Toast.makeText(requireContext(), "No application can handle this request", Toast.LENGTH_SHORT).show()
                // Optionally, you can redirect the user to the Play Store to download a browser app
//                    tvNote.setText("https://drive.google.com/drive/folders/1BSl7C9ACQL-ckRI0lL_s1ab-i2Vbd9J5")
//                    tvNote.setTextIsSelectable(true)
                copyTextToClipboard(binding.tvGetFileLink.text.toString())
            }
        }


    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Link", text)
        clipboard.setPrimaryClip(clip)

//        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        // Clear the systemUiVisibility flag
//        activity?.window?.decorView?.systemUiVisibility = 0
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}