// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 6/28/2012 2:58:47 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   XOR.java

package com.ing.ideal.connector.crypto.impl;


public class XOR {

    private static final int MASK = 0x5f;

    private XOR() {
    }

    public static byte[] decipher(byte inputData[]) {
        return xor(inputData);
    }

    public static byte[] encipher(byte inputData[]) {
        return xor(inputData);
    }

    private static byte[] xor(byte inputdata[]) {
        if (inputdata == null)
            throw new RuntimeException("Invalid password cipher");
        byte outputData[] = new byte[inputdata.length];
        for (int i = 0; i < inputdata.length; i++)
            outputData[i] = (byte) (MASK ^ inputdata[i]);
        return outputData;
    }
}