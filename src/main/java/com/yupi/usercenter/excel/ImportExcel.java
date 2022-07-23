package com.yupi.usercenter.excel;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * @author ljs
 * @date 2022-07-14
 * @description 导入 Excel
 */
public class ImportExcel {
    public static void main(String[] args) {

        String fileName = "D:\\workspace_idea\\user_center\\src\\main\\resources\\星球数据.xlsx";
        // listenerRead(fileName);

        synchronousRead(fileName);
    }

    public static void listenerRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new XingQiuTableUserInfoListener()).sheet().doRead();
    }

    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo data : list) {
            System.out.println(data);
        }
    }

}