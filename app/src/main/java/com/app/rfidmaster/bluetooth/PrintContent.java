package com.app.rfidmaster.bluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.rfidmaster.App;
import com.app.rfidmaster.bean.PrintBean;
import com.gprinter.command.CpclCommand;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.util.Vector;

/**
 * Created by Administrator on 2018/4/16.
 */

public class PrintContent {
    private static short center = 275;
    private static short left = 10;
    private static short right = 500;

    public static Vector<Byte> getReceipt(PrintBean printBean) {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        //打印走纸多少个单位
        esc.addPrintAndFeedLines((byte) 6);
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTB, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText("上海景禧医纺科技有限公司\n");
        //打印并换行
        esc.addPrintAndFeedLines((byte) 1);
        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("盘点单");
        esc.addPrintAndFeedLines((byte) 2);
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印文字
        esc.addText("医院:" + printBean.cusName);
        esc.addPrintAndFeedLines((byte) 1);
        esc.addText("科室:" + printBean.regionName);
        esc.addPrintAndFeedLines((byte) 2);

        esc.addSetAbsolutePrintPosition((short) 0);
        esc.addText("单据号:" + printBean.rcvNo);
        esc.addPrintAndFeedLines((byte) 1);
        esc.addText("盘点时间:" + printBean.createTime);
        esc.addPrintAndFeedLines((byte) 1);

        esc.addSetAbsolutePrintPosition((short) 0);
        esc.addText("已登记:" + printBean.loginCount + " 未登记:" + printBean.outCount);
        esc.addPrintAndFeedLines((byte) 1);
        esc.addText("盘点人:" + printBean.userName);
        esc.addPrintAndFeedLines((byte) 2);

        esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
        esc.addSetAbsolutePrintPosition(left);
        esc.addText("医院");
        esc.addSetAbsolutePrintPosition(center);
        esc.addText("品名");
        esc.addSetAbsolutePrintPosition(right);
        esc.addText("数量");
        esc.addPrintAndFeedLines((byte) 2);
        esc.addSelectCharacterFont(EscCommand.FONT.FONTA);

        for (PrintBean.Goods goods : printBean.list) {
            esc.addSetAbsolutePrintPosition(left);
            if(goods.hospital != null){
                esc.addText(goods.hospital);
            }
            esc.addSetAbsolutePrintPosition(center);
            if(goods.name != null){
                esc.addText(goods.name);
            }
            esc.addSetAbsolutePrintPosition(right);
            if(goods.count != null){
                esc.addText(goods.count);
            }
            esc.addPrintAndFeedLines((byte) 2);
        }
        esc.addPrintAndFeedLines((byte) 2);
        esc.addSetAbsolutePrintPosition(left);
        esc.addText("确认签名：");
        esc.addPrintAndFeedLines((byte) 1);
        //打印走纸n个单位
        esc.addPrintAndFeedLines((byte) 6);
        //开启切刀
        esc.addCutPaper();
        //添加缓冲区打印完成查询
        byte[] bytes = {0x1D, 0x72, 0x01};
        //添加用户指令
        esc.addUserCommand(bytes);
        Vector<Byte> datas = esc.getCommand();
        return datas;
    }
}
