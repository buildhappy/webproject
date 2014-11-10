/**
 * $RCSfile: BooleanList.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:09 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.util;

/**
 * A simplified List for boolean values. Only the bare number of methods needed
 * by Jive have been implemented so far, so additional implementation work
 * would be welcome.<p>
 *
 * The implementation uses an array for maximum speed. If the number of elements
 * grows larger than capacity, the capacity will automatically grow.
 */
public class BooleanList {

    boolean [] elements;
    int capacity;
    int size;

    /**
     * Creates a new list of boolean values with a default capacity of 50.
     */
    public BooleanList() {
        this(50);
    }

    /**
     * Creates a new list of boolean values with a specified initial capacity.
     *
     * @param initialCapacity a capacity to initialize the list with.
     */
    public BooleanList(int initialCapacity) {
        size = 0;
        capacity = initialCapacity;
        elements = new boolean[capacity];
    }

    /**
     * Adds a new boolean value to the end of the list.
     */
    public void add(boolean value) {
        elements[size] = value;
        size++;
        if (size == capacity) {
            capacity = capacity * 2;
            boolean[] newElements = new boolean[capacity];
            for (int i=0; i<size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    /**
     * Returns the boolean value at the specified index. If the index is not
     * valid, an IndexOutOfBoundException will be thrown.
     *
     * @param index the index of the value to return.
     * @return the value at the specified index.
     */
    public boolean get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " not valid.");
        }
        return elements[index];
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return the number of elements in the list.
     */
    public int size() {
        return size;
    }

    /**
     * Returns a new array containing the list elements.
     *
     * @return an array of the list elements.
     */
    public boolean[] toArray() {
        int size = this.size;
        boolean[] newElements = new boolean[size];
        for (int i=0; i<size; i++) {
            newElements[i] = elements[i];
        }
        return newElements;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<this.size; i++) {
            buf.append(elements[i]).append(" ");
        }
        return buf.toString();
    }
}