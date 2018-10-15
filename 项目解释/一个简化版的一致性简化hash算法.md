在[有待优化待问题](https://github.com/wabc1994/lxcRpc/blob/master/%E9%A1%B9%E7%9B%AE%E8%A7%A3%E9%87%8A/%E6%9C%89%E5%BE%85%E4%BC%98%E5%8C%96%E7%9A%84%E9%97%AE%E9%A2%98.md)
中提到待一直性hash算法
实现一个简单待hash算法需要待代码情况
[可以参考下面这段文字](https://blog.csdn.net/WANGYAN9110/article/details/70185652)

>node 每台机器可以认为是一个节点，节点作为数据存储的地方，由一些节点来组成一个集群。
map存储的是


如何得到一个实体的key,一般而言都是getkey(T pNode){
return pNode.toString()
```java
//写一个hash  函数接口
public interface HashFunction<T> {
    int hash(T t);
}
public class ConsistentHash<T> {

    private final HashFunction hashFunction;
    //虚拟节点的数目，对于每一个节点来说，
    private final int numberOfReplicas;
    //环的实现采用一个sortedMap来实现
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
            Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i <numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i <numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
   
}
    

```