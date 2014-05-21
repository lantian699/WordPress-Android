package org.wordpress.android.ui.notifications;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.models.Note;
import org.wordpress.android.util.PhotonUtils;
import org.wordpress.android.widgets.NoticonTextView;
import org.wordpress.android.widgets.WPNetworkImageView;

import java.util.ArrayList;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.notifications_list_item, parent, false);
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

        if (!TextUtils.isEmpty(note.getNoticonCharacter())) {
            noteViewHolder.noteIcon.setText(note.getNoticonCharacter());
            noteViewHolder.noteIcon.setVisibility(View.VISIBLE);
        } else {
            noteViewHolder.noteIcon.setVisibility(View.GONE);
        }

        noteViewHolder.unreadIndicator.setVisibility(note.isUnread() ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    private static class NoteViewHolder {
        private final TextView txtLabel;
        private final TextView txtDetail;
        private final View unreadIndicator;
        private final WPNetworkImageView imgAvatar;
        private final NoticonTextView noteIcon;

        NoteViewHolder(View view) {
            txtLabel = (TextView) view.findViewById(R.id.note_label);
            txtDetail = (TextView) view.findViewById(R.id.note_detail);
            unreadIndicator = view.findViewById(R.id.unread_indicator);
            imgAvatar = (WPNetworkImageView) view.findViewById(R.id.note_avatar);
            noteIcon = (NoticonTextView) view.findViewById(R.id.note_icon);
        }
    }
}
