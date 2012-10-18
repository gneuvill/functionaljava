package fj;

import static fj.Function.curry;

import fj.data.Array;
import fj.data.List;
import fj.data.Natural;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @version %build.number%
 */
public final class Semigroup<A> {
  private final F<A, F<A, A>> sum;

  private Semigroup(final F<A, F<A, A>> sum) {
    this.sum = sum;
  }

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The of the two given arguments.
   */
  public A sum(final A a1, final A a2) {
    return sum.f(a1).f(a2);
  }

  /**
   * Returns a function that sums the given value according to this semigroup.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this semigroup.
   */
  public F<A, A> sum(final A a1) {
    return sum.f(a1);
  }

  /**
   * Returns a function that sums according to this semigroup.
   *
   * @return A function that sums according to this semigroup.
   */
  public F<A, F<A, A>> sum() {
    return sum;
  }

  /**
   * Constructs a semigroup from the given function.
   *
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final F<A, F<A, A>> sum) {
    return new Semigroup<>(sum);
  }

  /**
   * Constructs a semigroup from the given function.
   *
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final F2<A, A, A> sum) {
    return new Semigroup<>(curry(sum));
  }

  /**
   * A semigroup that adds integers.
   */
  public static final Semigroup<Integer> intAdditionSemigroup = semigroup((final Integer i1, final Integer i2) -> i1 + i2);

  /**
   * A semigroup that adds doubles.
   */
  public static final Semigroup<Double> doubleAdditionSemigroup = semigroup((final Double d1, final Double d2) -> d1 + d2);

  /**
   * A semigroup that multiplies integers.
   */
  public static final Semigroup<Integer> intMultiplicationSemigroup = semigroup((final Integer i1, final Integer i2) -> i1 * i2);

  /**
   * A semigroup that multiplies doubles.
   */
  public static final Semigroup<Double> doubleMultiplicationSemigroup = semigroup((final Double d1, final Double d2) -> d1 * d2);

  /**
   * A semigroup that yields the maximum of integers.
   */
  public static final Semigroup<Integer> intMaximumSemigroup = semigroup(Ord.intOrd.max);

  /**
   * A semigroup that yields the minimum of integers.
   */
  public static final Semigroup<Integer> intMinimumSemigroup = semigroup(Ord.intOrd.min);

  /**
   * A semigroup that adds big integers.
   */
  public static final Semigroup<BigInteger> bigintAdditionSemigroup =
      semigroup((final BigInteger i1, final BigInteger i2) -> i1.add(i2));

  /**
   * A semigroup that multiplies big integers.
   */
  public static final Semigroup<BigInteger> bigintMultiplicationSemigroup =
      semigroup((final BigInteger i1, final BigInteger i2) -> i1.multiply(i2));

  /**
   * A semigroup that yields the maximum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMaximumSemigroup = semigroup(Ord.bigintOrd.max);

  /**
   * A semigroup that yields the minimum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMinimumSemigroup = semigroup(Ord.bigintOrd.min);

  /**
   * A semigroup that adds big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalAdditionSemigroup =
      semigroup((final BigDecimal i1, final BigDecimal i2) -> i1.add(i2));

  /**
   * A semigroup that multiplies big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalMultiplicationSemigroup =
      semigroup((final BigDecimal i1, final BigDecimal i2) -> i1.multiply(i2));

  /**
   * A semigroup that yields the maximum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMaximumSemigroup = semigroup(Ord.bigdecimalOrd.max);

  /**
   * A semigroup that yields the minimum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMinimumSemigroup = semigroup(Ord.bigdecimalOrd.min);

  /**
   * A semigroup that multiplies natural numbers.
   */
  public static final Semigroup<Natural> naturalMultiplicationSemigroup =
      semigroup((final Natural n1, final Natural n2) -> n1.multiply(n2));

  /**
   * A semigroup that multiplies natural numbers.
   */
  public static final Semigroup<Natural> naturalAdditionSemigroup =
      semigroup((final Natural n1, final Natural n2) -> n1.add(n2));

  /**
   * A semigroup that yields the maximum of natural numbers.
   */
  public static final Semigroup<Natural> naturalMaximumSemigroup = semigroup(Ord.naturalOrd.max);

  /**
   * A semigroup that yields the minimum of natural numbers.
   */
  public static final Semigroup<Natural> naturalMinimumSemigroup = semigroup(Ord.naturalOrd.min);

  /**
   * A semigroup that adds longs.
   */
  public static final Semigroup<Long> longAdditionSemigroup = semigroup((final Long x, final Long y) -> x + y);

  /**
   * A semigroup that multiplies longs.
   */
  public static final Semigroup<Long> longMultiplicationSemigroup = semigroup((final Long x, final Long y) -> x * y);

  /**
   * A semigroup that yields the maximum of longs.
   */
  public static final Semigroup<Long> longMaximumSemigroup = semigroup(Ord.longOrd.max);

  /**
   * A semigroup that yields the minimum of longs.
   */
  public static final Semigroup<Long> longMinimumSemigroup = semigroup(Ord.longOrd.min);

  /**
   * A semigroup that ORs booleans.
   */
  public static final Semigroup<Boolean> disjunctionSemigroup = semigroup((final Boolean b1, final Boolean b2) -> b1 || b2);

  /**
   * A semigroup that XORs booleans.
   */
  public static final Semigroup<Boolean> exclusiveDisjunctionSemiGroup = semigroup((final Boolean p, final Boolean q) -> p && !q || !p && q);

  /**
   * A semigroup that ANDs booleans.
   */
  public static final Semigroup<Boolean> conjunctionSemigroup = semigroup((final Boolean b1, final Boolean b2) -> b1 && b2);

  /**
   * A semigroup that appends strings.
   */
  public static final Semigroup<String> stringSemigroup = semigroup((final String s1, final String s2) -> s1 + s2);

  /**
   * A semigroup that appends string buffers.
   */
  public static final Semigroup<StringBuffer> stringBufferSemigroup =
      semigroup((final StringBuffer s1, final StringBuffer s2) -> new StringBuffer(s1).append(s2));

  /**
   * A semigroup that appends string builders.
   */
  public static final Semigroup<StringBuilder> stringBuilderSemigroup =
      semigroup((final StringBuilder s1, final StringBuilder s2) -> new StringBuilder(s1).append(s2));

  /**
   * A semigroup for functions.
   *
   * @param sb The smeigroup for the codomain.
   * @return A semigroup for functions.
   */
  public static <A, B> Semigroup<F<A, B>> functionSemigroup(final Semigroup<B> sb) {
    return semigroup((final F<A, B> a1, final F<A, B> a2) -> new F<A, B>() {
      public B f(final A a) {
        return sb.sum(a1.f(a), a2.f(a));
      }
    });
  }

  /**
   * A semigroup for lists.
   *
   * @return A semigroup for lists.
   */
  public static <A> Semigroup<List<A>> listSemigroup() {
    return semigroup((final List<A> a1, final List<A> a2) -> a1.append(a2));
  }

  /**
   * A semigroup for non-empty lists.
   *
   * @return A semigroup for non-empty lists.
   */
  public static <A> Semigroup<NonEmptyList<A>> nonEmptyListSemigroup() {
    return semigroup((final NonEmptyList<A> a1, final NonEmptyList<A> a2) -> a1.append(a2));
  }

  /**
   * A semigroup for optional values.
   ** @return A semigroup for optional values.
   */
  public static <A> Semigroup<Option<A>> optionSemigroup() {
    return semigroup((final Option<A> a1, final Option<A> a2) -> a1.isSome() ? a1 : a2);
  }

  /**
   * A semigroup for optional values that take the first available value.
   *
   * @return A semigroup for optional values that take the first available value.
   */
  public static <A> Semigroup<Option<A>> firstOptionSemigroup() {
    return semigroup((final Option<A> a1, final Option<A> a2) -> a1.orElse(a2));
  }

  /**
   * A semigroup for optional values that take the last available value.
   *
   * @return A semigroup for optional values that take the last available value.
   */
  public static <A> Semigroup<Option<A>> lastOptionSemigroup() {
    return semigroup((final Option<A> a1, final Option<A> a2) -> a2.orElse(a1));
  }

  /**
   * A semigroup for streams.
   *
   * @return A semigroup for streams.
   */
  public static <A> Semigroup<Stream<A>> streamSemigroup() {
    return semigroup((final Stream<A> a1, final Stream<A> a2) -> a1.append(a2));
  }

  /**
   * A semigroup for arrays.
   *
   * @return A semigroup for arrays.
   */
  public static <A> Semigroup<Array<A>> arraySemigroup() {
    return semigroup((final Array<A> a1, final Array<A> a2) -> a1.append(a2));
  }

  /**
   * A semigroup for unary products.
   *
   * @param sa A semigroup for the product's type.
   * @return A semigroup for unary products.
   */
  public static <A> Semigroup<P1<A>> p1Semigroup(final Semigroup<A> sa) {
    return semigroup((final P1<A> a1, final P1<A> a2) -> () -> sa.sum(a1._1(), a2._1()));
  }

  /**
   * A semigroup for binary products.
   *
   * @param sa A semigroup for the product's first type.
   * @param sb A semigroup for the product's second type.
   * @return A semigroup for binary products.
   */
  public static <A, B> Semigroup<P2<A, B>> p2Semigroup(final Semigroup<A> sa, final Semigroup<B> sb) {
    return semigroup((final P2<A, B> a1, final P2<A, B> a2) -> new P2<A, B>() {
      public A _1() {
        return sa.sum(a1._1(), a2._1());
      }

      public B _2() {
        return sb.sum(a1._2(), a2._2());
      }
    });
  }

  /**
   * A semigroup for the Unit value.
   */
  public static final Semigroup<Unit> unitSemigroup = semigroup((final Unit u1, final Unit u2) -> Unit.unit());

  /**
   * A semigroup for sets.
   *
   * @return a semigroup for sets.
   */
  public static <A> Semigroup<Set<A>> setSemigroup() {
    return semigroup((final Set<A> a, final Set<A> b) -> a.union(b));
  }

}
