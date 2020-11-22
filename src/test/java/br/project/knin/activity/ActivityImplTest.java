package br.project.knin.activity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class ActivityImplTest {

    @Test
    void shouldThrowNullPointerExceptionBecauseContractIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Activity.contract(null));
    }

    @Test
    void shouldThrowNullPointerExceptionBecauseEntryValueIsNull() {

        Assertions.assertThrows(NullPointerException.class, () -> Activity.contract(1).entry(null));

    }

    @Test
    void shouldReturnInitialContractBecausePredicateIsFalse() {

        final long contractValue = -1000L;

        Assertions
                .assertEquals
                        (
                                contractValue,
                                Activity
                                        .contract(contractValue)
                                        .entry(10)
                                        .decision(integer -> integer < 10)
                                        .exit(Long::valueOf)
                        );

    }

    @Test
    void shouldReturnObjectOfContractBecauseActionOfFunctionReturnNullObject() {

        final String initialContract = "contract";

        final String contract = Activity
                .contract(initialContract)
                .entry(1)
                .action(integer -> null)
                .action(o -> "")
                .exit(String::valueOf);

        Assertions.assertEquals(initialContract, contract);

    }

    @Test
    void shouldReturnObjectOfContractBecauseActionOfFunctionZReturnNullObject() {

        final String initialContract = "contract";

        final String contract = Activity
                .contract(initialContract)
                .entry(1)
                .action((integer, integer2) -> null)
                .exit(String::valueOf);

        Assertions.assertEquals(initialContract, contract);

    }

    @Test
    void shouldReturnObjectOfReturnedByOtherwiseBecauseActionOfFunctionReturnNullObject() {

        final int initialContract = 0;

        final int contractOtherwise = 2;

        final int contract = Activity
                .contract(initialContract)
                .entry(1)
                .action(integer -> null)
                .otherwise(() -> contractOtherwise)
                .exit(integer -> 3);

        Assertions.assertEquals(contractOtherwise, contract);

    }

    @Test
    void shouldReturnObjectOfReturnedByOtherwiseOfFunctionBecauseActionReturnNullObject() {

        final int initialContract = 0;

        final int contractOtherwise = 11;

        final int contract = Activity
                .contract(initialContract)
                .entry(1)
                .action((i, j) -> null)
                .otherwise(integer -> integer + 10)
                .exit(integer -> 3);

        Assertions.assertEquals(contractOtherwise, contract);

    }

    @Test
    @DisplayName("otherwise deve retornar null e outro otherwise deve mudar objeto de saida de contrato")
    void otherwiseShouldReturnNullAndNextOtherwiseFunctionShouldReturnFinalObjectOfContract() {

        final List<Object> list = Activity
                .contract(List.of())
                .entry(1)
                .decision(integer -> integer % 2 == 0)
                .otherwise(() -> null)
                .otherwise(integer -> List.of(integer * 2))
                .exit(List::of);

        Assertions
                .assertEquals(1, list.size());

        Assertions
                .assertEquals(2, list.get(0));

    }

    @Test
    @DisplayName("otherwise deve retornar null e outro otherwise deve mudar objeto de saida de contrato")
    void otherwiseFunctionShouldReturnNullAndNextOtherwiseShouldReturnFinalObjectOfContract() {

        final List<Object> list = Activity
                .contract(List.of())
                .entry(1)
                .decision(integer -> integer % 2 == 0)
                .otherwise(integer -> null)
                .otherwise(() -> List.of(20))
                .exit(List::of);

        Assertions
                .assertEquals(1, list.size());

        Assertions
                .assertEquals(20, list.get(0));

    }

    @Test
    void shouldReturnObjectOfInitialContractBecauseFunctionOfExitReturnedNullObject() {

        final int initialContract = 0;

        final Integer contractFinal = Activity
                .contract(initialContract)
                .entry(1)
                .action(Integer::sum)
                .exit(integer -> null);

        Assertions.assertEquals(initialContract, contractFinal);

    }

    @Test
    @DisplayName("Ação deve retornar nulo e decisão não pode ser executada")
    void actionFunctionShouldReturnNullAndDecisionsShouldNotBeExecuted() {

        final int initialContract = 0;

        final int contract = Activity
                .contract(initialContract)
                .entry(1)
                .action(i -> null)
                .decision(o -> {
                    throw new IllegalCallerException("Should not be here");
                })
                .exit(o -> 2);

        Assertions.assertEquals(initialContract, contract);

    }

    @Test
    @DisplayName("Ação deve retornar nulo e decisão não pode ser executada")
    void actionFunctionZShouldReturnNullAndDecisionsShouldNotBeExecuted() {

        final int initialContract = 0;

        final int contract = Activity
                .contract(initialContract)
                .entry(1)
                .action((integer, integer2) -> null)
                .decision(o -> {
                    throw new IllegalCallerException("Should not be here");
                })
                .exit(o -> 2);

        Assertions.assertEquals(initialContract, contract);

    }

    @Test
    @DisplayName("Segundo otherwise não pode alterar contrato final de fim de fluxo do otherwise anterior")
    void nextOtherwiseShouldNotAlterObjectOfBeforeOtherwiseCalled() {

        final String contract = Activity
                .contract("initialContract")
                .entry(1)
                .action((integer, integer2) -> null)
                .otherwise(integer -> String.valueOf(integer + 1))
                .otherwise(() -> {
                    throw new IllegalCallerException("Should not be here");
                })
                .exit(o -> "10000");

        Assertions.assertEquals("2", contract);

    }

    @Test
    @DisplayName("Segundo otherwise não pode alterar contrato final de fim de fluxo do otherwise anterior")
    void nextOtherwiseShouldNotAlterObjectOfBeforeOtherwiseProduceCalled() {

        final String contract =
                Activity
                        .contract("initialContract")
                        .entry(1)
                        .action((integer, integer2) -> null)
                        .otherwise(() -> String.valueOf(-1))
                        .otherwise(integer -> {
                            throw new IllegalCallerException("Should not be here");
                        })
                        .action((integer, o) -> {
                            throw new IllegalCallerException("Should not be here");
                        })
                        .action(integer -> {
                            throw new IllegalCallerException("Should not be here");
                        })
                        .exit(o -> "10000");

        Assertions.assertEquals("-1", contract);

    }

    @Test
    @DisplayName("Exit deve retornar objeto de contrato de fim de fluxo da atividade")
    void exitShouldReturnObjectOfContract() {

        final float contract = Activity
                .contract(0F)
                .entry(100)
                .action(integer -> integer * 3)
                .decision(i -> i > 0)
                .exit(Integer::floatValue);

        Assertions
                .assertEquals(300f, contract);

    }

    @Test
    @DisplayName("Channel deve encaminhar uma mensagem")
    void channelShouldCommitAMessage() {

        final AtomicInteger atomicReference = new AtomicInteger();

        final int expected = 3;

        final Integer integer = Activity
                .contract(0)
                .entry(Integer.toString(expected))
                .action((Function<String, Integer>) Integer::parseInt)
                .channel(atomicReference::set)
                .exit(Integer::intValue);

        Assertions.assertEquals(expected, integer);

        Assertions.assertEquals(expected, atomicReference.get());

    }

    @Test
    @DisplayName("Fim de fluxo deve ocorrer e channel não deve encaminhar mensagem")
    void channelShouldntCommitBecauseEndFlux() {

        final int valorInicial = -1;

        final AtomicInteger atomicReference = new AtomicInteger(valorInicial);

        final int contrato = 10;

        final int valor =
                Activity
                        .contract(contrato)
                        .entry(1)
                        .decision(integer -> integer > 1)
                        .channel(atomicReference::set)
                        .exit(integer -> integer * 2);

        Assertions.assertEquals(contrato, valor);

        Assertions.assertEquals(valorInicial, atomicReference.get());

    }

    @Test
    @DisplayName("Quando operação otherwise é chamada antes de uma ação ou decisão realizada, atividade deve retornar entrada")
    void otherwiseShouldntAlterContractWhenValueIsNotEqualsNull() {

        final String s =
                Activity
                        .contract("1")
                        .entry(0)
                        .otherwise(() -> "10")
                        .exit(String::valueOf);

        Assertions.assertEquals("0", s);

    }

    @Test
    @DisplayName("Quando operação otherwise para interface produção é chamada antes de uma ação ou decisão realizada, atividade deve retornar entrada")
    void otherwiseForProduceShouldntAlterContractWhenValueIsNotEqualsNull() {

        final String s =
                Activity
                        .contract("1")
                        .entry(0)
                        .otherwise(integer -> String.valueOf(integer * 2))
                        .exit(String::valueOf);

        Assertions.assertEquals("0", s);

    }

    @Test
    @DisplayName("Quando fim de fluxo já ocorreu e canal otherwise faz nada")
    void whenEndFlowHasAlreadyOccurredAndOtherwiseChannelDoNothing() {

        final AtomicReference<Float> atomicReference = new AtomicReference<>();

        final String entrada = "1000";

        final Float aFloat =
                Activity
                        .contract(0f)
                        .entry(entrada)
                        .decision(s -> s.length() > 30)
                        .otherwise(s -> Float.valueOf(s.concat("1")))
                        .otherwiseChannel(Float::parseFloat, atomicReference::set)
                        .exit(Float::valueOf);

        Assertions.assertEquals(Float.valueOf(entrada.concat("1")), aFloat);

        Assertions.assertNull(atomicReference.get());

    }

    @Test
    @DisplayName("Quando uma decisão ou ação não foi realizado canal otherwise não faz nada")
    void whenDecisionOrActionDoesntRealizedThatOtherwiseChannelDoNothing() {

        final AtomicReference<String> atomicReference = new AtomicReference<>();

        final String s =
                Activity
                        .contract("1")
                        .entry(0)
                        .otherwiseChannel(String::valueOf, atomicReference::set)
                        .exit(String::valueOf);

        Assertions.assertEquals("0", s);

        Assertions.assertNull(atomicReference.get());

    }

    @Test
    @DisplayName("Quando mensagem para canal otherwise é nulo e não commita nenhuma mensagem")
    void whenMessageToChannelOtherwiseIsNullAndNotPossibilityCommit() {

        final AtomicReference<String> atomicReference = new AtomicReference<>();

        final String expected = "2";

        final String exitString = Activity
                .contract("1")
                .entry(0)
                .decision(integer -> integer > 1)
                .otherwiseChannel(integer -> null, atomicReference::set)
                .otherwise(() -> expected)
                .exit(String::valueOf);

        Assertions.assertEquals(expected, exitString);

        Assertions.assertNull(atomicReference.get());

    }

    @Test
    @DisplayName("Quando mensagem é encaminhada pelo canal otherwise")
    void whenMessageIsCommitedToChannelOtherwise() {

        final AtomicLong atomicLong = new AtomicLong();

        final long contract = 0L;

        final Long aLong = Activity
                .contract(contract)
                .entry(1F)
                .decision(aFloat -> aFloat.compareTo(2F) > 0)
                .otherwiseChannel(Float::longValue, atomicLong::set)
                .exit(Float::longValue);

        Assertions.assertEquals(1L, atomicLong.get());

        Assertions.assertEquals(contract, aLong);

    }

    @Test
    @DisplayName("Otherwise após canal otherwise não deve fazer nada")
    void theLastOtherwiseShouldDoNothing() {

        final AtomicLong atomicLong = new AtomicLong();

        final long contract = 0L;

        final Long aLong = Activity
                .contract(contract)
                .entry(1F)
                .decision(aFloat -> aFloat.compareTo(2F) > 0)
                .otherwiseChannel(Float::longValue, atomicLong::set)
                .otherwise(() -> 100L)
                .exit(Float::longValue);

        Assertions.assertEquals(contract, aLong);

        Assertions.assertEquals(1L, atomicLong.get());

    }

    @Test
    @DisplayName("Quando valor encapsulado é nulo , channel não deve commitar nenhuma mensagem")
    void whenValueIsNullThatChannelShouldntCommitMessage() {

        final AtomicInteger atomicInteger = new AtomicInteger(10);

        final String valor = Activity
                .contract("1")
                .entry(-1)
                .decision(integer -> integer > 1)
                .channel(Integer::sum, atomicInteger::set)
                .exit(String::valueOf);

        Assertions.assertEquals("1", valor);

        Assertions.assertEquals(10, atomicInteger.get());

    }

    @Test
    @DisplayName("Quando valor mapeado é nulo , channel não deve commitar nenhuma mensagem")
    void whenMappedValueIsNullThatChannelShouldntCommitMessage() {

        final AtomicInteger atomicInteger = new AtomicInteger(100);

        final String valor = Activity
                .contract("1")
                .entry(-1)
                .channel((integer, integer2) -> null, atomicInteger::set)
                .exit(String::valueOf);

        Assertions.assertEquals("-1", valor);

        Assertions.assertEquals(100, atomicInteger.get());

    }

    @Test
    @DisplayName("Channel deve comitar uma mensagem mapeada")
    void channelShouldCommitAMessageMapped() {

        final AtomicInteger atomicInteger = new AtomicInteger();

        final String mensagem = Activity
                .contract("Mensagem Nula")
                .entry(4)
                .action(integer -> integer + 5)
                .channel(Integer::sum, atomicInteger::set)
                .exit(String::valueOf);

        Assertions.assertEquals("9", mensagem);

        Assertions.assertEquals(13, atomicInteger.get());

    }

    @Test
    @DisplayName("Quando fim de fluxo ocorre e otherwise faz nada")
    void whenEndFluxHasAlreadyInChannelAndOtherwiseDoNothing() {

        final AtomicInteger atomicInteger = new AtomicInteger();

        final String mensagemNula = "Mensagem Nula";

        final String mensagem = Activity
                .contract(mensagemNula)
                .entry('0')
                .decision(character -> character.equals('1'))
                .channel((a, b) -> String.valueOf(a), s -> atomicInteger.set(Integer.parseInt(s)))
                .otherwise(() -> "2")
                .exit(String::valueOf);

        Assertions.assertEquals(mensagemNula, mensagem);

        Assertions.assertEquals(0, atomicInteger.get());

    }

    @Test
    @DisplayName("When otherwise function was called before a action")
    void whenOtherwiseFunctionWasCalledBeforeAction() {

        Assertions.assertTrue
                (
                        Activity
                                .contract(false)
                                .entry(1)
                                .otherwise
                                        (
                                                integer -> {
                                                    throw new IllegalCallerException("");
                                                }
                                        )
                                .action(integer -> integer * 20)
                                .exit(integer -> integer > 2)
                );

    }
}