package droid.com.emojipack

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import droid.com.emoji.Emoji
import droid.com.emoji.EmojiEditText
import droid.com.emoji.EmojiPopup
import droid.com.emojipack.variant.AXDataAdapter
import droid.com.emojipack.variant.AXSimpleEmojiDataAdapter
import droid.com.emojipack.variant.EmojiSuggestionPopup
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    var chatAdapter: ChatAdapter? = null
    var emojiPopup: EmojiPopup? = null
    var editText: EmojiEditText? = null
    var rootView: ViewGroup? = null
    var emojiButton: ImageView? = null
    lateinit var dataAdapter: AXDataAdapter<Emoji>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatAdapter = ChatAdapter()
        editText = findViewById(R.id.main_activity_chat_bottom_message_edittext)
        /////// new  emoji data
        thread {
            dataAdapter = AXSimpleEmojiDataAdapter(this)
        }
        val variantPopup =
            EmojiSuggestionPopup(editText!!, object : EmojiSuggestionPopup.OnEmojiClickListener {
                override fun onEmojiClick(emoji: View, imageView: Emoji) {
                    Log.e(TAG, "onEmojiClick: " + imageView.unicode)
                }
            })


        editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    variantPopup.dismiss()
                    return
                }
                val result = dataAdapter.searchFor(p0.toString())
                if (!result.isNullOrEmpty()) {
                    Log.e(TAG, "onTextChanged: " + result.size)
                    variantPopup.dismiss()
                    variantPopup.show(editText!!, result)
                } else {
                    Log.e(TAG, "onTextChanged: " + result.size)
                    variantPopup.dismiss()
                }
            }

        })


        rootView = findViewById(R.id.main_activity_root_view)
        emojiButton = findViewById(R.id.main_activity_emoji)
        val sendButton: ImageView = findViewById(R.id.main_activity_send)
        emojiButton?.setColorFilter(
            ContextCompat.getColor(this, R.color.emoji_icons),
            PorterDuff.Mode.SRC_IN
        )
        sendButton.setColorFilter(
            ContextCompat.getColor(this, R.color.emoji_icons),
            PorterDuff.Mode.SRC_IN
        )
        emojiButton?.setOnClickListener {
            emojiPopup!!.toggle()
        }
        sendButton.setOnClickListener {
            val text = editText?.getText().toString().trim { it <= ' ' }
            if (text.length > 0) {
                chatAdapter!!.add(text)
                editText?.setText("")
            }
        }
        val recyclerView =
            findViewById<RecyclerView>(R.id.main_activity_recycler_view)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        setUpEmojiPopup()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.show_dialog -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_activity_root_view, MyFragment()).commit()
//                MainDialog.show(this)
                true
            }
            R.id.variantIos -> {
//                EmojiManager.install(IosEmojiProvider())
                recreate()
                true
            }
            R.id.variantGoogle -> {
//                EmojiManager.install(GoogleEmojiProvider())
                recreate()
                true
            }
            R.id.variantTwitter -> {
//                EmojiManager.install(TwitterEmojiProvider())
                recreate()
                true
            }
            R.id.variantGoogleCompat -> {
//                if (emojiCompat == null) {
//                    val config: EmojiCompat.Config = BundledEmojiCompatConfig(this)
//                    config.setReplaceAll(true)
//                    emojiCompat = EmojiCompat.init(config)
//                }
//                EmojiManager.install(GoogleCompatEmojiProvider(emojiCompat))
//                recreate()
                true
            }
            R.id.variantEmojiOne -> {
//                EmojiManager.install(EmojiOneProvider())
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (emojiPopup != null && emojiPopup!!.isShowing) {
            emojiPopup!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        if (emojiPopup != null) {
            emojiPopup!!.dismiss()
        }
        super.onStop()
    }

    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(this, rootView)
            .setOnEmojiBackspaceClickListener { Log.d(TAG, "Clicked on Backspace") }
            .setOnEmojiClickListener { imageView, emoji -> Log.d(TAG, "Clicked on emoji") }
            .setOnEmojiPopupShownListener { emojiButton?.setImageResource(R.drawable.ic_keyboard) }
            .setOnSoftKeyboardOpenListener { Log.d(TAG, "Opened soft keyboard") }
            .setOnEmojiPopupDismissListener { emojiButton?.setImageResource(R.drawable.emoji_ios_category_people) }
            .setOnSoftKeyboardCloseListener { Log.d(TAG, "Closed soft keyboard") }
            .setOnMediaSelectionFromKeyboardClickListener {
                Log.d(
                    TAG,
                    "Media selected keyboard $it"
                )
            }
            .build(editText!!)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
