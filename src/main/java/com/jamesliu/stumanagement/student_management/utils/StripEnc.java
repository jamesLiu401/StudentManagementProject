package com.jamesliu.stumanagement.student_management.utils;

/**
 * 加密字符串处理工具类
 * 用于处理加密配置字符串，支持ENC()格式的加密字符串解析
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>加密字符串解析 - 解析ENC()格式的加密字符串</li>
 *   <li>字符串格式检查 - 检查字符串是否为加密格式</li>
 *   <li>安全处理 - 安全地处理敏感配置信息</li>
 * </ul>
 * 
 * <p>使用场景：</p>
 * <ul>
 *   <li>数据库密码解密 - 处理加密的数据库密码</li>
 *   <li>配置文件解密 - 处理加密的配置信息</li>
 *   <li>敏感信息处理 - 处理其他敏感配置信息</li>
 * </ul>
 * 
 * <p>格式说明：</p>
 * <ul>
 *   <li>加密格式：ENC(加密内容)</li>
 *   <li>解密后：返回加密内容部分</li>
 *   <li>非加密格式：直接返回原字符串</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-06-16
 */
public class StripEnc {
    public static String stripEnc(String value) {
        if (value != null && value.startsWith("ENC(") && value.endsWith(")")) {
            return value.substring(4, value.length() - 1);
        }
        return value;
    }
}
