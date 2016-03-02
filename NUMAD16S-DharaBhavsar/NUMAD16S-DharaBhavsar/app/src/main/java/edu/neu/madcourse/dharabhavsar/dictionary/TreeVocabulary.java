package edu.neu.madcourse.dharabhavsar.dictionary;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Dhara on 2/27/2016.
 */
public class TreeVocabulary extends TreeSet<String> implements Vocabulary {

    public TreeVocabulary() {
        super();
    }

    public TreeVocabulary(Collection<String> c) {
        super(c);
    }

    public boolean isPrefix(String prefix) {
        String nextWord = ceiling(prefix);
        if (nextWord == null) {
            return false;
        }
        if (nextWord.equals(prefix)) {
            Set<String> tail = tailSet(nextWord, false);
            if (tail.isEmpty()) {
                return false;
            }
            nextWord = tail.iterator().next();
        }
        return nextWord.startsWith(prefix);
    }

    /**
     * There is a mismatch between the parameter types of vocabulary and TreeSet, so
     * force call to the upper-class method
     */
    public boolean contains(String word) {
        return super.contains(word);
    }
    @Override
    public String getName() {
        return getClass().getName();
    }
}
