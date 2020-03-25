package simpl.interpreter;

import java.util.HashMap;
import java.util.TreeSet;

public class Mem extends HashMap<Integer, Value> {

    private static final long serialVersionUID = -1155291135560730618L;

    public Value read(int ptr) {
        return get(ptr);
    }

    public void write(int ptr, Value val) {
        put(ptr, val);
    }

    public int alloc(State s) {
        // The original version of allocation
        /*var ptr = p.get();
        p.set(ptr + 1);
        return ptr;*/

        // Mark all cells that are bound to a name
        var marked = new TreeSet<Integer>();
        var env = s.E;
        while (env != null) {
            if (env.v instanceof RefValue)
                marked.add(((RefValue) env.v).p);
            env = env.E;
        }

        // Extend address space if all cells are in use
        if (marked.size() == s.p.get()) {
            var ptr = s.p.get();
            s.p.set(ptr + 1);
            return ptr;
        }

        // Get the first available cell in current address space
        for (int i = 0; i < s.p.get(); i++) {
            if (!marked.contains(i))
                return i;
        }
        throw new RuntimeException("unreachable");
    }
}
