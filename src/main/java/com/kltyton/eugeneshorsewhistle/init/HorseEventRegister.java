package com.kltyton.eugeneshorsewhistle.init;

import com.kltyton.eugeneshorsewhistle.whistle.HorseDiscardedEvent;
import com.kltyton.eugeneshorsewhistle.whistle.UseHorseEntityEvent;

@SuppressWarnings("InstantiationOfUtilityClass")
public class HorseEventRegister {
    public static void load() {
        new HorseDiscardedEvent();
        new UseHorseEntityEvent();
    }

}
