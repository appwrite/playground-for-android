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
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import io.appwrite.services.Storage
import io.appwrite.models.User
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlaygroundViewModel : ViewModel() {

    private val collectionId = "608faab562521"

    private lateinit var client: Client
    private lateinit var account: Account
    private lateinit var db: Database
    private lateinit var storage: Storage
    private lateinit var realtime: Realtime

    private val _items = MutableLiveData<String>()
    val items: LiveData<String> = _items

    fun create(context: Context) {
        client = Client(context)
            .setEndpoint("https://demo.appwrite.io/v1")
            .setProject("608fa1dd20ef0")

        account = Account(client)
        db = Database(client)
        storage = Storage(client)
        realtime = Realtime(client)
    }

    private val _user = MutableLiveData<User>().apply {
        value = null
    }

    val user: LiveData<User> = _user

    fun onLogin(context: Context) {
        viewModelScope.launch {
            try {
                account.createSession("user@appwrite.io", "password")
                getAccount()
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccount() {
        viewModelScope.launch {
            try {
                val user = account.get()
                _user.postValue(user)
            } catch (e: AppwriteException) {
                Log.d("Get Account", e.message.toString())
            }
        }
    }

    fun onLoginOauth(activity: ComponentActivity, provider: String, context: Context) {
        viewModelScope.launch {
            try {
                account.createOAuth2Session(
                    activity,
                    provider,
                    "https://demo.appwrite.io/auth/oauth2/success",
                    "https://demo.appwrite.io/auth/oauth2/failure"
                )
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun subscribe() {
        realtime.subscribe("collections.${collectionId}.documents") {
            _items.postValue(it.toJson())
        }
    }

    fun onLogout(context: Context) {
        viewModelScope.launch {
            try {
                account.deleteSession("current")
                _user.postValue(null)
                Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createDoc(context: Context) {
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
                Toast.makeText(context, json, Toast.LENGTH_LONG).show()
            } catch (e: AppwriteException) {
                e.printStackTrace()
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

                val read = listOf("role:all")
                val response = storage.createFile("unique()", file1, read, read)
                val json = response.toJson()
                Toast.makeText(context, json, Toast.LENGTH_LONG).show()
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }
}