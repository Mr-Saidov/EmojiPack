package droid.com.emoji;

import androidx.annotation.NonNull;

import droid.com.emoji.category.ActivityCategory;
import droid.com.emoji.category.FlagsCategory;
import droid.com.emoji.category.FoodCategory;
import droid.com.emoji.category.NatureCategory;
import droid.com.emoji.category.ObjectsCategory;
import droid.com.emoji.category.PeopleCategory;
import droid.com.emoji.category.SymbolsCategory;
import droid.com.emoji.category.TravelCategory;

public final class GoogleEmojiProvider implements EmojiProvider {
    @Override
    @NonNull
    public EmojiCategory[] getCategories() {
        return new EmojiCategory[]{
                new PeopleCategory(),
                new NatureCategory(),
                new FoodCategory(),
                new ActivityCategory(),
                new TravelCategory(),
                new ObjectsCategory(),
                new SymbolsCategory(),
                new FlagsCategory()
        };
    }
}
