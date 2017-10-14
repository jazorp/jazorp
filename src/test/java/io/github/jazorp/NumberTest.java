package io.github.jazorp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.jazorp.ErrorType.*;
import static io.github.jazorp.Validators.EPSILON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(Parameterized.class)
public class NumberTest {

    private final Env env;
    private final TestContext ctx;

    public NumberTest(TestContext ctx) {
        this.ctx = ctx;
        this.env = Env.empty();
    }

    @Test
    public void verify() {
        switch (ctx.type) {
            case EQUAL:
                if (ctx.epsilon != null) {
                    assertThat(info(ctx), isValid(Validators.equal("foo", ctx.value, ctx.reference, ctx.epsilon)), is(ctx.isValid));
                } else {
                    assertThat(info(ctx), isValid(Validators.equal("foo", ctx.value, ctx.reference)), is(ctx.isValid));
                }
                break;
            case LESS:
                if (ctx.epsilon != null) {
                    assertThat(info(ctx), isValid(Validators.less("foo", ctx.value, ctx.reference, ctx.epsilon)), is(ctx.isValid));
                } else {
                    assertThat(info(ctx), isValid(Validators.less("foo", ctx.value, ctx.reference)), is(ctx.isValid));
                }
                break;
            case LESS_EQUAL:
                if (ctx.epsilon != null) {
                    assertThat(info(ctx), isValid(Validators.lessEqual("foo", ctx.value, ctx.reference, ctx.epsilon)), is(ctx.isValid));
                } else {
                    assertThat(info(ctx), isValid(Validators.lessEqual("foo", ctx.value, ctx.reference)), is(ctx.isValid));
                }
                break;
            case GREATER:
                if (ctx.epsilon != null) {
                    assertThat(info(ctx), isValid(Validators.greater("foo", ctx.value, ctx.reference, ctx.epsilon)), is(ctx.isValid));
                } else {
                    assertThat(info(ctx), isValid(Validators.greater("foo", ctx.value, ctx.reference)), is(ctx.isValid));
                }
                break;
            case GREATER_EQUAL:
                if (ctx.epsilon != null) {
                    assertThat(info(ctx), isValid(Validators.greaterEqual("foo", ctx.value, ctx.reference, ctx.epsilon)), is(ctx.isValid));
                } else {
                    assertThat(info(ctx), isValid(Validators.greaterEqual("foo", ctx.value, ctx.reference)), is(ctx.isValid));
                }
                break;
            default:
                throw new IllegalArgumentException("type not supported at this test " + ctx.type);
        }
    }

    private String info(TestContext ctx) {
        return ctx.value + " " + ctx.type.name() + " " + ctx.reference;
    }

    private boolean isValid(ValidationThunk vt) {
        return vt.validate(env).isValid();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        List<TestContext> testContexts = new ArrayList<>();

        testContexts.add(new TestContext(EQUAL, 42,  42,  true));
        testContexts.add(new TestContext(EQUAL, 42,  0,  false));
        testContexts.add(new TestContext(EQUAL, 1.0000001,   1.0000002,   false));
        testContexts.add(new TestContext(EQUAL, 1.00000000,  1.00000000,  true));
        testContexts.add(new TestContext(EQUAL, 1.000000001, 1.000000002, true));
        testContexts.add(new TestContext(EQUAL, 1.00000001,  1.00000002,  true));
        testContexts.add(new TestContext(EQUAL, 1.0000001,   1.0000002,   true, EPSILON * 2));
        testContexts.add(new TestContext(EQUAL, 1.000001,    1.000002,    true, EPSILON * 18));
        testContexts.add(new TestContext(EQUAL, -0,   0,   true));
        testContexts.add(new TestContext(EQUAL, Double.MAX_VALUE,   Double.MAX_VALUE,   true));
        testContexts.add(new TestContext(EQUAL, Double.MIN_VALUE,   Double.MIN_VALUE,   true));
        testContexts.add(new TestContext(EQUAL, Double.NaN,   Double.NaN,   false));
        testContexts.add(new TestContext(EQUAL, 0,   Double.NaN,   false));

        testContexts.add(new TestContext(LESS,  1.0000001,   1.0000002,   true));
        testContexts.add(new TestContext(LESS,  1.000001,    1.000002,    true, EPSILON * 2));
        testContexts.add(new TestContext(LESS, 1, 2, true));
        testContexts.add(new TestContext(LESS, 1, 1, false));
        testContexts.add(new TestContext(LESS, -1,   0,   true));
        testContexts.add(new TestContext(LESS, -0,   0,   false));
        testContexts.add(new TestContext(LESS, 1, 0, false));
        testContexts.add(new TestContext(LESS, Double.MIN_VALUE,   Double.MAX_VALUE,   true));

        testContexts.add(new TestContext(LESS_EQUAL, 1.00000000,  1.00000000,  true));
        testContexts.add(new TestContext(LESS_EQUAL, 1.000000001, 1.000000002, true));
        testContexts.add(new TestContext(LESS_EQUAL, 1.00000001,  1.00000002,  true));
        testContexts.add(new TestContext(LESS_EQUAL, 1.0000001,   1.0000002,   true, EPSILON * 2));
        testContexts.add(new TestContext(LESS_EQUAL, 1.000001,    1.000002,    true, EPSILON * 18));
        testContexts.add(new TestContext(LESS_EQUAL, 1.0000001,   1.0000002,   true));
        testContexts.add(new TestContext(LESS_EQUAL, 1.000001,    1.000002,    true, EPSILON * 2));
        testContexts.add(new TestContext(LESS_EQUAL, 1, 2, true));

        testContexts.add(new TestContext(GREATER, 1.0000002,   1.0000001,   true));
        testContexts.add(new TestContext(GREATER, 1.000002,    1.000001,    true, EPSILON * 2));
        testContexts.add(new TestContext(GREATER, 2, 1, true));
        testContexts.add(new TestContext(GREATER, 1, 1, false));
        testContexts.add(new TestContext(GREATER, 0, 1, false));

        testContexts.add(new TestContext(GREATER_EQUAL, 1.00000000,  1.00000000,  true));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.000000001, 1.000000002, true));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.00000001,  1.00000002,  true));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.0000001,   1.0000002,   true, EPSILON * 2));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.000001,    1.000002,    true, EPSILON * 18));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.0000002,   1.0000001,   true));
        testContexts.add(new TestContext(GREATER_EQUAL, 1.000002,    1.000001,    true, EPSILON * 2));
        testContexts.add(new TestContext(GREATER_EQUAL, 2, 1, true));
        testContexts.add(new TestContext(GREATER_EQUAL, 0, 1, false));

        // feel free to add more test scenarios / combinations


        Object[][] data = new Object[testContexts.size()][];
        for (int i = 0; i < testContexts.size(); i++) {
            data[i] = new Object[]{testContexts.get(i)};
        }
        return Arrays.asList(data);
    }

    private static class TestContext {
        final ErrorType type;
        final Number value;
        final Number reference;
        final boolean isValid;
        Double epsilon = null;

        private TestContext(ErrorType type, Number value, Number reference, boolean isValid) {
            this.type = type;
            this.value = value;
            this.reference = reference;
            this.isValid = isValid;
        }

        private TestContext(ErrorType type, Number value, Number reference, boolean isValid, double epsilon) {
            this(type, value, reference, isValid);
            this.epsilon = epsilon;
        }

        @Override
        public String toString() {
            return  type +
                    ", value=" + value +
                    ", reference=" + reference +
                    ", isValid=" + isValid +
                    ", epsilon=" + epsilon +
                    '}';
        }
    }


}
