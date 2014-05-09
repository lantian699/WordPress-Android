package org.wordpress.android.ui.notifications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.models.Note;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.PhotonUtils;
import org.wordpress.android.widgets.WPNetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TestNotesAdapter extends ArrayAdapter {

    int mAvatarSz;
    Context mContext;
    ArrayList<Note> mNotesList;

    public TestNotesAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
        mContext = context;
        mNotesList = (ArrayList<Note>)objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = mNotesList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.note_list_item, parent, false);
            NoteViewHolder holder = new NoteViewHolder(convertView);
            convertView.setTag(holder);
        }

        NoteViewHolder noteViewHolder = (NoteViewHolder) convertView.getTag();

        noteViewHolder.txtLabel.setText(note.getSubject());
        if (note.isCommentType()) {
            noteViewHolder.txtDetail.setText(note.getCommentPreview());
            noteViewHolder.txtDetail.setVisibility(View.VISIBLE);
        } else {
            noteViewHolder.txtDetail.setVisibility(View.GONE);
        }

        String avatarUrl = PhotonUtils.fixAvatar(note.getIconURL(), mAvatarSz);
        noteViewHolder.imgAvatar.setImageUrl(avatarUrl, WPNetworkImageView.ImageType.AVATAR);

        noteViewHolder.imgNoteIcon.setImageDrawable(getDrawableForType(note.getType()));

        noteViewHolder.unreadIndicator.setVisibility(note.isUnread() ? View.VISIBLE : View.INVISIBLE);
        noteViewHolder.placeholderLoading.setVisibility(note.isPlaceholder() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    // HashMap of drawables for note types
    private HashMap<String, Drawable> mNoteIcons = new HashMap<String, Drawable>();

    private Drawable getDrawableForType(String noteType) {
        if (mContext == null || noteType == null)
            return null;

        // use like icon for comment likes
        if (noteType.equals(Note.NOTE_COMMENT_LIKE_TYPE))
            noteType = Note.NOTE_LIKE_TYPE;

        Drawable icon = mNoteIcons.get(noteType);
        if (icon != null)
            return icon;

        int imageId = mContext.getResources().getIdentifier("note_icon_" + noteType, "drawable", mContext.getPackageName());
        if (imageId == 0) {
            Log.w(AppLog.TAG, "unknown note type - " + noteType);
            return null;
        }

        icon = mContext.getResources().getDrawable(imageId);
        if (icon == null)
            return null;

        mNoteIcons.put(noteType, icon);
        return icon;
    }

    private static class NoteViewHolder {
        private final TextView txtLabel;
        private final TextView txtDetail;
        private final View unreadIndicator;
        private final ProgressBar placeholderLoading;
        private final WPNetworkImageView imgAvatar;
        private final ImageView imgNoteIcon;

        NoteViewHolder(View view) {
            txtLabel = (TextView) view.findViewById(R.id.note_label);
            txtDetail = (TextView) view.findViewById(R.id.note_detail);
            unreadIndicator = view.findViewById(R.id.unread_indicator);
            placeholderLoading = (ProgressBar) view.findViewById(R.id.placeholder_loading);
            imgAvatar = (WPNetworkImageView) view.findViewById(R.id.note_avatar);
            imgNoteIcon = (ImageView) view.findViewById(R.id.note_icon);
        }
    }
}
