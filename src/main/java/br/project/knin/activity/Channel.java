package br.project.knin.activity;

/**
 * @param <E> Type Generic E
 * @author Jefferson Lisboa (lisboa.jeff@gmail.com)
 */
@FunctionalInterface
public interface Channel<E> {

    void commit(final E e);

}
