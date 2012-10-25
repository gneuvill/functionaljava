package fj.data;

import fj.F;
import fj.Function;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import static fj.Bottom.errorF;
import static fj.Function.constant;
import static fj.Function.partialApply2;

public final class InputOutput {
    static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static IO<Unit> closeReader(final Reader r) {
        return () -> {
            r.close();
            return Unit.unit();
        };
    }

    /**
     * An IO monad that reads lines from the given file (using a {@link java.io.BufferedReader}) and passes
     * lines to the provided iteratee. May not be suitable for files with very long
     * lines, consider to use {@link #enumFileCharChunks(java.io.File, fj.data.Option, fj.data.Iteratee.IterV)} or {@link #enumFileChars(java.io.File, fj.data.Option, fj.data.Iteratee.IterV)}
     * as an alternative.
     *
     * @param f the file to read, must not be <code>null</code>
     * @param encoding the encoding to use, {@link fj.data.Option#none()} means platform default
     * @param i the iteratee that is fed with lines read from the file
     */
    public static <A> IO<Iteratee.IterV<String, A>> enumFileLines(final File f, final Option<Charset> encoding, final Iteratee.IterV<String, A> i) {
      return bracket(bufferedReader(f, encoding)
              , Function.<BufferedReader, IO<Unit>>vary(IO.closeReader)
              , partialApply2(<A>lineReader(), i));
    }

    /**
   * An IO monad that reads char chunks from the given file and passes them to the given iteratee.
   *
   * @param f the file to read, must not be <code>null</code>
   * @param encoding the encoding to use, {@link fj.data.Option#none()} means platform default
   * @param i the iteratee that is fed with char chunks read from the file
   */
  public static <A> IO<Iteratee.IterV<char[], A>> enumFileCharChunks(final File f, final Option<Charset> encoding, final Iteratee.IterV<char[], A> i) {
    return bracket(fileReader(f, encoding)
            , Function.<Reader, IO<Unit>>vary(IO.closeReader)
            , partialApply2(<A>charChunkReader(), i));
  }

    /**
   * An IO monad that reads char chunks from the given file and passes single chars to the given iteratee.
   *
   * @param f  the file to read, must not be <code>null</code>
   * @param encoding  the encoding to use, {@link fj.data.Option#none()} means platform default
   * @param i the iteratee that is fed with chars read from the file
   */
  public static <A> IO<Iteratee.IterV<Character, A>> enumFileChars(final File f, final Option<Charset> encoding, final Iteratee.IterV<Character, A> i) {
    return bracket(fileReader(f, encoding)
            , Function.<Reader, IO<Unit>>vary(IO.closeReader)
            , partialApply2(<A>charChunkReader2(), i));
  }

    public static IO<BufferedReader> bufferedReader(final File f, final Option<Charset> encoding) {
    return fileReader(f, encoding).map((F<Reader, BufferedReader>) BufferedReader::new);
  }

    public static IO<Reader> fileReader(final File f, final Option<Charset> encoding) {
    return () -> {
      final FileInputStream fis = new FileInputStream(f);
      return encoding.isNone() ? new InputStreamReader(fis) : new InputStreamReader(fis, encoding.some());
    };
  }

    public static final <A, B, C> IO<C> bracket(final IO<A> init, final F<A, IO<B>> fin, final F<A, IO<C>> body) {
    return () -> {
      final A a = init.run();
      try {
        return body.f(a).run();
      } finally {
        fin.f(a);
      }
    };
  }

    public static final <A> IO<A> unit(final A a) {
    return () -> a;
  }

    /**
   * A function that feeds an iteratee with lines read from a {@link java.io.BufferedReader}.
   */
  public static <A> F<BufferedReader, F<Iteratee.IterV<String, A>, IO<Iteratee.IterV<String, A>>>> lineReader() {
    final F<Iteratee.IterV<String, A>, Boolean> isDone =
      new F<Iteratee.IterV<String, A>, Boolean>() {
        final F<P2<A, Iteratee.Input<String>>, P1<Boolean>> done = constant(P.p(true));
        final F<F<Iteratee.Input<String>, Iteratee.IterV<String, A>>, P1<Boolean>> cont = constant(P.p(false));

        @Override
        public Boolean f(final Iteratee.IterV<String, A> i) {
          return i.fold(done, cont)._1();
        }
      };

    return r -> new F<Iteratee.IterV<String, A>, IO<Iteratee.IterV<String, A>>>() {
      final F<P2<A, Iteratee.Input<String>>, P1<Iteratee.IterV<String, A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

      @Override
      public IO<Iteratee.IterV<String, A>> f(final Iteratee.IterV<String, A> it) {
        // use loop instead of recursion because of missing TCO
        return () -> {
          Iteratee.IterV<String, A> i = it;
          while (!isDone.f(i)) {
            final String s = r.readLine();
            if (s == null) { return i; }
            final Iteratee.Input<String> input = Iteratee.Input.el(s);
            final F<F<Iteratee.Input<String>, Iteratee.IterV<String, A>>, P1<Iteratee.IterV<String, A>>> cont = Function.<Iteratee.Input<String>, Iteratee.IterV<String, A>>apply(input).lazy();
            i = i.fold(done, cont)._1();
          }
          return i;
        };
      }
    };
  }

    /**
   * A function that feeds an iteratee with character chunks read from a {@link java.io.Reader}
   * (char[] of size {@link InputOutput#DEFAULT_BUFFER_SIZE}).
   */
  public static <A> F<Reader, F<Iteratee.IterV<char[], A>, IO<Iteratee.IterV<char[], A>>>> charChunkReader() {
    final F<Iteratee.IterV<char[], A>, Boolean> isDone =
      new F<Iteratee.IterV<char[], A>, Boolean>() {
        final F<P2<A, Iteratee.Input<char[]>>, P1<Boolean>> done = constant(P.p(true));
        final F<F<Iteratee.Input<char[]>, Iteratee.IterV<char[], A>>, P1<Boolean>> cont = constant(P.p(false));

        @Override
        public Boolean f(final Iteratee.IterV<char[], A> i) {
          return i.fold(done, cont)._1();
        }
      };

    return r -> new F<Iteratee.IterV<char[], A>, IO<Iteratee.IterV<char[], A>>>() {
      final F<P2<A, Iteratee.Input<char[]>>, P1<Iteratee.IterV<char[], A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

      @Override
      public IO<Iteratee.IterV<char[], A>> f(final Iteratee.IterV<char[], A> it) {
        // use loop instead of recursion because of missing TCO
        return () -> {

          Iteratee.IterV<char[], A> i = it;
          while (!isDone.f(i)) {
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            final int numRead = r.read(buffer);
            if (numRead == -1) { return i; }
            if(numRead < buffer.length) {
              buffer = Arrays.copyOfRange(buffer, 0, numRead);
            }
            final Iteratee.Input<char[]> input = Iteratee.Input.el(buffer);
            final F<F<Iteratee.Input<char[]>, Iteratee.IterV<char[], A>>, P1<Iteratee.IterV<char[], A>>> cont =
                Function.<Iteratee.Input<char[]>, Iteratee.IterV<char[], A>>apply(input).lazy();
            i = i.fold(done, cont)._1();
          }
          return i;
        };
      }
    };
  }

    /**
   * A function that feeds an iteratee with characters read from a {@link java.io.Reader}
   * (chars are read in chunks of size {@link InputOutput#DEFAULT_BUFFER_SIZE}).
   */
  public static <A> F<Reader, F<Iteratee.IterV<Character, A>, IO<Iteratee.IterV<Character, A>>>> charChunkReader2() {
    final F<Iteratee.IterV<Character, A>, Boolean> isDone =
      new F<Iteratee.IterV<Character, A>, Boolean>() {
        final F<P2<A, Iteratee.Input<Character>>, P1<Boolean>> done = constant(P.p(true));
        final F<F<Iteratee.Input<Character>, Iteratee.IterV<Character, A>>, P1<Boolean>> cont = constant(P.p(false));

        @Override
        public Boolean f(final Iteratee.IterV<Character, A> i) {
          return i.fold(done, cont)._1();
        }
      };

    return r -> new F<Iteratee.IterV<Character, A>, IO<Iteratee.IterV<Character, A>>>() {
      final F<P2<A, Iteratee.Input<Character>>, Iteratee.IterV<Character, A>> done = errorF("iteratee is done"); //$NON-NLS-1$

      @Override
      public IO<Iteratee.IterV<Character, A>> f(final Iteratee.IterV<Character, A> it) {
        // use loop instead of recursion because of missing TCO
        return () -> {

          Iteratee.IterV<Character, A> i = it;
          while (!isDone.f(i)) {
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            final int numRead = r.read(buffer);
            if (numRead == -1) { return i; }
            if(numRead < buffer.length) {
              buffer = Arrays.copyOfRange(buffer, 0, numRead);
            }
            for (final char aBuffer : buffer) {
              final Iteratee.Input<Character> input = Iteratee.Input.el(aBuffer);
              final F<F<Iteratee.Input<Character>, Iteratee.IterV<Character, A>>, Iteratee.IterV<Character, A>> cont =
              Function.apply(input);
              i = i.fold(done, cont);
            }
          }
          return i;
        };
      }
    };
  }
}
