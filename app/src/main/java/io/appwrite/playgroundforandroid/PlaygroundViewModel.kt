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
import io.appwrite.models.InputFile
import io.appwrite.models.User
import io.appwrite.services.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.System.currentTimeMillis

class PlaygroundViewModel : ViewModel() {

    private val endpoint = "YOUR_ENDPOINT"
    private val projectId = "YOUR_PROJECT_ID"
    private val collectionId = "YOUR_COLLECTION_ID" // Single required 'username' string attribute
    private val functionId = "YOUR_FUNCTION_ID"
    private val bucketId = "YOUR_BUCKET_ID"
    private var databaseId = "YOUR_DATABASE_ID"

    private var documentId = ""
    private var executionId = ""
    private var fileId = ""

    private lateinit var client: Client

    private lateinit var account: Account
    private lateinit var avatars: Avatars
    private lateinit var databases: Databases
    private lateinit var storage: Storage
    private lateinit var functions: Functions
    private lateinit var locale: Locale
    private lateinit var teams: Teams
    private lateinit var realtime: Realtime

    private val _items = MutableLiveData<String>()
    val items: LiveData<String> = _items

    fun createClient(context: Context) {
        client = Client(context)
            .setEndpoint(endpoint)
            .setProject(projectId)
            .setSelfSigned(true)

        account = Account(client)
        avatars = Avatars(client)
        databases = Databases(client, databaseId)
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
                val session = account.createEmailSession(
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

    fun updateStatus() {
        viewModelScope.launch {
            try {
                val user = account.updateStatus()
                val json = user.toJson()
                _user.postValue(user)
                _dialogText.postValue(json)
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
                val response = databases.createDocument(
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
                val response = databases.listDocuments(collectionId)
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
                val response = databases.deleteDocument(collectionId, documentId)
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

    fun uploadFile(input: FileDescriptor, file: File) {
        viewModelScope.launch {
            try {
                val inputStream = FileInputStream(input)
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                val permission = listOf("role:all")
                val response = storage.createFile(
                    bucketId = bucketId,
                    fileId = "unique()",
                    file = InputFile.fromFile(file),
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
