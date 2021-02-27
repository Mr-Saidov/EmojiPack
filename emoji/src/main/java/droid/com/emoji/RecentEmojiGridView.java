package droid.com.emoji;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

import droid.com.emoji.listeners.OnEmojiClickListener;
import droid.com.emoji.listeners.OnEmojiLongClickListener;

final class RecentEmojiGridView extends EmojiGridView {
    private RecentEmoji recentEmojis;

    RecentEmojiGridView(@NonNull final Context context) {
        super(context);
    }

    public RecentEmojiGridView init(@Nullable final OnEmojiClickListener onEmojiClickListener,
                                    @Nullable final OnEmojiLongClickListener onEmojiLongClickListener,
                                    @NonNull final RecentEmoji recentEmoji) {
        recentEmojis = recentEmoji;

        final Collection<Emoji> emojis = recentEmojis.getRecentEmojis();
        emojiArrayAdapter = new EmojiArrayAdapter(getContext(), emojis.toArray(new Emoji[emojis.size()]), null,
                onEmojiClickListener, onEmojiLongClickListener);

        setAdapter(emojiArrayAdapter);

        return this;
    }

    public void invalidateEmojis() {
        emojiArrayAdapter.updateEmojis(recentEmojis.getRecentEmojis());
    }
}
