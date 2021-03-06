package com.narmical.fonetic.rawdictionary;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;


public class LoadArpabetToIpaMapInMemoryTest {

    @Test
    public void loadArpabetToIpaMapTest() throws IOException {
        ArpabetToIpaConverter arpabetToIpaConverter = new ArpabetToIpaConverter(null);

        Map<String, String> expected = new HashMap<String, String>() {
            {
                put("AA", "ɑ");
                put("AE", "æ");
                put("AH", "ʌ");
            }
        };

        Map<String, String> actual = arpabetToIpaConverter.loadMobyToIpaMap(new BufferedReader(new StringReader("AA\tɑ\n" +
                "AE\tæ\n" +
                "AH\tʌ")));

        assertThat(expected.entrySet(), everyItem(isIn(actual.entrySet())));
    }

}