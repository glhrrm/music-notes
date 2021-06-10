package br.edu.ifrs.musicnotes.helper;

import java.util.Iterator;
import java.util.List;

public class Helper {

    public static StringBuilder stringBuilder(List<String> string) {
        StringBuilder result = new StringBuilder();

        for (Iterator<String> iterator = string.iterator(); iterator.hasNext(); ) {
            result.append(iterator.next());
            if (iterator.hasNext()) {
                result.append(", ");
            }
        }

        return result;
    }
}
