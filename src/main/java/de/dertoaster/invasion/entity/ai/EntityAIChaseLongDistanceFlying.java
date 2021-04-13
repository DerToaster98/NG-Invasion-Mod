package de.dertoaster.invasion.entity.ai;

import java.util.EnumSet;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.util.math.Box;

public class EntityAIChaseLongDistanceFlying extends Goal {
	private final FlyingEntity taskOwner;
	private final double speed;

	private LivingEntity attackTarget;
	private int executionCheckCooldownTicks;

	public EntityAIChaseLongDistanceFlying(FlyingEntity taskOwner, double speed) {

		this.taskOwner = taskOwner;
		this.speed = speed;
		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {

		if (this.executionCheckCooldownTicks-- > 0) {
			return false;
		}

		this.executionCheckCooldownTicks += this.taskOwner.getRandom().nextInt(5) + 2;

		this.attackTarget = this.taskOwner.getAttacking();

		if (this.attackTarget == null) {
			return false;
		}

		if (this.taskOwner.distanceTo(this.attackTarget) < 64) {
			return false;
		}

		Box axisalignedbb = this.taskOwner.getBoundingBox();

		return this.taskOwner.world.getEntityCollisions(this.taskOwner, axisalignedbb, new Predicate<Entity>() {

			@Override
			public boolean test(Entity t) {
				return t != taskOwner;
			}
		}).count() <= 0;
	}

	@Override
	public boolean shouldContinue() {

		return false;
	}

	@Override
	public void start() {
		this.taskOwner.getMoveControl().moveTo(this.attackTarget.getPos().x, this.attackTarget.getPos().y, this.attackTarget.getPos().z, this.speed);
	}
}
