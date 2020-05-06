package simpl.parser;

import java.util.HashMap;
import java.util.Objects;

public class Symbol {

    private String name;

    private Symbol(String n) {
        name = n;
    }

    public String toString() {
        return name;
    }

    private static HashMap<String, Symbol> dict = new HashMap<String, Symbol>();

    /**
     * Make return the unique symbol associated with a string. Repeated calls to <tt>symbol("abc")</tt> will return the
     * same Symbol.
     */
    public static Symbol symbol(String n) {
        String u = n.intern();
        Symbol s = dict.get(u);
        if (s == null) {
            s = new Symbol(u);
            dict.put(u, s);
        }
        return s;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Symbol symbol = (Symbol) o;
        return name.equals(symbol.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
