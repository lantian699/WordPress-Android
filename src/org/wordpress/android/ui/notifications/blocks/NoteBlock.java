package org.wordpress.android.ui.notifications.blocks;

import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.ui.notifications.NotificationUtils;
import org.wordpress.android.util.JSONUtil;
import org.wordpress.android.widgets.WPTextView;

/**
 * A block of data displayed in a notification.
 * This basic block can support an image and/or text.
 */
public class NoteBlock {

    private JSONObject mNoteData;
    private OnNoteBlockTextClickListener mOnNoteBlockTextClickListener;

    public interface OnNoteBlockTextClickListener {
        public void onNoteBlockTextClicked();
    }

    public NoteBlock(JSONObject noteObject, OnNoteBlockTextClickListener onNoteBlockTextClickListener) {
        mNoteData = noteObject;
        mOnNoteBlockTextClickListener = onNoteBlockTextClickListener;
    }

    public BlockType getBlockType() {
        return BlockType.BASIC;
    }

    public Spannable getNoteText() {
        return NotificationUtils.getSpannableTextFromIndices(mNoteData, true, mOnNoteBlockTextClickListener);
    }

    public String getNoteImageUrl() {
        return JSONUtil.queryJSON(mNoteData, "media[0].url", "");
    }

    public int getLayoutResourceId() {
        return R.layout.note_block_basic;
    }

    public View configureView(View view) {
        BasicNoteBlockHolder noteBlockHolder = (BasicNoteBlockHolder)view.getTag();

        if (!TextUtils.isEmpty(getNoteImageUrl())) {
            noteBlockHolder.mImageView.setImageUrl(getNoteImageUrl(), WordPress.imageLoader);
            noteBlockHolder.mImageView.setVisibility(View.VISIBLE);
        } else {
            noteBlockHolder.mImageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(getNoteText())) {
            noteBlockHolder.mTextView.setText(getNoteText());
            noteBlockHolder.mTextView.setVisibility(View.VISIBLE);
        } else {
            noteBlockHolder.mTextView.setVisibility(View.GONE);
        }

        return view;
    }

    public Object getViewHolder(View view) {
        return new BasicNoteBlockHolder(view);
    }

    private static class BasicNoteBlockHolder {
        private final WPTextView mTextView;
        private final NetworkImageView mImageView;

        BasicNoteBlockHolder(View view) {
            mTextView = (WPTextView) view.findViewById(R.id.note_text);
            mImageView = (NetworkImageView) view.findViewById(R.id.note_image);
        }
    }
}
