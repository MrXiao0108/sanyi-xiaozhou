package com.dzics.data.business.controller.file;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.fms.model.dto.ReqUploadBase64;
import com.dzics.data.fms.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"上传文件"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/uplaod")
public class UploadController {

    @Value("${file.ip.address}")
    private String address;
    @Value("${file.parent.path}")
    private String parentPath;

    @Autowired
    FileService businessUserService;

    @ApiOperation(value = "上传文件")
    @PostMapping(headers = {"content-type=multipart/form-data"})
    public Result putAvatar(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestParam("files") MultipartFile[] files
    ) {
        Result msg = businessUserService.putUpload(sub, files,address,parentPath);
        return msg;
    }

    @ApiOperation(value = "上传文件BASE64")
    @PostMapping(value = "/base64")
    public Result putAvatarBase64(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  @RequestBody ReqUploadBase64 files) {
        Result msg = businessUserService.putAvatarBase64(sub, files,address,parentPath);
        return msg;
    }

}
