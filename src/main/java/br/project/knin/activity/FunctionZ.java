package br.project.knin.activity;

/**
 * @param <A>
 * @param <B>
 * @param <C>
 * @author Jefferson Lisboa (lisboa.jeff@gmail.com)
 */
@FunctionalInterface
public interface FunctionZ<A, B, C> {

    C apply(final A a, final B b);

}
