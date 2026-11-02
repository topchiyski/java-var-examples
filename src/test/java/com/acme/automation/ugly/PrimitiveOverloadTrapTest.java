package com.acme.automation.ugly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * With {@code var}, an integer literal without a suffix is always {@code int}, which can change
 * overload resolution.
 */
class PrimitiveOverloadTrapTest {

    /**
     * Returns whether the flag is {@code 0x01}; accepts only a {@code byte}.
     */
    private boolean verifyByteFlag(byte flag) {
        return flag == 0x01;
    }

    /**
     * Trap: an {@code int} from {@code var} calls {@link List#remove(int)} (by index), not remove by value.
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
     * Fix: explicit {@code byte}, literal suffixes, and {@link Integer#valueOf(int)} to remove by value.
     */
    @Test
    void numericOverloadSafe_SeniorPattern_ExplicitPrimitivesAndBoxedRemoval() {
        // Rule 1: Keep explicit 'byte' and 'short' declarations
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
