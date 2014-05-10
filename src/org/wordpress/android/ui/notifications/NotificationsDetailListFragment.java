/**
 * One fragment to rule them all (Notes, that is)
 */
package org.wordpress.android.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.models.Note;

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
        list.setDivider(getResources().getDrawable(R.drawable.list_divider));
        list.setDividerHeight(1);
        list.setHeaderDividersEnabled(false);

        JSONArray bodyArray = mNote.getBody();

        if (bodyArray != null && bodyArray.length() > 0) {
            for (int i=0; i < bodyArray.length(); i++) {
                try {
                    JSONObject noteObject = bodyArray.getJSONObject(i);
                    NoteBlock noteBlock = new NoteBlock(noteObject);
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

        NoteBlockAdapter(Context context, List<NoteBlock> noteBlocks) {
            super(context, R.layout.menu_drawer_row, R.id.menu_row_title, noteBlocks);

            mNoteBlockList = noteBlocks;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoteBlock noteBlock = mNoteBlockList.get(position);

            if (convertView == null) {
                convertView = new View(getActivity());
            }

            return convertView;
        }
    }

}
