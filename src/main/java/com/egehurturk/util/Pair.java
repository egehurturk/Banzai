package com.egehurturk.util;

public class Pair<A, B> {
    public static <P, Q> Pair<P, Q> makePair(P p, Q q) {
        return new Pair<P, Q>(p, q);
    }

    private final A first;
    private final B second;

    public Pair(A a, B b) {
        this.first = a;
        this.second = b;
    }

    @Override
    public String toString() {
        return "Pair{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            return other.second == null;
        } else return second.equals(other.second);
    }

    public boolean isInstance(Class<?> classA, Class<?> classB) {
        return classA.isInstance(first) && classB.isInstance(second);
    }

    @SuppressWarnings("unchecked")
    public static <P, Q> Pair<P, Q> cast(Pair<?, ?> pair, Class<P> pClass, Class<Q> qClass) {

        if (pair.isInstance(pClass, qClass)) {
            return (Pair<P, Q>) pair;
        }

        throw new ClassCastException();

    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
