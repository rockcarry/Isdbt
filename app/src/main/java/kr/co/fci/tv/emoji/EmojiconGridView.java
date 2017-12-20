

package kr.co.fci.tv.emoji;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import kr.co.fci.tv.R;

import java.util.Arrays;


public class EmojiconGridView{
	public View rootView;
	kr.co.fci.tv.emoji.EmojiconsPopup mEmojiconPopup;
    kr.co.fci.tv.emoji.EmojiconRecents mRecents;
    Emojicon[] mData;
    
    public EmojiconGridView(Context context, Emojicon[] emojicons, kr.co.fci.tv.emoji.EmojiconRecents recents, kr.co.fci.tv.emoji.EmojiconsPopup emojiconPopup) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		mEmojiconPopup = emojiconPopup;
		rootView = inflater.inflate(R.layout.emojicon_grid, null);
		setRecents(recents);
		 GridView gridView = (GridView) rootView.findViewById(R.id.Emoji_GridView);
	        if (emojicons== null) {
	            mData = kr.co.fci.tv.emoji.People.DATA;
	        } else {
	            Object[] o = (Object[]) emojicons;
	            mData = Arrays.asList(o).toArray(new Emojicon[o.length]);
	        }
	        EmojiAdapter mAdapter = new EmojiAdapter(rootView.getContext(), mData);
	        mAdapter.setEmojiClickListener(new OnEmojiconClickedListener() {
				
				@Override
				public void onEmojiconClicked(Emojicon emojicon) {
					if (mEmojiconPopup.onEmojiconClickedListener != null) {
			            mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
			        }
			        if (mRecents != null) {
			            mRecents.addRecentEmoji(rootView.getContext(), emojicon);
			        }
				}
			});
	        gridView.setAdapter(mAdapter);
	}
    
	private void setRecents(kr.co.fci.tv.emoji.EmojiconRecents recents) {
        mRecents = recents;
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }
    
}
