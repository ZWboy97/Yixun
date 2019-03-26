package com.jackchance.yixun.UI.LocateInMap;
import android.content.Context;
import android.content.Intent;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.UI.LocateInMap.LocateWays.ChooseToLocActivity;
import com.jackchance.yixun.UI.LocateInMap.LocateWays.SearchToLocActivity;
import com.jackchance.yixun.UI.QRScan.QRCodeActivity;

import static com.jackchance.yixun.UI.LocateInMap.LocateActivity.CUR_MAP_LOCACTIVITY;

/**
 * 工厂设计模式，用于生产多种定位方式
 * Created by 蚍蜉 on 2017/12/26.
 */

public class LocWaysFactory {

    final public static int SEARCH = 1;           //基于搜索定位
    final public static int CHOOSE = 2;           //选择定位
    final public static int QRSCAN = 3;           //QR定位
    final public static int NEWWAY = 4;           //其他方式

    /**
     * 基于方式选择，启动定位方式
     * @param context   loc上下文context，以启动新进程
     * @param TYPE      定位类型
     * @param currentMap
     * @return
     */
    public static Intent startLocInMap(Context context, int TYPE, Map currentMap){
        switch (TYPE){
            case SEARCH:
                Intent intent = new Intent(context,SearchToLocActivity.class);
                intent.putExtra(CUR_MAP_LOCACTIVITY,currentMap);
                return intent;
            case CHOOSE:
                intent = new Intent(context,ChooseToLocActivity.class);
                intent.putExtra(CUR_MAP_LOCACTIVITY,currentMap);
                return intent;
            case QRSCAN:
                intent = new Intent(context, QRCodeActivity.class);
                QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRToLocateInMap);
                return intent;
            case NEWWAY:
                return null;
            default:
                return null;
        }
    }

}
