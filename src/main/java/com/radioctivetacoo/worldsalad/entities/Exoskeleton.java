package com.radioctivetacoo.worldsalad.entities;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class Exoskeleton extends MonsterEntity implements IAnimatable {
	private AnimationFactory manager = new AnimationFactory(this);
	
	public Exoskeleton(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.experienceValue = 6;
	}
	
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	protected boolean canDropLoot() {
		return true;
	}
	
	protected boolean shouldBurnInDay() {
	      return true;
	   }

	@Override
	public boolean isPreventingPlayerRest(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public void livingTick() {
		this.updateArmSwingProgress();
		this.func_213623_ec();
		super.livingTick();
	}
	
	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
	}

	@Override
	protected SoundEvent getFallSound(int heightIn) {
		return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(8, new SwimGoal(this));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TraderAnt.class, true));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, SoldierAnt.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WorkerAnt.class, true));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31F);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
		this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SPIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SPIDER_DEATH;
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return super.canDespawn(distanceToClosestPlayer = 40);
	}

	@Override
	public AnimationFactory getFactory() {
		return manager;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
	}
	
	@SuppressWarnings("unchecked")
	private <E extends Exoskeleton> PlayState predicate(AnimationEvent<E> event)
	{
		if(event.isMoving())
		{
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.exoskeleton.walking", true));
			event.getController().transitionLengthTicks = 0; 
			return PlayState.CONTINUE;
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.exoskeleton.idle", true));
			event.getController().transitionLengthTicks = 0;
			return PlayState.CONTINUE;
		}
	}
}
