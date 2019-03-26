package com.jackchance.yixun.Util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 蚍蜉 on 2018/1/4.
 */
public class QRResultUtilTest {

    @Test
    public void analysisQRResult() throws Exception {
        //模拟二维码扫描后的活码查询
        QRResultUtil.analysisQRResult("34ac34w3423");//测试合法二维码信息
        //断言结果
        assertEquals(true,QRResultUtil.success);    //断言测试是否匹配
        assertEquals(4861820,QRResultUtil.y,0);     //断言y坐标是否匹配
        assertEquals(12961705,QRResultUtil.x,0);    //断言x坐标是否匹配
        assertEquals("10347",QRResultUtil.mapid);   //断言mapid是否匹配
        assertEquals(0,QRResultUtil.groupid);       //断言楼层是否匹配
    }
}