package droid.com.emojipack.variant

/**
 *  Created by Dilshodbek on 17:10 01-March 2021
 *  DataMicron
 */

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import droid.com.emoji.Emoji
import droid.com.emoji.EmojiManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AXSimpleEmojiDataAdapter(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ),
    AXDataAdapter<Emoji> {
    var querySearchLikeEnabled = true
    var context: Context
    private fun createDataBase() {
        if (!DB_FILE.exists()) {
            this.readableDatabase
            close()
            try {
                val myInput =
                    context.assets.open(DATABASE_NAME)
                val myOutput: OutputStream =
                    FileOutputStream(DB_FILE)
                val buffer = ByteArray(1024)
                var length: Int
                while (myInput.read(buffer).also { length = it } > 0) {
                    myOutput.write(buffer, 0, length)
                }
                myOutput.flush()
                myOutput.close()
                myInput.close()
                Log.e("createDataBase", "createDataBase: ")
                openDataBase()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.e("createDataBase", "uje created createDataBase: ")
            if (sqliteDataBase == null) openDataBase()
        }
    }

    @Throws(SQLException::class)
    private fun openDataBase() {
        sqliteDataBase = SQLiteDatabase.openDatabase(
            DB_FILE.absolutePath,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
    }

    @Synchronized
    override fun close() {
        if (sqliteDataBase != null) sqliteDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    override fun init() {
        try {
            createDataBase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun searchFor(value: String): List<Emoji> {
        if (value.isNullOrEmpty()) return emptyList()
        val search = fixSearchValue(value)
        val list: MutableList<Emoji> = ArrayList<Emoji>()
        if (customs != null) {
            list.addAll(customs!!)
            customs = null
        }
        if (sqliteDataBase == null || search.isEmpty()) return list
        load("SELECT * FROM emojis WHERE name = ? COLLATE NOCASE", search, list)
        if (querySearchLikeEnabled) load(
            "SELECT * FROM emojis WHERE name LIKE ? COLLATE NOCASE",
            "$search%",
            list
        )
        return list
    }

    protected fun load(
        query: String?,
        search: String,
        list: MutableList<Emoji>
    ) {
        val cursor =
            sqliteDataBase!!.rawQuery(
                query,
                arrayOf(search)
            )
        try {
            while (cursor.moveToNext()) {
                val em =
                    EmojiManager.findCandidateEmoji(cursor.getString(cursor.getColumnIndex("unicode")))
                if (em != null && list.indexOf(em) == -1) list.add(em)
            }
        } finally {
            cursor.close()
        }
    }

    override fun destroy() {
        close()
    }

    fun fixSearchValue(value: String): String {
        var text = value.trim { it <= ' ' }.toLowerCase()
        if (text.equals("heart", ignoreCase = true)) {
            loadSpecialEmoji(*getHeartEmojis())
            return text
        }
        if (text == ":)" || text == ":-)") text = "smile"
        if (text == ":(" || text == ":-(") {
            loadSpecialEmoji(
                "😔",
                "😕",
                "☹",
                "🙁",
                "🥺",
                "😢",
                "😥",
                "\uD83D\uDE2D",
                "\uD83D\uDE3F",
                "\uD83D\uDC94"
            )
            return ""
        }
        if (text == ":|" || text == ":/" || text == ":\\" || text == ":-/" || text == ":-\\" || text == ":-|") text =
            "meh"
        if (text == ";)" || text == ";-)" || text == ";-]") text = "wink"
        if (text == ":]") {
            loadSpecialEmoji("😏")
            return ""
        }
        if (text == ":D" || text == ";D") {
            loadSpecialEmoji("😁", "😃", "😄", "\uD83D\uDE06")
            return ""
        }
        if (text == "=|" || text == "=/" || text == "=\\") {
            loadSpecialEmoji("😐", "😕", "😟")
            return ""
        }
        return text
    }

    var customs: MutableList<Emoji>? = null
    protected fun loadSpecialEmoji(vararg emoji: String) {
        customs = ArrayList<Emoji>()
        for (e in emoji) {
            val em = EmojiManager.findCandidateEmoji(e)
            if (em != null && customs!!.indexOf(em) == -1) customs!!.add(em)
        }
    }

    companion object {
        private lateinit var DB_FILE: File
        private const val DATABASE_NAME = "emoji.db"
        private const val DATABASE_VERSION = 1
        var sqliteDataBase: SQLiteDatabase? = null
    }

    init {
        DB_FILE =
            context.getDatabasePath(DATABASE_NAME)
        this.context = context
    }

    fun getHeartEmojis() = arrayOf(
        "❤",
        "🧡",
        "💛",
        "💚",
        "💙",
        "💜",
        "🖤",
        "🤍",
        "🤎",
        "♥",
        "💔",
        "❣",
        "💕",
        "💞",
        "💓",
        "💗",
        "💖",
        "💘",
        "💝"
    )
}
