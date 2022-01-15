package me.middleclicker.diepbot.util;

/**
 * @Author https://stackoverflow.com/questions/16877427/how-to-implement-a-non-binary-tree
 */
public class SearchTree {
    class Child {
        Node node;
        Child next = null;

        public Child (Node node) {
            this.node = node;
        }

        public void addChild (Node node) {
            if (this.next == null)
                this.next = new Child (node);
            else
                this.next.addChild (node);
        }
    }

    class Node {
        public String data;
        public Child children = null;

        public Node (String data) {
            this.data = data;
        }

        public void addChild (Node node) {
            if (this.children == null)
                this.children = new Child (node);
            else
                this.children.addChild (node);
        }
    }
}
