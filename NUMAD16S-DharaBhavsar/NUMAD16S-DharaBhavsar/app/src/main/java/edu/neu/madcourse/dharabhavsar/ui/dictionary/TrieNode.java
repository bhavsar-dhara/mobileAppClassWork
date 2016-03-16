package edu.neu.madcourse.dharabhavsar.ui.dictionary;

import java.util.HashMap;

/**
 * Created by Dhara on 2/5/2016.
 */
/*public class TrieNode {
    char c;
    HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    boolean isLeaf;

    public TrieNode() {}

    public TrieNode(char c){
        this.c = c;
    }
}*/
// Trie Node, which stores a character and the children in a HashMap
class TrieNode {
    public TrieNode(char ch)  {
        value = ch;
        children = new HashMap<>();
        bIsEnd = false;
    }
    public HashMap<Character,TrieNode> getChildren() {   return children;  }
    public char getValue()                           {   return value;     }
    public void setIsEnd(boolean val)                {   bIsEnd = val;     }
    public boolean isEnd()                           {   return bIsEnd;    }

    private char value;
    private HashMap<Character,TrieNode> children;
    private boolean bIsEnd;
}

