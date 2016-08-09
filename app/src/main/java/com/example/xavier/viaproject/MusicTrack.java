package com.example.xavier.viaproject;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xavier on 05/08/2016.
 */
public class MusicTrack {

    private GameView mGameView;
    private List<TypeTrack> mNoteTrack;
    private String mMusicName;

    public class TypeTrack {
        int color;
        int offset;
        public TypeTrack (int color_ini, int offset_ini) {
            color = color_ini;
            offset = offset_ini;
        }
    }

    public MusicTrack (Context context, GameView gameView, String musicName) {
        mGameView = gameView;
        mMusicName = musicName;
        loadTrackFile(context);
    }

    private void loadTrackFile (Context context) {
        Resources res = context.getResources();
        TypedArray noteColor, noteOffset;

        switch (mMusicName){
            case "daft":
                noteColor = res.obtainTypedArray(R.array.daft_int_color);
                noteOffset = res.obtainTypedArray(R.array.daft_int_offset);
                break;
            case "gotc":
                noteColor = res.obtainTypedArray(R.array.gotc_int_color);
                noteOffset = res.obtainTypedArray(R.array.gotc_int_offset);
                break;
            default:
                noteColor = res.obtainTypedArray(R.array.gotc_int_color);
                noteOffset = res.obtainTypedArray(R.array.gotc_int_offset);
                break;
        }


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
