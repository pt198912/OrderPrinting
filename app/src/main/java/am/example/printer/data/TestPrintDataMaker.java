package am.example.printer.data;

import android.content.Context;

import com.order.print.R;
import com.order.print.bean.Order;
import com.order.print.bean.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import am.example.printer.util.FileUtils;
import am.example.printer.util.QRCodeUtil;
import am.util.printer.PrintDataMaker;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * 测试数据生成器
 * Created by Alex on 2016/11/10.
 */

public class TestPrintDataMaker implements PrintDataMaker {

    private Context context;
    private String qr;
    private int width;
    private int height;
    private List<Order> orders;


    public TestPrintDataMaker(Context context, String qr, int width, int height) {
        this.context = context;
        this.qr = qr;
        this.width = width;
        this.height = height;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public TestPrintDataMaker(Context context, String qr, int width, int height, List<Order> orders) {
        this.context = context;
        this.qr = qr;
        this.width = width;
        this.height = height;
        this.orders=orders;
    }

    private static final int BIG_FONT=1;
    private static final int NORMAL_FONT=0;



    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();

        try {
            PrinterWriter printer;
            printer = new PrinterWriter58mm(height,width);
            printer.setAlignCenter();
            data.add(printer.getDataAndReset());
            orders=orders.subList(0,1);
            for(int i=0;i<orders.size();i++) {
                Order order=orders.get(i);


//            ArrayList<byte[]> image1 = printer.getImageByte(context.getResources(), R.drawable.ic_printer_logo);
//            data.addAll(image1);

//            printer.setAlignLeft();
//            printer.printLine();
//            printer.printLineFeed();

//            printer.printLineFeed();
                printer.setAlignCenter();
                printer.setEmphasizedOn();
                printer.setFontSize(BIG_FONT);
                printer.print("好食亿点");
                printer.printLineFeed();
                printer.setFontSize(NORMAL_FONT);
                printer.setEmphasizedOff();
                printer.printLineFeed();
                printer.print(order.getShop_name());
//                printer.print("最时尚的明星餐厅");
                printer.printLineFeed();
                printer.printLineFeed();
                printer.setEmphasizedOn();
                printer.setFontSize(BIG_FONT);
                printer.print("在线支付(已支付)");
                printer.printLineFeed();
                printer.printLineFeed();
                printer.setEmphasizedOff();
                printer.setFontSize(NORMAL_FONT);
                printer.print("订单号："+order.getOrder_id());
                printer.printLineFeed();
                printer.print("下单时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(new Date(order.getCreate_time()*1000)));
                printer.printLineFeed();
                printer.setAlignCenter();
                printer.print("------------1号口袋------------");
                printer.printLineFeed();
//                printer.printLineFeed();

//                printer.print("订单号：88888888888888888");
//                printer.printLineFeed();
                for(int j=0;j<order.getItems().size();j++){
                    OrderItem item=order.getItems().get(j);
                    printer.setAlignLeft();
                    printer.print(item.getName()+"              ");
                    printer.setAlignRight();
                    printer.print("X"+item.getNum()+"  "+item.getTotal_price());
                    printer.printLineFeed();
                }
                if(order.getCj_money()!=0||order.getFull_reduce_price()!=0
                        ||order.getLogistics()!=0) {
                    printer.setAlignCenter();
                    printer.print("------------其他------------");
                    printer.printLineFeed();
                }
                printer.setAlignLeft();
                if(order.getCj_money()!=0){
                    printer.print("餐盒            "+order.getCj_money());
                    printer.printLineFeed();
                }
                if(order.getFull_reduce_price()!=0){
                    printer.print("已优惠              -"+order.getFull_reduce_price());
                    printer.printLineFeed();
                }
                if(order.getLogistics()!=0){
                    printer.print("配送费              "+order.getLogistics());
                    printer.printLineFeed();
                }
                printer.setAlignLeft();
//                printer.setEmphasizedOff();
//                printer.setFontSize(0);
//                printer.printLine();
                printer.setEmphasizedOn();
                printer.setFontSize(NORMAL_FONT);
//                printer.print("________________________________");
                printer.printLine();
//                printer.printLineFeed();
                printer.printLineFeed();
                printer.setAlignRight();
                printer.setEmphasizedOn();
                printer.setFontSize(BIG_FONT);
                printer.print("已付：" +order.getNeed_pay());
                printer.printLineFeed();
                printer.setAlignLeft();
                printer.setFontSize(NORMAL_FONT);
                printer.setEmphasizedOn();
                printer.printLine();
//                printer.print("________________________________");
//                printer.printLineFeed();
                printer.printLineFeed();
//                printer.print("已付：" +
//                        new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
//                                .format(new Date(System.currentTimeMillis())));
                printer.setAlignLeft();
                printer.setEmphasizedOn();
                printer.setFontSize(BIG_FONT);
                printer.print(order.getAddr().getAddr());
                printer.printLineFeed();
                printer.printLineFeed();
                printer.setAlignCenter();
                printer.print(order.getAddr().getMobile());
                printer.printLineFeed();
                printer.printLineFeed();
                printer.print(order.getAddr().getName());
                printer.printLineFeed();
                printer.printLineFeed();
                if(i!=orders.size()-1) {
                    printer.printLineFeed();
                    printer.printLineFeed();
                    printer.printLineFeed();
                }

//                printer.setEmphasizedOn();
//                printer.print("#8（已付款）");
//                printer.printLineFeed();
//                printer.print("××区××路×××大厦××楼×××室");
//                printer.printLineFeed();
//                printer.setEmphasizedOff();
//                printer.print("13843211234");
//                printer.print("（张某某）");
//                printer.printLineFeed();
//                printer.print("备注：多加点辣椒，多加点香菜，多加点酸萝卜，多送点一次性手套");
//                printer.printLineFeed();
//
//                printer.printLine();
//                printer.printLineFeed();
//
//                printer.printInOneLine("星级美食（豪华套餐）×1", "￥88.88", 0);
//                printer.printLineFeed();
//                printer.printInOneLine("星级美食（限量套餐）×1", "￥888.88", 0);
//                printer.printLineFeed();
//                printer.printInOneLine("餐具×1", "￥0.00", 0);
//                printer.printLineFeed();
//                printer.printInOneLine("配送费", "免费", 0);
//                printer.printLineFeed();
//
//                printer.printLine();
//                printer.printLineFeed();
//
//                printer.setAlignRight();
//                printer.print("合计：977.76");
//                printer.printLineFeed();
//                printer.printLineFeed();
//
//                printer.setAlignCenter();

                data.add(printer.getDataAndReset());

//                String bitmapPath = FileUtils.getExternalFilesDir(context, "Temp") + "tmp_qr.jpg";
//                if (QRCodeUtil.createQRImage(qr, 380, 380, null, bitmapPath)) {
//                    ArrayList<byte[]> image2 = printer.getImageByte(bitmapPath);
//                    data.addAll(image2);
//                } else {
//                    ArrayList<byte[]> image2 = printer
//                            .getImageByte(context.getResources(), R.drawable.ic_printer_qr);
//                    data.addAll(image2);
//                }
//
//                printer.printLineFeed();
//                printer.print("扫一扫，查看详情");
//                printer.printLineFeed();
//                printer.printLineFeed();
//                printer.printLineFeed();
//                printer.printLineFeed();
//                printer.printLineFeed();
//
//                printer.feedPaperCutPartial();
//
//                data.add(printer.getDataAndClose());
            }
            printer.setAlignCenter();
            printer.print("------完------");
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            data.add(printer.getDataAndClose());
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
