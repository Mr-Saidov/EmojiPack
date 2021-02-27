package droid.com.emojipack

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import droid.com.emoji.EmojiEditText
import droid.com.emoji.EmojiPopup

/**
 *  Created by Dilshodbek on 20:13 25-February 2021
 *  DataMicron
 */

class MainDialog : DialogFragment() {
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(getContext())
            .setView(buildView())
            .create()
    }

    private fun buildView(): View? {
        val result: View = View.inflate(getContext(), R.layout.dialog_main, null)
        editText = result.findViewById(R.id.main_dialog_chat_bottom_message_edittext)
        rootView = result.findViewById(R.id.main_dialog_root_view)
        emojiButton = result.findViewById(R.id.main_dialog_emoji)
        val sendButton: ImageView = result.findViewById(R.id.main_dialog_send)
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
        val recyclerView: RecyclerView = result.findViewById(R.id.main_dialog_recycler_view)
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
        emojiPopup = EmojiPopup.Builder.fromRootView(requireActivity(), rootView)
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
