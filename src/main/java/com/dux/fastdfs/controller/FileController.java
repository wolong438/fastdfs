package com.dux.fastdfs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.tobato.fastdfs.domain.MataData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.StringUtils.getFilename;

@Controller
public class FileController {
  @Autowired
  private FastFileStorageClient fastFileStorageClient;

  /**
   * 文件上传
   *
   * @param file
   * @return
   * @throws IOException
   */
  @ResponseBody
  @RequestMapping("/upload")
  public StorePath test(@RequestParam MultipartFile file) throws IOException {

    // 设置文件信息
    Set<MataData> mataData = new HashSet<>();
    mataData.add(new MataData("author", "fastdfs"));
    mataData.add(new MataData("description",file.getOriginalFilename()));

    // 上传   （文件上传可不填文件信息，填入null即可）
    StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), mataData);

    return storePath;
  }

  /**
   * 文件删除
   *
   * @param path 文件路径，例如：/group1/path=M00/00/00/rBsABlyI3zSABeg8AAAAAAAAAAA216.txt
   * @return Map 返回结果
   */
  @RequestMapping("/delete")
  @ResponseBody
  public Map delete(@RequestParam String path) {
    // 第一种删除：参数：完整地址
    fastFileStorageClient.deleteFile(path);

    // 第二种删除：参数：组名加文件路径
    // fastFileStorageClient.deleteFile(group,path);

    Map<String, Object> map = new HashMap<>();
    map.put("status", 200);
    map.put("msg", "删除成功");
    return map;
  }


  /**
   * 文件下载
   *
   * @param path 文件路径，例如：/group1/path=M00/00/00/rBsABlyI3zSABeg8AAAAAAAAAAA216.txt
   * @param filename 下载的文件命名
   * @return
   */
  @RequestMapping("/download")
  public void downLoad(@RequestParam String path, @RequestParam(required = false) String filename, HttpServletResponse response) throws IOException {
    // 获取文件
    StorePath storePath = StorePath.praseFromUrl(path);
    if (StringUtils.isBlank(filename)) {
      filename = FilenameUtils.getName(storePath.getPath());
    }
    byte[] bytes = fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());

    //设置相应类型application/octet-stream        （注：applicatoin/octet-stream 为通用，一些其它的类型苹果浏览器下载内容可能为空）
    response.reset();
    response.setContentType("applicatoin/octet-stream");
    //设置头信息                 Content-Disposition为属性名  附件形式打开下载文件   指定名称为 设定的fileName
    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
    // 写入到流
    ServletOutputStream out = response.getOutputStream();
    out.write(bytes);
    out.close();
  }

  /**
   * 文件查看
   *
   * @param path 文件路径，例如：/group1/path=M00/00/00/rBsABlyI3zSABeg8AAAAAAAAAAA216.txt
   * @return
   */
  @RequestMapping("/file")
  public void downLoad(@RequestParam String path, HttpServletResponse response) throws IOException {
    // 获取文件
    StorePath storePath = StorePath.praseFromUrl(path);
    String filename = FilenameUtils.getName(storePath.getPath());
    byte[] bytes = fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());

    // 设置消息头
    response.reset();
    response.setContentType("applicatoin/octet-stream");
    response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(filename, "UTF-8"));

    // 写入到流
    ServletOutputStream out = response.getOutputStream();
    out.write(bytes);
    out.close();
  }
}
