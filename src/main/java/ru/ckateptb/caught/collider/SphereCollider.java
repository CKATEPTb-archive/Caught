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
import org.bukkit.World;
import org.bukkit.util.Vector;
import ru.ckateptb.caught.AbstractCollider;
import ru.ckateptb.caught.Collider;
import ru.ckateptb.caught.math.ImmutableVector;

@Getter
public class SphereCollider extends AbstractCollider {
    public final ImmutableVector center;
    public final double radius;

    public SphereCollider(World world, double radius) {
        this(world, ImmutableVector.ZERO, radius);
    }

    public SphereCollider(World world, Vector center, double radius) {
        super(world);
        this.center = new ImmutableVector(center);
        this.radius = radius;
    }

    @Override
    public boolean intersects(Collider collider) {
        if (collider instanceof SphereCollider sphereCollider) {
            return intersects(sphereCollider);
        }
        if (collider instanceof AxisAlignedBoundingBoxCollider axisAlignedBoundingBoxCollider) {
            return intersects(axisAlignedBoundingBoxCollider);
        }
        if (collider instanceof RayCollider rayCollider) {
            return rayCollider.intersects(this);
        }
        if (collider instanceof OrientedBoundingBoxCollider orientedBoundingBoxCollider) {
            return intersects(orientedBoundingBoxCollider);
        }
        if (collider instanceof CompositeCollider compositeCollider) {
            return compositeCollider.intersects(this);
        }
        return false;
    }

    private boolean intersects(OrientedBoundingBoxCollider orientedBoundingBoxCollider) {
        ImmutableVector vector = center.subtract(orientedBoundingBoxCollider.closestPosition(center));
        return vector.dot(vector) <= radius * radius;
    }

    private boolean intersects(SphereCollider collider) {
        return collider.getWorld().equals(this.world) && this.center.distance(collider.center) <= this.radius + collider.radius;
    }

    public boolean intersects(AxisAlignedBoundingBoxCollider collider) {
        if (!collider.getWorld().equals(this.world)) return false;
        Vector minVector = collider.getMin();
        Vector maxVector = collider.getMax();
        double[] center = {this.center.getX(), this.center.getY(), this.center.getZ()};
        double[] min = {minVector.getX(), minVector.getY(), minVector.getZ()};
        double[] max = {maxVector.getX(), maxVector.getY(), maxVector.getZ()};
        double result = 0;
        for (int i = 0; i < 3; i++) {
            if (center[i] < min[i]) result += Math.pow(center[i] - min[i], 2);
            else if (center[i] > max[i]) result += Math.pow(center[i] - max[i], 2);
        }
        return result <= Math.pow(radius, 2);
    }

    @Override
    public ImmutableVector getPosition() {
        return center;
    }

    @Override
    public SphereCollider at(Vector point) {
        return new SphereCollider(this.world, point, radius);
    }

    @Override
    public ImmutableVector getHalfExtents() {
        return new ImmutableVector(radius, radius, radius);
    }

    @Override
    public boolean contains(ImmutableVector point) {
        return point.isInSphere(center, radius);
    }
}
