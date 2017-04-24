package com.ifengxue.rpc.client.pool;

import java.io.Serializable;

/**
 * Created by LiuKeFeng on 2017-04-24.
 */
public class ChannelPoolConfig implements Serializable {
    private static final long serialVersionUID = -669112614489736190L;
    private int connectTimeout = 3000;
    private int readTimeout = 10000;
    private int sendTimeout = 5000;
    private int minPoolSize = 1;
    private int maxPoolSize = 100;
    private long maxFrameLength = 5242880;
    private int minIdle = 0;
    private int maxIdle = 1;
    private boolean testOnBorrow = true;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 该方法会确保连接超时时间最小为1
     * @return
     */
    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout <= 0) {
            connectTimeout = 1;
        }
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 该方法会确保读取超时时间最小为1
     * @return
     */
    public void setReadTimeout(int readTimeout) {
        if (readTimeout <= 0) {
            readTimeout = 1;
        }
        this.readTimeout = readTimeout;
    }

    public int getSendTimeout() {
        return sendTimeout;
    }

    /**
     * 该方法会确保发送超时时间最小为1
     * @return
     */
    public void setSendTimeout(int sendTimeout) {
        if (sendTimeout <= 0) {
            sendTimeout = 1;
        }
        this.sendTimeout = sendTimeout;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * 该方法会确保连接池中连接数量最小为1
     * @param minPoolSize
     */
    public void setMinPoolSize(int minPoolSize) {
        if (minPoolSize <= 0) {
            minPoolSize = 1;
        }
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * 该方法会确保连接池中最大连接数最小为1
     * @return
     */
    public void setMaxPoolSize(int maxPoolSize) {
        if (maxPoolSize <= 0) {
            maxPoolSize = 1;
        }
        this.maxPoolSize = maxPoolSize;
    }

    public long getMaxFrameLength() {
        return maxFrameLength;
    }

    /**
     * 该方法会确保最大帧长度最小为1
     * @return
     */
    public void setMaxFrameLength(long maxFrameLength) {
        if (maxFrameLength <= 0) {
            maxFrameLength = 1;
        }
        this.maxFrameLength = maxFrameLength;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        if (minIdle < 0) {
            minIdle = 0;
        }
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        if (maxIdle < 0) {
            maxIdle = 0;
        }
        this.maxIdle = maxIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    @Override
    public String toString() {
        return "ChannelPoolConfig{" +
                "connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", sendTimeout=" + sendTimeout +
                ", minPoolSize=" + minPoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", maxFrameLength=" + maxFrameLength +
                '}';
    }
}
