package com.example.xavier.viaproject;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Xavier on 05/08/2016.
 */
public class MusicTrack {

    private GameView mGameView;
    private List<TypeTrack> mNoteTrack;

    public class TypeTrack {
        int color;
        int offset;
        public TypeTrack (int color_ini, int offset_ini) {
            color = color_ini;
            offset = offset_ini;
        }
    }

    public MusicTrack (Context context, GameView gameView) {
        mGameView = gameView;
        loadTrackFile(context);
    }

    private void loadTrackFile (Context context) {
        Resources res = context.getResources();
        TypedArray noteColor = res.obtainTypedArray(R.array.gotc_int_color);
        TypedArray noteOffset = res.obtainTypedArray(R.array.gotc_int_offset);

        mNoteTrack = new ArrayList<TypeTrack>();

        for (int i=0; i < noteOffset.length(); i ++) {
            mNoteTrack.add(new TypeTrack(noteColor.getInteger(i, -1), noteOffset.getInteger(i, -1)));
        }
        noteColor.recycle();
        noteOffset.recycle();
    }

    public void update (long time) {
        Iterator<TypeTrack> iterator = mNoteTrack.iterator();
        while (iterator.hasNext()) {
            TypeTrack note = iterator.next();
            if(note.offset > time) {
                break;
            }
            mGameView.addNote(note.color);
            iterator.remove();
        }

    }
}
