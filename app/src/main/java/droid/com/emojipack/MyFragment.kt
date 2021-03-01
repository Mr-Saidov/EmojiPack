package droid.com.emojipack

/**
 *  Created by Dilshodbek on 19:40 27-February 2021
 *  DataMicron
 */


import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import droid.com.emoji.EmojiEditText
import droid.com.emoji.EmojiPopup

/**
 *  Created by Dilshodbek on 20:13 25-February 2021
 *  DataMicron
 */

class MyFragment : Fragment() {
    var chatAdapter: ChatAdapter? = null
    var emojiPopup: EmojiPopup? = null
    var editText: EmojiEditText? = null
    var rootView: ViewGroup? = null
    var emojiButton: ImageView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatAdapter = ChatAdapter()
    }

    override fun onStop() {
        if (emojiPopup != null) {
            emojiPopup!!.dismiss()
        }
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result: View = inflater.inflate(R.layout.dialog_main, container, false)
        return buildView(result)
    }

    private fun buildView(v: View): View? {
        editText = v.findViewById(R.id.main_dialog_chat_bottom_message_edittext)
        rootView = v.findViewById(R.id.main_dialog_root_view)
        emojiButton = v.findViewById(R.id.main_dialog_emoji)
        val sendButton: ImageView = v.findViewById(R.id.main_dialog_send)
        emojiButton?.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.emoji_icons),
            PorterDuff.Mode.SRC_IN
        )
        sendButton.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.emoji_icons),
            PorterDuff.Mode.SRC_IN
        )
        emojiButton?.setOnClickListener { emojiPopup!!.toggle() }
        sendButton.setOnClickListener {
            val text = editText!!.text.toString().trim { it <= ' ' }
            if (text.isNotEmpty()) {
                chatAdapter?.add(text)
                editText!!.setText("")
            }
        }
        val recyclerView: RecyclerView = v.findViewById(R.id.main_dialog_recycler_view)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(
            getContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        setUpEmojiPopup()
        return rootView
    }

    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(requireActivity(), (activity as  MainActivity).rootView)
            .setOnEmojiBackspaceClickListener { Log.d(TAG, "Clicked on Backspace") }
            .setOnEmojiClickListener { imageView, emoji -> Log.d(TAG, "Clicked on emoji") }
            .setOnEmojiPopupShownListener { emojiButton?.setImageResource(R.drawable.ic_keyboard) }
            .setOnSoftKeyboardOpenListener { Log.d(TAG, "Opened soft keyboard") }
            .setOnEmojiPopupDismissListener { emojiButton?.setImageResource(R.drawable.emoji_ios_category_people) }
            .setOnSoftKeyboardCloseListener { Log.d(TAG, "Closed soft keyboard") }
            .build(editText!!)
    }

    companion object {
        const val FRAGMENT_MANAGER_TAG = "dialog_main"
        const val TAG = "MainDialog"
        fun show(activity: AppCompatActivity) {
            MainDialog().show(
                activity.supportFragmentManager,
                FRAGMENT_MANAGER_TAG
            )
        }
    }
}
