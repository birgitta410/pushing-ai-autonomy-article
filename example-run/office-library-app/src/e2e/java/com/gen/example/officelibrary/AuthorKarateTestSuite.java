package com.gen.example.officelibrary;

import com.intuit.karate.junit5.Karate;

public class AuthorKarateTestSuite {
    @Karate.Test
    Karate testAuthor() {
        return Karate.run("author").relativeTo(getClass());
    }
}