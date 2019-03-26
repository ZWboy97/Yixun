package com.jackchance.yixun.Bean;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 蚍蜉 on 2018/1/3.
 */
public class QRInfoTest {

    private QRInfo qrInfo;

    @Test
    public void getMapid() throws Exception {
        assertEquals("13534",qrInfo.getMapid());
        //断言(期望，真实值)
    }
    @Test
    public void getGroupid() throws Exception {
        assertEquals(4,qrInfo.getGroupid(),0);
    }
    @Test
    public void getDetail() throws Exception {
        assertEquals("detail",qrInfo.getDetail());
    }
    @Before
    public void setUp() throws Exception {
        qrInfo = new QRInfo();
        qrInfo.setMapid("13534");
        qrInfo.setGroupid(4);
        qrInfo.setDetail("detail");
    }


}