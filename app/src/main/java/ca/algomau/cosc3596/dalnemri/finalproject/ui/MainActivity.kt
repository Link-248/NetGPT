package ca.algomau.cosc3596.dalnemri.finalproject.ui

import android.R as popup
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.algomau.cosc3596.dalnemri.finalproject.R
import ca.algomau.cosc3596.dalnemri.finalproject.data.MainViewModel
import ca.algomau.cosc3596.dalnemri.finalproject.data.Message
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.RECEIVE_ID
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.SEND_ID
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Response
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Time
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MessagingAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessages: EditText
    private lateinit var sendButton: Button
    private lateinit var settings: ImageView
    private lateinit var clear: ImageView
    private lateinit var indicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.getBaseUrl.observe(
            this
        ) { base ->
            Response.setBaseUrl(base)
        }
        viewModel.getApiKey.observe(
            this
        ) { key ->
            Response.setApiKey(key)
        }
        viewModel.getModel.observe(
            this
        ) { model ->
            Response.setModel(model)
        }

        rvMessages = findViewById(R.id.rv_messages)
        etMessages = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send)


        recyclerView()
        clickEvents()
        customMessage("Hello! How can I help you?")

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val customView = layoutInflater.inflate(R.layout.menu_bar, null)
        supportActionBar?.customView = customView

        indicator = customView.findViewById(R.id.loading)

        var logo: ImageView = customView.findViewById(R.id.logo)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedBitmapDrawable.isCircular = true
        logo.setImageDrawable(roundedBitmapDrawable)

        settings = customView.findViewById(R.id.settings)
        clear = customView.findViewById(R.id.clear)
        settings.setOnClickListener{
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        clear.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Clear all messages?")
                .setTitle("Clear")
                .setPositiveButton("Clear", DialogInterface.OnClickListener { dialog, id ->
                    adapter.clearAll()
                    Response.clearChatHistory()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })
            // Create and show the AlertDialog
            builder.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        scrollToPosition()
    }

    private fun clickEvents() {
        sendButton.setOnClickListener {
            if(sendMessage()){
                sendButton.isEnabled = false
                etMessages.isEnabled = false
                sendButton.text ="Loading..."
                indicator.visibility = View.VISIBLE
            }
        }

        etMessages.setOnClickListener {
            scrollToPosition()
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rvMessages.adapter = adapter
        rvMessages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessage(): Boolean {
        val message = etMessages.text.toString()
        val timeStamp = Time.timeStamp()

        if(message != ""){

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rvMessages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
            return true
        }

        return false
    }

    private fun botResponse(message: String){
        val timeStamp = Time.timeStamp()
        GlobalScope.launch(Dispatchers.IO) { // Use Dispatchers.IO for network operations
            val response: String?
            try{
                response = Response.functionCallingResponse(message)
                withContext(Dispatchers.Main) { // Switch back to the main thread to update the UI
                    adapter.insertMessage(Message(response!!, RECEIVE_ID, timeStamp))
                    rvMessages.scrollToPosition(adapter.itemCount-1)
                    sendButton.isEnabled = true
                    etMessages.isEnabled = true
                    sendButton.text ="Send"
                    etMessages.setText("")
                    indicator.visibility = View.INVISIBLE
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) { // Switch back to the main thread to update the UI
                    adapter.insertMessage(Message(e.toString(), RECEIVE_ID, timeStamp))
                    rvMessages.scrollToPosition(adapter.itemCount-1)
                    sendButton.isEnabled = true
                    etMessages.isEnabled = true
                    sendButton.text ="Send"
                    etMessages.setText("")
                    indicator.visibility = View.INVISIBLE
                }
            }

        }
    }

    private fun customMessage(message: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rvMessages.scrollToPosition(adapter.itemCount-1)
            }
        }
    }
    private fun scrollToPosition() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}