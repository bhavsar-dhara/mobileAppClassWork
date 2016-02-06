package edu.neu.madcourse.dharabhavsar.dictionary;

import java.util.HashMap;

/**
 * Created by Dhara on 2/5/2016.
 */
public class TrieNode {
    char c;
    HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    boolean isLeaf;

    public TrieNode() {}

    public TrieNode(char c){
        this.c = c;
    }
}
