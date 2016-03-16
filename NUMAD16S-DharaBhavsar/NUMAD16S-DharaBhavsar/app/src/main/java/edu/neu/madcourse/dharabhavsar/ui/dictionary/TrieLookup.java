package edu.neu.madcourse.dharabhavsar.ui.dictionary;

import java.util.Collection;

import static edu.neu.madcourse.dharabhavsar.ui.dictionary.Alphabet.LOWERCASE;

/**
 * Created by Dhara on 2/5/2016.
 */
public class TrieLookup {
    private boolean isWord = false;
    // The number of possible children is the number of letters in the alphabet
    private TrieLookup[] children = new TrieLookup[LOWERCASE.size()];
    // This is the number of actual children
    private int numChildren = 0;

    public TrieLookup() {
    }

    public TrieLookup(Collection<String> words) {
        for (String w:words) {
            add(w);
        }
    }

    public boolean add(String s) {
        char first = s.charAt(0);
        int index = LOWERCASE.getIndex(first);
        if (index < 0) {
            System.out.println("uf");
        }
        TrieLookup child = children[index];
        if (child == null) {
            child = new TrieLookup();
            children[index] = child;
            numChildren++;
        }
        if (s.length() == 1) {
            if (child.isWord) {
                // The word is already in the trie
                return false;
            }
            child.isWord = true;
            return true;
        } else {
            // Recurse into sub-trie
            return child.add(s.substring(1));
        }
    }

    /**
     * Searches for a string in this trie
     * @param s
     * @return
     */
    public boolean contains(String s) {
        TrieLookup n = getNode(s);
        return n != null && n.isWord;
    }

    /**
     * Searches for a string prefix in this trie
     * @param s
     * @return
     */
    public boolean isPrefix(String s) {
        TrieLookup n = getNode(s);
        return n != null && n.numChildren > 0;
    }

    /**
     * Returns the node corresponding to the string
     * @param s
     * @return
     */
    public TrieLookup getNode(String s) {
        TrieLookup node = this;
        for (int i = 0; i < s.length(); i++) {
            int index = LOWERCASE.getIndex(s.charAt(i));
            TrieLookup child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node = child;
        }
        return node;
    }

    /**
     * Searches for a string represented as indices in this trie,
     * @param
     * @return
     */
    public boolean contains(byte[] indices, int offset, int len) {
        TrieLookup n = getNode(indices, offset, len);
        return n != null && n.isWord;
    }

    public boolean contains(byte[] indices, int offset) {
        TrieLookup n = getNode(indices, offset, indices.length-offset);
        return n != null && n.isWord;
    }

    /**
     * Searches for a string prefix represented as indices in this trie
     * @param
     * @return
     */
    public boolean isPrefix(byte[] indices, int offset, int len) {
        TrieLookup n = getNode(indices, offset, len);
        return n != null && n.numChildren > 0;
    }

    public boolean isPrefix(byte[] indices, int offset) {
        TrieLookup n = getNode(indices, offset, indices.length-offset);
        return n != null && n.numChildren > 0;
    }

    /**
     * Returns the node corresponding to the string represented as indices
     * @param
     * @return
     */
    public TrieLookup getNode(byte[] indices, int offset, int len) {
        TrieLookup node = this;
        for (int i = 0; i < len; i++) {
            int index = indices[offset+i];
            TrieLookup child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node=child;
        }
        return node;
    }

    public String getName() {
        return getClass().getName();
    }

    public boolean isWord() {
        return isWord;
    }

    public boolean hasChildren() {
        return numChildren > 0;
    }
}
