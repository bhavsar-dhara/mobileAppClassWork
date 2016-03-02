package edu.neu.madcourse.dharabhavsar.dictionary;

/**
 * Created by Dhara on 2/27/2016.
 */
public interface Vocabulary {
    boolean add(String word);
    boolean isPrefix(String prefix);
    boolean contains(String word);
    String getName();
}
