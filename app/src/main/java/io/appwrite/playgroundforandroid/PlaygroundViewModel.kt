package io.appwrite.playgroundforandroid

import android.content.Context
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
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.System.currentTimeMillis

class PlaygroundViewModel : ViewModel() {

    private val collectionId: String = "YOUR_COLLECTION_ID" // Single required 'username' string attribute
    private val functionId: String = "YOUR_FUNCTION_ID"
    private val bucketId: String = "YOUR_BUCKET_ID"

    private var documentId = ""
    private var executionId = ""
    private var fileId = ""

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

    var emailId = currentTimeMillis()

    fun createAccount() {
        viewModelScope.launch {
            try {
                val user = account.create(
                    userId = "unique()",
                    email = "$emailId@appwrite.io",
                    password = "password"
                )
                val json = user.toJson()
                _dialogText.postValue(json)
                _user.postValue(user)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createSession() {
        viewModelScope.launch {
            try {
                val session = account.createSession(
                    email = "$emailId@appwrite.io",
                    password = "password"
                )
                val json = session.toJson()
                _dialogText.postValue(json)
                getAccount()
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun getAccount() {
        viewModelScope.launch {
            try {
                val user = account.get()
                val json = user.toJson()
                _dialogText.postValue(json)
                _user.postValue(user)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createOAuth2Session(activity: ComponentActivity, provider: String) {
        viewModelScope.launch {
            try {
                account.createOAuth2Session(activity, provider)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun updateAccountEmail() {
        viewModelScope.launch {
            try {
                emailId = currentTimeMillis()
                val user = account.updateEmail(
                    email = "$emailId@email.com",
                    password = "password"
                )
                val json = user.toJson()
                _user.postValue(user)
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun updateAccountPrefs() {
        viewModelScope.launch {
            try {
                val user = account.updatePrefs(mapOf(
                    "key" to "value"
                ))
                val json = user.toJson()
                _user.postValue(user)
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                account.delete()
                _user.postValue(null)
                _dialogText.postValue("Deleted account!")
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteSession() {
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

    fun createDocument() {
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

    fun listDocuments() {
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

    fun deleteDocument() {
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

    fun createExecution() {
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

    fun listExecutions() {
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

    fun getExecution() {
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

    fun uploadFile(input: FileDescriptor, output: File) {
        viewModelScope.launch {
            try {
                val inputStream = FileInputStream(input)
                val outputStream = FileOutputStream(output)
                inputStream.copyTo(outputStream)

                val permission = listOf("role:all")
                val response = storage.createFile(
                    bucketId = bucketId,
                    fileId = "unique()",
                    file = output,
                    read = permission,
                    write = permission
                )
                fileId = response.id
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listFiles() {
        viewModelScope.launch {
            try {
                val response = storage.listFiles(bucketId = bucketId)
                val json = response.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteFile() {
        viewModelScope.launch {
            try {
                val response = storage.deleteFile(
                    bucketId = bucketId,
                    fileId = fileId
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
