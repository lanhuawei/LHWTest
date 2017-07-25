package baifu.www.lhwtest.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ivan.L on 2017/7/25.
 * 现金返还,收费返现
 */

public class CashBackBean implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8780835183350333687L;
    /**
     * code : 1000
     * info : 获取成功
     * result : [{"totalAmount":"10.00","subject":"APP收费返现","PosClearStatus":"0","addtime":"2017-05-18 14:28:47"}]
     * currentTime : 1495617568
     */

    private String code;
    private String info;
    private String currentTime;
    private List<ResultBean> result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * totalAmount : 10.00
         * subject : APP收费返现
         * PosClearStatus : 0
         * addtime : 2017-05-18 14:28:47
         */

        private String totalAmount;
        private String subject;
        private String PosClearStatus;
        private String addtime;

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getPosClearStatus() {
            return PosClearStatus;
        }

        public void setPosClearStatus(String PosClearStatus) {
            this.PosClearStatus = PosClearStatus;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        @Override
        public String toString() {
            return "ResultBean [totalAmount=" + totalAmount + ", subject=" + subject + ", PosClearStatus="
                    + PosClearStatus + ", addtime=" + addtime + "]";
        }
    }
}
