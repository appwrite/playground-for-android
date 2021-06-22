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
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlaygroundViewModel : ViewModel() {
    private val collectionId = "608faab562521"
    private lateinit var client : Client
    private lateinit var account : Account
    private lateinit var db : Database
    private lateinit var storage : Storage

    fun create(context: Context) {
        client = Client(context)
            .setEndpoint("https://demo.appwrite.io/v1")
            .setProject("608fa1dd20ef0")
        account = Account(client);
        db = Database(client);
        storage = Storage(client);
    }

    private val _user = MutableLiveData<JSONObject>().apply {
        value = null
    }

    val user: LiveData<JSONObject> = _user

    fun onLogin(context : Context) {
        viewModelScope.launch {
            try {
                var response = account.createSession("user@appwrite.io","password")
                getAccount()
                var json = response.body?.string() ?: ""
                json = JSONObject(json).toString(8)
            } catch( e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccount() {
        viewModelScope.launch {
            try {
                var response = account.get()
                var json = response.body?.string() ?: ""
                var user = JSONObject(json)
                _user.postValue(user)
            } catch( e: AppwriteException) {
                Log.d("Get Account", e.message.toString())
            }
        }
    }

    fun onLoginOauth(activity: ComponentActivity, provider: String, context : Context) {
        viewModelScope.launch {
            try {
                var response = account.createOAuth2Session(activity,
                    provider,
                    "appwrite-callback-6070749e6acd4://demo.appwrite.io/auth/oauth2/success",
                    "appwrite-callback-6070749e6acd4://demo.appwrite.io/auth/oauth2/failur"
                )
            } catch( e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onLogout(context : Context) {
        viewModelScope.launch {
            try {
                var response = account.deleteSession("current")
                _user.postValue(null)
                Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
            } catch( e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createDoc(context: Context) {
        viewModelScope.launch {
            try {
                var response = db.createDocument(collectionId,mapOf("username" to "Android"), listOf("*"),
                    listOf("*"))
                var json = response.body?.string() ?: ""
//                json = JSONObject(json).toString(8)
                Toast.makeText(context, json, Toast.LENGTH_LONG).show()
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri ?: return ,"r", null) ?: return

        viewModelScope.launch {
            try {
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val file1 =  File(context.cacheDir, context.contentResolver.getFileName(uri))
                val outputStream = FileOutputStream(file1)
                inputStream.copyTo(outputStream)

                val read = listOf("*")
                val response = storage.createFile(file1, read, read)
                var json = response.body?.string() ?: ""
                json = JSONObject(json).toString(4)
                Toast.makeText(context, json, Toast.LENGTH_LONG).show()
            } catch (e: AppwriteException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }
}