package org.wordpress.android.ui.notifications.blocks;

import android.view.View;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.widgets.WPTextView;

/**
 * A block that displays information about a User (such as a user that liked a post)
 * Will display an action button if available (e.g. follow button)
 */
public class UserActionNoteBlock extends NoteBlock {

    public UserActionNoteBlock(JSONObject noteObject) {
        super(noteObject);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.USER_ACTION;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.note_block_user_action;
    }

    @Override
    public View configureView(View view) {
        UserActionNoteBlockHolder noteBlockHolder = (UserActionNoteBlockHolder)view.getTag();
        noteBlockHolder.mNameTextView.setText(getNoteText());
        noteBlockHolder.mAvatarImageView.setImageUrl(getNoteImageUrl(), WordPress.imageLoader);

        return view;
    }

    @Override
    public Object getViewHolder(View view) {
        return new UserActionNoteBlockHolder(view);
    }

    private class UserActionNoteBlockHolder {
        private WPTextView mNameTextView;
        private WPTextView mUrlTextView;
        private WPTextView mActionButton;
        private NetworkImageView mAvatarImageView;

        public UserActionNoteBlockHolder(View view) {
            mNameTextView = (WPTextView) view.findViewById(R.id.name);
            mUrlTextView = (WPTextView) view.findViewById(R.id.url);
            mActionButton = (WPTextView) view.findViewById(R.id.action_button);
            mAvatarImageView = (NetworkImageView) view.findViewById(R.id.avatar);
        }
    }
}
