package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.junit.StepsAreReportedTest.TestSteps;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class StepsAreReportedTest extends ScenarioTest<TestSteps, TestSteps, TestSteps> {

    @Test
    public void given_steps_are_reported() throws Throwable {

        given().some_test_step();

        getScenario().finished();
        ScenarioModel model = getScenario().getModel().getLastScenarioModel();

        assertThat(model.getClassName()).isEqualTo( StepsAreReportedTest.class.getName() );
        assertThat(model.getTestMethodName()).isEqualTo( "given_steps_are_reported" );
        assertThat(model.getDescription()).isEqualTo( "given steps are reported" );
        assertThat( model.getExplicitParameters() ).isEmpty();
        assertThat(model.getTags()).isEmpty();
        assertThat( model.getScenarioCases() ).hasSize( 1 );

        ScenarioCaseModel scenarioCase = model.getCase( 0 );
        assertThat( scenarioCase.getExplicitArguments() ).isEmpty();
        assertThat(scenarioCase.getCaseNr()).isEqualTo( 1 );
        assertThat(scenarioCase.getSteps()).hasSize( 1 );

        StepModel step = scenarioCase.getSteps().get(0);
        assertThat( step.name ).isEqualTo( "some test step" );
        assertThat( step.words ).isEqualTo( Arrays.asList( Word.introWord( "Given" ), new Word( "some test step" ) ) );
        assertThat( step.isNotImplementedYet() ).isFalse();

    }

    @Test
    public void steps_annotated_with_NotImplementedYet_are_recognized() throws Throwable {
        given().some_not_implemented_step();

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        StepModel stepModel = model.getCase(0).getSteps().get(0);
        assertThat( stepModel.isNotImplementedYet() ).isTrue();
        assertThat( model.getExecutionStatus() ).isEqualTo( ExecutionStatus.NONE_IMPLEMENTED );
    }

    @Test
    public void if_some_steps_are_not_implemented_then_scenario_status_is_partially() throws Throwable {
        given().some_test_step();
        given().some_not_implemented_step();

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.getExecutionStatus() ).isEqualTo( ExecutionStatus.PARTIALLY_IMPLEMENTED );
    }

    @Retention( RetentionPolicy.RUNTIME )
    @IsTag( explodeArray = false )
    public @interface TestTag {
        String[] value();
    }

    @Test
    @TestTag( { "foo", "bar", "baz" } )
    public void annotations_are_translated_to_tags() throws Throwable {
        given().some_test_step();
        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat(model.getTags()).hasSize( 1 );

        assertThat( model.getTags().get( 0 ).getName() ).isEqualTo( "TestTag" );
        assertThat( model.getTags().get( 0 ).getValues() ).containsExactly( "foo", "bar", "baz" );
    }

    @DataProvider
    public static Object[][] testValues() {
        return new Object[][] { { 1 }, { 2 } };
    }

    @Test
    @TestTag( { "foo", "bar", "baz" } )
    @UseDataProvider( "testValues" )
    public void annotations_are_translated_to_tags_only_once( int n ) throws Throwable {
        given().some_test_step();
        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat(model.getTags()).hasSize( 1 );

        assertThat( model.getTags().get( 0 ).getName() ).isEqualTo( "TestTag" );
        assertThat( model.getTags().get( 0 ).getValues() ).containsExactly( "foo", "bar", "baz" );
    }

    @Test
    public void hidden_steps_do_not_appear_in_the_report() throws Throwable {
        given().aHiddenStep();

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.getCase( 0 ).getSteps() ).isEmpty();
    }

    @Test
    public void hidden_arguments_do_not_appear_in_the_report() throws Throwable {
        given().a_step_with_a_hidden_argument( "test arg" );

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.getCase( 0 ).getStep( 0 ).getWords() ).hasSize( 2 );
    }

    public static class TestSteps extends Stage<TestSteps> {
        public void some_test_step() {

        }

        @NotImplementedYet
        public void some_not_implemented_step() {

        }

        public void a_step_fails() {
            assertThat( true ).isFalse();
        }

        @Hidden
        public void aHiddenStep() {}

        public void a_step_with_a_hidden_argument( @Hidden String arg ) {

        }
    }

}
