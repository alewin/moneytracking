package com.unibo.koci.moneytracking.Entità;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by koale on 12/08/17.
 */
@IntDef({ComeType.OUTCOME, ComeType.INCOME})
@Retention(RetentionPolicy.SOURCE)
@interface ComeType{
    int OUTCOME = 0;
    int INCOME = 1;
}

class MoneyTypology {
    int money;
    ComeType type;

}
