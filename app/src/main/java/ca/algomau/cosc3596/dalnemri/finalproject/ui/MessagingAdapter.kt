package ca.algomau.cosc3596.dalnemri.finalproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ca.algomau.cosc3596.dalnemri.finalproject.R
import ca.algomau.cosc3596.dalnemri.finalproject.data.Message
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.RECEIVE_ID
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Constants.SEND_ID

class MessagingAdapter : RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {
    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun insertMessage(message: Message) {
        messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        val tvMessage = holder.itemView.findViewById<TextView>(R.id.tv_message)
        val tvBotMessage = holder.itemView.findViewById<TextView>(R.id.tv_bot_message)

        holder.itemView.setOnClickListener {
            messagesList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

        when (currentMessage.id) {
            SEND_ID -> {
                tvMessage.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                tvBotMessage.visibility = View.GONE
            }

            RECEIVE_ID -> {
                tvBotMessage.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                tvMessage.visibility = View.GONE
            }
        }
    }
}