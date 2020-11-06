package br.project.knin.activity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Activity is an API model based on the UML activity diagram concepts to facilitate the development of use case actions
 *
 * @param <C> represent contract
 * @param <E> represent entry initial
 * @param <V> represente a value of flux
 * @author Jefferson Lisboa (lisboa.jeff@gmail.com)
 */
public interface Activity<C, E, V> {

    /**
     * The contract specifies the output object in the event that no action is taken
     *
     * @param c   instance of contract C type
     * @param <C> C Type
     * @return new instance of ActivityBuilder C Type
     */
    static <C> ActivityBuilder<C> contract(final C c) {
        Objects.requireNonNull(c);
        return new ActivityBuilder<>(c);
    }

    /**
     * ActivityBuilder inicialize a new Instance of Activity
     *
     * @param <C>
     */
    class ActivityBuilder<C> {

        private ActivityBuilder(final C c) {
            this.c = c;
        }

        private final C c;

        /**
         * principal Method of ActivityBuilder
         *
         * @param e   type object of entry
         * @param <E> E generic Type
         * @return new instance of Activity
         */
        public <E> Activity<C, E, E> entry(final E e) {
            Objects.requireNonNull(e);
            return ActivityImpl.create(c, e, e);
        }

    }

    /**
     * Operation necessary to make a decision of flux continue
     *
     * @param predicate instance of predicate
     * @return case decision is satisfy the self activity is returned, otherwise activity exit will be returned
     */
    Activity<C, E, V> decision(final Predicate<? super V> predicate);

    /**
     * Action opera uma função de mapeamento A -> B, para transformar uma computação. Está transformação de computação
     * está relacionada com conceitos básicos do diagrama UML de atividade.
     *
     * @param function é uma instância cuja interface funcional seja Function. Especifica
     *                 para transformar um V (value) no tipo genérico W
     * @param <W>      novo tipo de valor encapsulado
     * @return caso novo valor encapsulado não seja nulo, retornará uma nova atividade, caso contrário uma atividade
     * que represente o estado de fim de uma ação.
     */
    <W> Activity<C, E, W> action(final Function<? super V, ? extends W> function);

    /**
     * Action opera uma função de mapeamento (A,B) -> C, para transformar uma computação.
     * Está transformação de computação está relacionada com conceitos básicos do diagrama UML de atividade.
     *
     * @param function é uma instância cuja interface funcional seja Function. Especifica
     *                 para transformar um E (entryObject) e V (value) no tipo genérico W
     * @param <W>      novo tipo de valor encapsulado
     * @return caso novo valor encapsulado não seja nulo, retornará uma nova atividade, caso contrário uma atividade
     * que represente o estado de fim de uma ação.
     */
    <W> Activity<C, E, W> action(final FunctionZ<? super E, ? super V, ? extends W> function);

    /**
     * Otherwise opera uma função que produz uma saída para um fluxo de atividade alternativo. Após ser especificado
     * o primeiro fim de fluxo, os próximos não serão processados.
     *
     * @param produce uma instância cuja interface funcional seja Produce
     * @return caso novo objeto de contrato não seja nulo, retornará uma nova atividade com objeto de fim de contrato
     * alterado, caso contrário, a mesma atividade.
     */
    Activity<C, E, V> otherwise(final Produce<? extends C> produce);

    /**
     * Otherwise opera uma função que produz uma saída para um fluxo de atividade alternativo. Após ser especificado
     * o primeiro fim de fluxo, os próximos não serão processados.
     *
     * @param function uma instância
     * @return caso novo objeto de contrato não seja nulo, retornará uma nova atividade com objeto de fim de contrato
     * alterado, caso contrário, a mesma atividade.
     */
    Activity<C, E, V> otherwise(final Function<? super E, ? extends C> function);

    /**
     * Channel é uma operação necessária para encaminhar uma mensagem a um canal quando valor encapsulado é diferente
     * de nulo.
     *
     * @param channel é uma instância da interface Channel do tipo V
     * @return mesma instância de interface
     */
    Activity<C, E, V> channel(final Channel<? super V> channel);

    /**
     * Operação para encaminhar uma mensagem mapeada em um canal
     *
     * @param function instância de uma funçãoZ
     * @param channel  instância de uma canal
     * @param <W>      E,V -> W é um mapeamento que transforma entrada e valor em W
     * @return mesma instância de atividade
     */
    <W> Activity<C, E, V> channel(final FunctionZ<? super E, ? super V, ? extends W> function, Channel<? super W> channel);

    /**
     * otherwise channel é uma operação necessária para encaminhar uma mensagem a um canal quando valor encapsulado é nulo,
     * ou seja, quando ocorre fim de fluxo de atividade.
     *
     * @param function é uma instância da interface Function
     * @param channel  é uma instância da interface Channel do tipo V
     * @return mesma instância de interface
     */
    Activity<C, E, V> otherwiseChannel(final Function<? super E, ? extends C> function, final Channel<? super C> channel);

    /**
     * exit é uma operação cuja proposta é fim de fluxo de uma atividade.
     *
     * @param function instância de função cuja interface seja Function. Tem como proposta um mapeamento A -> B.
     *                 Sendo A (value)  e C (contract)
     * @return retorna objeto final de contrato. Caso ocorra fim de atividade em alguma operação otherwise, retornará
     * objeto de contrato estabelecido em fluxo alternativo.
     */
    C exit(final Function<? super V, ? extends C> function);

}
