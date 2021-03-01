package droid.com.emojipack.variant

/**
 *  Created by Dilshodbek on 17:31 01-March 2021
 *  DataMicron
 */

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import droid.com.emoji.Emoji
import droid.com.emoji.R

class EmojiSuggestionPopup(
    private val rootView: View,
    val listener: OnEmojiClickListener?
) {
    var rootImageView: View? = null
    private var popupWindow: PopupWindow? = null
    fun show(clickedImage: View, emojies: List<Emoji>) {
        dismiss()
        rootImageView = clickedImage
        val content =
            initView(clickedImage.context, emojies, clickedImage.width)
        popupWindow = PopupWindow(
            content,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        popupWindow!!.isFocusable = true
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        popupWindow!!.setBackgroundDrawable(
            BitmapDrawable(
                clickedImage.context.resources,
                null as Bitmap?
            )
        )
        content.measure(
            makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val location = locationOnScreen(clickedImage)
        val desiredLocation = Point(
            location.x - content.measuredWidth / 2 + clickedImage.width / 2,
            location.y - content.measuredHeight
        )
        popupWindow!!.showAtLocation(
            rootView,
            Gravity.NO_GRAVITY,
            desiredLocation.x,
            desiredLocation.y
        )
        rootImageView!!.parent.requestDisallowInterceptTouchEvent(true)
        fixPopupLocation(popupWindow!!, desiredLocation)
    }

    fun dismiss() {
        rootImageView = null
        if (popupWindow != null) {
            popupWindow!!.dismiss()
            popupWindow = null
        }
    }

    private fun initView(
        context: Context,
        variants: List<Emoji>,
        width: Int
    ): View {
        val result =
            View.inflate(context, R.layout.emoji_skin_popup, null)
        val imageContainer = result.findViewById<LinearLayout>(R.id.container)

        val inflater = LayoutInflater.from(context)
        for (variant in variants) {
            val emojiImage = inflater.inflate(
                R.layout.emoji_item,
                imageContainer,
                false
            ) as ImageView
            val layoutParams =
                emojiImage.layoutParams as MarginLayoutParams
            val margin =
                dpToPx(context, MARGIN.toFloat())

            // Use the same size for Emojis as in the picker.
            layoutParams.width = width
            layoutParams.setMargins(margin, margin, margin, margin)
            emojiImage.setImageDrawable(variant.getDrawable(context))
            emojiImage.setOnClickListener {
                if (listener != null && rootImageView != null) {
                    listener.onEmojiClick(rootImageView!!, variant)
                }
            }
            imageContainer.addView(emojiImage)
        }
        return result
    }

    fun locationOnScreen(view: View): Point {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Point(location[0], location[1])
    }

    fun fixPopupLocation(
        popupWindow: PopupWindow,
        desiredLocation: Point
    ) {
        popupWindow.contentView.post {
            val actualLocation =
                locationOnScreen(popupWindow.contentView)
            if (!(actualLocation.x == desiredLocation.x && actualLocation.y == desiredLocation.y)) {
                val differenceX = actualLocation.x - desiredLocation.x
                val differenceY = actualLocation.y - desiredLocation.y
                val fixedOffsetX: Int
                val fixedOffsetY: Int
                fixedOffsetX = if (actualLocation.x > desiredLocation.x) {
                    desiredLocation.x - differenceX
                } else {
                    desiredLocation.x + differenceX
                }
                fixedOffsetY = if (actualLocation.y > desiredLocation.y) {
                    desiredLocation.y - differenceY
                } else {
                    desiredLocation.y + differenceY
                }
                popupWindow.update(
                    fixedOffsetX,
                    fixedOffsetY,
                    -1,
                    -1
                )
            }
        }
    }

    fun dpToPx(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    companion object {
        private const val MARGIN = 2
    }

    public interface OnEmojiClickListener {
        fun onEmojiClick(emoji: View, imageView: Emoji)
    }
}
