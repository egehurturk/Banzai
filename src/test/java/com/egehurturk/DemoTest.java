package com.egehurturk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DemoTest {

    public String val;

    @BeforeEach
    public void setUp() {
        this.val = "TRUE";
    }

    @Test
    public void assertThat() {
        Assertions.assertEquals("TRUE", this.val);
        Assertions.assertTrue(true);
    }



}
