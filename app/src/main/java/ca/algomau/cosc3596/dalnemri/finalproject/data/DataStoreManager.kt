package ca.algomau.cosc3596.dalnemri.finalproject.data

import android.content.Context
import android.health.connect.datatypes.ExerciseRoute.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val baseUrl = stringPreferencesKey("baseUrl")
        val apiKey = stringPreferencesKey("apiKey")
        val model = stringPreferencesKey("model")
    }

    suspend fun setBaseUrl(url: String) {
        dataStore.edit {  pref->
            pref[baseUrl] = url
        }
    }
    suspend fun setApiKey(key: String) {
        dataStore.edit {  pref->
            pref[apiKey] = key
        }
    }

    suspend fun setModel(modelName: String) {
        dataStore.edit {  pref->
            pref[model] = modelName
        }
    }

    fun getBaseUrl(): Flow<String> {
         return dataStore.data
             .catch { exception ->
                 if(exception is IOException){
                     emit(emptyPreferences())
                 }
                 else{
                     throw exception
                 }
             }
             .map { pref ->
                 val baseUrl = pref[baseUrl] ?: "https://api.openai.com/v1"
                 baseUrl

             }
    }

    fun getApiKey(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if(exception is IOException){
                    emit(emptyPreferences())
                }
                else{
                    throw exception
                }
            }
            .map { pref ->
                val apiKey = pref[apiKey] ?: ""
                apiKey

            }
    }

    fun getModel(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if(exception is IOException){
                    emit(emptyPreferences())
                }
                else{
                    throw exception
                }
            }
            .map { pref ->
                val model = pref[model] ?: ""
                model

            }
    }
}