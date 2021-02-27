package droid.com.emoji;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

import droid.com.emoji.listeners.OnMediaSelectionFromKeyboard;

@SuppressWarnings("CPD-START")
public class EmojiEditText extends AppCompatEditText {
    private float emojiSize;
    private OnMediaSelectionFromKeyboard onMediaSelectionFromKeyboard;

    public EmojiEditText(final Context context) {
        this(context, null);
    }

    public EmojiEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            EmojiManager.getInstance().verifyInstalled();
        }

        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        emojiSize = fontMetrics.descent - fontMetrics.ascent;

        setText(getText());
    }

    final InputConnectionCompat.OnCommitContentListener callback =
            new InputConnectionCompat.OnCommitContentListener() {
                @Override
                public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                               int flags, Bundle opts) {
                    // read and display inputContentInfo asynchronously
                    if (BuildCompat.isAtLeastNMR1() && (flags &
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            return false; // return false if failed
                        }
                        if (onMediaSelectionFromKeyboard != null) {
                            onMediaSelectionFromKeyboard.onMediaSelect(inputContentInfo.getContentUri());
                        }
                    }

                    // read and display inputContentInfo asynchronously.
                    // call inputContentInfo.releasePermission() as needed.

                    return true;  // return true if succeeded
                }

            };

    @Override
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/png", "image/gif"});
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
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

    public void setOnMediaSelectionFromKeyboard(OnMediaSelectionFromKeyboard onMediaSelectionFromKeyboard) {
        this.onMediaSelectionFromKeyboard = onMediaSelectionFromKeyboard;
    }
}
