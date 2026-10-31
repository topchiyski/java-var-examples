package com.acme.automation.ugly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pillar 2 (The Ugly - Edge Case 3D / Guideline G7): Primitive Literals & Overload Resolution Traps.
 */
class PrimitiveOverloadTrapTest {

    private boolean verifyByteFlag(byte flag) {
        return flag == 0x01;
    }

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
