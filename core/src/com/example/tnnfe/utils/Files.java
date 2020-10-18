package com.example.tnnfe.utils;

import java.io.File;
import java.util.regex.Pattern;

public final class Files {

    private Files() {
    }

    public static String[] getFiles(final String path, final String searchPattern) {

        final Pattern pattern = Pattern.compile(searchPattern.replace("*", ".*").replace("?", ".?"));
        return new File(path).list((dir, name) -> new File(dir, name).isFile() && pattern.matcher(name).matches());
    }
}
