package io.appwrite.playgroundforandroid

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.*
import io.appwrite.exceptions.AppwriteException
import io.appwrite.extensions.toJson
import io.appwrite.models.*
import io.appwrite.services.*
import io.appwrite.services.Locale
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.System.currentTimeMillis
import io.appwrite.models.Account as AppwriteAccount
import io.appwrite.models.File as AppwriteFile

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
        databases = Databases(client)
        storage = Storage(client)
        functions = Functions(client)
        locale = Locale(client)
        teams = Teams(client)
        realtime = Realtime(client)
    }

    private val _user = MutableLiveData<AppwriteAccount<Any>?>(null)
    val user: LiveData<AppwriteAccount<Any>?> = _user

    private val _dialogText = MutableLiveData<String?>(null)
    val dialogText: LiveData<String?> = _dialogText

    var emailId = currentTimeMillis()

    fun createAccount() {
        viewModelScope.launch {
            try {
                val user: AppwriteAccount<Any> = account.create(
                    userId = ID.unique(),
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
                val session: Session = account.createEmailSession(
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

    fun getSession() {
        viewModelScope.launch {
            try {
                val session: Session = account.getSession("current")
                val json = session.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun getAccount() {
        viewModelScope.launch {
            try {
                val user: AppwriteAccount<Any> = account.get()
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
                val user: AppwriteAccount<Any> = account.updateEmail(
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
                val user: AppwriteAccount<Any> = account.updatePrefs(
                    mapOf(
                        "key" to "value"
                    )
                )
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
                val user: AppwriteAccount<Any> = account.updateStatus()
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
                val document: Document<Any> = databases.createDocument(
                    databaseId,
                    collectionId,
                    documentId = ID.unique(),
                    data = mapOf(
                        "username" to "Android"
                    ),
                    permissions = listOf(
                        Permission.read(Role.users()),
                        Permission.update(Role.users()),
                        Permission.delete(Role.users()),
                    ),
                )
                val json = document.toJson()
                documentId = document.id
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listDocuments() {
        viewModelScope.launch {
            try {
                val documentList: DocumentList<Any> = databases.listDocuments(
                    databaseId,
                    collectionId,
                    queries = listOf(
                        Query.equal("username", "Android")
                    )
                )
                val json = documentList.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteDocument() {
        viewModelScope.launch {
            try {
                databases.deleteDocument(
                    databaseId,
                    collectionId,
                    documentId
                )
                _dialogText.postValue("Deleted document!")
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun createExecution() {
        viewModelScope.launch {
            try {
                val execution: Execution = functions.createExecution(
                    functionId,
                    "{}"
                )
                val json = execution.toJson()
                executionId = execution.id
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listExecutions() {
        viewModelScope.launch {
            try {
                val executionList: ExecutionList = functions.listExecutions(functionId)
                val json = executionList.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun getExecution() {
        viewModelScope.launch {
            try {
                val execution: Execution = functions.getExecution(
                    functionId,
                    executionId
                )
                val json = execution.toJson()
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

                val storageFile: AppwriteFile = storage.createFile(
                    bucketId,
                    fileId = ID.unique(),
                    file = InputFile.fromFile(file),
                    permissions = listOf(
                        Permission.read(Role.users()),
                        Permission.update(Role.users()),
                        Permission.delete(Role.users()),
                    ),
                )
                fileId = storageFile.id
                val json = storageFile.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun listFiles() {
        viewModelScope.launch {
            try {
                val fileList: FileList = storage.listFiles(bucketId)
                val json = fileList.toJson()
                _dialogText.postValue(json)
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun deleteFile() {
        viewModelScope.launch {
            try {
                storage.deleteFile(
                    bucketId = bucketId,
                    fileId = fileId
                )
                _dialogText.postValue("File deleted!")
            } catch (e: AppwriteException) {
                _dialogText.postValue(e.message)
            }
        }
    }

    fun subscribeToRealtime() {
        val channel = "databases.${databaseId}.collections.${collectionId}.documents"

        realtime.subscribe(channel) {
            _items.postValue(it.toJson())
        }
    }
}
