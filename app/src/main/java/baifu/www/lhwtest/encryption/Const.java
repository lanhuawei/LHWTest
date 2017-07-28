package baifu.www.lhwtest.encryption;

import com.newland.mtype.module.common.pin.WorkingKeyType;

/**
 * Created by Ivan.L on 2017/7/27.
 * 常量，用于加密
 */

public class Const {
    /**
     * 主秘钥索引
     * 各索引若相同则表示使用同一组主密钥索引
     */

    public static class MKIndexConst{
        /**
         * 主密钥索引
         */
        public static final int DEFAULT_MK_INDEX = 1;
    }

    /**
     * 工作密钥类型:{@link WorkingKeyType#PININPUT}
     */
    public static class PinWKIndexConst{
        /**
         * 默认PIN加密工作密钥索引
         */
        public static final int DEFAULT_PIN_WK_INDEX = 2;
    }
    /**
     * 工作密钥类型:{@link WorkingKeyType#MAC}
     */
    public static class MacWKIndexConst{
        /**
         * 默认MAC加密工作密钥索引
         */
        public static final int DEFAULT_MAC_WK_INDEX = 3;
    }
    /**
     * 工作密钥类型:{@link WorkingKeyType#DATAENCRYPT}
     */
    public static class DataEncryptWKIndexConst{
        /**
         * 默认磁道加密工作密钥索引
         */
        public static final int DEFAULT_TRACK_WK_INDEX = 4;

        public static final int DEFAULT_MUTUALAUTH_WK_INDEX = 5;
    }

    /**
     * 设备参数存放相关规格
     */
    public static class DeviceParamsPattern{
        /**
         * 默认存放编码格式
         */
        public static final String DEFAULT_STORENCODING = "utf-8";

        /**
         * 日期格式化规规范
         */
        public static final String DEFAULT_DATEPATTERN = "yyyyMMddHHmmss";
    }

    /**
     * 设备参数<tt>tag</tt>
     */
    public static class DeviceParamsTag{
        /**
         * 商户号存
         */
        public static final int MRCH_NO = 0xFF9F11;
        /**
         * 终端号存
         */
        public static final int TRMNL_NO = 0xFF9F12;
        /**
         * 工作密钥存放
         */
        public static final int WK_UPDATEDATE = 0xFF9F13;
        /**
         * pos标示存放
         */
        public static final int DEVICE_TYPE = 0xFF9F14;
        /**
         * 终端号存
         */
        public static final int MRCH_NAME = 0xFF9F15;
    }

    public static class KeyIndexConst {
        /**
         * KSN 初始化索
         */
        public static final int KSN_INITKEY_INDEX = 1;
    }

}
