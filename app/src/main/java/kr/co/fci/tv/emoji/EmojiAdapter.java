

package kr.co.fci.tv.emoji;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import kr.co.fci.tv.R;

import java.util.List;

class EmojiAdapter extends ArrayAdapter<kr.co.fci.tv.emoji.Emojicon> {
    kr.co.fci.tv.emoji.EmojiconGridView.OnEmojiconClickedListener emojiClickListener;
    public EmojiAdapter(Context context, List<kr.co.fci.tv.emoji.Emojicon> data) {
        super(context, R.layout.emojicon_item, data);
    }

    public EmojiAdapter(Context context, kr.co.fci.tv.emoji.Emojicon[] data) {
        super(context, R.layout.emojicon_item, data);
    }
    
    public void setEmojiClickListener(kr.co.fci.tv.emoji.EmojiconGridView.OnEmojiconClickedListener listener){
    	this.emojiClickListener = listener;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (TextView) v.findViewById(R.id.emojicon_icon);
            v.setTag(holder);
        }
        kr.co.fci.tv.emoji.Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        holder.icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				emojiClickListener.onEmojiconClicked(getItem(position));
			}
		});
        return v;
    }

    class ViewHolder {
        TextView icon;
    }
}