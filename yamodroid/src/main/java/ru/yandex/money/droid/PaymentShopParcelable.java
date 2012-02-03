package ru.yandex.money.droid;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dvmelnikov
 */

class PaymentShopParcelable implements Parcelable {

    private BigDecimal sum;
    private String patternId;
    private Map<String, String> params;
//    private boolean payWithCard;

    public PaymentShopParcelable(BigDecimal sum, String patternId,
            Map<String, String> params/*, boolean payWithCard*/) {
        this.sum = sum;
        this.patternId = patternId;
        this.params = params;
//        this.payWithCard = payWithCard;
    }

    private PaymentShopParcelable(Parcel in) {
        sum = BigDecimal.valueOf(in.readDouble());
        patternId = in.readString();
        int count = in.readInt();
        params = new HashMap<String, String>(count);
        for (int i = 0; i < count; i++) {
            String name = in.readString();
            String value = in.readString();
            params.put(name, value);
        }
//        payWithCard = in.readInt() == 1;
    }

    public static final Creator<PaymentShopParcelable> CREATOR =
            new Creator<PaymentShopParcelable>() {
                public PaymentShopParcelable createFromParcel(Parcel in) {
                    return new PaymentShopParcelable(in);
                }

                public PaymentShopParcelable[] newArray(int size) {
                    return new PaymentShopParcelable[size];
                }
            };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(sum.doubleValue());
        parcel.writeString(patternId);
        parcel.writeInt(params.size());
        for (String key : params.keySet()) {
            parcel.writeString(key);
            parcel.writeString(params.get(key));
        }
//        if (payWithCard)
//            parcel.writeInt(1);
//        else
//            parcel.writeInt(0);
    }

    public BigDecimal getSum() {
        return sum;
    }

    public String getPatternId() {
        return patternId;
    }

    public Map<String, String> getParams() {
        return params;
    }

//    public boolean isPayWithCard() {
//        return payWithCard;
//    }
}
