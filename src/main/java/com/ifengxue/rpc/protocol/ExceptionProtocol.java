package com.ifengxue.rpc.protocol;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Optional;

/**
 * 异常通讯协议
 *
 * Created by LiuKeFeng on 2017-04-25.
 */
public class ExceptionProtocol implements Serializable {
    private static final long serialVersionUID = 2967563984190657342L;
    private String message;
    private InnerStackTraceElement[] innerStackTraceElements;
    public ExceptionProtocol() {}

    /**
     * 从{@link Exception} 构建异常协议
     * @param e 异常对象
     * @return
     */
    public static ExceptionProtocol fromException(Exception e) {
        return fromThrowable(e);
    }

    /**
     * 从{@link Throwable} 构建异常协议
     * @param e 异常对象
     * @return
     */
    public static ExceptionProtocol fromThrowable(Throwable e) {
        //转换为真正的异常
        if (e instanceof InvocationTargetException) {
            e = e.getCause();
        }
        ExceptionProtocol protocol = new ExceptionProtocol();
        protocol.message = e.getMessage();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        protocol.innerStackTraceElements = new InnerStackTraceElement[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            InnerStackTraceElement innerStackTraceElement = new InnerStackTraceElement();
            innerStackTraceElement.setDeclaringClass(stackTraceElement.getClassName());
            innerStackTraceElement.setMethodName(stackTraceElement.getMethodName());
            innerStackTraceElement.setFileName(stackTraceElement.getFileName());
            innerStackTraceElement.setLineNumber(stackTraceElement.getLineNumber());
            protocol.innerStackTraceElements[i] = innerStackTraceElement;
        }
        return protocol;
    }

    /**
     * 转换为{@link RemoteException}
     * @return
     */
    public RemoteException asRemoteException() {
        StringBuilder remoteExceptionContentBuilder = new StringBuilder("Exception cause by remote service");
        remoteExceptionContentBuilder.append(Optional.ofNullable(message).map(s -> ":" + s).orElse("."));
        remoteExceptionContentBuilder.append("\n\t==========Remote Stack Trace Begin==========");
        for (InnerStackTraceElement innerStackTraceElement : innerStackTraceElements) {
            remoteExceptionContentBuilder.append("\n\tat ")
                    .append(innerStackTraceElement.declaringClass)
                    .append(".").append(innerStackTraceElement.methodName)
                    .append("(").append(innerStackTraceElement.fileName).append(":")
                    .append(innerStackTraceElement.lineNumber).append(")");
        }
        remoteExceptionContentBuilder.append("\n\t==========Remote Stack Trace End==========");
        RemoteException remoteException = new RemoteException(remoteExceptionContentBuilder.toString());
        return remoteException;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InnerStackTraceElement[] getInnerStackTraceElements() {
        return innerStackTraceElements;
    }

    /**
     * 转换为{@link StackTraceElement}
     * @return
     */
    public StackTraceElement[] asStackTraceElements() {
        StackTraceElement[] stackTraceElements = new StackTraceElement[innerStackTraceElements.length];
        for (int i = 0; i < innerStackTraceElements.length; i++) {
            InnerStackTraceElement innerStackTraceElement = innerStackTraceElements[i];
            stackTraceElements[i] = new StackTraceElement(
                    innerStackTraceElement.getDeclaringClass(),
                    innerStackTraceElement.getMethodName(),
                    innerStackTraceElement.getFileName(),
                    innerStackTraceElement.getLineNumber());
        }
        return stackTraceElements;
    }

    public void setInnerStackTraceElements(InnerStackTraceElement[] innerStackTraceElements) {
        this.innerStackTraceElements = innerStackTraceElements;
    }

    @Override
    public String toString() {
        return "ExceptionProtocol{" +
                "message='" + message + '\'' +
                ", innerStackTraceElements=" + Arrays.toString(innerStackTraceElements) +
                '}';
    }

    /**
     * 用于序列化{@link StackTraceElement}
     */
    private static class InnerStackTraceElement implements Serializable {
        private static final long serialVersionUID = 4467700853352381315L;
        private String declaringClass;
        private String methodName;
        private String fileName;
        private int    lineNumber;
        private InnerStackTraceElement() {}

        public String getDeclaringClass() {
            return declaringClass;
        }

        public void setDeclaringClass(String declaringClass) {
            this.declaringClass = declaringClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return "InnerStackTraceElement{" +
                    "declaringClass='" + declaringClass + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", lineNumber=" + lineNumber +
                    '}';
        }
    }
}
