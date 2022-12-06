package com.dzics.data.fms.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.fms.model.dto.ReqUploadBase64;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理接口
 *
 * @author ZhangChengJun
 * Date 2022/1/27.
 * @since
 */
public interface FileService {
    /**
     * 文件流上传
     *
     * @param sub      用户
     * @param file     文件数组
     * @param address  访问地址
     * @param filePath 上传磁盘路径
     * @return
     */
    Result putUpload(String sub, MultipartFile[] file, String address, String filePath);

    /**
     * base64格式文件上传
     *
     * @param sub      用户
     * @param files    base64 文件数组
     * @param address  访问地址
     * @param filePath 上传磁盘路径
     * @return
     */
    Result putAvatarBase64(String sub, ReqUploadBase64 files, String address, String filePath);

    Object putAvatar(MultipartFile [] files);
}
