/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.black.tree;

/**
 *
 * @author dell
 */
class Node {
  int data;

  Node left;
  Node right;
  Node parent;

  boolean color;

  public Node(int data) {
    this.data = data;
  }
}
public class RedBlackTree {
    Node root;
   private void rotateRight(Node node) {
  Node parent = node.parent;
  Node leftChild = node.left;

  node.left = leftChild.right;
  if (leftChild.right != null) {
    leftChild.right.parent = node;
  }

  leftChild.right = node;
  node.parent = leftChild;

  replaceParentsChild(parent, node, leftChild);
}
   private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
  if (parent == null) {
    root = newChild;
  } else if (parent.left == oldChild) {
    parent.left = newChild;
  } else if (parent.right == oldChild) {
    parent.right = newChild;
  } else {
    throw new IllegalStateException("Node is not a child of its parent");
  }

  if (newChild != null) {
    newChild.parent = parent;
  }
}
   private void rotateLeft(Node node) {
  Node parent = node.parent;
  Node rightChild = node.right;

  node.right = rightChild.left;
  if (rightChild.left != null) {
    rightChild.left.parent = node;
  }

  rightChild.left = node;
  node.parent = rightChild;

  replaceParentsChild(parent, node, rightChild);
}
  public Node searchNode(int key) {
  Node node = root;
  while (node != null) {
    if (key == node.data) {
      return node;
    } else if (key < node.data) {
      node = node.left;
    } else {
      node = node.right;
    }
  }

  return node;
}
   public void insertNode(int key) {
  Node node = root;
  Node parent = null;

  // Traverse the tree to the left or right depending on the key
  while (node != null) {
    parent = node;
    if (key < node.data) {
      node = node.left;
    } else if (key > node.data) {
      node = node.right;
    } else {
      System.out.println("BST already contains a node with key " + key);
      return;
    }
  }

  // Insert new node
  Node newNode = new Node(key);
  newNode.color = false;
  if (parent == null) {
    root = newNode;
  } else if (key < parent.data) {
    parent.left = newNode;
  } else {
    parent.right = newNode;
  }
  newNode.parent = parent;

  fixRedBlackPropertiesAfterInsert(newNode);
}
   private void fixRedBlackPropertiesAfterInsert(Node node) {
  Node parent = node.parent;

  // Case 1: Parent is null, we've reached the root, the end of the recursion
  if (parent == null) {
    // Uncomment the following line if you want to enforce black roots (rule 2):
    // node.color = BLACK;
    return;
  }

  // Parent is black --> nothing to do
  if (parent.color == true) {
    return;
  }

  // From here on, parent is red
  Node grandparent = parent.parent;

  // Case 2:
  // Not having a grandparent means that parent is the root. If we enforce black roots
  // (rule 2), grandparent will never be null, and the following if-then block can be
  // removed.
  if (grandparent == null) {
    // As this method is only called on red nodes (either on newly inserted ones - or -
    // recursively on red grandparents), all we have to do is to recolor the root black.
    parent.color = true;
    return;
  }

  // Get the uncle (may be null/nil, in which case its color is BLACK)
  Node uncle = getUncle(parent);

  // Case 3: Uncle is red -> recolor parent, grandparent and uncle
  if (uncle != null && uncle.color == false) {
    parent.color = true;
    grandparent.color = false;
    uncle.color = true;

    // Call recursively for grandparent, which is now red.
    // It might be root or have a red parent, in which case we need to fix more...
    fixRedBlackPropertiesAfterInsert(grandparent);
  }

  // Parent is left child of grandparent
  else if (parent == grandparent.left) {
    // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
    if (node == parent.right) {
      rotateLeft(parent);

      // Let "parent" point to the new root node of the rotated sub-tree.
      // It will be recolored in the next step, which we're going to fall-through to.
      parent = node;
    }

    // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
    rotateRight(grandparent);

    // Recolor original parent and grandparent
    parent.color = true;
    grandparent.color = false;
  }

  // Parent is right child of grandparent
  else {
    // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
    if (node == parent.left) {
      rotateRight(parent);

      // Let "parent" point to the new root node of the rotated sub-tree.
      // It will be recolored in the next step, which we're going to fall-through to.
      parent = node;
    }

    // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
    rotateLeft(grandparent);

    // Recolor original parent and grandparent
    parent.color = true;
    grandparent.color = false;
  }
}
   private Node getUncle(Node parent) {
  Node grandparent = parent.parent;
  if (grandparent.left == parent) {
    return grandparent.right;
  } else if (grandparent.right == parent) {
    return grandparent.left;
  } else {
    throw new IllegalStateException("Parent is not a child of its grandparent");
  }
}
   public void deleteNode(int key) {
  Node node = root;

  // Find the node to be deleted
  while (node != null && node.data != key) {
    // Traverse the tree to the left or right depending on the key
    if (key < node.data) {
      node = node.left;
    } else {
      node = node.right;
    }
  }

  // Node not found?
  if (node == null) {
    System.out.println("not fond");
    return;
  }

  // At this point, "node" is the node to be deleted

  // In this variable, we'll store the node at which we're going to start to fix the R-B
  // properties after deleting a node.
  Node movedUpNode;
  boolean deletedNodeColor;

  // Node has zero or one child
  if (node.left == null || node.right == null) {
    movedUpNode = deleteNodeWithZeroOrOneChild(node);
    deletedNodeColor = node.color;
  }

  // Node has two children
  else {
    // Find minimum node of right subtree ("inorder successor" of current node)
    Node inOrderSuccessor = findMinimum(node.right);

    // Copy inorder successor's data to current node (keep its color!)
    node.data = inOrderSuccessor.data;

    // Delete inorder successor just as we would delete a node with 0 or 1 child
    movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
    deletedNodeColor = inOrderSuccessor.color;
  }

  if (deletedNodeColor == true) {
    fixRedBlackPropertiesAfterDelete(movedUpNode);

    // Remove the temporary NIL node
    if (movedUpNode.getClass() == NilNode.class) {
      replaceParentsChild(movedUpNode.parent, movedUpNode, null);
    }
  }
}
   private Node deleteNodeWithZeroOrOneChild(Node node) {
  // Node has ONLY a left child --> replace by its left child
  if (node.left != null) {
    replaceParentsChild(node.parent, node, node.left);
    return node.left; // moved-up node
  }

  // Node has ONLY a right child --> replace by its right child
  else if (node.right != null) {
    replaceParentsChild(node.parent, node, node.right);
    return node.right; // moved-up node
  }

  // Node has no children -->
  // * node is red --> just remove it
  // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
  else {
    Node newChild = node.color == true ? new NilNode() : null;
    replaceParentsChild(node.parent, node, newChild);
    return newChild;
  }
}
   private static class NilNode extends Node {
  private NilNode() {
    super(0);
    this.color = true;
  }
}
   private Node findMinimum(Node node) {
  while (node.left != null) {
    node = node.left;
  }
  return node;
}
   private void fixRedBlackPropertiesAfterDelete(Node node) {
  // Case 1: Examined node is root, end of recursion
  if (node == root) {
    // Uncomment the following line if you want to enforce black roots (rule 2):
    // node.color = BLACK;
    return;
  }

  Node sibling = getSibling(node);

  // Case 2: Red sibling
  if (sibling.color == false) {
    handleRedSibling(node, sibling);
    sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
  }

  // Cases 3+4: Black sibling with two black children
  if (isBlack(sibling.left) && isBlack(sibling.right)) {
    sibling.color = false;

    // Case 3: Black sibling with two black children + red parent
    if (node.parent.color == false) {
      node.parent.color = true;
    }

    // Case 4: Black sibling with two black children + black parent
    else {
      fixRedBlackPropertiesAfterDelete(node.parent);
    }
  }

  // Case 5+6: Black sibling with at least one red child
  else {
    handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
  }
}
   private Node getSibling(Node node) {
  Node parent = node.parent;
  if (node == parent.left) {
    return parent.right;
  } else if (node == parent.right) {
    return parent.left;
  } else {
    throw new IllegalStateException("Parent is not a child of its grandparent");
  }
}
   private void handleRedSibling(Node node, Node sibling) {
  // Recolor...
  sibling.color = true;
  node.parent.color = false;

  // ... and rotate
  if (node == node.parent.left) {
    rotateLeft(node.parent);
  } else {
    rotateRight(node.parent);
  }
}

private boolean isBlack(Node node) {
  return node == null || node.color == true;
}
private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
  boolean nodeIsLeftChild = node == node.parent.left;

  // Case 5: Black sibling with at least one red child + "outer nephew" is black
  // --> Recolor sibling and its child, and rotate around sibling
  if (nodeIsLeftChild && isBlack(sibling.right)) {
    sibling.left.color = true;
    sibling.color = false;
    rotateRight(sibling);
    sibling = node.parent.right;
  } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
    sibling.right.color = true;
    sibling.color = false;
    rotateLeft(sibling);
    sibling = node.parent.left;
  }

  // Fall-through to case 6...

  // Case 6: Black sibling with at least one red child + "outer nephew" is red
  // --> Recolor sibling + parent + sibling's child, and rotate around parent
  sibling.color = node.parent.color;
  node.parent.color = true;
  if (nodeIsLeftChild) {
    sibling.right.color = true;
    rotateLeft(node.parent);
  } else {
    sibling.left.color = true;
    rotateRight(node.parent);
  }
}
}