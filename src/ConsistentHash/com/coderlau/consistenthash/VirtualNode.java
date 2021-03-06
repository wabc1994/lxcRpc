package com.coderlau.consistenthash;



/**
 * @author liuxiongcheng145@gmail.com
 * @param <T>
 *     */


public class VirtualNode<T extends Node> implements Node {
    final T physicalNode;
    final int replicaIndex;


    public T getPhysicalNode() {
        return physicalNode;
    }


    public VirtualNode(T physicalNode, int replicaIndex) {
        this.physicalNode = physicalNode;
        this.replicaIndex = replicaIndex;
    }


    @Override
    public String getKey() {
        return physicalNode.getKey() + "-" + replicaIndex;
    }

    public boolean isVirtualNodeOf(T pNode) {
        return physicalNode.getKey().equals(pNode.getKey());
    }

}
