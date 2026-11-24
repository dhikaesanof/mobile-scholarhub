package com.example.scholarhub.feature.profile

import com.example.scholarhub.feature.mentor.BankAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileRulesTest {
    @Test fun passwordNeedsLengthLettersAndDigits() {
        assertEquals("Minimal 8 karakter.", passwordProblem("abc1"))
        assertEquals("Harus mengandung huruf dan angka.", passwordProblem("abcdefgh"))
        assertEquals("Harus mengandung huruf dan angka.", passwordProblem("12345678"))
        assertNull(passwordProblem("rahasia123"))
    }

    @Test fun gpaAcceptsCommaAndRange() {
        assertNull(gpaProblem(""))
        assertNull(gpaProblem("3,84"))
        assertNull(gpaProblem("4.00"))
        assertNotNull(gpaProblem("4,5"))
        assertNotNull(gpaProblem("3,8,4"))
    }

    @Test fun accountNumberIsMasked() {
        assertEquals("BCA • 8271****90", BankAccount("BCA", "8271553490", "Sarah").masked)
        assertEquals("BRI • 12345", BankAccount("BRI", "12345", "A").masked)
    }
}
