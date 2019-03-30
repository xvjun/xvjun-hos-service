package com.xvjun.bigdata.hos.server.service.Impl;

import com.xvjun.bigdata.hos.common.ErrorCodes;
import com.xvjun.bigdata.hos.server.error.HosServerException;
import com.xvjun.bigdata.hos.server.service.IHbaseService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class HbaseServiceImpl implements IHbaseService {


    /**
     * 创建表
     * @param connection 连接
     * @param tableName 表名
     * @param cfs 列族
     * @param splitKeys 分割大小
     * @return
     */
    @Override
    public boolean createTable(Connection connection, String tableName, String[] cfs, byte[][] splitKeys) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            for (int i = 0; i < cfs.length; i++) {
                HColumnDescriptor hcolumnDesc = new HColumnDescriptor(cfs[i]);
                hcolumnDesc.setMaxVersions(1);
                tableDesc.addFamily(hcolumnDesc);
            }
            admin.createTable(tableDesc, splitKeys);
        } catch (Exception e) {
            String msg = String.format("create table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 创建表
     * @param connection
     * @param tableName
     * @param cfs
     * @return
     */
    @Override
    public boolean createTable(Connection connection, String tableName, String[] cfs) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            for (int i = 0; i < cfs.length; i++) {
                HColumnDescriptor hcolumnDescriptor = new HColumnDescriptor(cfs[i]);
                hcolumnDescriptor.setMaxVersions(1);
                tableDesc.addFamily(hcolumnDescriptor);
            }
            admin.createTable(tableDesc);
        } catch (Exception e) {
            String msg = String.format("create table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 删除表
     * @param connection
     * @param tableName
     * @return
     */
    @Override
    public boolean deleteTable(Connection connection, String tableName) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);

        } catch (Exception e) {
            String msg = String.format("delete table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 删除ColumnFamily.
     */
    @Override
    public boolean deleteColumnFamily(Connection connection, String tableName, String columnFamilyName) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.deleteColumn(tableName, columnFamilyName);
        } catch (IOException e) {
            String msg = String
                    .format("delete table=%s , column family=%s error. msg=%s", tableName, columnFamilyName,
                            e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }
    /**
     * 删除qualifier.
     */
    @Override
    public boolean deleteQualifier(Connection connection, String tableName, String rowName, String columnFamilyName, String qualifierName) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Delete delete = new Delete(rowName.getBytes());
            delete.addColumns(columnFamilyName.getBytes(), qualifierName.getBytes());
            table.delete(delete);
        } catch (IOException e) {
            String msg = String
                    .format("delete table=%s , column family=%s , qualifier=%s error. msg=%s", tableName,
                            columnFamilyName, qualifierName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }
    /**
     * 删除row.
     */
    @Override
    public boolean delete(Connection connection, String tableName, String rowName) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Delete delete = new Delete(Bytes.toBytes(rowName));
            table.delete(delete);
        } catch (IOException e) {
            String msg = String
                    .format("delete table=%s , row=%s error. msg=%s", tableName, rowName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }
    /**
     * delete rows.
     *
     * @param tableName tableName
     * @param rows rows
     * @return success of failed
     */
    @Override
    public boolean delete(Connection connection, String tableName, List<String> rows) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Delete> list = new ArrayList<Delete>();
            for (String row : rows) {
                Delete d = new Delete(Bytes.toBytes(row));
                list.add(d);
            }
            if (list.size() > 0) {
                table.delete(list);
            }
        } catch (IOException e) {
            String msg = String
                    .format("delete table=%s , rows error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }
    /**
     * 扫描整张表，记得释放rs.
     */
    @Override
    public ResultScanner scanner(Connection connection, String tableName) {
        ResultScanner results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setCaching(1000);
            results = table.getScanner(scan);
        } catch (IOException e) {
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, String startRowKey, String stopRowKey) {
        return scanner(connection, tableName, Bytes.toBytes(startRowKey), Bytes.toBytes(stopRowKey));
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, byte[] startRowKey, byte[] stopRowKey) {
        ResultScanner results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setStartRow(startRowKey);
            scan.setStopRow(stopRowKey);
            scan.setCaching(1000);
            results = table.getScanner(scan);
        } catch (IOException e) {
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, FilterList filterList) {
        ResultScanner results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setCaching(1000);
            scan.setFilter(filterList);
            results = table.getScanner(scan);
        } catch (IOException e) {
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, Scan scan) {
        ResultScanner results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            results = table.getScanner(scan);
        } catch (IOException e) {
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, byte[] startRowKey, byte[] stopRowKey, FilterList filterList) {
        ResultScanner results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setStartRow(startRowKey);
            scan.setStopRow(stopRowKey);
            scan.setCaching(1000);
            scan.setFilter(filterList);
            results = table.getScanner(scan);
        } catch (IOException e) {
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public ResultScanner scanner(Connection connection, String tableName, String startRowKey, String stopRowKey, FilterList filterList) {
        return scanner(connection, tableName, Bytes.toBytes(startRowKey), Bytes.toBytes(stopRowKey),
                filterList);
    }

    @Override
    public boolean existsRow(Connection connection, String tableName, String row) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get g = new Get(Bytes.toBytes(row));
            return table.exists(g);
        } catch (IOException e) {
            String msg = String
                    .format("check exists row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public Result getRow(Connection connection, String tableName, String row, FilterList filterList) {
        Result rs;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get g = new Get(Bytes.toBytes(row));
            g.setFilter(filterList);
            rs = table.get(g);
        } catch (IOException e) {
            String msg = String
                    .format("get row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return rs;
    }

    @Override
    public Result getRow(Connection connection, String tableName, Get get) {
        Result rs;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            rs = table.get(get);
        } catch (IOException e) {
            String msg = String
                    .format("get row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return rs;
    }

    @Override
    public Result getRow(Connection connection, String tableName, String row) {
        Result rs;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get g = new Get(Bytes.toBytes(row));
            rs = table.get(g);
        } catch (IOException e) {
            String msg = String
                    .format("get row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return rs;
    }

    @Override
    public Result getRow(Connection connection, String tableName, String row, byte[] column, byte[] qualifier) {
        Result rs;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get g = new Get(Bytes.toBytes(row));
            g.addColumn(column, qualifier);
            rs = table.get(g);
        } catch (IOException e) {
            String msg = String
                    .format("get row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return rs;
    }

    @Override
    public Result[] getRows(Connection connection, String tableName, List<String> rows, FilterList filterList) {
        Result[] results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Get> gets = null;
            gets = new ArrayList<Get>();
            for (String row : rows) {
                if (row != null) {
                    Get g = new Get(Bytes.toBytes(row));
                    g.setFilter(filterList);
                    gets.add(g);
                }
            }
            if (gets.size() > 0) {
                results = table.get(gets);
            }
        } catch (IOException e) {
            String msg = String
                    .format("get rows from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    @Override
    public Result[] getRows(Connection connection, String tableName, List<String> rows) {
        Result[] results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Get> gets = null;
            gets = new ArrayList<Get>();
            for (String row : rows) {
                if (row != null) {
                    Get g = new Get(Bytes.toBytes(row));
                    gets.add(g);
                }
            }
            if (gets.size() > 0) {
                results = table.get(gets);
            }
        } catch (IOException e) {
            String msg = String
                    .format("get rows from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return results;
    }

    /**
     * count 递增
     * @param connection
     * @param tableName
     * @param row
     * @param columnFamily
     * @param qualifier
     * @param num
     * @return
     */
    @Override
    public long incrementColumnValue(Connection connection, String tableName, String row, byte[] columnFamily, byte[] qualifier, int num) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.incrementColumnValue(Bytes.toBytes(row), columnFamily, qualifier, num);
        } catch (IOException e) {
            String msg = String
                    .format("incrementColumnValue table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public boolean putRow(Connection connection, String tableName, String row, String columnFamily, String qualifier, String data) {
        try {
            Put put = new Put(Bytes.toBytes(row));
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier),
                    Bytes.toBytes(data));
            putRows(connection, tableName, Arrays.asList(put));
        } catch (Exception e) {
            String msg = String
                    .format("put row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    @Override
    public boolean putRow(Connection connection, String tableName, Put put) {
        try {
            putRows(connection, tableName, Arrays.asList(put));
        } catch (Exception e) {
            String msg = String
                    .format("put row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    @Override
    public boolean putRows(Connection connection, String tableName, List<Put> puts) {
        long currentTime = System.currentTimeMillis();
        final BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {
            @Override
            public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
                String msg = String
                        .format("put rows from table=%s error. msg=%s", tableName, e.getMessage());
                throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
            }
        };
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName))
                .listener(listener);
        params.writeBufferSize(5 * 1024 * 1024);
        try (final BufferedMutator mutator = connection.getBufferedMutator(params)) {
            mutator.mutate(puts);
            mutator.flush();
        } catch (IOException e) {
            String msg = String
                    .format("put rows from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
        return true;
    }

    //自己添加的

    @Override
    public void getAllTables(Connection connection) {
        try{
            //获取列簇的描述信息
            HTableDescriptor[] listTables = connection.getAdmin().listTables();
            for (HTableDescriptor listTable : listTables) {
                //转化为表名
                String tbName = listTable.getNameAsString();
                //获取列的描述信息
                HColumnDescriptor[] columnFamilies = listTable.getColumnFamilies();
                System.out.println("tableName:"+tbName);
                for(HColumnDescriptor columnFamilie : columnFamilies) {
                    //获取列簇的名字
                    String columnFamilyName = columnFamilie.getNameAsString();
                    System.out.print("\t"+"columnFamilyName:"+columnFamilyName);
                }
                System.out.println();
            }

        } catch(IOException e){
            String msg = String.format("getAllTables error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public Table getTable(Connection connection, String tableName) {
        try{
            Table table;
            TableName name = TableName.valueOf(tableName);
            if(connection.getAdmin().tableExists(name)) {
                table = connection.getTable(name);
            }else {
                table = null;
            }
//        table = connection.getTable(name);
            return table;

        }catch(Exception e){
            String msg = String.format("getTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public void descTable(Connection connection, String tableName) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //判断表是否存在
            if(connection.getAdmin().tableExists(name)) {
                //获取表中列簇的描述信息
                HTableDescriptor tableDescriptor = connection.getAdmin().getTableDescriptor(name);
                //获取列簇中列的信息
                HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
                for(HColumnDescriptor columnFamily : columnFamilies) {
                    System.out.println(columnFamily);
                }

            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("descTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public boolean existTable(Connection connection, String tableName) {
        try{
            TableName name = TableName.valueOf(tableName);
            return connection.getAdmin().tableExists(name);

        }catch(Exception e){
            String msg = String.format("existTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public void disableTable(Connection connection, String tableName) {
        try{
            TableName name = TableName.valueOf(tableName);

            if(connection.getAdmin().tableExists(name)) {
                if(connection.getAdmin().isTableEnabled(name)) {
                    connection.getAdmin().disableTable(name);
                }else {
                    System.out.println("table不是活动状态");
                }
            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("disableTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public void dropTable(Connection connection, String tableName) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //判断表是否存在
            if(connection.getAdmin().tableExists(name)) {
                //判断表是否处于可用状态
                boolean tableEnabled = connection.getAdmin().isTableEnabled(name);

                if(tableEnabled) {
                    //使表变成不可用状态
                    connection.getAdmin().disableTable(name);
                }
                //删除表
                connection.getAdmin().deleteTable(name);
                //判断表是否存在
                if(connection.getAdmin().tableExists(name)) {
                    System.out.println("删除失败");
                }else {
                    System.out.println("删除成功");
                }

            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("dropTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public void modifyTable_add(Connection connection, String tableName, String[] addColumn) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //判断表是否存在
            if(connection.getAdmin().tableExists(name)) {
                //判断表是否可用状态
                boolean tableEnabled = connection.getAdmin().isTableEnabled(name);

                if(tableEnabled) {
                    //使表变成不可用
                    connection.getAdmin().disableTable(name);
                }
                //根据表名得到表
                HTableDescriptor tableDescriptor = connection.getAdmin().getTableDescriptor(name);
                for(String add : addColumn) {
                    HColumnDescriptor addColumnDescriptor = new HColumnDescriptor(add);
                    tableDescriptor.addFamily(addColumnDescriptor);
                }
                //替换该表所有的列簇
                connection.getAdmin().modifyTable(name, tableDescriptor);

            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("modifyTable_add error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public void modifyTable_remove(Connection connection, String tableName, String[] removeColumn) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //判断表是否存在
            if(connection.getAdmin().tableExists(name)) {
                //判断表是否可用状态
                boolean tableEnabled = connection.getAdmin().isTableEnabled(name);

                if(tableEnabled) {
                    //使表变成不可用
                    connection.getAdmin().disableTable(name);
                }
                //根据表名得到表
                HTableDescriptor tableDescriptor = connection.getAdmin().getTableDescriptor(name);
                for(String remove : removeColumn) {
                    HColumnDescriptor removeColumnDescriptor = new HColumnDescriptor(remove);
                    tableDescriptor.removeFamily(removeColumnDescriptor.getName());
                }
                //替换该表所有的列簇
                connection.getAdmin().modifyTable(name, tableDescriptor);

            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("modifyTable_remove error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public void modifyTable(Connection connection, String tableName, String[] addColumn, String[] removeColumn) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //判断表是否存在
            if(connection.getAdmin().tableExists(name)) {
                //判断表是否可用状态
                boolean tableEnabled = connection.getAdmin().isTableEnabled(name);

                if(tableEnabled) {
                    //使表变成不可用
                    connection.getAdmin().disableTable(name);
                }
                //根据表名得到表
                HTableDescriptor tableDescriptor = connection.getAdmin().getTableDescriptor(name);
                //创建列簇结构对象，添加列
                for(String add : addColumn) {
                    HColumnDescriptor addColumnDescriptor = new HColumnDescriptor(add);
                    tableDescriptor.addFamily(addColumnDescriptor);
                }
                //创建列簇结构对象，删除列
                for(String remove : removeColumn) {
                    HColumnDescriptor removeColumnDescriptor = new HColumnDescriptor(remove);
                    tableDescriptor.removeFamily(removeColumnDescriptor.getName());
                }

                connection.getAdmin().modifyTable(name, tableDescriptor);


            }else {
                System.out.println("table不存在");
            }

        }catch(Exception e){
            String msg = String.format("modifyTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public void modifyTable(Connection connection, String tableName, HColumnDescriptor hcds) {
        try{
            //转化为表名
            TableName name = TableName.valueOf(tableName);
            //根据表名得到表
            HTableDescriptor tableDescriptor = connection.getAdmin().getTableDescriptor(name);
            //获取表中所有的列簇信息
            HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();

            boolean flag = false;
            //判断参数中传入的列簇是否已经在表中存在
            for(HColumnDescriptor columnFamily : columnFamilies) {
                if(columnFamily.equals(hcds)) {
                    flag = true;
                }
            }
            //存在提示，不存在直接添加该列簇信息
            if(flag) {
                System.out.println("该列簇已经存在");
            }else {
                tableDescriptor.addFamily(hcds);
                connection.getAdmin().modifyTable(name, tableDescriptor);
            }

        }catch(Exception e){
            String msg = String.format("modifyTable error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    /**
     *
     * 添加数据的定义前提
     * @param familyName
     * @param name
     * @throws IOException
     */
    private void putData_Temp(Connection connection,String familyName, TableName name) {
        try{
            if(connection.getAdmin().tableExists(name)) {
            }else {
                //根据表明创建表结构
                HTableDescriptor tableDescriptor = new HTableDescriptor(name);
                //定义列簇的名字
                HColumnDescriptor columnFamilyName = new HColumnDescriptor(familyName);
                tableDescriptor.addFamily(columnFamilyName);
                connection.getAdmin().createTable(tableDescriptor);
            }

        }catch(Exception e){
            String msg = String.format("putData_Temp error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }


    }

    @Override
    public void putData(Connection connection, String tableName, String rowKey, String familyName, String columnName, String value, long timestamp) {
        try{
            // 转化为表名
            TableName name = TableName.valueOf(tableName);
            // 添加数据之前先判断表是否存在，不存在的话先创建表
            putData_Temp(connection,familyName, name);

            Table table = connection.getTable(name);
            Put put = new Put(rowKey.getBytes());

            //put.addColumn(familyName.getBytes(), columnName.getBytes(), value.getBytes());
            put.addImmutable(familyName.getBytes(), columnName.getBytes(), timestamp, value.getBytes());
            table.put(put);

        }catch(Exception e){
            String msg = String.format("putData error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }

    @Override
    public Result getResult(Connection connection, String tableName, String rowKey, String familyName) {
        try{
            Result result;
            TableName name = TableName.valueOf(tableName);
            if(connection.getAdmin().tableExists(name)) {
                Table table = connection.getTable(name);
                Get get = new Get(rowKey.getBytes());
                get.addFamily(familyName.getBytes());
                result = table.get(get);

            }else {
                result = null;
            }

            return result;

        }catch(Exception e){
            String msg = String.format("getResult error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public Result getResult(Connection connection, String tableName, String rowKey, String familyName, String columnName) {
        try{
            Result result;
            TableName name = TableName.valueOf(tableName);
            if(connection.getAdmin().tableExists(name)) {
                Table table = connection.getTable(name);
                Get get = new Get(rowKey.getBytes());
                get.addColumn(familyName.getBytes(), columnName.getBytes());
                result = table.get(get);

            }else {
                result = null;
            }

            return result;

        }catch(Exception e){
            String msg = String.format("getResult error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }

    }

    @Override
    public Result getResultByVersion(Connection connection, String tableName, String rowKey, String familyName, String columnName, int versions) {
        try{
            Result result;
            TableName name = TableName.valueOf(tableName);
            if(connection.getAdmin().tableExists(name)) {
                Table table = connection.getTable(name);
                Get get = new Get(rowKey.getBytes());
                get.addColumn(familyName.getBytes(), columnName.getBytes());
                get.setMaxVersions(versions);
                result = table.get(get);

            }else {
                result = null;
            }

            return result;

        }catch(Exception e){
            String msg = String.format("getResultByVersion error. msg=%s",  e.getMessage());
            throw new HosServerException(ErrorCodes.ERROR_HBASE, msg);
        }
    }


}
