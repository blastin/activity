package br.project.knin.activity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class ActivityProxyTest {

    private static final Activity<Integer, String, String> ACTIVITY_PROXY = new ActivityImpl.ActivityProxy<>(null);

    @Test
    void decision() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.decision(null));
    }

    @Test
    void action() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.action((Function<? super String, ? super String>) null));
    }

    @Test
    void testAction() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.action((FunctionZ<? super String, ? super String, ?>) null));
    }

    @Test
    void otherwise() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.otherwise((Produce<? extends Integer>) null));
    }

    @Test
    void testOtherwise() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.otherwise((Function<? super String, ? extends Integer>) null));
    }

    @Test
    void testChannel() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.channel(null));
    }

    @Test
    void channelWithFunctionNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.channel(null, null));
    }

    @Test
    void channelWithoutFunctionNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.channel((s, s2) -> 1, null));
    }

    @Test
    void otherwiseChannelWithFunctionNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.otherwiseChannel(null, null));
    }


    @Test
    void otherwiseChannelWithoutFunctionNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.otherwiseChannel(Integer::parseInt, null));
    }

    @Test
    void exit() {
        Assertions.assertThrows(NullPointerException.class, () -> ACTIVITY_PROXY.exit(null));
    }

}
