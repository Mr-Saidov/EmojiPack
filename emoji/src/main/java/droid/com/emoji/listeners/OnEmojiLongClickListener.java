package droid.com.emoji.listeners;

import androidx.annotation.NonNull;

import droid.com.emoji.Emoji;
import droid.com.emoji.EmojiImageView;

public interface OnEmojiLongClickListener {
  void onEmojiLongClick(@NonNull EmojiImageView view, @NonNull Emoji emoji);
}
