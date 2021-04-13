package de.dertoaster.invasion.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIChaseLongDistance extends Goal {

	private final PathAwareEntity taskOwner;
	private final double speed;
	private Path path;

	public EntityAIChaseLongDistance(PathAwareEntity taskOwner, double speed) {

		this.taskOwner = taskOwner;
		this.speed = speed;
		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {

		LivingEntity target = this.taskOwner.getAttacking();

		if (target == null) {
			return false;
		}

		double followRange = this.taskOwner.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);

		if (this.taskOwner.distanceTo(target) < followRange) {
			return false;
		}

		EntityNavigation navigator = this.taskOwner.getNavigation();

		if (navigator.isFollowingPath()) {
			return false;
		}

		// if (target instanceof EntityPlayer) {
		// EntityPlayer player = (EntityPlayer) target;
		//
		// if (player.isSpectator() || player.isCreative()) {
		// return false;
		// }
		// }

		Vec3d originVector = this.taskOwner.getPos();
		Vec3d targetVector = target.getPos();
		Random random = this.taskOwner.getRandom();
		int searchRangeXZ = 4;
		int searchRangeY = 16;
		int distance = (int) Math.max(0, followRange - searchRangeXZ * 2);

		Vec3d pathVec = this.findTargetBlock(this.taskOwner, navigator, random, originVector, targetVector, distance, searchRangeXZ, searchRangeY);

		// if (pathVec == null) {
		// pathVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.taskOwner, (int)
		// (followRange * 0.75), 64, targetVector);
		// }

		if (pathVec != null) {
			this.path = navigator.findPathTo(pathVec.x, pathVec.y, pathVec.z, (int) (followRange * 2));
		}

		// System.out.println(this.path);

		return (this.path != null);
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity target = this.taskOwner.getAttacking();

		if (target == null) {
			return false;
		}

		if (!target.isAlive()) {
			return false;
		}

		if (this.taskOwner.getNavigation().isIdle()) {
			return false;
		}

		// if (target instanceof EntityPlayer) {
		// EntityPlayer player = (EntityPlayer) target;
		//
		// if (player.isSpectator() || player.isCreative()) {
		// return false;
		// }
		// }

		double followRange = this.taskOwner.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);

		return (this.taskOwner.distanceTo(target) >= followRange);
	}

	@Override
	public void start() {
		super.start();
		this.taskOwner.getNavigation().startMovingAlong(this.path, this.speed);
	}

	@Override
	public void stop() {
		this.taskOwner.getNavigation().stop();
		super.stop();
	}

	/**
	 * Searches 10 blocks at random within searchRangeXZ and searchRangeY distance from a point located between origin and target at the given distance.
	 *
	 * @see RandomPositionGenerator#generateRandomPos(EntityCreature, int, int, Vec3d, boolean)
	 */
	@Nullable
	private Vec3d findTargetBlock(PathAwareEntity entity, EntityNavigation navigator, Random random, Vec3d origin, Vec3d target, int distance, int searchRangeXZ, int searchRangeY) {

		Vec3d subTarget = target.subtract(origin).normalize().multiply(distance).add(origin);

		float largestBlockPathWeight = -99999;
		int storedX = 0;
		int storedY = 0;
		int storedZ = 0;
		boolean validBlockPosFound = false;

		for (int i = 0; i < 10; i++) {

			int x = random.nextInt(2 * searchRangeXZ + 1) - searchRangeXZ;
			int y = random.nextInt(2 * searchRangeY + 1) - searchRangeY;
			int z = random.nextInt(2 * searchRangeXZ + 1) - searchRangeXZ;

			if (x * target.x + z * target.z < 0) {
				continue;
			}

			BlockPos blockPos = new BlockPos(x + subTarget.x, y + subTarget.y, z + subTarget.z);

			if (navigator.isValidPosition(blockPos)) {

				float blockPathWeight = (entity instanceof PathAwareEntity) ? ((PathAwareEntity) entity).getPathfindingFavor(blockPos) : 0;

				if (blockPathWeight > largestBlockPathWeight) {
					largestBlockPathWeight = blockPathWeight;
					storedX = x;
					storedY = y;
					storedZ = z;
					validBlockPosFound = true;
				}
			}
		}

		if (validBlockPosFound) {
			return new Vec3d(storedX + subTarget.x, storedY + subTarget.y, storedZ + subTarget.z);
		}

		return null;
	}
}
