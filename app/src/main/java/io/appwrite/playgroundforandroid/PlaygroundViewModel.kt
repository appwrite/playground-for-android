package io.appwrite.playgroundforandroid

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.extensions.toJson
import io.appwrite.models.User
import io.appwrite.services.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlaygroundViewModel : ViewModel() {

    private val collectionId: String = "test"
    private val functionId: String = "test"

    private var documentId = ""
    private var executionId = ""

    private lateinit var client: Client

    private lateinit var account: Account
    private lateinit var avatars: Avatars
    private lateinit var db: Database
    private lateinit var storage: Storage
    private lateinit var functions: Functions
    private lateinit var locale: Locale
    private lateinit var teams: Teams
    private lateinit var realtime: Realtime

    private val _items = MutableLiveData<String>()
    val items: LiveData<String> = _items

    fun createClient(context: Context) {
        client = Client(context)
            .setEndpoint("http://localhost/v1")
            .setProject("test")
            .setSelfSigned(true)

        account = Account(client)
        avatars = Avatars(client)
        db = Database(client)
        storage = Storage(client)
        functions = Functions(client)
        locale = Locale(client)
        teams = Teams(client)
        realtime = Realtime(client)
    }

    private val _user = MutableLiveData<User?>().apply {
        value = null
    }
    val user: LiveData<User?> = _user

    private val _dialogText = MutableLiveData<String?>().apply {
        value = null
    }
    val dialogText: LiveData<String?> = _dialogText

    fun createSession(context: Context) {
        viewModelScope.launch {
            try {
                account.createSession("user@appwrite.io", "password")
                getAccount()
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    private fun getAccount() {
        viewModelScope.launch {
            try {
                val user = account.get()
                _user.postValue(user)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createOAuth2Session(activity: ComponentActivity, provider: String, context: Context) {
        viewModelScope.launch {
            try {
                account.createOAuth2Session(activity, provider)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteSession(context: Context) {
        viewModelScope.launch {
            try {
                account.deleteSession("current")
                _user.postValue(null)
                _dialogText.postValue("Logged out!")
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createDocument(context: Context) {
        viewModelScope.launch {
            try {
                val response = db.createDocument(
                    collectionId,
                    "unique()",
                    mapOf("username" to "Android"),
                    listOf("role:all"),
                    listOf("role:all")
                )
                val json = response.toJson()
                documentId = response.id
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listDocuments(context: Context) {
        viewModelScope.launch {
            try {
                val response = db.listDocuments(collectionId)
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteDocument(context: Context) {
        viewModelScope.launch {
            try {
                val response = db.deleteDocument(collectionId, documentId)
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createExecution(context: Context) {
        viewModelScope.launch {
            try {
                val response = functions.createExecution(functionId, "{}")
                val json = response.toJson()
                executionId = response.id
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listExecutions(context: Context) {
        viewModelScope.launch {
            try {
                val response = functions.listExecutions(functionId)
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun getExecution(context: Context) {
        viewModelScope.launch {
            try {
                val response = functions.getExecution(functionId, executionId)
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    fun uploadFile(uri: Uri?, context: Context) {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri ?: return, "r", null) ?: return

        viewModelScope.launch {
            try {
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val file1 = File(context.cacheDir, context.contentResolver.getFileName(uri))
                val outputStream = FileOutputStream(file1)
                inputStream.copyTo(outputStream)

                val permission = listOf("role:all")
                val response = storage.createFile(
                    bucketId = "default",
                    fileId = "unique()",
                    file = file1,
                    read = permission,
                    write = permission
                )
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun subscribeToRealtime() {
        realtime.subscribe("collections.${collectionId}.documents") {
            _items.postValue(it.toJson())
        }
    }
}
