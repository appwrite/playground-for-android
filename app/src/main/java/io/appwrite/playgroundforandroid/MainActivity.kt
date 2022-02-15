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
import io.appwrite.playgroundforandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PlaygroundViewModel

    private val output: TextView by lazy {
        binding.textView
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.uploadFile(uri, this)
        }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getContent.launch("image/*")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(PlaygroundViewModel::class.java)
        viewModel.create(this)

        setClickListeners()
        setObservers()

    }

    private fun setClickListeners() {
        binding.loginWithEmail.setOnClickListener { view ->
            viewModel.onLogin(this)
        }
        binding.createDoc.setOnClickListener { view ->
            viewModel.createDoc(this)
        }
        binding.loginWithFacebook.setOnClickListener { view ->
            viewModel.onLoginOauth(this, "facebook", this)
        }
        binding.loginWithGithub.setOnClickListener { view ->
            viewModel.onLoginOauth(this, "github", this)
        }
        binding.loginWithGoogle.setOnClickListener { view ->
            viewModel.onLoginOauth(this, "google", this)
        }
        binding.subscribeButton.setOnClickListener { view ->
            viewModel.subscribe()
            Toast.makeText(this, R.string.subscribed, Toast.LENGTH_SHORT).show()
        }
        binding.logoutButton.setOnClickListener { view ->
            viewModel.onLogout(this)
        }

        binding.uploadFile.setOnClickListener { view ->
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    getContent.launch("image/*")
                }
                else -> {
                    requestPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.user.observe(this) { user ->
            if (user != null) {
                output.text = user.name
            } else {
                output.text = getString(R.string.anonymous)
            }
        }
        viewModel.items.observe(this) {
            Snackbar.make(binding.root, "REALTIME EVENT: $it", Snackbar.LENGTH_LONG).show()
        }
    }
}