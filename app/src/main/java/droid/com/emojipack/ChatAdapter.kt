package droid.com.emojipack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import droid.com.emoji.EmojiTextView
import droid.com.emoji.EmojiUtils

/**
 *  Created by Dilshodbek on 20:13 25-February 2021
 *  DataMicron
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder?>() {
    private val texts: MutableList<String> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(
            layoutInflater.inflate(
                R.layout.adapter_chat,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        chatViewHolder: ChatViewHolder,
        position: Int
    ) {
        val text = texts[position]
        val emojiInformation = EmojiUtils.emojiInformation(text)
        val res: Int
        if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size == 1) {
            res = R.dimen.emoji_size_single_emoji
        } else if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size > 1) {
            res = R.dimen.emoji_size_only_emojis
        } else {
            res = R.dimen.emoji_size_default
        }
        chatViewHolder.textView.setEmojiSizeRes(res, false)
        chatViewHolder.textView.text = text
    }

    override fun getItemCount(): Int {
        return texts.size
    }

    fun add(text: String) {
        texts.add(text)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: EmojiTextView

        init {
            textView = view.findViewById(R.id.adapter_chat_text_view)
        }
    }
}
