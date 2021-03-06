package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.util.Iterator;

abstract class RawDictionaryIterator implements Iterator<Pair<String, String>> {
    BufferedReader reader;
    IpaConverter ipaConverter;
    boolean hasNext = true;

    RawDictionaryIterator(BufferedReader reader, IpaConverter ipaConverter) {
        this.reader = reader;
        this.ipaConverter = ipaConverter;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    String formatWord(String word) {
        return word.replaceAll("\\(\\d+\\)", "");
    }
}
