package com.xvjun.bigdata.hos.server.service;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;

import java.util.List;

public interface IHbaseService {

    public boolean createTable(Connection connection, String tableName, String[] cfs,byte[][] splitKeys);

    public boolean createTable(Connection connection, String tableName, String[] cfs);

    public boolean deleteTable(Connection connection, String tableName);

    public boolean deleteColumnFamily(Connection connection, String tableName,String columnFamilyName);

    public boolean deleteQualifier(Connection connection, String tableName, String rowName,String columnFamilyName, String qualifierName);

    public boolean delete(Connection connection, String tableName, String rowName);

    public boolean delete(Connection connection, String tableName, List<String> rows);

    /**
     * 扫描整张表，记得释放rs.
     */
    public ResultScanner scanner(Connection connection, String tableName);

    public ResultScanner scanner(Connection connection, String tableName, String startRowKey,String stopRowKey);

    public ResultScanner scanner(Connection connection, String tableName, byte[] startRowKey,byte[] stopRowKey);

    public ResultScanner scanner(Connection connection, String tableName,FilterList filterList);

    public ResultScanner scanner(Connection connection, String tableName, Scan scan);

    public ResultScanner scanner(Connection connection, String tableName, byte[] startRowKey,byte[] stopRowKey, FilterList filterList);

    public ResultScanner scanner(Connection connection, String tableName, String startRowKey,String stopRowKey, FilterList filterList);

    public boolean existsRow(Connection connection, String tableName, String row);

    public Result getRow(Connection connection, String tableName, String row,FilterList filterList);

    public Result getRow(Connection connection, String tableName, Get get);

    public Result getRow(Connection connection, String tableName, String row);

    public Result getRow(Connection connection, String tableName, String row, byte[] column,byte[] qualifier);

    public Result[] getRows(Connection connection, String tableName, List<String> rows,FilterList filterList);

    public Result[] getRows(Connection connection, String tableName, List<String> rows);

    public long incrementColumnValue(Connection connection, String tableName, String row,byte[] columnFamily, byte[] qualifier, int num);

    public boolean putRow(Connection connection, String tableName, String row,
                                 String columnFamily,
                                 String qualifier, String data);

    public boolean putRow(Connection connection, String tableName, Put put);

    public boolean putRows(Connection connection, String tableName, List<Put> puts);

    //TODO... 自己添加的方法

    public void getAllTables(Connection connection);

    public Table getTable(Connection connection,String tableName);

    public void descTable(Connection connection,String tableName);

    public boolean existTable(Connection connection,String tableName);

    public void disableTable(Connection connection,String tableName);

    public void dropTable(Connection connection,String tableName);

    public void modifyTable_add(Connection connection,String tableName, String[] addColumn);

    public void modifyTable_remove(Connection connection,String tableName, String[] removeColumn);

    public void modifyTable(Connection connection,String tableName, String[] addColumn, String[] removeColumn);

    public void modifyTable(Connection connection,String tableName, HColumnDescriptor hcds);

    public void putData(Connection connection,String tableName, String rowKey, String familyName, String columnName, String value,
                        long timestamp);

    public Result getResult(Connection connection,String tableName, String rowKey, String familyName);

    public Result getResult(Connection connection,String tableName, String rowKey, String familyName, String columnName);

    public Result getResultByVersion(Connection connection,String tableName, String rowKey, String familyName, String columnName,
                                     int versions);
}
