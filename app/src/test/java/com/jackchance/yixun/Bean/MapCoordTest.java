package com.jackchance.yixun.Bean;
import com.fengmap.android.map.geometry.FMMapCoord;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by 蚍蜉 on 2018/1/3.
 */
public class MapCoordTest {

    private MapCoord mapCoord;

    @Before
    public void setUp() throws Exception {
        mapCoord = new MapCoord();
        mapCoord.setGroupId(10);
        mapCoord.setMapCoord(new FMMapCoord(200,99));
    }

    @Test
    public void getGroupId() throws Exception {
        assertEquals(10,mapCoord.getGroupId(),0);
    }

    @Test
    public void getMapCoord() throws Exception {
        assertEquals(200,mapCoord.getMapCoord().x,0);
        assertEquals(99,mapCoord.getMapCoord().y,0);
    }

}