package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.client.pool.ChannelPoolConfig;
import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.client.pool.SimpleChannelPool;
import com.ifengxue.rpc.client.register.IRegisterCenter;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ClientConfigFactory {
    private static final ClientConfigFactory INSTANCE = new ClientConfigFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigFactory.class);
    private static volatile boolean isInitial = false;
    private static IRegisterCenter registerCenter;
    private static ChannelPoolConfig channelPoolConfig = new ChannelPoolConfig();
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
            LOGGER.info("RegisterCenter:" + className);
            registerCenter = (IRegisterCenter) Class.forName(className).newInstance();
            LOGGER.info("init registerCenter...");
            registerCenter.init(registerCenterElement);
            //初始化连接池配置
            Element socketPoolElement = rootElement.element("socket-pool");
            List<Element> propertyElements = socketPoolElement.elements("property");
            for (Element propertyElement : propertyElements) {
                String propertyName = propertyElement.attributeValue("name");
                String propertyValue = propertyElement.attributeValue("value");
                BeanUtils.setProperty(channelPoolConfig, propertyName, propertyValue);
            }
            LOGGER.info("初始化连接池配置信息成功...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientConfigFactory getInstance() {
        return INSTANCE;
    }

    public long compressRequestIfLengthGreaterTo() {
        return 1024 * 1024 * 10;
    }

    public SerializerTypeEnum getSerializerTypeEnum() {
        return SerializerTypeEnum.KRYO_SERIALIZER;
    }

    public CompressTypeEnum getCompressTypeEnum() {
        return CompressTypeEnum.DEFLATER;
    }

    public IChannelPool getChannelPool() {
        return new SimpleChannelPool(getRegisterCenter());
    }

    public IRegisterCenter getRegisterCenter() {
        return registerCenter;
    }

    public ChannelPoolConfig getChannelPoolConfig() {
        return channelPoolConfig;
    }
}
