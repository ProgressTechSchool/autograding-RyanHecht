/*
 * Copyright (C) 2013 Moribus
 * Copyright (C) 2015 ProkopyL <prokopylmc@gmail.com>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.moribus.imageonmap.commands.maptool;

import fr.moribus.imageonmap.Permissions;
import fr.moribus.imageonmap.commands.IoMCommand;
import fr.moribus.imageonmap.ui.MapItemManager;
import fr.zcraft.zlib.components.commands.CommandException;
import fr.zcraft.zlib.components.commands.CommandInfo;
import fr.zcraft.zlib.components.i18n.I;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo (name = "getremaining", aliases = {"getrest"})
public class GetRemainingCommand extends IoMCommand
{
    @Override
    protected void run() throws CommandException
    {
        Player player = playerSender();
        
        if(MapItemManager.getCacheSize(player) <= 0)
        {
            info(I.t("You have no remaining map."));
            return;
        }
        
        int givenMaps = MapItemManager.giveCache(player);
        
        if(givenMaps == 0)
        {
            error(I.t("Your inventory is full! Make some space before requesting the remaining maps."));
        }
        else
        {
            info(I.tn("There is {0} map remaining.", "There are {0} maps remaining.", MapItemManager.getCacheSize(player)));
        }
    }

    @Override
    public boolean canExecute(CommandSender sender)
    {
        return Permissions.NEW.grantedTo(sender) || Permissions.GET.grantedTo(sender);
    }
}
