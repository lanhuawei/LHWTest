package baifu.www.lhwtest.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Ivan.L on 2017/7/27.
 * 历史记录子类
 */

public class HistorySubclass {
    private String count;
    private String sumhj;
    private List<Map<String,String>> list;

    public HistorySubclass() {
        super();
    }

    public static HistorySubclass getHistorySubclass() {
        return new HistorySubclass();
    }

    public HistorySubclass(String count, String sumhj, List<Map<String, String>> list) {
        super();
        this.count = count;
        this.sumhj = sumhj;
        this.list = list;
    }

    public String getCount() {
        return count;
    }

    public String getSumhj() {
        return sumhj;
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setCount(String count) {

        this.count = count;
    }

    public void setSumhj(String sumhj) {
        this.sumhj = sumhj;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }
    @Override
    public String toString() {
        return "HistorySubclass [count=" + count + ",sumhj" + sumhj + ",list" + list + "]";
    }
}
