package fj.test;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.Function;
import static fj.Function.compose;
import static fj.P.p;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;
import fj.data.Array;
import fj.data.Either;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.Enumerator.charEnumerator;
import fj.data.List;
import static fj.data.List.asString;
import static fj.data.List.list;
import fj.data.Option;
import static fj.data.Option.some;
import fj.data.Stream;
import static fj.data.Stream.range;
import static fj.test.Gen.choose;
import static fj.test.Gen.elements;
import static fj.test.Gen.fail;
import static fj.test.Gen.frequency;
import static fj.test.Gen.listOf;
import static fj.test.Gen.oneOf;
import static fj.test.Gen.promote;
import static fj.test.Gen.sized;
import static fj.test.Gen.value;

import static java.lang.Math.abs;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import static java.util.Locale.getAvailableLocales;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import static java.util.EnumSet.copyOf;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * The type used to generate arbitrary values of the given type parameter (<code>A</code>). Common
 * arbitrary implementations are provided.
 *
 * @version %build.number%
 */
public final class Arbitrary<A> {
  /**
   * The generator associated with this arbitrary.
   */
  @SuppressWarnings({"PublicField"})
  public final Gen<A> gen;

  private Arbitrary(final Gen<A> gen) {
    this.gen = gen;
  }

  /**
   * Constructs and arbitrary with the given generator.
   *
   * @param g The generator to construct an arbitrary with.
   * @return A new arbitrary that uses the given generator.
   */
  public static <A> Arbitrary<A> arbitrary(final Gen<A> g) {
    return new Arbitrary<>(g);
  }

  /**
   * An arbitrary for functions.
   *
   * @param c The coarbitrary for the function domain.
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Arbitrary<F<A, B>> arbF(final Coarbitrary<A> c, final Arbitrary<B> a) {
    return arbitrary(promote((final A x) -> c.coarbitrary(x, a.gen)));
  }

  /**
   * An arbitrary for functions.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Arbitrary<F<A, B>> arbFInvariant(final Arbitrary<B> a) {
    return arbitrary(a.gen.map(Function.<A, B>constant()));
  }

  /**
   * An arbitrary for function-2.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Arbitrary<F2<A, B, C>> arbF2(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                       final Arbitrary<C> a) {
    return arbitrary(arbF(ca, arbF(cb, a)).gen.map(Function.<A, B, C>uncurryF2()));
  }

  /**
   * An arbitrary for function-2.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Arbitrary<F2<A, B, C>> arbF2Invariant(final Arbitrary<C> a) {
    return arbitrary(a.gen.map(
        compose(Function.<A, B, C>uncurryF2(), compose(Function.<A, F<B, C>>constant(), Function.<B, C>constant()))));
  }

  /**
   * An arbitrary for function-3.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Arbitrary<F3<A, B, C, D>> arbF3(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                             final Coarbitrary<C> cc, final Arbitrary<D> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, a))).gen.map(Function.<A, B, C, D>uncurryF3()));
  }

  /**
   * An arbitrary for function-3.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Arbitrary<F3<A, B, C, D>> arbF3Invariant(final Arbitrary<D> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D>uncurryF3(), compose(Function.<A, F<B, F<C, D>>>constant(),
                                                                                 compose(
                                                                                     Function.<B, F<C, D>>constant(),
                                                                                     Function.<C, D>constant())))));
  }

  /**
   * An arbitrary for function-4.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Arbitrary<F4<A, B, C, D, E>> arbF4(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                                   final Coarbitrary<C> cc, final Coarbitrary<D> cd,
                                                                   final Arbitrary<E> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, a)))).gen.map(Function.<A, B, C, D, E>uncurryF4()));
  }

  /**
   * An arbitrary for function-4.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Arbitrary<F4<A, B, C, D, E>> arbF4Invariant(final Arbitrary<E> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E>uncurryF4(),
                                       compose(Function.<A, F<B, F<C, F<D, E>>>>constant(),
                                               compose(Function.<B, F<C, F<D, E>>>constant(),
                                                       compose(Function.<C, F<D, E>>constant(),
                                                               Function.<D, E>constant()))))));
  }

  /**
   * An arbitrary for function-5.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Arbitrary<F5<A, B, C, D, E, F$>> arbF5(final Coarbitrary<A> ca,
                                                                           final Coarbitrary<B> cb,
                                                                           final Coarbitrary<C> cc,
                                                                           final Coarbitrary<D> cd,
                                                                           final Coarbitrary<E> ce,
                                                                           final Arbitrary<F$> a) {
    return arbitrary(
        arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, a))))).gen.map(Function.<A, B, C, D, E, F$>uncurryF5()));
  }

  /**
   * An arbitrary for function-5.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Arbitrary<F5<A, B, C, D, E, F$>> arbF5Invariant(final Arbitrary<F$> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$>uncurryF5(),
                                       compose(Function.<A, F<B, F<C, F<D, F<E, F$>>>>>constant(),
                                               compose(Function.<B, F<C, F<D, F<E, F$>>>>constant(),
                                                       compose(Function.<C, F<D, F<E, F$>>>constant(),
                                                               compose(Function.<D, F<E, F$>>constant(),
                                                                       Function.<E, F$>constant())))))));
  }

  /**
   * An arbitrary for function-6.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Arbitrary<F6<A, B, C, D, E, F$, G>> arbF6(final Coarbitrary<A> ca,
                                                                                 final Coarbitrary<B> cb,
                                                                                 final Coarbitrary<C> cc,
                                                                                 final Coarbitrary<D> cd,
                                                                                 final Coarbitrary<E> ce,
                                                                                 final Coarbitrary<F$> cf,
                                                                                 final Arbitrary<G> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, a)))))).gen.map(
        Function.<A, B, C, D, E, F$, G>uncurryF6()));
  }

  /**
   * An arbitrary for function-6.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Arbitrary<F6<A, B, C, D, E, F$, G>> arbF6Invariant(final Arbitrary<G> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G>uncurryF6(),
                                       compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>constant(),
                                               compose(Function.<B, F<C, F<D, F<E, F<F$, G>>>>>constant(),
                                                       compose(Function.<C, F<D, F<E, F<F$, G>>>>constant(),
                                                               compose(Function.<D, F<E, F<F$, G>>>constant(),
                                                                       compose(Function.<E, F<F$, G>>constant(),
                                                                               Function.<F$, G>constant()))))))));
  }

  /**
   * An arbitrary for function-7.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param cg A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Arbitrary<F7<A, B, C, D, E, F$, G, H>> arbF7(final Coarbitrary<A> ca,
                                                                                       final Coarbitrary<B> cb,
                                                                                       final Coarbitrary<C> cc,
                                                                                       final Coarbitrary<D> cd,
                                                                                       final Coarbitrary<E> ce,
                                                                                       final Coarbitrary<F$> cf,
                                                                                       final Coarbitrary<G> cg,
                                                                                       final Arbitrary<H> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, a))))))).gen.map(
        Function.<A, B, C, D, E, F$, G, H>uncurryF7()));
  }

  /**
   * An arbitrary for function-7.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Arbitrary<F7<A, B, C, D, E, F$, G, H>> arbF7Invariant(final Arbitrary<H> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G, H>uncurryF7(),
                                       compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>constant(),
                                               compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>constant(),
                                                       compose(Function.<C, F<D, F<E, F<F$, F<G, H>>>>>constant(),
                                                               compose(Function.<D, F<E, F<F$, F<G, H>>>>constant(),
                                                                       compose(Function.<E, F<F$, F<G, H>>>constant(),
                                                                               compose(Function.<F$, F<G, H>>constant(),
                                                                                       Function.<G, H>constant())))))))));
  }

  /**
   * An arbitrary for function-8.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param cg A coarbitrary for the part of the domain of the function.
   * @param ch A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Arbitrary<F8<A, B, C, D, E, F$, G, H, I>> arbF8(final Coarbitrary<A> ca,
                                                                                             final Coarbitrary<B> cb,
                                                                                             final Coarbitrary<C> cc,
                                                                                             final Coarbitrary<D> cd,
                                                                                             final Coarbitrary<E> ce,
                                                                                             final Coarbitrary<F$> cf,
                                                                                             final Coarbitrary<G> cg,
                                                                                             final Coarbitrary<H> ch,
                                                                                             final Arbitrary<I> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, arbF(ch, a)))))))).gen.map(
        Function.<A, B, C, D, E, F$, G, H, I>uncurryF8()));
  }

  /**
   * An arbitrary for function-8.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Arbitrary<F8<A, B, C, D, E, F$, G, H, I>> arbF8Invariant(
      final Arbitrary<I> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G, H, I>uncurryF8(),
                                       compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>constant(),
                                               compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>constant(),
                                                       compose(Function.<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>constant(),
                                                               compose(
                                                                   Function.<D, F<E, F<F$, F<G, F<H, I>>>>>constant(),
                                                                   compose(Function.<E, F<F$, F<G, F<H, I>>>>constant(),
                                                                           compose(
                                                                               Function.<F$, F<G, F<H, I>>>constant(),
                                                                               compose(Function.<G, F<H, I>>constant(),
                                                                                       Function.<H, I>constant()))))))))));
  }

  /**
   * An arbitrary implementation for boolean values.
   */
  public static final Arbitrary<Boolean> arbBoolean = arbitrary(elements(true, false));

  /**
   * An arbitrary implementation for integer values.
   */
  public static final Arbitrary<Integer> arbInteger = arbitrary(sized(i -> choose(-i, i)));

  /**
   * An arbitrary implementation for integer values that checks boundary values <code>(0, 1, -1,
   * max, min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link
   * #arbInteger} the remainder of the time (93%).
   */
  public static final Arbitrary<Integer> arbIntegerBoundaries = arbitrary(sized(i -> frequency(list(p(1, value(0)),
                        p(1, value(1)),
                        p(1, value(-1)),
                        p(1, value(Integer.MAX_VALUE)),
                        p(1, value(Integer.MIN_VALUE)),
                        p(1, value(Integer.MAX_VALUE - 1)),
                        p(1, value(Integer.MIN_VALUE + 1)),
                        p(93, arbInteger.gen)))));

  /**
   * An arbitrary implementation for long values.
   */
  public static final Arbitrary<Long> arbLong =
      arbitrary(arbInteger.gen.bind(arbInteger.gen, i1 -> i2 -> (long) i1 << 32L & i2));

  /**
   * An arbitrary implementation for long values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbLong}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Long> arbLongBoundaries = arbitrary(sized(i -> frequency(list(p(1, value(0L)),
                        p(1, value(1L)),
                        p(1, value(-1L)),
                        p(1, value(Long.MAX_VALUE)),
                        p(1, value(Long.MIN_VALUE)),
                        p(1, value(Long.MAX_VALUE - 1L)),
                        p(1, value(Long.MIN_VALUE + 1L)),
                        p(93, arbLong.gen)))));

  /**
   * An arbitrary implementation for byte values.
   */
  public static final Arbitrary<Byte> arbByte = arbitrary(arbInteger.gen.map(i -> (byte) i.intValue()));

  /**
   * An arbitrary implementation for byte values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbByte}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Byte> arbByteBoundaries = arbitrary(sized(i -> frequency(list(p(1, value((byte) 0)),
                        p(1, value((byte) 1)),
                        p(1, value((byte) -1)),
                        p(1, value(Byte.MAX_VALUE)),
                        p(1, value(Byte.MIN_VALUE)),
                        p(1, value((byte) (Byte.MAX_VALUE - 1))),
                        p(1, value((byte) (Byte.MIN_VALUE + 1))),
                        p(93, arbByte.gen)))));

  /**
   * An arbitrary implementation for short values.
   */
  public static final Arbitrary<Short> arbShort = arbitrary(arbInteger.gen.map(i -> (short) i.intValue()));

  /**
   * An arbitrary implementation for short values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbShort}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Short> arbShortBoundaries = arbitrary(sized(i -> frequency(list(p(1, value((short) 0)),
                        p(1, value((short) 1)),
                        p(1, value((short) -1)),
                        p(1, value(Short.MAX_VALUE)),
                        p(1, value(Short.MIN_VALUE)),
                        p(1, value((short) (Short.MAX_VALUE - 1))),
                        p(1, value((short) (Short.MIN_VALUE + 1))),
                        p(93, arbShort.gen)))));

  /**
   * An arbitrary implementation for character values.
   */
  public static final Arbitrary<Character> arbCharacter = arbitrary(choose(0, 65536).map(i -> (char) i.intValue()));

  /**
   * An arbitrary implementation for character values that checks boundary values <code>(max, min,
   * max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbCharacter}
   * the remainder of the time (96%).
   */
  public static final Arbitrary<Character> arbCharacterBoundaries = arbitrary(sized(i -> frequency(list(p(1, value(Character.MIN_VALUE)),
                        p(1, value((char) (Character.MIN_VALUE + 1))),
                        p(1, value(Character.MAX_VALUE)),
                        p(1, value((char) (Character.MAX_VALUE - 1))),
                        p(95, arbCharacter.gen)))));

  /**
   * An arbitrary implementation for double values.
   */
  public static final Arbitrary<Double> arbDouble = arbitrary(sized(i -> choose((double) -i, i)));

  /**
   * An arbitrary implementation for double values that checks boundary values <code>(0, 1, -1, max,
   * min, min (normal), NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then
   * generates from {@link #arbDouble} the remainder of the time (91%).
   */
  public static final Arbitrary<Double> arbDoubleBoundaries = arbitrary(sized(i -> frequency(list(p(1, value(0D)),
                        p(1, value(1D)),
                        p(1, value(-1D)),
                        p(1, value(Double.MAX_VALUE)),
                        p(1, value(Double.MIN_VALUE)),
                        p(1, value(Double.NaN)),
                        p(1, value(Double.NEGATIVE_INFINITY)),
                        p(1, value(Double.POSITIVE_INFINITY)),
                        p(1, value(Double.MAX_VALUE - 1D)),
                        p(91, arbDouble.gen)))));

  /**
   * An arbitrary implementation for float values.
   */
  public static final Arbitrary<Float> arbFloat = arbitrary(arbDouble.gen.map(d -> (float) d.doubleValue()));

  /**
   * An arbitrary implementation for float values that checks boundary values <code>(0, 1, -1, max,
   * min, NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then generates from
   * {@link #arbFloat} the remainder of the time (91%).
   */
  public static final Arbitrary<Float> arbFloatBoundaries = arbitrary(sized(i -> frequency(list(p(1, value(0F)),
                        p(1, value(1F)),
                        p(1, value(-1F)),
                        p(1, value(Float.MAX_VALUE)),
                        p(1, value(Float.MIN_VALUE)),
                        p(1, value(Float.NaN)),
                        p(1, value(Float.NEGATIVE_INFINITY)),
                        p(1, value(Float.POSITIVE_INFINITY)),
                        p(1, value(Float.MAX_VALUE - 1F)),
                        p(91, arbFloat.gen)))));

  /**
   * An arbitrary implementation for string values.
   */
  public static final Arbitrary<String> arbString =
      arbitrary(arbList(arbCharacter).gen.map(cs -> asString(cs)));

  /**
   * An arbitrary implementation for string values with characters in the US-ASCII range.
   */
  public static final Arbitrary<String> arbUSASCIIString =
      arbitrary(arbList(arbCharacter).gen.map(cs -> asString(cs.map((F<Character, Character>) c -> (char) (c % 128)))));

  /**
   * An arbitrary implementation for string values with alpha-numeric characters.
   */
  public static final Arbitrary<String> arbAlphaNumString =
      arbitrary(arbList(arbitrary(elements(range(charEnumerator, 'a', 'z').append(
          range(charEnumerator, 'A', 'Z')).append(
          range(charEnumerator, '0', '9')).toArray().array(Character[].class)))).gen.map(asString()));

  /**
   * An arbitrary implementation for string buffer values.
   */
  public static final Arbitrary<StringBuffer> arbStringBuffer =
      arbitrary(arbString.gen.map(s -> new StringBuffer(s)));

  /**
   * An arbitrary implementation for string builder values.
   */
  public static final Arbitrary<StringBuilder> arbStringBuilder =
      arbitrary(arbString.gen.map(s -> new StringBuilder(s)));

  /**
   * Returns an arbitrary implementation for generators.
   *
   * @param aa an arbitrary implementation for the type over which the generator is defined.
   * @return An arbitrary implementation for generators.
   */
  public static <A> Arbitrary<Gen<A>> arbGen(final Arbitrary<A> aa) {
    return arbitrary(sized(i -> {
      if (i == 0)
        return fail();
      else
        return aa.gen.map(a -> value(a)).resize(i - 1);
    }));
  }

  /**
   * Returns an arbitrary implementation for optional values.
   *
   * @param aa an arbitrary implementation for the type over which the optional value is defined.
   * @return An arbitrary implementation for optional values.
   */
  public static <A> Arbitrary<Option<A>> arbOption(final Arbitrary<A> aa) {
    return arbitrary(sized(i -> i == 0 ?
           value(Option.<A>none()) :
           aa.gen.map(new F<A, Option<A>>() {
             public Option<A> f(final A a) {
               return some(a);
             }
           }).resize(i - 1)));
  }

  /**
   * Returns an arbitrary implementation for the disjoint union.
   *
   * @param aa An arbitrary implementation for the type over which one side of the disjoint union is
   *           defined.
   * @param ab An arbitrary implementation for the type over which one side of the disjoint union is
   *           defined.
   * @return An arbitrary implementation for the disjoint union.
   */
  @SuppressWarnings({"unchecked"})
  public static <A, B> Arbitrary<Either<A, B>> arbEither(final Arbitrary<A> aa, final Arbitrary<B> ab) {
    final Gen<Either<A, B>> left = aa.gen.map(a -> left(a));
    final Gen<Either<A, B>> right = ab.gen.map(b -> right(b));
    return arbitrary(oneOf(list(left, right)));
  }

  /**
   * Returns an arbitrary implementation for lists.
   *
   * @param aa An arbitrary implementation for the type over which the list is defined.
   * @return An arbitrary implementation for lists.
   */
  public static <A> Arbitrary<List<A>> arbList(final Arbitrary<A> aa) {
    return arbitrary(listOf(aa.gen));
  }

  /**
   * Returns an arbitrary implementation for streams.
   *
   * @param aa An arbitrary implementation for the type over which the stream is defined.
   * @return An arbitrary implementation for streams.
   */
  public static <A> Arbitrary<Stream<A>> arbStream(final Arbitrary<A> aa) {
    return arbitrary(arbList(aa).gen.map((final List<A> as) -> as.toStream()));
  }

  /**
   * Returns an arbitrary implementation for arrays.
   *
   * @param aa An arbitrary implementation for the type over which the array is defined.
   * @return An arbitrary implementation for arrays.
   */
  public static <A> Arbitrary<Array<A>> arbArray(final Arbitrary<A> aa) {
    return arbitrary(arbList(aa).gen.map((final List<A> as) -> as.toArray()));
  }

  /**
   * Returns an arbitrary implementation for throwables.
   *
   * @param as An arbitrary used for the throwable message.
   * @return An arbitrary implementation for throwables.
   */
  public static Arbitrary<Throwable> arbThrowable(final Arbitrary<String> as) {
    return arbitrary(as.gen.map(msg -> new Throwable(msg)));
  }

  /**
   * An arbitrary implementation for throwables.
   */
  public static final Arbitrary<Throwable> arbThrowable = arbThrowable(arbString);

  // BEGIN java.util

  /**
   * Returns an arbitrary implementation for array lists.
   *
   * @param aa An arbitrary implementation for the type over which the array list is defined.
   * @return An arbitrary implementation for array lists.
   */
  public static <A> Arbitrary<ArrayList<A>> arbArrayList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new ArrayList<>(a.toCollection())));
  }

  /**
   * An arbitrary implementation for bit sets.
   */
  public static final Arbitrary<BitSet> arbBitSet =
      arbitrary(arbList(arbBoolean).gen.map(bs -> {
        final BitSet s = new BitSet(bs.length());
        bs.zipIndex().foreach(new Effect<P2<Boolean, Integer>>() {
          public void e(final P2<Boolean, Integer> bi) {
            s.set(bi._2(), bi._1());
          }
        });
        return s;
      }));

  /**
   * An arbitrary implementation for calendars.
   */
  public static final Arbitrary<Calendar> arbCalendar = arbitrary(arbLong.gen.map(i -> {
    final Calendar c = Calendar.getInstance();
    c.setTimeInMillis(i);
    return c;
  }));

  /**
   * An arbitrary implementation for dates.
   */
  public static final Arbitrary<Date> arbDate = arbitrary(arbLong.gen.map(i -> new Date(i)));

  /**
   * Returns an arbitrary implementation for a Java enumeration.
   *
   * @param clazz The type of enum to return an arbtrary of.
   * @return An arbitrary for instances of the supplied enum type.
   */
  public static <A extends Enum<A>> Arbitrary<A> arbEnumValue(final Class<A> clazz) {
    return arbitrary(Gen.elements(clazz.getEnumConstants()));
  }

  /**
   * Returns an arbitrary implementation for enum maps.
   *
   * @param ak An arbitrary implementation for the type over which the enum map's keys are defined.
   * @param av An arbitrary implementation for the type over which the enum map's values are
   *           defined.
   * @return An arbitrary implementation for enum maps.
   */
  public static <K extends Enum<K>, V> Arbitrary<EnumMap<K, V>> arbEnumMap(final Arbitrary<K> ak,
                                                                           final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new EnumMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for enum sets.
   *
   * @param aa An arbitrary implementation for the type over which the enum set is defined.
   * @return An arbitrary implementation for enum sets.
   */
  public static <A extends Enum<A>> Arbitrary<EnumSet<A>> arbEnumSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map((F<Array<A>, EnumSet<A>>) a -> copyOf(a.toCollection())));
  }

  /**
   * An arbitrary implementation for gregorian calendars.
   */
  public static final Arbitrary<GregorianCalendar> arbGregorianCalendar =
      arbitrary(arbLong.gen.map(i -> {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(i);
        return c;
      }));

  /**
   * Returns an arbitrary implementation for hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the hash map's keys are defined.
   * @param av An arbitrary implementation for the type over which the hash map's values are
   *           defined.
   * @return An arbitrary implementation for hash maps.
   */
  public static <K, V> Arbitrary<HashMap<K, V>> arbHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new HashMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Arbitrary<HashSet<A>> arbHashSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new HashSet<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for hash tables.
   *
   * @param ak An arbitrary implementation for the type over which the hash table's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the hash table's values are
   *           defined.
   * @return An arbitrary implementation for hash tables.
   */
  public static <K, V> Arbitrary<Hashtable<K, V>> arbHashtable(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbList(ak).gen.bind(arbList(av).gen, ks -> new F<List<V>, Hashtable<K, V>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public Hashtable<K, V> f(final List<V> vs) {
        final Hashtable<K, V> t = new Hashtable<>();

        ks.zip(vs).foreach(new Effect<P2<K, V>>() {
          public void e(final P2<K, V> kv) {
            t.put(kv._1(), kv._2());
          }
        });

        return t;
      }
    }));
  }

  /**
   * Returns an arbitrary implementation for identity hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the identity hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the identity hash map's values
   *           are defined.
   * @return An arbitrary implementation for identity hash maps.
   */
  public static <K, V> Arbitrary<IdentityHashMap<K, V>> arbIdentityHashMap(final Arbitrary<K> ak,
                                                                           final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new IdentityHashMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for linked hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the linked hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the linked hash map's values are
   *           defined.
   * @return An arbitrary implementation for linked hash maps.
   */
  public static <K, V> Arbitrary<LinkedHashMap<K, V>> arbLinkedHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new LinkedHashMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Arbitrary<LinkedHashSet<A>> arbLinkedHashSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedHashSet<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for linked lists.
   *
   * @param aa An arbitrary implementation for the type over which the linked list is defined.
   * @return An arbitrary implementation for linked lists.
   */
  public static <A> Arbitrary<LinkedList<A>> arbLinkedList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedList<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for priority queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority queue is defined.
   * @return An arbitrary implementation for priority queues.
   */
  public static <A> Arbitrary<PriorityQueue<A>> arbPriorityQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new PriorityQueue<>(a.toCollection())));
  }

  /**
   * An arbitrary implementation for properties.
   */
  public static final Arbitrary<Properties> arbProperties =
      arbitrary(arbHashtable(arbString, arbString).gen.map(ht -> {
        final Properties p = new Properties();

        for (final String k : ht.keySet()) {
          p.setProperty(k, ht.get(k));
        }

        return p;
      }));

  /**
   * Returns an arbitrary implementation for stacks.
   *
   * @param aa An arbitrary implementation for the type over which the stack is defined.
   * @return An arbitrary implementation for stacks.
   */
  public static <A> Arbitrary<Stack<A>> arbStack(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> {
      final Stack<A> s = new Stack<>();
      s.addAll(a.toCollection());
      return s;
    }));
  }

  /**
   * Returns an arbitrary implementation for tree maps.
   *
   * @param ak An arbitrary implementation for the type over which the tree map's keys are defined.
   * @param av An arbitrary implementation for the type over which the tree map's values are
   *           defined.
   * @return An arbitrary implementation for tree maps.
   */
  public static <K, V> Arbitrary<TreeMap<K, V>> arbTreeMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new TreeMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for tree sets.
   *
   * @param aa An arbitrary implementation for the type over which the tree set is defined.
   * @return An arbitrary implementation for tree sets.
   */
  public static <A> Arbitrary<TreeSet<A>> arbTreeSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new TreeSet<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for vectors.
   *
   * @param aa An arbitrary implementation for the type over which the vector is defined.
   * @return An arbitrary implementation for vectors.
   */
  @SuppressWarnings({"UseOfObsoleteCollectionType"})
  public static <A> Arbitrary<Vector<A>> arbVector(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new Vector<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for weak hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the weak hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the weak hash map's values are
   *           defined.
   * @return An arbitrary implementation for weak hash maps.
   */
  public static <K, V> Arbitrary<WeakHashMap<K, V>> arbWeakHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new WeakHashMap<>(ht)));
  }

  // END java.util

  // BEGIN java.util.concurrent

  /**
   * Returns an arbitrary implementation for array blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the array blocking queue is
   *           defined.
   * @return An arbitrary implementation for array blocking queues.
   */
  public static <A> Arbitrary<ArrayBlockingQueue<A>> arbArrayBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.bind(arbInteger.gen, arbBoolean.gen,
            a -> capacity -> new F<Boolean, ArrayBlockingQueue<A>>() {
              public ArrayBlockingQueue<A> f(final Boolean fair) {
                return new ArrayBlockingQueue<>(a.length() + abs(capacity),
                                                 fair, a.toCollection());
              }
            }));
  }

  /**
   * Returns an arbitrary implementation for concurrent hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the concurrent hash map's keys
   *           are defined.
   * @param av An arbitrary implementation for the type over which the concurrent hash map's values
   *           are defined.
   * @return An arbitrary implementation for concurrent hash maps.
   */
  public static <K, V> Arbitrary<ConcurrentHashMap<K, V>> arbConcurrentHashMap(final Arbitrary<K> ak,
                                                                               final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ht -> new ConcurrentHashMap<>(ht)));
  }

  /**
   * Returns an arbitrary implementation for concurrent linked queues.
   *
   * @param aa An arbitrary implementation for the type over which the concurrent linked queue is
   *           defined.
   * @return An arbitrary implementation for concurrent linked queues.
   */
  public static <A> Arbitrary<ConcurrentLinkedQueue<A>> arbConcurrentLinkedQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new ConcurrentLinkedQueue<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array lists.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array list is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array lists.
   */
  public static <A> Arbitrary<CopyOnWriteArrayList<A>> arbCopyOnWriteArrayList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new CopyOnWriteArrayList<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array sets.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array set is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array sets.
   */
  public static <A> Arbitrary<CopyOnWriteArraySet<A>> arbCopyOnWriteArraySet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new CopyOnWriteArraySet<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for delay queues.
   *
   * @param aa An arbitrary implementation for the type over which the delay queue is defined.
   * @return An arbitrary implementation for delay queues.
   */
  public static <A extends Delayed> Arbitrary<DelayQueue<A>> arbDelayQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new DelayQueue<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for linked blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the linked blocking queue is
   *           defined.
   * @return An arbitrary implementation for linked blocking queues.
   */
  public static <A> Arbitrary<LinkedBlockingQueue<A>> arbLinkedBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedBlockingQueue<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Arbitrary<PriorityBlockingQueue<A>> arbPriorityBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new PriorityBlockingQueue<>(a.toCollection())));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Arbitrary<SynchronousQueue<A>> arbSynchronousQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.bind(arbBoolean.gen, a -> new F<Boolean, SynchronousQueue<A>>() {
      public SynchronousQueue<A> f(final Boolean fair) {
        final SynchronousQueue<A> q = new SynchronousQueue<>(fair);
        q.addAll(a.toCollection());
        return q;
      }
    }));
  }

  // END java.util.concurrent

  // BEGIN java.sql

  /**
   * An arbitrary implementation for SQL dates.
   */
  public static final Arbitrary<java.sql.Date> arbSQLDate = arbitrary(arbLong.gen.map(i -> new java.sql.Date(i)));

  /**
   * An arbitrary implementation for SQL times.
   */
  public static final Arbitrary<Time> arbTime = arbitrary(arbLong.gen.map(i -> new Time(i)));

  /**
   * An arbitrary implementation for SQL time stamps.
   */
  public static final Arbitrary<Timestamp> arbTimestamp = arbitrary(arbLong.gen.map(i -> new Timestamp(i)));

  // END java.sql

  // BEGIN java.math

  /**
   * An arbitrary implementation for big integers.
   */
  public static final Arbitrary<BigInteger> arbBigInteger =
      arbitrary(arbArray(arbByte).gen.bind(arbByte.gen, a -> new F<Byte, BigInteger>() {
        public BigInteger f(final Byte b) {
          final byte[] x = new byte[a.length() + 1];

          for (int i = 0; i < a.array().length; i++) {
            x[i] = a.get(i);
          }

          x[a.length()] = b;

          return new BigInteger(x);
        }
      }));

  /**
   * An arbitrary implementation for big decimals.
   */
  public static final Arbitrary<BigDecimal> arbBigDecimal =
      arbitrary(arbBigInteger.gen.map(i -> new BigDecimal(i)));

  // END java.math

  /**
   * An arbitrary implementation for locales.
   */
  public static final Arbitrary<Locale> arbLocale = arbitrary(elements(getAvailableLocales()));

  /**
   * Returns an arbitrary implementation for product-1 values.
   *
   * @param aa An arbitrary implementation for the type over which the product-1 is defined.
   * @return An arbitrary implementation for product-1 values.
   */
  public static <A> Arbitrary<P1<A>> arbP1(final Arbitrary<A> aa) {
    return arbitrary(aa.gen.map(a -> p(a)));
  }

  /**
   * Returns an arbitrary implementation for product-2 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-2 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-2 is
   *           defined.
   * @return An arbitrary implementation for product-2 values.
   */
  public static <A, B> Arbitrary<P2<A, B>> arbP2(final Arbitrary<A> aa, final Arbitrary<B> ab) {
    return arbitrary(aa.gen.bind(ab.gen, a -> new F<B, P2<A, B>>() {
      public P2<A, B> f(final B b) {
        return p(a, b);
      }
    }));
  }

  /**
   * Returns an arbitrary implementation for product-3 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @return An arbitrary implementation for product-3 values.
   */
  public static <A, B, C> Arbitrary<P3<A, B, C>> arbP3(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                       final Arbitrary<C> ac) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, a -> new F<B, F<C, P3<A, B, C>>>() {
      public F<C, P3<A, B, C>> f(final B b) {
        return new F<C, P3<A, B, C>>() {
          public P3<A, B, C> f(final C c) {
            return p(a, b, c);
          }
        };
      }
    }));
  }

  /**
   * Returns an arbitrary implementation for product-4 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @return An arbitrary implementation for product-4 values.
   */
  public static <A, B, C, D> Arbitrary<P4<A, B, C, D>> arbP4(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                             final Arbitrary<C> ac, final Arbitrary<D> ad) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, a -> b -> new F<C, F<D, P4<A, B, C, D>>>() {
      public F<D, P4<A, B, C, D>> f(final C c) {
        return new F<D, P4<A, B, C, D>>() {
          public P4<A, B, C, D> f(final D d) {
            return p(a, b, c, d);
          }
        };
      }
    }));
  }

  /**
   * Returns an arbitrary implementation for product-5 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @return An arbitrary implementation for product-5 values.
   */
  public static <A, B, C, D, E> Arbitrary<P5<A, B, C, D, E>> arbP5(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                   final Arbitrary<C> ac, final Arbitrary<D> ad,
                                                                   final Arbitrary<E> ae) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, a -> b -> c -> d -> new F<E, P5<A, B, C, D, E>>() {
      public P5<A, B, C, D, E> f(final E e) {
        return p(a, b, c, d, e);
      }
    }));
  }

  /**
   * Returns an arbitrary implementation for product-6 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @return An arbitrary implementation for product-6 values.
   */
  public static <A, B, C, D, E, F$> Arbitrary<P6<A, B, C, D, E, F$>> arbP6(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                           final Arbitrary<C> ac, final Arbitrary<D> ad,
                                                                           final Arbitrary<E> ae,
                                                                           final Arbitrary<F$> af) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen,
            a -> new F<B, F<C, F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>>>() {
              public F<C, F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>> f(final B b) {
                return new F<C, F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>>() {
                  public F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>> f(final C c) {
                    return new F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>() {
                      public F<E, F<F$, P6<A, B, C, D, E, F$>>> f(final D d) {
                        return new F<E, F<F$, P6<A, B, C, D, E, F$>>>() {
                          public F<F$, P6<A, B, C, D, E, F$>> f(final E e) {
                            return new F<F$, P6<A, B, C, D, E, F$>>() {
                              public P6<A, B, C, D, E, F$> f(final F$ f) {
                                return p(a, b, c, d, e, f);
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            }));
  }

  /**
   * Returns an arbitrary implementation for product-7 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ag An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @return An arbitrary implementation for product-7 values.
   */
  public static <A, B, C, D, E, F$, G> Arbitrary<P7<A, B, C, D, E, F$, G>> arbP7(final Arbitrary<A> aa,
                                                                                 final Arbitrary<B> ab,
                                                                                 final Arbitrary<C> ac,
                                                                                 final Arbitrary<D> ad,
                                                                                 final Arbitrary<E> ae,
                                                                                 final Arbitrary<F$> af,
                                                                                 final Arbitrary<G> ag) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen, ag.gen,
            a -> new F<B, F<C, F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>>>() {
              public F<C, F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>> f(final B b) {
                return new F<C, F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>>() {
                  public F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>> f(final C c) {
                    return new F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>() {
                      public F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>> f(final D d) {
                        return new F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>() {
                          public F<F$, F<G, P7<A, B, C, D, E, F$, G>>> f(final E e) {
                            return new F<F$, F<G, P7<A, B, C, D, E, F$, G>>>() {
                              public F<G, P7<A, B, C, D, E, F$, G>> f(final F$ f) {
                                return new F<G, P7<A, B, C, D, E, F$, G>>() {
                                  public P7<A, B, C, D, E, F$, G> f(final G g) {
                                    return p(a, b, c, d, e, f, g);
                                  }
                                };
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            }));
  }

  /**
   * Returns an arbitrary implementation for product-8 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ag An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ah An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @return An arbitrary implementation for product-8 values.
   */
  public static <A, B, C, D, E, F$, G, H> Arbitrary<P8<A, B, C, D, E, F$, G, H>> arbP8(final Arbitrary<A> aa,
                                                                                       final Arbitrary<B> ab,
                                                                                       final Arbitrary<C> ac,
                                                                                       final Arbitrary<D> ad,
                                                                                       final Arbitrary<E> ae,
                                                                                       final Arbitrary<F$> af,
                                                                                       final Arbitrary<G> ag,
                                                                                       final Arbitrary<H> ah) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen, ag.gen, ah.gen,
            a -> new F<B, F<C, F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>>>() {
              public F<C, F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>> f(
                  final B b) {
                return new F<C, F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>>() {
                  public F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>> f(
                      final C c) {
                    return new F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>() {
                      public F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>> f(
                          final D d) {
                        return new F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>() {
                          public F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>> f(final E e) {
                            return new F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>() {
                              public F<G, F<H, P8<A, B, C, D, E, F$, G, H>>> f(final F$ f) {
                                return new F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>() {
                                  public F<H, P8<A, B, C, D, E, F$, G, H>> f(final G g) {
                                    return new F<H, P8<A, B, C, D, E, F$, G, H>>() {
                                      public P8<A, B, C, D, E, F$, G, H> f(final H h) {
                                        return p(a, b, c, d, e, f, g, h);
                                      }
                                    };
                                  }
                                };
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            }));
  }
}
