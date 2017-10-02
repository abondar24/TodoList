package org.abondar.experimental.todolist.test;

import org.apache.cxf.rt.security.crypto.CryptoUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class ShmacTest {

    @Test
    public void genShmacSigTest() {
        String sig =CryptoUtils.encodeBytes("borscht".getBytes());
        System.out.println(sig);
    }
}
