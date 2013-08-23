package org.elasticsearch.river.hbase;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.catalog.CatalogTracker;
import org.apache.hadoop.hbase.executor.ExecutorService;
import org.apache.hadoop.hbase.ipc.RpcServerInterface;
import org.apache.hadoop.hbase.master.TableLockManager;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.regionserver.*;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class UnimplementedInHRegionShim {

  public boolean checkOOME(Throwable e) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void updateRegionFavoredNodesMapping(String encodedRegionName, List<HBaseProtos.ServerName> favoredNodes) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public InetSocketAddress[] getFavoredNodesForRegion(String encodedRegionName) {
    return new InetSocketAddress[0];  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void addToOnlineRegions(HRegion r) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public boolean removeFromOnlineRegions(HRegion r, ServerName destination) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public HRegion getFromOnlineRegions(String encodedRegionName) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public List<HRegion> getOnlineRegions(TableName tableName) throws IOException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void abort(String why, Throwable e) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public boolean isAborted() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void process(WatchedEvent watchedEvent) {
  }


  public void stop(String why) {
    throw new RuntimeException("Not implemented");
  }



  public boolean isStopped() {
    throw new RuntimeException("Not implemented");
  }


  public boolean isStopping() {
    throw new RuntimeException("Not implemented");
  }


  public HLog getWAL(HRegionInfo regionInfo) throws IOException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public CompactionRequestor getCompactionRequester() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public FlushRequester getFlushRequester() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public RegionServerAccounting getRegionServerAccounting() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public TableLockManager getTableLockManager() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public void postOpenDeployTasks(HRegion r, CatalogTracker ct) throws KeeperException, IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public RpcServerInterface getRpcServer() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public ConcurrentMap<byte[], Boolean> getRegionsInTransitionInRS() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public FileSystem getFileSystem() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public Leases getLeases() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public ExecutorService getExecutorService() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public CatalogTracker getCatalogTracker() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public ServerName getServerName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public Map<String, HRegion> getRecoveringRegions() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public ClientProtos.MultiResponse replay(RpcController controller, ClientProtos.MultiRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.RollWALWriterResponse rollWALWriter(RpcController controller, AdminProtos.RollWALWriterRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.GetServerInfoResponse getServerInfo(RpcController controller, AdminProtos.GetServerInfoRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.StopServerResponse stopServer(RpcController controller, AdminProtos.StopServerRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.GetRegionInfoResponse getRegionInfo(RpcController controller, AdminProtos.GetRegionInfoRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.GetStoreFileResponse getStoreFile(RpcController controller, AdminProtos.GetStoreFileRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.GetOnlineRegionResponse getOnlineRegion(RpcController controller, AdminProtos.GetOnlineRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.OpenRegionResponse openRegion(RpcController controller, AdminProtos.OpenRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.CloseRegionResponse closeRegion(RpcController controller, AdminProtos.CloseRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.FlushRegionResponse flushRegion(RpcController controller, AdminProtos.FlushRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.SplitRegionResponse splitRegion(RpcController controller, AdminProtos.SplitRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.CompactRegionResponse compactRegion(RpcController controller, AdminProtos.CompactRegionRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public AdminProtos.MergeRegionsResponse mergeRegions(RpcController controller, AdminProtos.MergeRegionsRequest request) throws ServiceException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }



}
