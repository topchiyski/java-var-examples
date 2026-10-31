package com.acme.automation.ugly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pillar 2 (The Ugly - Edge Case 3D / Guideline G7): Primitive Literals &amp; Overload Resolution Traps.
 * <p>
 * With {@code var}, a numeric literal's type is decided by the literal alone: an integer
 * literal is always {@code int}, never {@code byte}, {@code short}, or {@code Integer}.
 * That can silently change which overload a method call resolves to.
 */
class PrimitiveOverloadTrapTest {

    /**
     * Accepts only a {@code byte}, so callers must declare the argument's type explicitly.
     *
     * @param flag the flag to check
     * @return {@code true} if the flag is {@code 0x01}
     */
    private boolean verifyByteFlag(byte flag) {
        return flag == 0x01;
    }

    /**
     * Trap: {@code var codeToRemove = 200} infers {@code int}, so
     * {@code statusCodes.remove(codeToRemove)} resolves to {@link List#remove(int)}
     * (remove by index) instead of {@link java.util.Collection#remove(Object)}
     * (remove by value), and throws {@link IndexOutOfBoundsException}.
     */
    @Test
    void numericOverloadTrap_Junior_RemoveByIndexExplosion() {
        var statusCodes = new ArrayList<>(List.of(200, 404, 500));

        // TRAP: Changing 'Integer codeToRemove = 200;' to 'var codeToRemove = 200;'
        // infers primitive 'int', silently switching the overload from
        // Collection.remove(Object) to List.remove(int index)!
        var codeToRemove = 200;

        assertThatThrownBy(() -> statusCodes.remove(codeToRemove))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    /**
     * Fix: declare {@code byte} and {@code short} explicitly, use the {@code L} and
     * {@code d} literal suffixes when {@code var} should infer {@code long} or
     * {@code double}, and box explicitly with {@link Integer#valueOf(int)} to remove
     * by value.
     */
    @Test
    void numericOverloadSafe_SeniorPattern_ExplicitPrimitivesAndBoxedRemoval() {
        // Rule 1 (Guideline G7): Keep explicit 'byte' and 'short' declarations
        // because integer literals with 'var' always infer 'int'.
        byte expectedFlag = 0x01;
        var pollDelayMs = 5000L; // Explicit 'L' suffix infers 'long'
        var delta = 0.05d;       // Explicit 'd' suffix infers 'double'

        assertThat(verifyByteFlag(expectedFlag)).isTrue();
        assertThat(pollDelayMs).isEqualTo(5000L);
        assertThat(delta).isEqualTo(0.05d);

        // Rule 2: Disambiguate Collection.remove(Object) vs List.remove(int) explicitly
        var statusCodes = new ArrayList<>(List.of(200, 404, 500));
        statusCodes.remove(Integer.valueOf(200));

        assertThat(statusCodes).containsExactly(404, 500);
    }
}
