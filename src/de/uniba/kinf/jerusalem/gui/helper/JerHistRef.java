package de.uniba.kinf.jerusalem.gui.helper;

import java.util.Stack;

/**
 * Provides history of user actions in GUI via two stacks of {@link JerHistObj}.
 * 
 * @author Hanno Wierichs
 * 
 */
public class JerHistRef {

        private final Stack<JerHistObj> currentStack;
        private final Stack<JerHistObj> oldStack;

        public JerHistRef() {
                currentStack = new Stack<>();
                oldStack = new Stack<>();
        }

        public final void add(final JerHistObj jho) {
                if (jho.getId() != -1) {
                        currentStack.push(jho);
                }
        }

        public final JerHistObj getNextHistObj() {
                if (oldStack.empty()) {
                        return null;
                } else {
                        currentStack.push(oldStack.pop());
                        if (currentStack.isEmpty()) {
                                return null;
                        }
                        return oldStack.peek();
                }
        }

        public final JerHistObj getPrevHistObj() {
                if (currentStack.empty()) {
                        return null;
                } else {
                        oldStack.push(currentStack.pop());
                        if (currentStack.isEmpty()) {
                                return null;
                        }
                        return currentStack.peek();
                }
        }

}
