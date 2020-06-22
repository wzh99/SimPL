package simpl.typing;

import simpl.parser.Symbol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public abstract class Substitution {

    public abstract Type apply(Type t);

    private static final class Identity extends Substitution {
        public Type apply(Type t) {
            return t;
        }

        @Override public List<Replace> toList() {
            return List.of();
        }

        @Override public String toString() {
            return "";
        }
    }

    public abstract List<Replace> toList();

    private static final class Replace extends Substitution {
        private TypeVar a;
        private Type t;

        public Replace(TypeVar a, Type t) {
            this.a = a;
            this.t = t;
            if (t instanceof TypeVar) {
                if (((TypeVar) t).compareTo(a) > 0) {
                    this.a = (TypeVar) t;
                    this.t = a;
                }
            }
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Replace replace = (Replace) o;
            return a.equals(replace.a) && t.equals(replace.t);
        }

        @Override public int hashCode() {
            return a.hashCode() ^ t.hashCode();
        }

        public Type apply(Type b) {
            return b.replace(a, t);
        }

        @Override public List<Replace> toList() {
            return List.of(this);
        }

        @Override public String toString() {
            return "Replace{" + "a=" + a + ", t=" + t + "}\n";
        }
    }

    private static final class Compose extends Substitution {

        private ArrayList<Replace> list;

        private Substitution f, g;

        public Compose(Substitution f, Substitution g) throws TypeError {
            this.f = f;
            this.g = g;

            // Solve constraints iteratively
            var set = new HashSet<Replace>();
            set.addAll(f.toList());
            set.addAll(g.toList());
            list = new ArrayList<>(set);
            while (true) {
                var prevSize = list.size();
                for (var s1 : list) {
                    for (var s2 : list) {
                        if (s1.a.equals(s2.a) && s1 != s2) {
                            set.addAll(s1.t.unify(s2.t).toList());
                        }
                    }
                }
                list = new ArrayList<>(set);
                if (list.size() == prevSize)
                    break;
            }

            // Sort constraints
            list = new ArrayList<>(set);
            list.sort(Comparator.comparingInt((Replace r) -> r.t.height()).reversed()
                .thenComparing((Replace r1, Replace r2) -> -r1.a.compareTo(r2.a)));
        }

        public Type apply(Type t) {
            //            return f.apply(g.apply(t));
            var result = t;
            for (var entry : list) {
                result = entry.apply(result);
            }
            return result;
        }

        @Override public List<Replace> toList() {
            return list;
        }

        @Override public String toString() {
            return f.toString() + g.toString();
        }
    }

    public static final Substitution IDENTITY = new Identity();

    public static Substitution of(TypeVar a, Type t) {
        return new Replace(a, t);
    }

    public Substitution compose(Substitution inner) throws TypeError {
        return new Compose(this, inner);
    }

    public TypeEnv compose(final TypeEnv E) {
        return new TypeEnv() {
            public Type get(Symbol x) {
                return apply(E.get(x));
            }
        };
    }

}
