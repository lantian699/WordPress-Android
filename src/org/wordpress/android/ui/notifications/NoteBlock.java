package org.wordpress.android.ui.notifications;

import org.json.JSONObject;

/**
 * A block of data displayed in a notification.
 * Created by dan on 5/9/14.
 */
public class NoteBlock {

    private enum BlockType {
        UNKNOWN,
        TEXT,
        IMAGE_AND_TEXT;
    }

    private JSONObject mNoteData;
    private BlockType mBlockType;

    public NoteBlock(JSONObject noteObject) {
        mNoteData = noteObject;
        setBlockType();
    }

    public JSONObject getNoteData() {
        return mNoteData;
    }

    private void setBlockType() {
        if (mNoteData.has("text") && mNoteData.has("media")) {
            mBlockType = BlockType.IMAGE_AND_TEXT;
        } else if (mNoteData.has("text")) {
            mBlockType = BlockType.TEXT;
        } else {
            mBlockType = BlockType.UNKNOWN;
        }
    }
    private BlockType getBlockType() {
        return mBlockType;
    }
}
