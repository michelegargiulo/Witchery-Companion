package com.smokeythebandicoot.witcherycompanion.patches.entity.vampire;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.ArrayList;

/**
 * Restores the melee attack of the Vampire mob as it was in the original Witchery. The melee attack AI (priority 8,
 * speed 1.2, short memory) is missing in W:R, which has an EntityAINearestAttackableTarget at that priority instead,
 * in the tasks instead of the target tasks: a bad translation of EntityAIAttackOnCollide. Vampires pick their target
 * (the villagers of their village) with the real target tasks, but never walk to them nor hit them. And that stray
 * target AI makes them also target any living entity nearby, ignoring the villager rules of the real target AI.
 * <p>
 * NOTE: W:R also adds an EntityAIWanderAvoidWater at priority 5 (OG Witchery had only the EntityAIWander at
 * priority 10). It can interrupt the attack to wander around, but it is not a bug by itself
 */
public class VampireAIPatch {

    /** The priority of the melee attack AI in the original Witchery */
    public static final int MELEE_PRIORITY = 8;

    /** The movement speed of the melee attack AI in the original Witchery */
    public static final double MELEE_SPEED = 1.2D;

    public static void fixMeleeAI(EntityCreature vampire) {
        EntityAITasks tasks = vampire.tasks;

        // Remove the stray target AI (the real ones are in targetTasks). Copy, as removing while iterating would fail
        for (EntityAITasks.EntityAITaskEntry entry : new ArrayList<>(tasks.taskEntries)) {
            if (entry.action instanceof EntityAINearestAttackableTarget) {
                tasks.removeTask(entry.action);
            }
        }

        tasks.addTask(MELEE_PRIORITY, new EntityAIAttackMelee(vampire, MELEE_SPEED, false));
    }

}
