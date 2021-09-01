package io.appwrite.playgroundforandroid

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PlaygroundViewModel
    private var output: TextView? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.uploadFile(uri, this)
        }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("DEBUG", "${it.key} = ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(PlaygroundViewModel::class.java)
        viewModel.create(this)

        output = findViewById(R.id.textView)

        findViewById<Button>(R.id.loginWithEmail).setOnClickListener { view ->
            viewModel.onLogin(this)
        }
        findViewById<Button>(R.id.createDoc).setOnClickListener { view ->
            viewModel.createDoc(this)
        }
        findViewById<Button>(R.id.loginWithFacebook).setOnClickListener { view ->
            viewModel.onLoginOauth(this, "facebook", this)
        }
        findViewById<Button>(R.id.loginWithGithub).setOnClickListener { view ->
            viewModel.onLoginOauth(this, "github", this)
        }
        findViewById<Button>(R.id.loginWithGoogle).setOnClickListener { view ->
            viewModel.onLoginOauth(this, "google", this)
        }
        findViewById<Button>(R.id.subscribeButton).setOnClickListener { view ->
            viewModel.subscribe()
            Toast.makeText(this, R.string.subscribed, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.logoutButton).setOnClickListener { view ->
            viewModel.onLogout(this)
        }

        findViewById<Button>(R.id.uploadFile).setOnClickListener { view ->
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getContent.launch("image/*")
                }
                else -> {
                    requestPermissions.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            }
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                output?.text = user["name"].toString()
            } else {
                output?.text = "Anonymous"
            }
        }
        viewModel.items.observe(this) {
            Snackbar.make(window.decorView, "REALTIME EVENT: $it", Snackbar.LENGTH_LONG).show()
        }
    }
}