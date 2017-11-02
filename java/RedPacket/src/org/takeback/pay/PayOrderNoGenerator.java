// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.pay;

import java.util.UUID;

public class PayOrderNoGenerator
{
    public static String generator() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
