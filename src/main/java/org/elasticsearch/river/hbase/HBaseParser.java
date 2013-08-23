package org.elasticsearch.river.hbase;


import com.google.protobuf.ByteString;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.ipc.HBaseRPCErrorHandler;
import org.apache.hadoop.hbase.ipc.RpcServer;
import org.apache.hadoop.hbase.ipc.SimpleRpcScheduler;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;
import org.apache.zookeeper.Watcher;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.logging.ESLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A separate Thread that provides a replication sink and stores that data
 * from an HBase cluster.
 */
class HBaseParser extends UnimplementedInHRegionShim
    implements Watcher,
    HBaseRPCErrorHandler,
    Runnable,
    RegionServerServices,
    AdminProtos.AdminService.BlockingInterface {

  private final InetSocketAddress initialIsa;
  private final int port_number;
  private RpcServer rpcServer;
  Configuration c;

  private final HBaseRiver river;
  private final ESLogger logger;
  private int indexCounter;
  int numHandler;
  int metaHandlerCount;
  boolean verbose;

  /**
   * @return list of blocking services and their security info classes that this server supports
   */
  private List<RpcServer.BlockingServiceAndInterface> getServices() {
    List<RpcServer.BlockingServiceAndInterface> bssi = new ArrayList<RpcServer.BlockingServiceAndInterface>(2);
    bssi.add(new RpcServer.BlockingServiceAndInterface(
        AdminProtos.AdminService.newReflectiveBlockingService(this),
        AdminProtos.AdminService.BlockingInterface.class));
    return bssi;
  }

  HBaseParser(final HBaseRiver river, int port_number) {
    this.river = river;
    this.logger = river.getLogger();
    initialIsa = new InetSocketAddress(port_number);
    numHandler = 10;
    metaHandlerCount = 10;
    verbose = true;
    c = HBaseConfiguration.create();
    this.port_number = port_number;
    String name = "regionserver/" + initialIsa.toString();
    SimpleRpcScheduler scheduler = new SimpleRpcScheduler(
        c,
        numHandler,
        0, // we don't use high priority handlers in master
        0, // we don't use replication handlers in master
        null, // this is a DNC w/o high priority handlers
        0);

    try {
      this.rpcServer = new RpcServer(this, name, getServices(),
          initialIsa,
          c,
          scheduler);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

  }

  @Override
  public void run() {
  }

  public Configuration getConfiguration() {
    return c;
  }


  public ZooKeeperWatcher getZooKeeper() {
    throw new RuntimeException("Not implemented");
  }

  private void replicateLogEntry(AdminProtos.WALEntry entry) {
    final BulkRequestBuilder bulkRequest = this.river.getEsClient().prepareBulk();
    for (ByteString kvBytes : entry.getKeyValueBytesList()) {
      KeyValue kv = new KeyValue(kvBytes.toByteArray());
      final ESKey.Key key = ESKey
          .Key
          .newBuilder()
          .setRow(ByteString.copyFrom(kv.getRow()))
          .setFamily(ByteString.copyFrom(kv.getFamily()))
          .setColumn(ByteString.copyFrom(kv.getQualifier()))
          .build();
      final String keyString = Base64.encodeBytes(key.toByteArray());
      if (kv.isDelete()) {
        final DeleteRequestBuilder request = this.
            river.
            getEsClient().
            prepareDelete(this.river.getIndex(), this.river.getType(), keyString);
        bulkRequest.add(request);
      } else {
        final IndexRequestBuilder request = this.
            river.
            getEsClient().
            prepareIndex(this.river.getIndex(), this.river.getType(), keyString);
        request.setSource(readDataTree(Arrays.asList(kv)));
        request.setTimestamp(String.valueOf(kv.getTimestamp()));
        bulkRequest.add(request);
      }
    }
    final BulkResponse response = bulkRequest.execute().actionGet();
    this.indexCounter += response.getItems().length;
    this.logger.info("HBase river has indexed {} entries so far", this.indexCounter);
    final List<byte[]> failedKeys = new ArrayList<byte[]>();
    if (response.hasFailures()) {
      this.logger.error("Errors have occurred while trying to index new data from HBase");
      this.logger.debug("Failed keys are {}", failedKeys);
    }
  }

  /**
   * Generate a tree structure that ElasticSearch can read and index from one of the rows that has been returned from
   * HBase.
   *
   * @param row The row in which to generate tree data
   * @return a map representation of the HBase row
   */
  protected Map<String, Object> readDataTree(final List<KeyValue> row) {
    final Map<String, Object> dataTree = new HashMap<String, Object>();
    for (final KeyValue column : row) {
      final String family = this.river.normalizeField(new String(column.getFamily(), this.river.getCharset()));
      final String qualifier = new String(column.getQualifier(), this.river.getCharset());
      final String value = new String(column.getValue(), this.river.getCharset());
      if (!dataTree.containsKey(family)) {
        dataTree.put(family, new HashMap<String, Object>());
      }
      readQualifierStructure((Map<String, Object>) dataTree.get(family), qualifier, value);
    }
    return dataTree;
  }


  /**
   * Will separate a column into sub column and return the value at the right json tree level.
   *
   * @param parent
   * @param qualifier
   * @param value
   */
  protected void readQualifierStructure(final Map<String, Object> parent,
                                        final String qualifier,
                                        final String value) {
    if (this.river.getColumnSeparator() != null && !this.river.getColumnSeparator().isEmpty()) {
      final int separatorPos = qualifier.indexOf(this.river.getColumnSeparator());
      if (separatorPos != -1) {
        final String parentQualifier = this.river.normalizeField(qualifier.substring(0, separatorPos));
        final String childQualifier = qualifier.substring(separatorPos + this.river.getColumnSeparator().length());
        if (!childQualifier.isEmpty()) {
          if (!(parent.get(parentQualifier) instanceof Map)) {
            parent.put(parentQualifier, new HashMap<String, Object>());
          }
          readQualifierStructure((Map<String, Object>) parent.get(parentQualifier), childQualifier, value);
          return;
        }
        parent.put(this.river.normalizeField(qualifier.replace(this.river.getColumnSeparator(), "")), value);
        return;
      }
    }
    parent.put(this.river.normalizeField(qualifier), value);
  }

  protected String findKeyInDataTree(final Map<String, Object> dataTree, final String keyPath) {
    if (!keyPath.contains(this.river.getColumnSeparator())) {
      return (String) dataTree.get(keyPath);
    }
    final String key = keyPath.substring(0, keyPath.indexOf(this.river.getColumnSeparator()));
    if (dataTree.get(key) instanceof Map) {
      final int subKeyIndex = keyPath.indexOf(this.river.getColumnSeparator()) + this.river.getColumnSeparator().length();
      return findKeyInDataTree((Map<String, Object>) dataTree.get(key), keyPath.substring(subKeyIndex));
    }
    return null;
  }

  @Override
  public AdminProtos.ReplicateWALEntryResponse replicateWALEntry(RpcController controller,
                                                                 AdminProtos.ReplicateWALEntryRequest request)
      throws ServiceException {
    for (AdminProtos.WALEntry entry : request.getEntryList()) {
      replicateLogEntry(entry);
    }

    return AdminProtos.ReplicateWALEntryResponse.newBuilder().build();
  }

}
