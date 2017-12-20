package kr.co.fci.tv.saves;

/**
 * Created by live.kim on 2015-09-14.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.fci.tv.channelList.Channel;

public class SharedPreference {

    public static final String PREFS_NAME = "Channel_APP";
    public static final String FAVORITES = "Channel_Favorite";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Channel> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Channel channel) {
        List<Channel> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Channel>();
        favorites.add(channel);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Channel channel) {
        ArrayList<Channel> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(channel);
            saveFavorites(context, favorites);
        }
    }

    public void removeAllFavorites(Context context, Channel channel) {
        SharedPreference sharedPreference = new SharedPreference();
        ArrayList<Channel> favorites = sharedPreference.getFavorites(context);
            if (favorites != null) {
                for (int i = 0; i < favorites.size(); i++) {
                    favorites.remove(i);
                    saveFavorites(context, favorites);
            }
        }

    }



    public ArrayList<Channel> getFavorites(Context context) {
        SharedPreferences settings;
        List<Channel> favorites;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Channel[] favoriteItems = gson.fromJson(jsonFavorites, Channel[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Channel>(favorites);
        } else
            return null;

        return (ArrayList<Channel>) favorites;
    }

}

