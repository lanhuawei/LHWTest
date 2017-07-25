package baifu.www.lhwtest.wheel;

/**
 * Created by Ivan.L on 2017/7/25.
 */

public interface OnWheelChangedListener {
    /**
     * Callback method to be invoked when current item changed
     *
     * @param wheel
     *            the wheel view whose state has changed
     * @param oldValue
     *            the old value of current item
     * @param newValue
     *            the new value of current item
     */
    void onChanged(WheelView wheel, int oldValue, int newValue);
}
