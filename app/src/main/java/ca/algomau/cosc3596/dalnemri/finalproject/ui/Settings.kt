package ca.algomau.cosc3596.dalnemri.finalproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import ca.algomau.cosc3596.dalnemri.finalproject.R
import ca.algomau.cosc3596.dalnemri.finalproject.data.DataStoreManager
import ca.algomau.cosc3596.dalnemri.finalproject.data.MainViewModel
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Response

class Settings : AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var baseUrl: EditText
    private lateinit var apiKey: EditText
    private lateinit var model: EditText

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        saveButton = findViewById(R.id.saveButton)
        baseUrl = findViewById(R.id.baseUrl)
        apiKey = findViewById(R.id.apiKey)
        model = findViewById(R.id.model)

        viewModel.getBaseUrl.observe(
            this
        ) { base ->
            Response.setBaseUrl(base)
            baseUrl.setText(base)
        }
        viewModel.getApiKey.observe(
            this
        ) { key ->
            Response.setApiKey(key)
            apiKey.setText(key)
        }
        viewModel.getModel.observe(
            this
        ) { modelName ->
            Response.setModel(modelName)
            model.setText(modelName)
        }

        saveButton.setOnClickListener {
            viewModel.setBaseUrl(baseUrl.text.toString())
            viewModel.setApiKey(apiKey.text.toString())
            viewModel.setModel(model.text.toString())
            finish()
        }
    }
}
