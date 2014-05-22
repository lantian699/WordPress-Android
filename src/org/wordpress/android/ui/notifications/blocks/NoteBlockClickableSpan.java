package org.wordpress.android.ui.notifications.blocks;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wordpress.android.util.JSONUtil;

/**
 * A clickable span that includes extra ids/urls
 * Maps to an 'id' in a WordPress.com note object
 */
public class NoteBlockClickableSpan extends ClickableSpan {
    private long mId;
    private NoteBlockIdType mType;
    private String mUrl;
    private int[] mIndices;

    private JSONObject mBlockData;

    public NoteBlockClickableSpan(JSONObject idData) {
        mBlockData = idData;
        processIdData();
    }


    private void processIdData() {
        if (mBlockData != null) {
            mId = JSONUtil.queryJSON(mBlockData, "id", 0);
            mType = NoteBlockIdType.fromString(JSONUtil.queryJSON(mBlockData, "type", ""));
            mUrl = JSONUtil.queryJSON(mBlockData, "url", "");
            mIndices = new int[]{0,0};
            JSONArray indicesArray = mBlockData.optJSONArray("indices");
            if (indicesArray != null) {
                mIndices[0] = indicesArray.optInt(0);
                mIndices[1] = indicesArray.optInt(1);
            }
        }
    }

    private boolean hasUrl() {
        return !TextUtils.isEmpty(mUrl);
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        // Enforce the original color we want, even for links
        textPaint.setColor(textPaint.getColor());
        // No underlines
        textPaint.setUnderlineText(false);
    }

    // return the desired style for this id type
    public int getSpanStyle() {
        switch (getType()) {
            case USER:
                return Typeface.BOLD;
            case SITE:
            case POST:
                return Typeface.ITALIC;
            default:
                return Typeface.NORMAL;
        }
    }

    @Override
    public void onClick(View widget) {
    }

    public NoteBlockIdType getType() {
        return mType;
    }

    public int[] getIndices() {
        return mIndices;
    }

    public long getId() {
        return mId;
    }
}
