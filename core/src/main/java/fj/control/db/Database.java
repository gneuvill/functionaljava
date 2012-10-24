package fj.control.db;

import fj.F;
import fj.Function;

import java.sql.Connection;

public class Database {
    /**
     * Constructs a database action as a function from a database connection to a value.
     *
     * @param f A function from a database connection to a value.
     * @return A database action representing the given function.
     */
    public static <A> DB<A> db(final F<Connection, A> f) {
      return f::f;
    }

    /**
     * Promotes any given function so that it transforms between values in the database.
     *
     * @param f The function to promote.
     * @return A function equivalent to the given one, which operates on values in the database.
     */
    public static <A, B> F<DB<A>, DB<B>> liftM(final F<A, B> f) {
      return a -> a.map(f);
    }

    /**
     * Constructs a database action that returns the given value completely intact.
     *
     * @param a A value to be wrapped in a database action.
     * @return A new database action that returns the given value.
     */
    public static <A> DB<A> unit(final A a) {
      return c -> a;
    }

    /**
     * Removes one layer of monadic structure.
     *
     * @param a A database action that results in another.
     * @return A new database action equivalent to the result of the given action.
     */
    public static <A> DB<A> join(final DB<DB<A>> a) {
      return a.bind(Function.<DB<A>>identity());
    }
}
