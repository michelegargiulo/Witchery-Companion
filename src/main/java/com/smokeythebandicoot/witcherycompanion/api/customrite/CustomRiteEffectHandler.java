package com.smokeythebandicoot.witcherycompanion.api.customrite;

/**
 * Effect of a custom Rite. It is invoked once, on the server, when the rite reaches its effect stage (after the coven
 * has been summoned and every sacrifice has been consumed), exactly like the process() of a W:R RiteEffect.
 */
public interface CustomRiteEffectHandler {

    void run(CustomRiteContext context);

}
