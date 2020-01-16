package com.zzyang;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityTest {

    @Test
    public void test() {
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        System.out.println("bc.encode(\"zzyang\") = " + bc.encode("zzyang"));
    }
}
