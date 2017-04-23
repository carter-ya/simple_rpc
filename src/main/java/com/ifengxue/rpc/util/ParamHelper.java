package com.ifengxue.rpc.util;

import java.util.*;

/**
 * 用来解析运行时外界传入的参数
 * 
 * @author LiuKeFeng
 * @date 2016年12月13日
 */
public final class ParamHelper {
	/**
	 * 每个参数的格式都必须是--key:value的形式
	 * 
	 * @param args
	 *            参数应该是原生的main方法的参数
	 * @param verifyKeys
	 *            用于验证输入的参数是否与verifyParams的参数列表一致，也就是说输入的参数只可以多，
	 *            不可以比verifyParams少
	 * @return 解析好的Map
	 */
	public static Map<String, String> parse(String[] args, List<Param> verifyKeys) {
		if (Arrays.toString(args).contains("--help")) {// 打印参数提示信息
			printHelpV2(verifyKeys);
			System.exit(0);
		}
		Map<String, String> params = parse(args);
		for (int i = 0, size = verifyKeys.size(); i < size; i++) {
			Param param = verifyKeys.get(i);
			if (!params.containsKey(param.getKey())) {
				if (param.isRequired()) {
					printHelpV2(verifyKeys);
					throw new IllegalArgumentException("应该有" + param.getKey() + "参数，但是输入的参数列表中不存在！");
				} else {
					params.put(param.getKey(), param.getValue());
				}
			}
		}
		return params;
	}

    /**
     * 每个参数的格式都必须是--key:value的形式
     *
     * @param args
     *            参数应该是原生的main方法的参数
     * @param params
     *            用于验证输入的参数是否与verifyParams的参数列表一致，也就是说输入的参数只可以多，
     *            不可以比verifyParams少
     * @return 解析好的Map
     */
	public static Map<String, String> parse(String[] args, Param...params) {
		return parse(args, Arrays.asList(params));
	}

    /**
     * 每个参数的格式都必须是--key:value的形式
     *
     * @param args
     *            参数应该是原生的main方法的参数
     * @return 解析好的Map
     */
    private static Map<String, String> parse(String[] args) {
        Map<String, String> params = new TreeMap<>();
        for (String arg : args) {
            int paramBorder = arg.indexOf(':');// :所在的边界
            if (!arg.startsWith("--") || paramBorder == -1) {
                throw new IllegalArgumentException(arg + "不是标准的参数格式，标准的参数格式是:--key:value");
            }
            String key = arg.substring(2, paramBorder);
            String value = arg.substring(paramBorder + 1);
            params.put(key, value);
        }
        return params;
    }

	private static void printHelpV2(List<Param> verifyKeys) {
		System.out.println("期望的参数:");
		Collections.sort(verifyKeys, (o1, o2) -> Boolean.compare(o1.isRequired(), o2.isRequired()));
		System.out.println("\t键\t\t必须\t\t默认值\t\t示例\t\t参数注释");
		for (int i = 0, size = verifyKeys.size(); i < size; i++) {
			Param param = verifyKeys.get(i);
			System.out.println(String.format("\t--%s\t\t%s\t\t%s\t\t%s\t\t%s", param.getKey(),
					param.isRequired() ? "是" : "否", param.getValue() != null ? param.getValue() : "",
					"--" + param.getKey() + ":值", param.getComment() != null ? param.getComment() : ""));
		}
	}
}
