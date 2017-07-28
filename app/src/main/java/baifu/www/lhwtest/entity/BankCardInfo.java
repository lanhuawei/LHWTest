package baifu.www.lhwtest.entity;

/**
 * Created by Ivan.L on 2017/7/25.
 * 银行卡列表实体类
 */

public class BankCardInfo {
    private String cardTp;   //0借记卡 2信用卡
    private String cardId;     //卡号
    private String bankName;   //银行名
    private String is_quickpay;//0无快捷支付 1有快捷
    private String if_record;// if_record  0 不是入账卡  1是入账卡

    private String phone;

    public BankCardInfo() {super();
    }

    public BankCardInfo(String cardTp, String cardId, String bankName, String is_quickpay, String if_record, String phone) {
        super();
        this.cardTp = cardTp;
        this.cardId = cardId;
        this.bankName = bankName;
        this.is_quickpay = is_quickpay;
        this.if_record = if_record;
        this.phone = phone;
    }

    public void setCardTp(String cardTp) {
        this.cardTp = cardTp;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setIs_quickpay(String is_quickpay) {
        this.is_quickpay = is_quickpay;
    }

    public void setIf_record(String if_record) {
        this.if_record = if_record;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardTp() {
        return cardTp;
    }

    public String getCardId() {
        return cardId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getIs_quickpay() {
        return is_quickpay;
    }

    public String getIf_record() {
        return if_record;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "BankCardInfo{" + "cardTp='" + cardTp + '\'' + ", cardId='" + cardId + '\'' +
                ", bankName='" + bankName + '\'' + ", is_quickpay='" + is_quickpay + '\'' +
                ", if_record='" + if_record + '\'' + ", phone='" + phone + '\'' + '}';
    }
}
