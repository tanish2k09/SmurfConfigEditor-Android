package com.tanish2k09.sce

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.updatePadding
import com.tanish2k09.sce.databinding.ActivityMainBinding
import com.tanish2k09.sce.databinding.EntryGreetgateBinding
import com.tanish2k09.sce.utils.extensions.rippleAnimationActivityOpener

import com.topjohnwu.superuser.Shell

import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var greetBinding: EntryGreetgateBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        greetBinding = binding.mainContentLayout.greetLayout

        binding.mainContentLayout.root.setOnApplyWindowInsetsListener {view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            Log.d("SCE-INSET", insets.systemWindowInsetBottom.toString())
            insets
        }

        greetBinding.settingsButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, SettingsActivity::class.java)
                    rippleAnimationActivityOpener(m, v, intent)
                    return true
                }
                return false
            }
        })

        greetBinding.infoButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, InfoActivity::class.java)
                    rippleAnimationActivityOpener(m, v, intent)
                    return true
                }
                return false
            }
        })

        greetBinding.importDirectButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, ConfigActivity::class.java)
                    rippleAnimationActivityOpener(m, v, intent)
                    return true
                }
                return false
            }
        })

        try {
            Shell.su("cd /").submit()
        } catch (e: IOException) {
            Log.d("SCE-MAIN", "Caught exception executing shell, probably not rooted")
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1)
        }

        greetBinding.winkAnim.resumeAnimation()

        updateTheme()
    }

    private fun updateTheme() {
        this.window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.StoragePermDenied, Toast.LENGTH_LONG).show()
            finish()
            moveTaskToBack(true)
        }
    }

    override fun onPause() {
        super.onPause()
        greetBinding.winkAnim.pauseAnimation()
    }

    override fun onDestroy() {
        try {
            val shell = Shell.getCachedShell()
            shell?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    companion object {
        init {
            /* Shell.Config methods shall be called before any shell is created
         * This is the reason why you should call it in a static block
         * The followings are some examples, check Javadoc for more details */
            Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_VERBOSE_LOGGING)
            Shell.Config.setTimeout(5)
        }
    }
}
