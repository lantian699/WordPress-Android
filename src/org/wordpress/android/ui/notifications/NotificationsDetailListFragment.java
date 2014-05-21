/**
 * One fragment to rule them all (Notes, that is)
 */
package org.wordpress.android.ui.notifications;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.models.Note;
import org.wordpress.android.ui.notifications.blocks.NoteBlock;
import org.wordpress.android.ui.notifications.blocks.UserActionNoteBlock;
import org.wordpress.android.widgets.NoticonTextView;
import org.wordpress.android.widgets.WPTextView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsDetailListFragment extends ListFragment implements NotificationFragment {
    private Note mNote;
    private List<NoteBlock> mNoteBlockArray = new ArrayList<NoteBlock>();

    public NotificationsDetailListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notifications_detail_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        ListView list = getListView();
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setHeaderDividersEnabled(false);

        // Add header if we have a subject
        if (hasActivity() && mNote.getSubject() != null) {
            LinearLayout headerLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.notifications_detail_header, null);
            if (headerLayout != null) {
                NoticonTextView noticonTextView = (NoticonTextView)headerLayout.findViewById(R.id.notification_header_icon);
                noticonTextView.setText(mNote.getNoticonCharacter());

                WPTextView subjectTextView = (WPTextView)headerLayout.findViewById(R.id.notification_header_subject);
                subjectTextView.setText(mNote.getSubject());

                getListView().addHeaderView(headerLayout);
            }
        }

        // Loop through the body items in this note, and create blocks for each.
        JSONArray bodyArray = mNote.getBody();
        if (bodyArray != null && bodyArray.length() > 0) {
            for (int i=0; i < bodyArray.length(); i++) {
                try {
                    JSONObject noteObject = bodyArray.getJSONObject(i);
                    // Determine NoteBlock type and add it to the array
                    NoteBlock noteBlock;
                    if (noteObject.has("text") && noteObject.has("media") && noteObject.has("meta")) {
                        noteBlock = new UserActionNoteBlock(noteObject);
                    } else {
                        noteBlock = new NoteBlock(noteObject);
                    }

                    mNoteBlockArray.add(noteBlock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            setListAdapter(new NoteBlockAdapter(getActivity(), mNoteBlockArray));
        }
    }

    @Override
    public Note getNote() {
        return mNote;
    }

    @Override
    public void setNote(Note note) {
        mNote = note;
    }

    private class NoteBlockAdapter extends ArrayAdapter<NoteBlock> {

        private List<NoteBlock> mNoteBlockList;
        private LayoutInflater mLayoutInflater;

        NoteBlockAdapter(Context context, List<NoteBlock> noteBlocks) {
            super(context, R.layout.menu_drawer_row, R.id.menu_row_title, noteBlocks);

            mNoteBlockList = noteBlocks;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoteBlock noteBlock = mNoteBlockList.get(position);

            if (convertView == null || noteBlock.getBlockType() != convertView.getTag(R.id.note_block_tag_id)) {
                convertView = mLayoutInflater.inflate(noteBlock.getLayoutResourceId(), parent, false);
                convertView.setTag(noteBlock.getViewHolder(convertView));
            }

            // Update the block type for this view
            convertView.setTag(R.id.note_block_tag_id, noteBlock.getBlockType());

            return noteBlock.configureView(convertView);
        }
    }

    private boolean hasActivity() {
        return getActivity() != null;
    }

}
