/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import java.nio.ByteBuffer;

/**
 *
 * @author Welcome
 */
public class ByteArray {
    
    public static void put(byte[] arr, int this_offset, int arr_offset, byte[] arr2, int length) {
        System.arraycopy(arr2, arr_offset, arr, this_offset, length);
    }
    
    public static void put(byte[] arr, int this_offset, ByteBuffer buf) {
        if(buf.hasArray()) {
           byte[] arr2 = buf.array();
           put(arr, this_offset, buf.position(), arr2, buf.remaining());
        } else {
            while(true) {
                arr[this_offset++] = buf.get();
            }
        }
    }

    public static ByteBuffer toBuffer(byte[] arr) {
        ByteBuffer buf = ByteBuffer.allocate(arr.length);
        buf.put(arr);
        buf.flip();
        return buf;
    }
    
    public static byte[] fromBuffer(ByteBuffer buf) {
        byte[] arr = new byte[buf.remaining()];
        put(arr, 0, buf);
        return arr;
    }

    public static void put(byte[] data, int offset, byte b) {
        data[offset] = b;
    }

    public static void putFloat(byte[] arr, int offset, float f) {
        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES);
        buf.putFloat(f);
        buf.position(0);
        put(arr, offset, buf);
    }
    
    public static void putInt(byte[] arr, int offset, int i) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.putInt(i);
        buf.position(0);
        put(arr, offset, buf);
    }
    
    public static int getInt(byte[] arr, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.put(arr, offset, Integer.BYTES);
        buf.flip();
        return buf.getInt();
    }
    
    public static byte get(byte[] arr, int offset) {
        return arr[offset];
    }

    public static float getFloat(byte[] arr, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES);
        buf.put(arr, offset, Float.BYTES);
        buf.flip();
        return buf.getFloat();
    }
    
    public static void main(String[] args) {
        byte[] b1 = new byte[13];
        byte[] b2 = new byte[4];
        System.arraycopy(b2, 5, b1, 0, 0);
    }
}
