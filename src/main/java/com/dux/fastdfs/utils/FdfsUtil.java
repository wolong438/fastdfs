package com.dux.fastdfs.utils;

import com.github.tobato.fastdfs.exception.FdfsException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class FdfsUtil {
  public static final String DEFAULT_CHARSET = "UTF-8";

  /**
   * md5加密
   *
   * @param source the input buffer
   * @return md5 string
   */
  public static String md5(byte[] source) throws NoSuchAlgorithmException {
    char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
    md.update(source);
    byte tmp[] = md.digest();
    char str[] = new char[32];
    int k = 0;
    for (int i = 0; i < 16; i++) {
      str[k++] = hexDigits[tmp[i] >>> 4 & 0xf];
      str[k++] = hexDigits[tmp[i] & 0xf];
    }

    return new String(str);
  }

  /**
   * 获取访问服务器的token，拼接到地址后面
   *
   * @param filepath 文件路径 group1/M00/00/00/wKgzgFnkTPyAIAUGAAEoRmXZPp876.jpeg
   * @param ts              unix timestamp, unit: second
   * @param httpSecretKey      httpSecretKey 密钥
   * @return 返回token，如： token=078d370098b03e9020b82c829c205e1f&ts=1508141521
   */
  public static String getToken(String filepath, int ts, String httpSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, FdfsException {
    byte[] bsFilename = filepath.getBytes(DEFAULT_CHARSET);
    byte[] bsKey = httpSecretKey.getBytes(DEFAULT_CHARSET);
    byte[] bsTimestamp = (new Integer(ts)).toString().getBytes(DEFAULT_CHARSET);

    byte[] buff = new byte[bsFilename.length + bsKey.length + bsTimestamp.length];
    System.arraycopy(bsFilename, 0, buff, 0, bsFilename.length);
    System.arraycopy(bsKey, 0, buff, bsFilename.length, bsKey.length);
    System.arraycopy(bsTimestamp, 0, buff, bsFilename.length + bsKey.length, bsTimestamp.length);

    return md5(buff);
  }
}
