package com.smokeythebandicoot.witcherycompanion.api.progress;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class WitcheryProgressEvent extends Event {

    public final EntityPlayer player;
    public final String progressKey;
    public final String activityTrigger;


    public WitcheryProgressEvent(EntityPlayer player, String progressKey, String activityTrigger) {
        this.player = player;
        this.progressKey = progressKey;
        this.activityTrigger = activityTrigger;
    }

    public enum EProgressTriggerActivity {

        /** Player has discovered a secret bind spirit to fetish recipe **/
        BIND_TO_FETISH("bind_fetish"),

        /** Player has triggered a secret brazier recipe **/
        BRAZIER_RECIPE("brazier_recipe"),

        /** Player has created a brew with a secret ingredient **/
        CAULDRON_BREW("cauldron_brewing"),

        /** Player has triggered a secret rite with circle magic **/
        CIRCLE_MAGIC("circle_magic"),

        /** Player has unlocked progress through a command **/
        COMMAND("command"),

        /** Player has fulfilled a Quest issued by a Coven Witch **/
        COVEN_QUEST_FULFILLED("coven_quest"),

        /** Player has a full coven **/
        COVEN_FULL("full_coven"),

        /** Player has visited a Witchery Dimension **/
        DIMENSION_VISIT("dimension_visit"),

        /** Player has acquired an Infusion **/
        INFUSION_OBTAINED("infusion_obtained"),

        /** Player has drawn a secret symbol **/
        MYSTIC_BRANCH("mystic_branch"),

        /** Player has used a Torn Page for the Observations of an Immortal book **/
        USE_VAMPIRE_PAGE("torn_page_use"),

        /** Player has levelled up as a Vampire or Werewolf **/
        TRAIT_LEVEL_UP("trait_level_up"),

        /** Player has been infected by a Werewolf using the Wolf Trap and Altar **/
        WEREWOLF_INFECTION("werewolf_infection"),
        ;

        public final String activityTrigger;

        EProgressTriggerActivity(String triggerDescription) {
            this.activityTrigger = triggerDescription;
        }
    }

}
