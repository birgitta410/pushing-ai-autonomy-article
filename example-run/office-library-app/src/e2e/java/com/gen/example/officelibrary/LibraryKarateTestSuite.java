package com.gen.example.officelibrary;

import com.intuit.karate.junit5.Karate;

public class LibraryKarateTestSuite {
    @Karate.Test
    Karate testLibrary() {
        return Karate.run("library").relativeTo(getClass());
    }
}