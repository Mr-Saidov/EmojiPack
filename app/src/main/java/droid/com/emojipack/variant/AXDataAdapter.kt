package droid.com.emojipack.variant

/**
 *  Created by Dilshodbek on 17:11 01-March 2021
 *  DataMicron
 */


interface AXDataAdapter<T> {
    fun init()
    fun searchFor(value: String): List<T>
    fun destroy()
}
