/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.ckateptb.caught.collider;

import lombok.Getter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import ru.ckateptb.caught.AbstractCollider;
import ru.ckateptb.caught.Collider;
import ru.ckateptb.caught.math.ImmutableVector;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public class RayCollider extends AbstractCollider {
    private final double raySize;
    private final ImmutableVector original;
    private final ImmutableVector direction;
    private double maxDistance;

    public RayCollider(LivingEntity livingEntity, double maxDistance) {
        this(livingEntity, maxDistance, 0);
    }

    public RayCollider(LivingEntity livingEntity, double maxDistance, double raySize) {
        super(livingEntity.getWorld());
        Location eyeLocation = livingEntity.getEyeLocation();
        this.original = new ImmutableVector(eyeLocation);
        this.direction = new ImmutableVector(eyeLocation.getDirection());
        this.maxDistance = maxDistance;
        this.raySize = raySize;
    }

    public RayCollider(World world, Vector original, Vector direction, double maxDistance, double raySize) {
        super(world);
        this.original = new ImmutableVector(original);
        this.direction = new ImmutableVector(direction);
        this.maxDistance = maxDistance;
        this.raySize = raySize;
    }

    @Override
    public boolean intersects(Collider collider) {
        return toBoundingBoxCollider().intersects(collider);
    }

    @Override
    public ImmutableVector getPosition() {
        return this.original;
    }

    @Override
    public RayCollider at(Vector point) {
        return new RayCollider(world, point, direction, maxDistance, raySize);
    }

    public AxisAlignedBoundingBoxCollider toBoundingBoxCollider() {
        BoundingBox boundingBox = BoundingBox.of(original, original).expandDirectional(direction.normalize().multiply(maxDistance)).expand(raySize);
        return new AxisAlignedBoundingBoxCollider(world, boundingBox.getMin(), boundingBox.getMax());
    }

    @Override
    public ImmutableVector getHalfExtents() {
        return toBoundingBoxCollider().getHalfExtents();
    }

    @Override
    public boolean contains(ImmutableVector point) {
        return toBoundingBoxCollider().contains(point);
    }

    public Optional<Map.Entry<Block, BlockFace>> getFirstBlock(boolean ignoreLiquids, boolean ignorePassable) {
        RayTraceResult traceResult = world.rayTraceBlocks(original.toLocation(world), direction, maxDistance, ignoreLiquids ? FluidCollisionMode.NEVER : FluidCollisionMode.ALWAYS, ignorePassable);
        if (traceResult == null) return Optional.empty();
        Block block = traceResult.getHitBlock();
        BlockFace blockFace = traceResult.getHitBlockFace();
        return block == null || blockFace == null ? Optional.empty() : Optional.of(Map.entry(block, blockFace));
    }

    public Optional<Block> getBlock(boolean ignoreLiquids, boolean ignorePassable, Predicate<Block> filter) {
        return this.getBlock(ignoreLiquids, ignorePassable, true, filter);
    }

    public Optional<Block> getBlock(boolean ignoreLiquids, boolean ignorePassable, boolean ignoreObstacles, Predicate<Block> filter) {
        BlockIterator it = new BlockIterator(world, original, direction, raySize, Math.min(100, (int) Math.ceil(maxDistance)));
        while (it.hasNext()) {
            Block block = it.next();
            boolean passable = block.isPassable();
            if (passable) {
                if (block.isLiquid()) {
                    if (ignoreLiquids) {
                        continue;
                    }
                } else if (ignorePassable) {
                    continue;
                }
            }
            if (filter.test(block)) {
                return Optional.of(block);
            }
            if (!ignoreObstacles && !passable) {
                break;
            }
        }
        return Optional.empty();
    }

    public Optional<Entity> getEntity(Predicate<Entity> filter) {
        RayTraceResult traceResult = world.rayTraceEntities(original.toLocation(world), direction, maxDistance, raySize, filter);
        if (traceResult == null) return Optional.empty();
        return Optional.ofNullable(traceResult.getHitEntity());
    }

    public Optional<Vector> getPosition(boolean ignoreEntity, boolean ignoreBlock, boolean ignoreLiquid, boolean ignorePassable, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        final double maxDistance = this.maxDistance;

        Vector blockPosition = null;
        Vector entityPosition = null;
        Vector position = original.add(direction.normalize().multiply(maxDistance));

        if (!ignoreBlock) {
            Optional<Block> optional = getBlock(ignoreLiquid, ignorePassable, true, blockFilter);
            if (optional.isPresent()) {
                Block block = optional.get();
                ImmutableVector immutableVector = new ImmutableVector(toCenterLocation(block.getLocation()));
                blockPosition = original.add(direction.normalize().multiply(original.distance(immutableVector) - 0.5));
                this.maxDistance = original.distance(blockPosition);
            }
        }

        if (!ignoreEntity) {
            Optional<Entity> optional = getEntity(entityFilter);
            if (optional.isPresent()) {
                Entity entity = optional.get();
                entityPosition = new ImmutableVector(entity.getLocation()).add(new ImmutableVector(0, entity.getHeight() / 2, 0));
            }
        }
        this.maxDistance = maxDistance;
        return Optional.of(entityPosition == null ? blockPosition == null ? position : blockPosition : entityPosition);
    }


    /**
     * @return A new location where X/Y/Z are the center of the block
     */
    private Location toCenterLocation(Location location) {
        Location centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setY(location.getBlockY() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);
        return centerLoc;
    }
}
