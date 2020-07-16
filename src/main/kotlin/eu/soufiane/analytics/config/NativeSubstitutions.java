package eu.soufiane.analytics.config;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.elasticsearch.client.Node;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@SuppressWarnings("ALL")
@TargetClass(className = "org.jboss.logmanager.AtomicArray")
final class Target_org_jboss_logmanager_AtomicArray<T, V> {
  @Alias
  private AtomicReferenceFieldUpdater<T, V[]> updater;
  @Alias
  private Class<V> componentType;

  @Alias
  private static <V> V[] copyOf(final Class<V> componentType, V[] old, int newLen) {
    return null;
  }

  @Substitute
  public void add(T instance, V value) {
    for (; ; ) {
      V[] oldVal = updater.get(instance);
      if (oldVal == null) {
        oldVal = (V[]) Array.newInstance(componentType, 0);
        updater.set(instance, oldVal);
      }
      final int oldLen = oldVal.length;
      final V[] newVal = copyOf(componentType, oldVal, oldLen + 1);
      newVal[oldLen] = value;
      if (updater.compareAndSet(instance, oldVal, newVal)) {
        return;
      }
    }
  }
}

@TargetClass(className = "org.elasticsearch.client.RestClient", innerClass = "NodeTuple")
final class NodeTuple<T> {
  @SuppressWarnings("unused")
  @Alias
  NodeTuple(T unmodifiableList, AuthCache authCache) {
  }
}

@SuppressWarnings("ALL")
@TargetClass(className = "org.elasticsearch.client.RestClient")
final class Target_org_elasticsearch_client_RestClient {
  @Alias
  private NodeTuple<List<Node>> nodeTuple;
  @Alias
  private ConcurrentMap<HttpHost, ?> blacklist;

  @Substitute
  public synchronized void setNodes(Collection<Node> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      throw new IllegalArgumentException("nodes must not be null or empty");
    }
    AuthCache authCache = new BasicSchemeAuthCache();

    Map<HttpHost, Node> nodesByHost = new LinkedHashMap<>();
    for (Node node : nodes) {
      Objects.requireNonNull(node, "node cannot be null");
      nodesByHost.put(node.getHost(), node);
      authCache.put(node.getHost(), new BasicScheme());
    }
    this.nodeTuple = new NodeTuple<>(
      Collections.unmodifiableList(new ArrayList<>(nodesByHost.values())), authCache);
    this.blacklist.clear();
  }
}

class BasicSchemeAuthCache extends BasicAuthCache {
  Set<HttpHost> hosts = new HashSet<>();
  @Override
  public void put(HttpHost host, AuthScheme authScheme) {
    hosts.add(host);
  }
  @Override
  public AuthScheme get(HttpHost host) {
    if (hosts.contains(host)) {
      return new BasicScheme();
    } else {
      return null;
    }
  }
}
