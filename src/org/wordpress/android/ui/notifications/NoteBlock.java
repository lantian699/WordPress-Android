package org.wordpress.android.ui.notifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.R;
import org.wordpress.android.util.JSONUtil;

/**
 * A block of data displayed in a notification.
 */
public class NoteBlock {
    public enum BlockType {
        UNKNOWN,
        TEXT,
        MEDIA,
        MEDIA_WITH_TEXT,
        MEDIA_WITH_TEXT_AND_META
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
        if (mNoteData.has("text") && mNoteData.has("media") && mNoteData.has("meta")) {
            mBlockType = BlockType.MEDIA_WITH_TEXT_AND_META;
        } else if (mNoteData.has("text") && mNoteData.has("media")) {
            mBlockType = BlockType.MEDIA_WITH_TEXT;
        } else if (mNoteData.has("media")) {
            mBlockType = BlockType.MEDIA;
        } else if (mNoteData.has("text")) {
            mBlockType = BlockType.TEXT;
        } else {
            mBlockType = BlockType.UNKNOWN;
        }
    }

    public BlockType getBlockType() {
        return mBlockType;
    }

    public String getNoteText() {
        if (mNoteData.has("text")) {
            try {
                return mNoteData.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public String getNoteImageUrl() {
        if (mNoteData.has("media")) {
            try {
                JSONArray mediaItems = mNoteData.getJSONArray("media");
                for (int i=0; i < mediaItems.length(); i++) {
                    JSONObject mediaItem = mediaItems.getJSONObject(i);
                    if (JSONUtil.queryJSON(mediaItem, "type", "").equals("image")) {
                        return JSONUtil.queryJSON(mediaItem, "url", "");
                    }
                }
            } catch (JSONException e) {
                return "";
            }
        }

        return "";
    }

    public int getLayoutResourceId() {
        switch (getBlockType()) {
            case UNKNOWN:
            case TEXT:
            case MEDIA:
            case MEDIA_WITH_TEXT:
                return R.layout.note_block_basic;
            case MEDIA_WITH_TEXT_AND_META:
                return R.layout.notifications_follow_row;
            default:
                return R.layout.note_block_basic;
        }
    }

}
