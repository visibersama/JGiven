package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@RunWith( DataProviderRunner.class )
public class ScenarioExecutionTest extends ScenarioTest<BeforeAfterTestStage, WhenTestStep, ThenTestStep> {

    @Test
    public void before_and_after_is_correctly_executed() {
        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 0 );

        given().something();

        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 1 );

        when().something();

        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 1 );
        assertThat( getScenario().getGivenStage().afterCalled ).isEqualTo( 1 );
    }

    static class TestStage extends Stage<TestStage> {
        boolean beforeCalled;

        @BeforeScenario
        public void beforeCalled() {
            beforeCalled = true;
        }

        public void an_exception_is_thrown() {
            throw new RuntimeException( "this exception should not be thrown" );
        }
    }

    @Test
    public void beforeStage_is_executed_for_stages_added_with_the_test_method() {
        TestStage stage = addStage( TestStage.class );
        given().something();
        assertThat( stage.beforeCalled ).isTrue();
    }

    @Test
    @NotImplementedYet
    public void NotImplementedYet_annotation_works_on_test_methods() {
        TestStage stage = addStage( TestStage.class );
        stage.given().an_exception_is_thrown();
        assertThat( true ).isTrue();
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_before_method_is_propagated() {
        addStage( TestStageWithExceptionInBeforeScenario.class );
        given().something();
    }

    public static class TestStageWithExceptionInBeforeScenario {
        @BeforeScenario
        public void throwException() {
            throw new IllegalStateException( "BeforeScenario" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_after_method_is_propagated() throws Throwable {
        addStage( TestStageWithExceptionInAfterScenario.class );
        given().something();
        getScenario().getExecutor().finished();
    }

    public static class TestStageWithExceptionInAfterScenario {
        @AfterScenario
        public void throwException() {
            throw new IllegalStateException( "AfterScenario" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_before_rule_method_is_propagated() throws Throwable {
        addStage( TestStageWithRuleThatThrowsExceptionInBefore.class );
        given().something();
    }

    public static class TestStageWithRuleThatThrowsExceptionInBefore {
        @ScenarioRule
        RuleThatThrowsExceptionInBefore rule = new RuleThatThrowsExceptionInBefore();
    }

    public static class RuleThatThrowsExceptionInBefore {
        public void before() {
            throw new IllegalStateException( "BeforeRule" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_after_rule_method_is_propagated() throws Throwable {
        addStage( TestStageWithRuleThatThrowsExceptionInAfter.class );
        given().something();
        getScenario().getExecutor().finished();
    }

    public static class TestStageWithRuleThatThrowsExceptionInAfter {
        @ScenarioRule
        RuleThatThrowsExceptionInAfter rule = new RuleThatThrowsExceptionInAfter();
    }

    public static class RuleThatThrowsExceptionInAfter {
        public void after() {
            throw new IllegalStateException( "AfterRule" );
        }
    }

}
