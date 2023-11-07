package ca.algomau.cosc3596.dalnemri.finalproject.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.algomau.cosc3596.dalnemri.finalproject.R
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.RECEIVE_ID
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ca.algomau.cosc3596.dalnemri.finalproject.data.Message
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.SEND_ID
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Response
import ca.algomau.cosc3596.dalnemri.finalproject.utils.WebSearch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MessagingAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessages: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMessages = findViewById(R.id.rv_messages)
        etMessages = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send)

        recyclerView()
        clickEvents()
        customMessage("Hello! How can I help you?")
    }

    override fun onStart() {
        super.onStart()
        scrollToPosition()
    }

    private fun clickEvents() {
        sendButton.setOnClickListener {
            sendMessage()
            sendButton.isEnabled = false
            etMessages.isEnabled = false
            sendButton.text ="Loading..."
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

    private fun sendMessage() {
        val message = etMessages.text.toString()
        val timeStamp = Time.timeStamp()

        if(message.isNotEmpty()){

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rvMessages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
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
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) { // Switch back to the main thread to update the UI
                    adapter.insertMessage(Message(e.toString(), RECEIVE_ID, timeStamp))
                    rvMessages.scrollToPosition(adapter.itemCount-1)
                    sendButton.isEnabled = true
                    etMessages.isEnabled = true
                    sendButton.text ="Send"
                    etMessages.setText("")
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