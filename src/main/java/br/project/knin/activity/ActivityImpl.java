package br.project.knin.activity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

final class ActivityImpl<C, E, V> implements Activity<C, E, V> {

    /**
     * @param activity activity that should'be proxied
     * @param <C>      type Generic to type of contract
     * @param <E>      type Generic to entry value
     * @param <V>      type Generic to value
     * @return new instance of activity with proxy validation
     */
    private static <C, E, V> Activity<C, E, V> proxied(final Activity<C, E, V> activity) {
        return new ActivityProxy<>(activity);
    }

    static final class ActivityProxy<C, E, V> implements Activity<C, E, V> {

        ActivityProxy(final Activity<C, E, V> activity) {
            this.activity = activity;
        }

        private final Activity<C, E, V> activity;

        @Override
        public Activity<C, E, V> decision(final Predicate<? super V> predicate) {
            Objects.requireNonNull(predicate, "Predicado em 'decisão' não deve ser nulo");
            return activity.decision(predicate);
        }

        @Override
        public <W> Activity<C, E, W> action(final Function<? super V, ? extends W> function) {
            Objects.requireNonNull(function, "Função em 'ação' não deve ser nula");
            return activity.action(function);
        }

        @Override
        public <W> Activity<C, E, W> action(final FunctionZ<? super E, ? super V, ? extends W> function) {
            Objects.requireNonNull(function, "FunçãoZ em 'ação' não deve ser nula");
            return activity.action(function);
        }

        @Override
        public Activity<C, E, V> otherwise(final Produce<? extends C> produce) {
            Objects.requireNonNull(produce, "Produção em 'otherwise' não deve ser nula");
            return activity.otherwise(produce);
        }

        @Override
        public Activity<C, E, V> otherwise(final Function<? super E, ? extends C> function) {
            Objects.requireNonNull(function, "Função em 'otherwise' não deve ser nula");
            return activity.otherwise(function);
        }

        @Override
        public Activity<C, E, V> channel(final Channel<? super V> channel) {
            Objects.requireNonNull(channel, "Canal em 'channel' não deve ser nula");
            return activity.channel(channel);
        }

        @Override
        public <W> Activity<C, E, V> channel(final FunctionZ<? super E, ? super V, ? extends W> function, final Channel<? super W> channel) {
            Objects.requireNonNull(function, "Função em 'channel' não deve ser nula");
            Objects.requireNonNull(channel, "Canal em 'channel' não deve ser nula");
            return activity.channel(function, channel);
        }

        @Override
        public Activity<C, E, V> otherwiseChannel(final Function<? super E, ? extends C> function, final Channel<? super C> channel) {
            Objects.requireNonNull(function, "Função em 'otherwise channel' não deve ser nula");
            Objects.requireNonNull(channel, "Canal em 'otherwise channel' não deve ser nula");
            return activity.otherwiseChannel(function, channel);
        }

        @Override
        public C exit(final Function<? super V, ? extends C> function) {
            Objects.requireNonNull(function, "Função em 'exit' não deve ser nula");
            return activity.exit(function);
        }

    }

    public static <C, E, V> Activity<C, E, V> create(final C c, final E e, final V v) {
        return create(c, e, v, false);
    }

    private static <C, E, V> Activity<C, E, V> create(final C c, final E e, final V v, final boolean isOver) {
        return proxied(new ActivityImpl<>(c, e, v, isOver));
    }

    private static <T, E, V> Activity<T, E, V> exitActivity(final T t) {
        return create(t, null, null, true);
    }

    private ActivityImpl(final C contract, final E entryObject, final V value, final boolean isOver) {

        this.contract = contract;

        this.entryObject = entryObject;

        this.value = value;

        this.isOver = isOver;

        exitActivityForFail = proxied(new ActivityImpl<>(contract, entryObject));

    }

    private ActivityImpl(final C contract, final E entryObject) {

        this.contract = contract;

        this.entryObject = entryObject;

        value = null;

        isOver = true;

        exitActivityForFail = this;

    }

    private final C contract;

    private final E entryObject;

    private final V value;

    private final boolean isOver;

    private final Activity<C, E, V> exitActivityForFail;

    @Override
    public Activity<C, E, V> decision(final Predicate<? super V> predicate) {

        if (value == null) return exitActivityForFail;

        final boolean isTrue = predicate.test(value);

        if (isTrue) return this;

        return create(contract, entryObject, null);

    }

    @Override
    public <W> Activity<C, E, W> action(final Function<? super V, ? extends W> function) {

        if (value == null) return exitActivity(contract);

        final W w = function.apply(value);

        return create(contract, entryObject, w);

    }

    @Override
    public <W> Activity<C, E, W> action(final FunctionZ<? super E, ? super V, ? extends W> function) {

        if (value == null) return exitActivity(contract);

        final W w = function.apply(entryObject, value);

        return create(contract, entryObject, w);

    }

    @Override
    public Activity<C, E, V> otherwise(final Produce<? extends C> produce) {

        if (isOver || value != null) return this;

        final C c = produce.get();

        if (c == null) return this;

        return create(c, entryObject, null, true);

    }

    @Override
    public Activity<C, E, V> otherwise(final Function<? super E, ? extends C> function) {

        if (isOver || value != null) return this;

        final C c = function.apply(entryObject);

        if (c == null) return this;

        return create(c, entryObject, null, true);

    }

    @Override
    public Activity<C, E, V> channel(final Channel<? super V> channel) {

        if (value == null) return exitActivityForFail;

        channel.commit(value);

        return this;

    }

    @Override
    public <W> Activity<C, E, V> channel(final FunctionZ<? super E, ? super V, ? extends W> function, final Channel<? super W> channel) {

        if (value == null) return exitActivityForFail;

        final W w = function.apply(entryObject, value);

        if (w != null) channel.commit(w);

        return this;

    }

    @Override
    public Activity<C, E, V> otherwiseChannel(final Function<? super E, ? extends C> function, final Channel<? super C> channel) {

        if (isOver || value != null) return this;

        final C c = function.apply(entryObject);

        if (c == null) return this;

        channel.commit(c);

        return exitActivityForFail;

    }

    @Override
    public C exit(final Function<? super V, ? extends C> function) {

        if (value == null) return contract;

        final C c = function.apply(value);

        if (c == null) return contract;

        return c;

    }
}

