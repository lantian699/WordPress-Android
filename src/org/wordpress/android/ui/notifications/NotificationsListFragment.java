package org.wordpress.android.ui.notifications;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.simperium.client.Bucket;

import org.json.JSONArray;
import org.json.JSONException;
import org.wordpress.android.R;
import org.wordpress.android.models.Note;
import org.wordpress.android.util.SimperiumUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NotificationsListFragment extends ListFragment implements Bucket.Listener<Note> {
    private TestNotesAdapter mNotesAdapter;
    private OnNoteClickListener mNoteClickListener;
    private boolean mShouldLoadFirstNote;

    Bucket<Note> mBucket;

    /**
     * For responding to tapping of notes
     */
    public interface OnNoteClickListener {
        public void onClickNote(Note note);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.empty_listview, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup the initial notes adapter, starts listening to the bucket
        mBucket = SimperiumUtils.getNotesBucket();

        try {
            JSONArray notesArray = new JSONArray(loadJSONFromAsset());
            List<Note> notesArrayList = new ArrayList<Note>();
            for (int i=0; i < notesArray.length(); i++) {
                Note note = new Note(notesArray.getJSONObject(i));
                notesArrayList.add(note);
            }

            mNotesAdapter = new TestNotesAdapter(getActivity(), R.layout.notifications_list_item, notesArrayList);

            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setDivider(getResources().getDrawable(R.drawable.list_divider));
            listView.setDividerHeight(1);
            listView.setBackgroundColor(getResources().getColor(R.color.white));
            setListAdapter(mNotesAdapter);

            // Set empty text if no notifications
            TextView textview = (TextView) listView.getEmptyView();
            if (textview != null) {
                textview.setText(getText(R.string.notifications_empty_list));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotes();

        registerReceiver();
        // start listening to bucket change events
        //mBucket.addListener(this);
    }

    @Override
    public void onPause() {
        // unregister the listener and close the cursor
        //mBucket.removeListener(this);

        unregisterReceiver();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //mNotesAdapter.closeCursor();

        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Note note = (Note)mNotesAdapter.getItem(position);
        l.setItemChecked(position, true);
        if (note != null && mNoteClickListener != null) {
            mNoteClickListener.onClickNote(note);
        }
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        mNoteClickListener = listener;
    }

    protected void updateLastSeenTime() {
       /* // set the timestamp to now
        try {
            if (mNotesAdapter != null && mNotesAdapter.getCount() > 0) {
                Note newestNote = mNotesAdapter.getNote(0);
                BucketObject meta = SimperiumUtils.getMetaBucket().get("meta");
                meta.setProperty("last_seen", newestNote.getTimestamp());
                meta.save();
            }
        } catch (BucketObjectMissingException e) {
            // try again later, meta is created by wordpress.com
        }*/
    }

    public void refreshNotes() {
        if (!hasActivity() || mNotesAdapter == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*mNotesAdapter.reloadNotes();
                updateLastSeenTime();

                // Show first note if we're on a landscape tablet
                if (mShouldLoadFirstNote && mNotesAdapter.getCount() > 0) {
                    mShouldLoadFirstNote = false;
                    Note note = mNotesAdapter.getNote(0);
                    if (note != null && mNoteClickListener != null) {
                        mNoteClickListener.onClickNote(note);
                        getListView().setItemChecked(0, true);
                    }
                }*/
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState.isEmpty()) {
            outState.putBoolean("bug_19917_fix", true);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Simperium bucket listener methods
     */
    @Override
    public void onSaveObject(Bucket<Note> bucket, Note object) {
        refreshNotes();
    }

    @Override
    public void onDeleteObject(Bucket<Note> bucket, Note object) {
        refreshNotes();
    }

    @Override
    public void onChange(Bucket<Note> bucket, Bucket.ChangeType type, String key) {
        refreshNotes();
    }

    @Override
    public void onBeforeUpdateObject(Bucket<Note> noteBucket, Note note) {
        //noop
    }

    public void setShouldLoadFirstNote(boolean shouldLoad) {
        mShouldLoadFirstNote = shouldLoad;
    }

    private boolean hasActivity() {
        return getActivity() != null;
    }


    /**
     * Broadcast listener for simperium sign in
     */
    private void registerReceiver() {
        if (!hasActivity())
            return;

        IntentFilter filter = new IntentFilter();
        filter.addAction(SimperiumUtils.BROADCAST_ACTION_SIMPERIUM_SIGNED_IN);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        if (!hasActivity())
            return;

        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            // exception occurs if receiver already unregistered (safe to ignore)
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null || !hasActivity())
                return;

            if (intent.getAction().equals(SimperiumUtils.BROADCAST_ACTION_SIMPERIUM_SIGNED_IN)) {
                // Get the new bucket instance and start listening again
                mBucket.removeListener(NotificationsListFragment.this);
                mBucket = SimperiumUtils.getNotesBucket();
                mBucket.addListener(NotificationsListFragment.this);
            }
        }
    };

    // FOR NEW MODEL TESTING, REMOVE L8R!!!
    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getActivity().getAssets().open("test.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
