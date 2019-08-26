package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.util.Pair;

import com.hoardingsinc.phoneticskeyboard.rawdictionary.ArpabetToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.CmuPronouncingDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class InMemoryPronunciationDictionary extends PronunciationDictionary {


    private Map<String, List<String>> dictionary;

    public InMemoryPronunciationDictionary(Context context, RawDictionary rawDictionary) throws IOException {
        if (rawDictionary != null)
            this.dictionary = this.loadDictionary(rawDictionary);

    }

    public List<String> exactMatch(String ipa) {
        return this.dictionary.get(ipa);
    }

    public SortedSet<String> lookAheadMatch(String ipa) {
        SortedSet<String> lookahead = new TreeSet<>(new StringLengthComparator());
        for (Map.Entry<String, List<String>> entry : this.dictionary.entrySet()) {
            if (entry.getKey().startsWith(ipa)) {
                lookahead.addAll(entry.getValue());
            }
        }
        return lookahead;
    }

    public int numEntries() {
        return this.dictionary.size();


    }

    Map<String, List<String>> loadDictionary(RawDictionary rawDictionary) throws IOException {
        Map<String, List<String>> dictionary = new HashMap<>();
        for (Pair<String, String> entry : rawDictionary) {
            String ipa = entry.first;
            String word = entry.second;
            if (dictionary.containsKey(ipa)) {
                List<String> wordList = dictionary.get(ipa);
                wordList.add(word);
            } else {
                ArrayList<String> wordList = new ArrayList<>();
                wordList.add(word);
                dictionary.put(ipa, wordList);
            }
        }
        return dictionary;
    }
}