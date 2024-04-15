package com.kltyton.eugeneshorsewhistle.init;

import com.kltyton.eugeneshorsewhistle.whistle.horsediscarded;
import com.kltyton.eugeneshorsewhistle.whistle.usehorseentity;

@SuppressWarnings("InstantiationOfUtilityClass")
public class HorseDiscarded {
    public static void load() {
        new horsediscarded();
        new usehorseentity();
    }

}
