package io.appwrite.playgroundforandroid

import android.Manifest
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
import io.appwrite.playgroundforandroid.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PlaygroundViewModel

    private val output: TextView by lazy {
        binding.textView
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            val descriptor = contentResolver.openFileDescriptor(uri, "r", null)
            val copyDest = File(cacheDir, contentResolver.getFileName(uri))
            viewModel.uploadFile(descriptor!!.fileDescriptor, copyDest)
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
        binding.createAccount.setOnClickListener {
            viewModel.createAccount()
        }
        binding.loginWithEmail.setOnClickListener {
            viewModel.createSession()
        }
        binding.createDoc.setOnClickListener {
            viewModel.createDocument()
        }
        binding.listDoc.setOnClickListener {
            viewModel.listDocuments()
        }
        binding.deleteDoc.setOnClickListener {
            viewModel.deleteDocument()
        }
        binding.createExecution.setOnClickListener {
            viewModel.createExecution()
        }
        binding.listExecutions.setOnClickListener {
            viewModel.listExecutions()
        }
        binding.getExecution.setOnClickListener {
            viewModel.getExecution()
        }
        binding.loginWithFacebook.setOnClickListener {
            viewModel.createOAuth2Session(this, "facebook")
        }
        binding.loginWithGithub.setOnClickListener {
            viewModel.createOAuth2Session(this, "github")
        }
        binding.loginWithGoogle.setOnClickListener {
            viewModel.createOAuth2Session(this, "google")
        }
        binding.updateEmail.setOnClickListener {
            viewModel.updateAccountEmail()
        }
        binding.updatePrefs.setOnClickListener {
            viewModel.updateAccountPrefs()
        }
        binding.updateStatus.setOnClickListener {
            viewModel.updateStatus()
        }
        binding.subscribeButton.setOnClickListener {
            viewModel.subscribeToRealtime()
            Toast.makeText(this, R.string.subscribed, Toast.LENGTH_SHORT).show()
        }
        binding.logoutButton.setOnClickListener {
            viewModel.deleteSession()
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
        binding.listFiles.setOnClickListener {
            viewModel.listFiles()
        }
        binding.deleteFile.setOnClickListener {
            viewModel.deleteFile()
        }
    }

    private fun setObservers() {
        viewModel.user.observe(this) { user ->
            if (user != null) {
                output.text = "${user.name} ${user.email}"
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