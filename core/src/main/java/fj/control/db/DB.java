package fj.control.db;

import fj.F;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * The DB monad represents a database action, or a value within the context of a database connection.
 */
public interface DB<A> {

  /**
   * Executes the database action, given a database connection.
   *
   * @param c The connection against which to execute the action.
   * @return The result of the action.
   * @throws SQLException if a database error occurred.
   */
  A run(final Connection c) throws SQLException;

    /**
   * Returns the callable-valued function projection of this database action.
   *
   * @return The callable-valued function which is isomorphic to this database action.
   */
  F<Connection, Callable<A>> asFunction() default {
    return c -> () -> run(c);
  }

  /**
   * Map a function over the result of this action.
   *
   * @param f The function to map over the result.
   * @return A new database action that applies the given function to the result of this action.
   */
  <B> DB<B> map(final F<A, B> f) default {
    return new DB<B>() {
      public B run(final Connection c) throws SQLException {
        return f.f(DB.this.run(c));
      }
    };
  }

    /**
   * Binds the given action across the result of this database action.
   *
   * @param f The function to bind across the result of this database action.
   * @return A new database action equivalent to applying the given function to the result of this action.
   */
  <B> DB<B> bind(final F<A, DB<B>> f) default {
    return c -> f.f(run(c)).run(c);
  }
}