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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CompositeCollider extends AbstractCollider {
    private final List<Collider> colliders = new ArrayList<>();
    private final ImmutableVector position;

    public CompositeCollider(World world, Vector position, Collider... colliders) {
        super(world);
        this.colliders.addAll(Arrays.asList(colliders));
        this.position = new ImmutableVector(position);
    }

    public boolean allIntersects(Collider other) {
        return colliders.stream().allMatch(collider -> collider.intersects(other));
    }

    public boolean anyIntersects(Collider other) {
        return colliders.stream().anyMatch(collider -> collider.intersects(other));
    }

    @Override
    public boolean intersects(Collider collider) {
        return anyIntersects(collider);
    }

    @Override
    public ImmutableVector getPosition() {
        return this.position;
    }

    @Override
    public CompositeCollider at(Vector point) {
        CompositeCollider compositeCollider = new CompositeCollider(world, new ImmutableVector(point));
        for (Collider collider : colliders) {
            compositeCollider.colliders.add(collider.at(point));
        }
        return compositeCollider;
    }

    @Override
    public ImmutableVector getHalfExtents() {
        return ImmutableVector.ONE;
    }

    public boolean allContains(ImmutableVector point) {
        return colliders.stream().allMatch(collider -> collider.contains(point));
    }

    public boolean anyContains(ImmutableVector point) {
        return colliders.stream().anyMatch(collider -> collider.contains(point));
    }

    @Override
    public boolean contains(ImmutableVector point) {
        return anyContains(point);
    }
}
