

package kr.co.fci.tv.emoji;


import android.content.Context;
import android.widget.GridView;

import kr.co.fci.tv.R;


public class EmojiconRecentsGridView extends EmojiconGridView implements EmojiconRecents {
	EmojiAdapter mAdapter;
	
	public EmojiconRecentsGridView(Context context, Emojicon[] emojicons,
								   EmojiconRecents recents, kr.co.fci.tv.emoji.EmojiconsPopup emojiconsPopup) {
		super(context, emojicons, recents, emojiconsPopup);
		kr.co.fci.tv.emoji.EmojiconRecentsManager recents1 = kr.co.fci.tv.emoji.EmojiconRecentsManager
	            .getInstance(rootView.getContext());
		mAdapter = new EmojiAdapter(rootView.getContext(),  recents1);
		mAdapter.setEmojiClickListener(new OnEmojiconClickedListener() {
			
			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				if (mEmojiconPopup.onEmojiconClickedListener != null) {
		            mEmojiconPopup.onEmojiconClickedListener.onEmojiconClicked(emojicon);
		        }
		    }
		});
        GridView gridView = (GridView) rootView.findViewById(R.id.Emoji_GridView);
        gridView.setAdapter(mAdapter);
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        kr.co.fci.tv.emoji.EmojiconRecentsManager recents = kr.co.fci.tv.emoji.EmojiconRecentsManager
            .getInstance(context);
        recents.push(emojicon);

        // notify dataset changed
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

}
