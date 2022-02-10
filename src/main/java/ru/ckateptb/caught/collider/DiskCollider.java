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
import ru.ckateptb.caught.Collider;
import ru.ckateptb.caught.math.ImmutableVector;

@Getter
public class DiskCollider extends CompositeCollider {
    private final OrientedBoundingBoxCollider orientedBoundingBoxCollider;
    private final SphereCollider sphereCollider;

    public DiskCollider(World world, OrientedBoundingBoxCollider orientedBoundingBoxCollider, SphereCollider sphereCollider) {
        this(world, ImmutableVector.ZERO, orientedBoundingBoxCollider, sphereCollider);
    }

    public DiskCollider(World world, Vector position, OrientedBoundingBoxCollider orientedBoundingBoxCollider, SphereCollider sphereCollider) {
        super(world, position, orientedBoundingBoxCollider, sphereCollider);
        this.orientedBoundingBoxCollider = orientedBoundingBoxCollider;
        this.sphereCollider = sphereCollider;
    }

    @Override
    public boolean intersects(Collider collider) {
        return super.allIntersects(collider);
    }

    @Override
    public DiskCollider at(Vector point) {
        return new DiskCollider(world, point, orientedBoundingBoxCollider.at(point), sphereCollider.at(point));
    }

    @Override
    public ImmutableVector getHalfExtents() {
        return orientedBoundingBoxCollider.getHalfExtents();
    }

    @Override
    public boolean contains(ImmutableVector point) {
        return allContains(point);
    }
}
