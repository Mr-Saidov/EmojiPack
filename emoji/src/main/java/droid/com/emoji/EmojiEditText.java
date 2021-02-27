package droid.com.emoji;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatEditText;

@SuppressWarnings("CPD-START")
public class EmojiEditText extends AppCompatEditText {
    private float emojiSize;

    public EmojiEditText(final Context context) {
        this(context, null);
    }

    public EmojiEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            EmojiManager.getInstance().verifyInstalled();
        }

        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;

        if (attrs == null) {
            emojiSize = defaultEmojiSize;
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EmojiEditText);

            try {
                emojiSize = a.getDimension(R.styleable.EmojiEditText_emojiSize, defaultEmojiSize);
            } finally {
                a.recycle();
            }
        }

        setText(getText());
    }

    @Override
    @CallSuper
    protected void onTextChanged(final CharSequence text, final int start, final int lengthBefore, final int lengthAfter) {
        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
        EmojiManager.replaceWithImages(getContext(), getText(), emojiSize, defaultEmojiSize);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        String text = getText().toString();
        boolean b = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
            case android.R.id.copy:
                copyEncryptedTextToClipboard(text);
                break;
            case android.R.id.paste:
                pasteDecryptedText(text);
                break;
        }
        return b;
    }

    private void pasteDecryptedText(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String clipData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        setText(text + Security.getInstance().getDecryptedText(getContext(), clipData));
        setSelection(getText().toString().length());
    }

    private void copyEncryptedTextToClipboard(String text) {
        text = text.trim();
        if (text.isEmpty()) return;
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String encryptedText = Security.getInstance().getEncryptedText(getContext(), text);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", encryptedText));
    }
    @CallSuper
    public void backspace() {
        final KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        dispatchKeyEvent(event);
    }

    @CallSuper
    public void input(final Emoji emoji) {
        if (emoji != null) {
            final int start = getSelectionStart();
            final int end = getSelectionEnd();

            if (start < 0) {
                append(emoji.getUnicode());
            } else {
                getText().replace(Math.min(start, end), Math.max(start, end), emoji.getUnicode(), 0, emoji.getUnicode().length());
            }
        }
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSize(@Px final int pixels) {
        setEmojiSize(pixels, true);
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSize(@Px final int pixels, final boolean shouldInvalidate) {
        emojiSize = pixels;

        if (shouldInvalidate) {
            setText(getText());
        }
    }

    /**
     * sets the emoji size in pixels with the provided resource and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSizeRes(@DimenRes final int res) {
        setEmojiSizeRes(res, true);
    }

    /**
     * sets the emoji size in pixels with the provided resource and invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSizeRes(@DimenRes final int res, final boolean shouldInvalidate) {
        setEmojiSize(getResources().getDimensionPixelSize(res), shouldInvalidate);
    }
}
