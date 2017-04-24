package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.client.pool.ChannelPoolConfig;
import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.client.pool.SimpleChannelPool;
import com.ifengxue.rpc.client.register.IRegisterCenter;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ClientConfigFactory {
    private static final String SERIALIZE_TYPE_KEY = "serialize";
    private static final String DEFAULT_SERIALIZE_TYPE = "kryo";
    private static final String COMPRESS_TYPE_KEY = "compress";
    private static final String DEFAULT_COMPRESS_TYPE = "deflater";
    private static final String MIN_COMPRESS_FRAME_LENGTH_KEY = "minCompressFrameLength";
    private static final String DEFAULT_MIN_COMPRESS_FRAME_LENGTH = "3145728";
    private static final ClientConfigFactory INSTANCE = new ClientConfigFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigFactory.class);
    private static volatile boolean isInitial = false;
    private static IRegisterCenter registerCenter;
    private static ChannelPoolConfig channelPoolConfig = new ChannelPoolConfig();
    private static Properties protocolProperties = new Properties();
    private ClientConfigFactory() {}

    public static synchronized void initConfigFactory(String config) {
        if (isInitial) {
            throw new IllegalStateException("客户端已经完成初始化！");
        }
        LOGGER.info("Client config path:{}", config);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(config));
            Element rootElement = document.getRootElement();
            //初始化注册中心
            Element registerCenterElement = rootElement.element("register-center");
            String className = registerCenterElement.attributeValue("class");
            LOGGER.info("注册中心实现:" + className);
            registerCenter = (IRegisterCenter) Class.forName(className).newInstance();
            LOGGER.info("初始化注册中心成功...");
            registerCenter.init(registerCenterElement);
            //初始化连接池配置
            Element socketPoolElement = rootElement.element("socket-pool");
            if (socketPoolElement != null) {
                List<Element> propertyElements = socketPoolElement.elements("property");
                for (Element propertyElement : propertyElements) {
                    String propertyName = propertyElement.attributeValue("name");
                    String propertyValue = propertyElement.attributeValue("value");
                    BeanUtils.setProperty(channelPoolConfig, propertyName, propertyValue);
                }
                LOGGER.info("初始化连接池配置信息成功...");
            }
            //初始化协议
            Element protocolElement = rootElement.element("protocol");
            if (protocolElement != null) {
                protocolElement.attributeValue(SERIALIZE_TYPE_KEY, DEFAULT_SERIALIZE_TYPE);
                protocolElement.attributeValue(COMPRESS_TYPE_KEY, DEFAULT_COMPRESS_TYPE);
                protocolElement.attributeValue(MIN_COMPRESS_FRAME_LENGTH_KEY, DEFAULT_MIN_COMPRESS_FRAME_LENGTH);
                LOGGER.info("初始化协议成功...");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientConfigFactory getInstance() {
        return INSTANCE;
    }

    public long minCompressFrameLength() {
        return Long.parseLong(protocolProperties.getProperty(MIN_COMPRESS_FRAME_LENGTH_KEY, DEFAULT_MIN_COMPRESS_FRAME_LENGTH));
    }

    public SerializerTypeEnum getSerializerTypeEnum() {
        return SerializerTypeEnum.getEnumByName(protocolProperties.getProperty(SERIALIZE_TYPE_KEY, DEFAULT_SERIALIZE_TYPE));
    }

    public CompressTypeEnum getCompressTypeEnum() {
        return CompressTypeEnum.getEnumByName(protocolProperties.getProperty(COMPRESS_TYPE_KEY, DEFAULT_COMPRESS_TYPE));
    }

    public IChannelPool getChannelPool() {
        return new SimpleChannelPool(newKeyedChannelPoolConfig(), getRegisterCenter());
    }

    public IRegisterCenter getRegisterCenter() {
        return registerCenter;
    }

    public ChannelPoolConfig getChannelPoolConfig() {
        return channelPoolConfig;
    }

    /**
     * 创建用于commons-pool2配置的新实例
     * @return
     */
    public GenericKeyedObjectPoolConfig newKeyedChannelPoolConfig() {
        GenericKeyedObjectPoolConfig genericKeyedObjectPoolConfig = new GenericKeyedObjectPoolConfig();
        genericKeyedObjectPoolConfig.setMinIdlePerKey(channelPoolConfig.getMinIdle());
        genericKeyedObjectPoolConfig.setMaxTotalPerKey(channelPoolConfig.getMaxPoolSize());
        genericKeyedObjectPoolConfig.setMaxIdlePerKey(channelPoolConfig.getMaxIdle());
        genericKeyedObjectPoolConfig.setTestOnBorrow(channelPoolConfig.isTestOnBorrow());
        genericKeyedObjectPoolConfig.setMaxWaitMillis(channelPoolConfig.getMaxWaitTimeout());
        return genericKeyedObjectPoolConfig;
    }
}
