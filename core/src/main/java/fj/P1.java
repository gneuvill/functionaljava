package fj;

import java.lang.ref.SoftReference;

/**
 * A product-1. Also, the identity monad.
 *
 * @version %build.number%
 */
public interface P1<A> {
  /**
   * Access the first element of the product.
   *
   * @return The first element of the product.
   */
  A _1();

  /**
   * Map the element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  <X> P1<X> map(final F<A, X> f) default {
    return new P1<X>() {
      public X _1() {
        return f.f(P1.this._1());
      }
    };
  }

    /**
   * Provides a memoising P1 that remembers its value.
   *
   * @return A P1 that calls this P1 once and remembers the value for subsequent calls.
   */
  P1<A> memo() default {
    final P1<A> self = this;
    return new P1<A>() {
      private final Object latch = new Object();
      @SuppressWarnings({"InstanceVariableMayNotBeInitialized"})
      private volatile SoftReference<A> v;

      public A _1() {
        A a = v != null ? v.get() : null;
        if (a == null)
          synchronized (latch) {
            if (v == null || v.get() == null)
              a = self._1();
            v = new SoftReference<>(a);
          }
        return a;
      }
    };
  }

  /**
   * Returns a constant function that always uses this value.
   *
   * @return A constant function that always uses this value. 
   */
  <B> F<B, A> constant() default {
    return b -> _1();
  }
}
