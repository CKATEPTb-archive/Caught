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

package ru.ckateptb.caught;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import ru.ckateptb.caught.math.ImmutableVector;

@Getter
@Setter
public abstract class AbstractCollider implements Collider {
    protected World world;

    public AbstractCollider(World world) {
        this.world = world;
    }

    @Override
    public boolean contains(Vector point) {
        return contains(new ImmutableVector(point));
    }

    @Override
    public Location getLocation() {
        return getPosition().toLocation(world);
    }

    @Override
    public boolean contains(Location location) {
        return world.equals(location.getWorld()) && contains(location.toVector());
    }
}
