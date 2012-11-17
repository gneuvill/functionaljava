package fj.data;

import fj.F;
import fj.Unit;

import java.io.IOException;
import java.io.Reader;

/**
 * IO monad for processing files, with main methods {@link InputOutput#enumFileLines(java.io.File, Option},
 * {@link InputOutput#enumFileChars(java.io.File, Option } and {@link InputOutput#enumFileCharChunks(java.io.File, Option }
 * (the latter one is the fastest as char chunks read from the file are directly passed to the iteratee
 * without indirection in between).
 *
 * @author Martin Grotzke
 *
 * @param <A> the type of the result produced by the wrapped iteratee
 */
public interface IO<A> {
  F<Reader, IO<Unit>> closeReader = InputOutput::closeReader;

  A run() throws IOException;

  <B> IO<B> map(final F<A, B> f) default {
      return () -> f.f(IO.this.run());
  }

  <B> IO<B> bind(final F<A, IO<B>> f) default {
      return () -> f.f(IO.this.run()).run();
  }

}