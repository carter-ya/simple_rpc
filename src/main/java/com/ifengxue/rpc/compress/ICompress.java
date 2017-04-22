package com.ifengxue.rpc.compress;

/**
 * 压缩/解压缩接口
 *
 * Created by LiuKeFeng on 2017-04-20.
 */
public interface ICompress {
    /**
     * 压缩
     * @param buffer
     * @return
     */
    byte[] compress(byte[] buffer);

    /**
     * 解压缩
     * @param buffer
     * @return
     */
    byte[] decompress(byte[] buffer);
}
