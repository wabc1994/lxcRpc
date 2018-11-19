package com.coderlau.consistenthash;
/**
 * @author liuxiongcheng145@gmail.com
 *
 * Hash String to long value
 */
public interface HashFunction {
    long hash(String key);
}
