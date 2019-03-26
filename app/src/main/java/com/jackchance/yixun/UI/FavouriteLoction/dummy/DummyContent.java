package com.jackchance.yixun.UI.FavouriteLoction.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","备注:好吃","1楼","2018/4/10/18:54"));
//        addItem(new DummyItem("西直门凯德MALL","KFC","","1楼","2018/4/10/18:54"));
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public  String id;//mapname
        public  String content; //model name
        public  String details; //beizhu
        public  String groupid;
        public  String time;

        public DummyItem(String mapname, String locname, String detail, String groupid, String time) {
            this.id = mapname;
            this.content = locname;
            this.details = detail;
            this.groupid = groupid;
            this.time = time;
        }

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.groupid = "1";
            this.time = "2018/4/10/18:48";
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
