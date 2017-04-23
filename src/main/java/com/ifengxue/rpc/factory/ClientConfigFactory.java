package com.ifengxue.rpc.factory;

import com.ifengxue.rpc.client.pool.IChannelPool;
import com.ifengxue.rpc.client.pool.SimpleChannelPool;
import com.ifengxue.rpc.client.register.IRegisterCenter;
import com.ifengxue.rpc.client.register.SimpleRegisterCenter;
import com.ifengxue.rpc.protocol.enums.CompressTypeEnum;
import com.ifengxue.rpc.protocol.enums.SerializerTypeEnum;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by LiuKeFeng on 2017-04-21.
 */
public class ClientConfigFactory {
    private static final ClientConfigFactory INSTANCE = new ClientConfigFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigFactory.class);
    private static volatile boolean isInitial = false;
    private static IRegisterCenter registerCenter;
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
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
}
