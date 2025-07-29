package heap;
///Name: Malaki-Jacob Taub
///Date: 7/21/2025
///Description: Contains the needed functions for the heap itself.
import java.util.NoSuchElementException;

/** An instance is a min-heap of distinct values of type V with
 *  priorities of type P. Since it's a min-heap, the value
 *  with the smallest priority is at the root of the heap. */
public final class Heap<V, P extends Comparable<P>> {

    // TODO 1.0: Read and understand the class invariants given in the
    // following comment:

    /**
     * The contents of c represent a complete binary tree. We use square-bracket
     * shorthand to denote indexing into the AList (which is actually
     * accomplished using its get method. In the complete tree,
     * c[0] is the root; c[2i+1] is the left child of c[i] and c[2i+2] is the
     * right child of i.  If c[i] is not the root, then c[(i-1)/2] (using
     * integer division) is the parent of c[i].
     *
     * Class Invariants:
     *
     *   The tree is complete:
     *     1. `c[0..c.size()-1]` are non-null
     *
     *   The tree satisfies the heap property:
     *     2. if `c[i]` has a parent, then `c[i]`'s parent's priority
     *        is smaller than `c[i]`'s priority
     *
     *   In Phase 3, the following class invariant also must be maintained:
     *     3. The tree cannot contain duplicate *values*; note that dupliate
     *        *priorities* are still allowed.
     *     4. map contains one entry for each element of the heap, so
     *        map.size() == c.size()
     *     5. For each value v in the heap, its map entry contains in the
     *        the index of v in c. Thus: map.get(c[i]) = i.
     */
    protected AList<Entry> c;
    protected HashTable<V, Integer> map;

    /** Constructor: an empty heap with capacity 10. */
    public Heap() {
        c = new AList<Entry>(10);
        map = new HashTable<V, Integer>();
    }

    /** An Entry contains a value and a priority. */
    class Entry {
        public V value;
        public P priority;

        /** An Entry with value v and priority p*/
        Entry(V v, P p) {
            value = v;
            priority = p;
        }

        public String toString() {
            return value.toString();
        }
    }

    /** Add v with priority p to the heap.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap. Precondition: p is not null.
     *  In Phase 3 only:
     *  @throws IllegalArgumentException if v is already in the heap.*/
    public void add(V v, P p) throws IllegalArgumentException {
        c.append(new Entry(v, p));
        bubbleUp(c.size-1);
        // TODO 3.1: Update this method to maintain class invariants 3-5.
        // (delete the following line after completing TODO 1.1)
    }

    /** Return the number of values in this heap.
     *  This operation takes constant time. */
    public int size() {
        return c.size();
    }

    /** Swap c[h] and c[k].
     *  precondition: h and k are >= 0 and < c.size() */
    protected void swap(int h, int k) {
        Entry swapVal = c.get(h);
        c.put(h, c.get(k));
        c.put(k, swapVal);
        // TODO 3.2 Change this method to additionally maintain class
        // invariants 3-5 by updating the map field.
    }

    /** Bubble c[k] up in heap to its right place.
     *  Precondition: Priority of every c[i] >= its parent's priority
     *                except perhaps for c[k] */
    protected void bubbleUp(int k) {
        if(c.get(k).priority.compareTo(c.get((k-1)/2).priority)<0){
            swap(k, (k-1)/2);
            bubbleUp((k-1)/2);
        }
        return;
    }

    /** Return the value of this heap with lowest priority. Do not
     *  change the heap. This operation takes constant time.
     *  @throws NoSuchElementException if the heap is empty. */
    public V peek() throws NoSuchElementException {
        // TODO 1.4: Do peek. This is an easy one.
        //         test120Peek will not find errors if this is correct.
        if(c.size()==0){
            throw new NoSuchElementException();
        }
        return c.get(0).value;
    }

    /** Remove and return the element of this heap with lowest priority.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws NoSuchElementException if the heap is empty. */
    public V poll() throws NoSuchElementException {
        if(c.size()==0){
            throw new NoSuchElementException();
        }
        V answer = c.get(0).value;
        c.put(0, c.get(size()-1));
        c.pop();
        bubbleDown(0);
        return answer;
    }

    /** Bubble c[k] down in heap until it finds the right place.
     *  If there is a choice to bubble down to both the left and
     *  right children (because their priorities are equal), choose
     *  the right child.
     *  Precondition: Each c[i]'s priority <= its childrens' priorities
     *                except perhaps for c[k] */
    protected void bubbleDown(int k) {
        int newIndex;
        if(2*k+1 < size() || 2*k+2 < size()){
            newIndex = smallerChild(k);
            if(c.get(k).priority.compareTo(c.get(newIndex).priority)>0){
                swap(newIndex, k);
                bubbleDown(newIndex);
            }
        }
    }

    /** Return true if the value v is in the heap, false otherwise.
     *  The average case runtime is O(1).  */
    public boolean contains(V v) {
        return true;
    }

    /** Change the priority of value v to p.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws IllegalArgumentException if v is not in the heap. */
    public void changePriority(V v, P p) throws IllegalArgumentException {
        if(map.get(v)==null){
            throw new IllegalArgumentException();
        }
        int index = map.get(v);
        P oldPriority = c.get(index).priority;
        c.put(index, new Entry(v, p));
        if(p.compareTo(oldPriority)<0){
            bubbleUp(index);

        }
        else{
            bubbleDown(index);
        }
    }

    // Recommended helper method spec:
    /* Return the index of the child of k with smaller priority.
     * if only one child exists, return that child's index
     * Precondition: at least one child exists.*/
    private int smallerChild(int k) {
      if(2*k+1 >= size() && 2*k+2 < size()){
        return 2*k+2;
      }
      else if(2*k+1<size() && 2*k+2>=size()){
        return 2*k+1;
      }
      else{
        if(c.get(2*k+1).priority.compareTo(c.get(2*k+2).priority)==-1){
            return 2*k+1;
        }
        else{
            return 2*k+2;
        }
      }
    }

}
