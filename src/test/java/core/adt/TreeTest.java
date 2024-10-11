package core.adt;

import hydrafp.io.core.adt.Tree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreeTest {

    @Test
    void testLeafCreation() {
        Tree<Integer> leaf = Tree.leaf(5);
        assertNotNull(leaf);
    }

    @Test
    void testNodeCreation() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.leaf(3));
        assertNotNull(node);
    }

    @Test
    void testLeafGetValue() {
        Tree<Integer> leaf = Tree.leaf(5);
        assertEquals(5, leaf.getValue());
    }

    @Test
    void testNodeGetValue() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.leaf(3));
        assertEquals(1, node.getValue());
    }

    @Test
    void testLeafSize() {
        Tree<Integer> leaf = Tree.leaf(5);
        assertEquals(1, leaf.size());
    }

    @Test
    void testNodeSize() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.leaf(3));
        assertEquals(3, node.size());
    }

    @Test
    void testLeafDepth() {
        Tree<Integer> leaf = Tree.leaf(5);
        assertEquals(1, leaf.depth());
    }

    @Test
    void testNodeDepth() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.node(3, Tree.leaf(4), Tree.leaf(5)));
        assertEquals(3, node.depth());
    }

    @Test
    void testLeafMap() {
        Tree<Integer> leaf = Tree.leaf(5);
        Tree<String> mappedLeaf = leaf.map(Object::toString);
        assertEquals("5", mappedLeaf.getValue());
    }

    @Test
    void testNodeMap() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.leaf(3));
        Tree<String> mappedNode = node.map(i -> "Value: " + i);
        assertEquals("Value: 1", mappedNode.getValue());
    }

    @Test
    void testNodeMapPreservesStructure() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.node(3, Tree.leaf(4), Tree.leaf(5)));
        Tree<String> mappedNode = node.map(Object::toString);
        assertEquals("Node(1, Leaf(2), Node(3, Leaf(4), Leaf(5)))", node.toString());
        assertEquals("Node(1, Leaf(2), Node(3, Leaf(4), Leaf(5)))", mappedNode.toString());
    }

    @Test
    void testLeafToString() {
        Tree<Integer> leaf = Tree.leaf(5);
        assertEquals("Leaf(5)", leaf.toString());
    }

    @Test
    void testNodeToString() {
        Tree<Integer> node = Tree.node(1, Tree.leaf(2), Tree.leaf(3));
        assertEquals("Node(1, Leaf(2), Leaf(3))", node.toString());
    }

    @Test
    void testComplexTreeStructure() {
        Tree<Integer> complexTree = Tree.node(1,
                Tree.node(2, Tree.leaf(3), Tree.leaf(4)),
                Tree.node(5, Tree.leaf(6), Tree.node(7, Tree.leaf(8), Tree.leaf(9)))
        );
        assertEquals(9, complexTree.size());
        assertEquals(4, complexTree.depth());
    }
}