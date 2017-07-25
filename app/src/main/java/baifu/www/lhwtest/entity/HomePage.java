package baifu.www.lhwtest.entity;

import java.util.List;

/**
 * Created by Ivan.L on 2017/7/25.
 */

public class HomePage {

    /**
     * code : 1000
     * info : 获取成功
     * result : [{"name":"实名认证","img":"/Public/Uploads/589a714e3a0a6.png","sort":"1","type":"smrz"},{"name":"还款","img":"/Public/Uploads/589a7272d23ce.png","sort":"2","type":"hk"},{"name":"账号绑定","img":"/Public/Uploads/589a7288b27fa.png","sort":"3","type":"zhbd"},{"name":"信用卡申请","img":"/Public/Uploads/589a729399ffc.png","sort":"4","type":"xyksq"},{"name":"查询","img":"/Public/Uploads/589a7297c0913.png","sort":"5","type":"cx"},{"name":"收款码","img":"/Public/Uploads/589a729b7a2a8.png","sort":"6","type":"skm"},{"name":"使用说明","img":"/Public/Uploads/589a729e81e86.png","sort":"7","type":"sysm"},{"name":"安全退出","img":"/Public/Uploads/589a72a520b38.png","sort":"8","type":"aqtc"},{"name":"","sort":"9","type":"nine"},{"name":"","img":"/Public/Uploads/589a72a9d6c6d.png","sort":"10","type":"gundong"}]
     * currentTime : 1486517660
     */

    private String code;
    private String info;
    private String currentTime;
    private List<ResultBean> result;

    @Override
    public String toString() {
        return "HomePage [code=" + code + ", info=" + info + ", currentTime="
                + currentTime + ", result=" + result + "]";
    }

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
         * name : 实名认证
         * img : /Public/Uploads/589a714e3a0a6.png
         * sort : 1
         * type : smrz
         */

        private String name;
        private String img;
        private String sort;
        private String type;

        @Override
        public String toString() {
            return "ResultBean [name=" + name + ", img=" + img + ", sort="
                    + sort + ", type=" + type + "]";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
