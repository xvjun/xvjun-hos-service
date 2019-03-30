package com.xvjun.bigdata.hos.web.rest;


import com.google.common.base.Splitter;
import com.xvjun.bigdata.hos.common.HosHeaders;
import com.xvjun.bigdata.hos.common.ObjectUtil.HosObject;
import com.xvjun.bigdata.hos.common.ObjectUtil.HosObjectSummary;
import com.xvjun.bigdata.hos.common.ObjectUtil.ObjectListResult;
import com.xvjun.bigdata.hos.core.usermgr.model.SystemRole;
import com.xvjun.bigdata.hos.core.usermgr.model.UserInfo;
import com.xvjun.bigdata.hos.server.model.BucketModel;
import com.xvjun.bigdata.hos.server.service.IBucketService;
import com.xvjun.bigdata.hos.server.service.IHosStoreService;
import com.xvjun.bigdata.hos.web.security.ContextUtil;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("hos/v1/")
public class HosController extends BaseController{

    private static Logger logger = Logger.getLogger(HosController.class);

    @Autowired
    @Qualifier("bucketServiceImpl")
    IBucketService bucketService;

    @Autowired
    @Qualifier("hosStoreService")
    IHosStoreService hosStoreService;

    private static long MAX_FILE_IN_MEMORY = 2 * 1024 * 1024;

    private final int readBufferSize = 32 * 1024;

    private static String TMP_DIR = System.getProperty("user.dir") + File.separator + "tmp";

    public HosController(){
        File file = new File(TMP_DIR);
        file.mkdir();
    }

    /**
     * 创建bucket，不允许游客
     * @param bucketName
     * @param detail
     * @return
     */
    @RequestMapping(value = "bucket",method = RequestMethod.POST)
    public Object createBucket(@RequestParam("bucket") String bucketName,
                               @RequestParam(name = "detail",required = false,defaultValue = "") String detail){
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if(!currentUser.getSystemRole().equals(SystemRole.VISITER)){
            bucketService.addBucket(currentUser,bucketName,detail);
            try{
                hosStoreService.createBucketStore(bucketName);

            } catch (Exception e){
                bucketService.deleteBucket(bucketName);
                return "create bucket error";
            }
            return "success";
        }
        return "permision defind";
    }

    /**
     * 删除bucket,
     * @param bucket
     * @return
     */
    @RequestMapping(value = "bucket", method = RequestMethod.DELETE)
    public Object deleteBucket(@RequestParam("bucket") String bucket) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkBucketOwner(currentUser.getUserName(), bucket)) {
            try {
                hosStoreService.deleteBucketStore(bucket);
            } catch (IOException ioe) {
                return "delete bucket error";
            }
            bucketService.deleteBucket(bucket);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    /**
     * 更新buxket，判断bucket是否属于user
     * @param bucket
     * @param detail
     * @return
     */
    @RequestMapping(value = "bucket", method = RequestMethod.PUT)
    public Object updateBucket(
            @RequestParam(name = "bucket") String bucket,
            @RequestParam(name = "detail") String detail) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessControl
                .checkBucketOwner(currentUser.getUserName(), bucketModel.getBucketName())) {
            bucketService.updateBucket(bucket, detail);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    /**
     * 获得bucket信息，通过token判读是否有权限
     * @param bucket
     * @return
     */
    @RequestMapping(value = "bucket", method = RequestMethod.GET)
    public Object getBucket(@RequestParam(name = "bucket") String bucket) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessControl
                .checkPermission(currentUser.getUserId(), bucketModel.getBucketName())) {
            return bucketModel;
        }
        return "PERMISSION DENIED";

    }

    /**
     * 查看当前用户的所有bucket
     * @return
     */
    @RequestMapping(value = "bucket/list", method = RequestMethod.GET)
    public Object getBucket() {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        List<BucketModel> bucketModels = bucketService.getUserBuckets(currentUser.getUserId());
        return bucketModels;
    }

    /**
     * 将文件或者目录保存到bucket中的key路径下
     * @param bucket
     * @param key
     * @param mediaType
     * @param file
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "object", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public Object putObject(@RequestParam("bucket") String bucket,
                            @RequestParam("key") String key,
                            @RequestParam(value = "mediaType", required = false) String mediaType,
                            @RequestParam(value = "content", required = false) MultipartFile file,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN); // 403
            response.getWriter().write("Permission denied");
            return "Permission denied";
        }
        if (!key.startsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST); //400
            response.getWriter().write("object key must start with /");
        }

        Enumeration<String> headNames = request.getHeaderNames();
        Map<String, String> attrs = new HashMap<>();
        String contentEncoding = request.getHeader("content-encoding");
        if (contentEncoding != null) {
            attrs.put("content-encoding", contentEncoding);
        }
        while (headNames.hasMoreElements()) {
            String header = headNames.nextElement();
            if (header.startsWith(HosHeaders.COMMON_ATTR_PREFIX)) {
                attrs.put(header.replace(HosHeaders.COMMON_ATTR_PREFIX, ""), request.getHeader(header));
            }
        }
        ByteBuffer buffer = null;
        File distFile = null;
        try {
            //put dir object
            if (key.endsWith("/")) {
                if (file != null) {
                    response.setStatus(HttpStatus.SC_BAD_REQUEST); //400
                    file.getInputStream().close();
                    return null;
                }
                hosStoreService.put(bucket, key, null, 0, mediaType, attrs);
                response.setStatus(HttpStatus.SC_OK);
                return "success";
            }
            if (file == null || file.getSize() == 0) {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.getWriter().write("object content could not be empty");
                return "object content could not be empty";
            }

            if (file != null) {
                if (file.getSize() > MAX_FILE_IN_MEMORY) {
                    distFile = new File(TMP_DIR + File.separator + UUID.randomUUID().toString());
                    file.transferTo(distFile);
                    file.getInputStream().close();
                    buffer = new FileInputStream(distFile).getChannel()
                            .map(FileChannel.MapMode.READ_ONLY, 0, file.getSize());
                } else {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    org.apache.commons.io.IOUtils.copy(file.getInputStream(), outputStream);
                    buffer = ByteBuffer.wrap(outputStream.toByteArray());
                    file.getInputStream().close();
                }
            }
            hosStoreService.put(bucket, key, buffer, file.getSize(), mediaType, attrs);
            return "success";
        } catch (IOException ioe) {
            logger.error(ioe);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("server error");
            return "server error";
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
            if (file != null) {
                try {
                    file.getInputStream().close();
                } catch (Exception e) {
                    //nothing to do
                }
            }
            if (distFile != null) {
                distFile.delete();
            }
        }
    }

    /**
     * 获得bucket中startkey到endkey的文件和目录
     * @param bucket
     * @param startKey
     * @param endKey
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "object/list", method = RequestMethod.GET)
    public ObjectListResult listObject(@RequestParam("bucket") String bucket,
                                       @RequestParam("startKey") String startKey,
                                       @RequestParam("endKey") String endKey,
                                       HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (startKey.compareTo(endKey) > 0) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
        }
        ObjectListResult result = new ObjectListResult();
        List<HosObjectSummary> summaryList = hosStoreService.list(bucket, startKey, endKey);
        result.setBucket(bucket);
        if (summaryList.size() > 0) {
            result.setMaxkey(summaryList.get(summaryList.size() - 1).getKey());
            result.setMinkey(summaryList.get(0).getKey());
        }
        result.setObjectCount(summaryList.size());
        result.setHosObjectSummaryList(summaryList);
        return result;
    }

    /**
     * 通过key获得bucket中的文件或者目录
     * @param bucket
     * @param key
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "object/info", method = RequestMethod.GET)
    public HosObjectSummary getSummary(String bucket, String key, HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        HosObjectSummary summary = hosStoreService.getSummary(bucket, key);
        if (summary == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
        return summary;
    }

    /**
     * 通过前缀查询bucket中dir中的文件或者目录，并可以设置startkey
     * @param bucket
     * @param dir
     * @param prefix
     * @param start
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "object/list/prefix", method = RequestMethod.GET)
    public ObjectListResult listObjectByPrefix(@RequestParam("bucket") String bucket,
                                               @RequestParam("dir") String dir,
                                               @RequestParam("prefix") String prefix,
                                               @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                               HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }
        ObjectListResult result = this.hosStoreService.listByPrefix(bucket, dir, prefix, start, 100);
        return result;
    }

    /**
     * 在bucket中的dir中获得文件和目录的列表，可以设置startkey
     * @param bucket
     * @param dir
     * @param start
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "object/list/dir", method = RequestMethod.GET)
    public ObjectListResult listObjectByDir(@RequestParam("bucket") String bucket,
                                            @RequestParam("dir") String dir,
                                            @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                            HttpServletResponse response)
            throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }

        ObjectListResult result = this.hosStoreService.listDir(bucket, dir, start, 100);
        return result;
    }

    /**
     * 删除这个文件或者目录，需要有权限
     * @param bucket
     * @param key
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "object", method = RequestMethod.DELETE)
    public Object deleteObject(@RequestParam("bucket") String bucket,
                               @RequestParam("key") String key) throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            return  "PERMISSION DENIED";
        }
        this.hosStoreService.deleteObject(bucket, key);
        return "success";
    }

    /**
     * 获得bucket中的key下的文件和目录
     * @param bucket
     * @param key
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "object/content", method = RequestMethod.GET)
    public void getObject(@RequestParam("bucket") String bucket,
                          @RequestParam("key") String key, HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return;
        }
        HosObject object = this.hosStoreService.getObject(bucket, key);
        if (object == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return;
        }
        response.setHeader(HosHeaders.COMMON_OBJ_BUCKET, bucket);
        response.setHeader(HosHeaders.COMMON_OBJ_KEY, key);
        response.setHeader(HosHeaders.RESPONSE_OBJ_LENGTH, "" + object.getMetaData().getLength());
        String iflastModify = request.getHeader("If-Modified-Since");
        String lastModify = object.getMetaData().getLastModiafyTime() + "";
        response.setHeader("Last-Modified", lastModify);
        String contentEncoding = object.getMetaData().getContentEcoding();
        if (contentEncoding != null) {
            response.setHeader("content-encoding", contentEncoding);
        }
        if (iflastModify != null && iflastModify.equals(lastModify)) {
            response.setStatus(HttpStatus.SC_NOT_MODIFIED);
            return;
        }
        response.setHeader(HosHeaders.COMMON_OBJ_BUCKET, object.getMetaData().getBucket());
        response.setContentType(object.getMetaData().getMediaType());
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = object.getContent();
        try {
            byte[] buffer = new byte[readBufferSize];
            int len = -1;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            response.flushBuffer();
        } finally {
            inputStream.close();
            outputStream.close();
        }

    }

}