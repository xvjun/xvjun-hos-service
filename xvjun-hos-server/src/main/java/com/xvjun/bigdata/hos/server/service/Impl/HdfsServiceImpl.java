package com.xvjun.bigdata.hos.server.service.Impl;

import com.xvjun.bigdata.hos.common.HosConfiguration;
import com.xvjun.bigdata.hos.server.service.IHdfsService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class HdfsServiceImpl implements IHdfsService {

    private static Logger logger = Logger.getLogger(HdfsServiceImpl.class);
    private FileSystem fileSystem;
    private long defaultBlockSize = 128*1024*1024;
    private long initBlockSize = defaultBlockSize/2;

    public HdfsServiceImpl() throws Exception{
        String confDir = System.getenv("HADOOP_CONF_DIR");
        if(confDir == null){
            confDir = System.getProperty("HADOOP_CONF_DIR");
        }
        if(confDir == null){
            HosConfiguration hosConfiguration = HosConfiguration.getConfiguration();
            confDir = hosConfiguration.getString("hadoop.conf.dir");
        }
        if(!new File(confDir).exists()){
            throw new FileNotFoundException(confDir);
        }
        Configuration configuration = new Configuration();
        configuration.addResource(new Path(confDir + "/core-site.xml"));
        configuration.addResource(new Path(confDir + "/hdfs-site.xml"));
        fileSystem = FileSystem.get(new URI("hdfs://hadoop001:9000"),configuration);

    }
    public HdfsServiceImpl(String HDFS_PATH) throws Exception{
        Configuration configuration = new Configuration();
        fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, "root");

    }

    /**
     * 将文件保存到hdfs中
     * @param dir 保存的文件目录
     * @param name 文件名
     * @param input 文件内容的流
     * @param length 文件长度
     * @param replication 副本数量
     * @throws IOException
     */
    @Override
    public void saveFile(String dir, String name, InputStream input, long length, short replication) throws IOException {
        //TODO... 判断dir是否存在，不存在则新建
        Path dirPath = new Path(dir);
        try{
            if(!fileSystem.exists(dirPath)){
                boolean succ = fileSystem.mkdirs(dirPath,FsPermission.getDefault()); //同时赋予默认权限00777
                logger.info("create dir" +dirPath+"success" + succ);
                if(!succ){
                    throw new IOException("dir create failed:" + dir);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        //TODO... 保存文件
        Path path = new Path(dir + name);
        long blockSize = length <= initBlockSize ? initBlockSize : defaultBlockSize;
        FSDataOutputStream outputStream = fileSystem.create(path,true,521*1024,replication,blockSize);
        try{
            fileSystem.setPermission(path,FsPermission.getDefault());
            byte[] buffer = new byte[521*1024];
            int len = -1;
            while((len = input.read(buffer)) > 0){
                outputStream.write(buffer,0,len);
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            input.close();
            outputStream.close();
        }
    }

    @Override
    public void deleteFile(String dir, String name) throws IOException {
        fileSystem.delete(new Path(dir + "/" + name),false);
    }

    @Override
    public InputStream openFile(String dir, String name) throws IOException {
        return fileSystem.open(new Path(dir + "/" + name));
    }

    @Override
    public void mikDir(String dir) throws IOException {
        fileSystem.mkdirs(new Path(dir));
    }

    @Override
    public void deleteDir(String dir) throws IOException {
        this.fileSystem.delete(new Path(dir),true);
    }
}
