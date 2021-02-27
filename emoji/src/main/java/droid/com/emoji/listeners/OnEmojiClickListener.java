package droid.com.emoji.listeners;


import androidx.annotation.NonNull;

import droid.com.emoji.Emoji;
import droid.com.emoji.EmojiImageView;

public interface OnEmojiClickListener {
  void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView);
}
