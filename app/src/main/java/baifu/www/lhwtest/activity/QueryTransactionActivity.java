package baifu.www.lhwtest.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import baifu.www.lhwtest.R;

/**
 * Created by Ivan on 2017/7/30.
 * 交易查询页面
 */

public class QueryTransactionActivity extends BaseActivity implements View.OnClickListener{

    // 服务器公钥
    private static String smzfPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAppd/jziXi/QKI4lQda/zf+v3m0i+orR7skU74fpwqQgQaW56XTmP9d/nVzlAYbFcseHx7+dBQHnaRBaAvitG80o0k3PvJ290ekbtNdhScwE17nETYWgFeGwxOYvhFRsION+4hqgsj2oVaElYpg7XDu/55LmdiYPir5Uamgw84ICmlsfr7ApqCGeWyjXLstwEtI2OobZGLP4ZC8VLHrPZfmSKax1cVrzpaCrxa6i5fYqZWdkn1Qiu9IeUe1WaO+qRJED1zfp+rf3VXmf9Sd+Ae70ilAdVvAh0Xw7ROhyCb5wJJpoz5H0Uqn/i4NUxV/WLHuyx5XDd3qr32V+UzrGGUQIDAQAB";
    private static String cooperatorPriKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDa0gb/12mHW9fn+2Eybv20mvZhj9veTzrayZJYpnK3bQHzRojGV5ekyZlJ7Roejw7WMXjvwQ4GliTjvZZYlPZ2gUFXSGCI9utGHFfHK/MSBf/Dkr4fEMu2QHgi/fRHpRPS1d8qCbNN66t51iMmZsLaVap58gpfi4EEMzqe6PkDWGvys3kEwOqgu+ZlvHZLJuzLP5JlzAf2JyHB0uM/wYL9uob2kv86qXvvwX/+igUDPtGY1awUABVd69bapNE3d2bGf1VN0KEgZvp8DVyNLddWbC94+IihqT+Xwq0v5123/fN9gpr7PgBv0a1mZDRPipeE7xn410HKMOn0ad5UZ7HxAgMBAAECggEAPC/MsQ4k/pAqgKpJ/787lTOSVR9mkG0meQs8b5nIr98RzEI4WKW7Fh5FRWor5v/eStfstbVaEEG3/9QjyFWgvNOsjz9fHg0chXpCQow6HVj9EmQIWy8ZeRWedbF4QUSMgU9GRJ/Ka8JItghKPsPBEKDDdSs34zPR1r/ofdECvQJQ6zc0y0o/JvoZiUXj9nXYit5YwR+K+6ss36JokhDfQBOsRuV30Y6KhFgtzATj0VUyoPXKJQwvpRr90JDY5qZQ+jzP+AKm7pxUBDhPdOQ+qDZgEiLY/UqWks5qwkGy2gFbLbuJ/zY+wVH31Cf4d54lbKUJ8cBUR7r009I+dskkaQKBgQDtY4cChCyTpv+FswguPGWm+AUDllpGfpyn4IjIPyfztISf66JflLgtn/igul6jOE9THUI41akBKv0gAwQgwIa07LUrixxR589Z+WS8s52ciAgXurBm90//triEMFCy6Vnb34Unp3dv6mY0aBFaWB2YzYQrDi7mXeToWn7xV+B3jwKBgQDr+dOlMMBGB5t6sjN7mtflYqdDWZgn2BT2pQ5XfrDGZ04Y4fUZ6Q4FTWOOHWrdr201SO0+/2wAnFQZ8rcUKvGb4xSiSwDmbETD6/INRWpYkQZixyFuGNgLV2H2FwPOSYoko4wC1swTRK2bnY5ZBVWwe3OWg5Pn/Z6ftkcIsYx+fwKBgQCjgpta81mU9kEogVGQL0/BOzKQ7v4NcmZLB9CPRVkqdtunH3G/LjvSSU7Cvrwy5rVUxWF4rkpzoH2rkAhG2vWuJyD+9hmynN3o1mw5weo9CEGyvoeE1LJIqz3a50Ceiza2yPX5g1M0RhrR3CCfvvPb7SstUN6jFGd4V+T6LZJSJwKBgQCngm6lItg6XggWrw0wvxhm8wL9IkqKbi9jboOhxINEM+0SaMQOooubY/Y52dQUjgUeACi5waMvQ8nHqa4gmONt32K0Wj3HVC/0TDdFCHOXb0tzLxwiBWzHkD4v7OJ8u2Ne4uHu2f9/5g6/GrcDpm/PmbLRs5F462aAxWnjV/X9jQKBgQDhWbh2ULM9aWCEcoyrZ6uliZn9GSA+mq+11F4xfV17kidcXoHNWzKl6rRSPgmeolbA9Viyf1vuMl9asn/W3fpkH9EGOyDX3UH/x5w7FyVcVuHY0FHZdF9Bk3wlEx6aZMvTBQ4+Tedngk2XCjtFEkolvbvSKOpY0WEnC/re+N3v0A==";
    private Context context = QueryTransactionActivity.this;// 上下文
    private TextView start_date_tv, end_date_tv, no_consumption_tv;
    private int mYear;// 年
    private int mMonth;// 月
    private int mDay;// 日
    static final int DATE_DIALOG_ID = 0;
    private int type = 0;
    private Handler myHandler;
    private String SN, mCommand, timetype;
    private String time_most = null;// 截至
    private String time_low = null;// 起始
    private MyBroadcast myService;
    private HistoryInfoAdapter hd1;// 设备adapter
    private QRCodeHistoryInfoAdapter hd2;// 二维码adapter
    private int page1 = 1;// 当前页
    private int allPage1 = 1;// 一共页数
    private int page2 = 1;// 当前页
    private int allPage2 = 1;// 一共页数
    private static final int WHAT_DID_LOAD_DATA = 0;
    private static final int WHAT_DID_MORE = 2;
    private List<Map<String, String>> strings1, strings2;
    private PullDownView pullDownView1 = null;// 显示设备交易信息 LinearLayout
    private PullDownView pullDownView2 = null;// 显示二维码消费信息 linearlayout
    private ScrollOverListView listView1, listView2;
    public SharedPreferences sp;
    private String status="1";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_query_transaction_activity;
    }

    @Override
    public void onClick(View view) {

    }
}
