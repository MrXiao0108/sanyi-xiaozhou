package com.dzics.data.fms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.Message;
import com.dzics.data.common.base.model.constant.UpLoadSize;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.fms.model.dto.ReqUploadBase64;
import com.dzics.data.fms.service.FileService;
import com.dzics.data.fms.upload.CompressImg;
import com.dzics.data.fms.upload.fileHashutil.UploadUtil;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传实现
 *
 * @author ZhangChengJun
 * Date 2022/1/27.
 * @since
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private UploadUtil uploadUtil;
    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 文件流上传
     *
     * @param sub            用户
     * @param multipartFiles 文件数组
     * @return
     */
    @Override
    public Result putUpload(String sub, MultipartFile[] multipartFiles, String address, String parentPath) {
        if (multipartFiles != null) {
            String errorMsg = "";
            List<String> addressUrl = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                if (file.getSize() < UpLoadSize.SIZE) {
                    //   原始文件byte
                    try {
                        boolean image = isImage(file.getInputStream());
                        if (image) {
                            byte[] bytesStart = CompressImg.inputStream2byte(file.getInputStream());
//            压缩后文件byte
                            byte[] bytesEnd = CompressImg.compressPicForScale(bytesStart, UpLoadSize.SIZE_MIN);
                            InputStream inputStreamStart = CompressImg.byte2InputStream(bytesEnd);
                            Long size = Long.valueOf(bytesEnd.length);
//            上传文件名
                            String contentType = file.getContentType();
//                        文件名
                            String fileHashName = uploadUtil.getHashName(inputStreamStart, size, contentType);
//                       上传文件地址
                            String parpath = uploadUtil.getDirPath(contentType);
//                        文件磁盘地址
                            String dirPath = parentPath + parpath;
//            文件上传流
                            InputStream stream = CompressImg.byte2InputStream(bytesEnd);
                            File filePath = new File(dirPath);
                            if (!filePath.exists()) {
                                //创建文件夹
                                filePath.mkdirs();
                            }
                            //创建目标文件
                            String url = dirPath + fileHashName;
                            File targetFile = new File(url);
                            FileOutputStream fos = new FileOutputStream(targetFile);
                            try {
//                       写入目标文件
                                byte[] buffer = new byte[1024 * 1024];
                                int byteRead = 0;
                                while ((byteRead = stream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, byteRead);
                                    fos.flush();
                                }
                                fos.close();
                                stream.close();
                            } catch (Throwable e) {
                                fos.close();
                                stream.close();
                            }
                            String getUrl = address + parpath + fileHashName;
                            addressUrl.add(getUrl);
                        } else {
                            errorMsg = errorMsg + "存在文件类型错误|";
                            continue;
                        }

                    } catch (IOException e) {
                        errorMsg = errorMsg + "稍后再试|";
                        log.error("上传文件获取文件流异常：{}", e);
                        continue;
                    } catch (MimeTypeException e) {
                        errorMsg = errorMsg + "文件类型错误|";
                        log.error("上传文件获取文件类型异常：{}", e);
                        continue;
                    }
                } else {
                    errorMsg = errorMsg + "文件过大";
                    log.error("文件过大");
                    continue;
                }
            }
            if (org.springframework.util.StringUtils.isEmpty(errorMsg)) {
                return Result.ok(addressUrl);
            } else {
                Result result = new Result(CustomExceptionType.OK, errorMsg);
                result.setData(addressUrl);
                return result;
            }
        }
        //文件为空
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_52);
    }

    public static boolean isImage(InputStream imageFile) {
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }

    /**
     * base64格式文件上传
     *
     * @param sub   用户
     * @param files base64 文件数组
     * @return
     */
    @Override
    public Result putAvatarBase64(String sub, ReqUploadBase64 files, String address, String parentPath) {
        List<String> imgUrl = new ArrayList<>();
        List<String> imgBase64 = files.getImgBase64();
        if (CollectionUtils.isNotEmpty(imgBase64)) {
            for (String base64 : imgBase64) {
//                文件名
                String fileHashName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
//                       上传文件地址
                String parpath = uploadUtil.getDirPathBase64("png");
//                        文件磁盘地址
                String dirPath = parentPath + parpath;
//            文件上传流
                File filePath = new File(dirPath);
                if (!filePath.exists()) {
                    //创建文件夹
                    filePath.mkdirs();
                }
                InputStream stream = null;
                FileOutputStream fos = null;
                try {
                    String[] urlXXX = base64.split(",");
                    String img = urlXXX[1];
                    byte[] imgByte = new byte[0];
                    imgByte = Base64.getDecoder().decode(img);
                    stream = CompressImg.byte2InputStream(imgByte);
                    //创建目标文件
                    String url = dirPath + fileHashName;
                    File targetFile = new File(url);
                    fos = new FileOutputStream(targetFile);
//                       写入目标文件
                    byte[] buffer = new byte[1024 * 1024];
                    int byteRead = 0;
                    while ((byteRead = stream.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteRead);
                        fos.flush();
                    }
                } catch (Throwable throwable) {
                    log.error("上传文件错误：{}", throwable.getMessage(), throwable);
                } finally {
                    try {
                        fos.close();
                        stream.close();
                    } catch (IOException e) {
                        log.error("关闭流异常：{}", e.getMessage(), e);
                    }
                }
                String getUrl = address + parpath + fileHashName;
                imgUrl.add(getUrl);
            }
            return Result.OK(imgUrl);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }

    @Override
    public Object putAvatar(MultipartFile[] files) {
        try {
            MultipartFile file = files[0];
            String originalFileName = file.getOriginalFilename();
            String substring = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            InputStream inputStream = file.getInputStream();
            StorePath storePath = storageClient.uploadFile("group1", inputStream, file.getSize(), substring);
            return storePath;
        } catch (Throwable throwable) {
            return throwable.getMessage();
        }
    }
}
