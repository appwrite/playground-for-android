package io.appwrite.playgroundforandroid

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
        viewModel.createClient(this)

        setClickListeners()
        setObservers()

    }

    private fun setClickListeners() {
        binding.loginWithEmail.setOnClickListener {
            viewModel.createSession(this)
        }
        binding.createDoc.setOnClickListener {
            viewModel.createDocument(this)
        }
        binding.listDoc.setOnClickListener {
            viewModel.listDocuments(this)
        }
        binding.deleteDoc.setOnClickListener {
            viewModel.deleteDocument(this)
        }
        binding.createExecution.setOnClickListener {
            viewModel.createExecution(this)
        }
        binding.listExecutions.setOnClickListener {
            viewModel.listExecutions(this)
        }
        binding.getExecution.setOnClickListener {
            viewModel.getExecution(this)
        }
        binding.loginWithFacebook.setOnClickListener {
            viewModel.createOAuth2Session(this, "facebook", this)
        }
        binding.loginWithGithub.setOnClickListener {
            viewModel.createOAuth2Session(this, "github", this)
        }
        binding.loginWithGoogle.setOnClickListener {
            viewModel.createOAuth2Session(this, "google", this)
        }
        binding.subscribeButton.setOnClickListener {
            viewModel.subscribeToRealtime()
            Toast.makeText(this, R.string.subscribed, Toast.LENGTH_SHORT).show()
        }
        binding.logoutButton.setOnClickListener {
            viewModel.deleteSession(this)
        }

        binding.uploadFile.setOnClickListener {
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
            Toast.makeText(
                binding.root.context,
                "REALTIME EVENT: $it",
                Toast.LENGTH_LONG
            ).show()
        }
        viewModel.dialogText.observe(this) {
            if (it == null) {
                return@observe
            }
            AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(it)
                .setNeutralButton("OK") { d, _ -> d.dismiss() }
                .create()
                .show()
        }
    }
}