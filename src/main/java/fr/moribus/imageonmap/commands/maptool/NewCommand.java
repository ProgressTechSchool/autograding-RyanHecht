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
import fr.moribus.imageonmap.image.ImageRendererExecutor;
import fr.moribus.imageonmap.image.ImageUtils;
import fr.moribus.imageonmap.map.ImageMap;
import fr.zcraft.zlib.components.commands.CommandException;
import fr.zcraft.zlib.components.commands.CommandInfo;
import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.components.worker.WorkerCallback;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;

@CommandInfo (name = "new", usageParameters = "<URL> [resize]")
public class NewCommand  extends IoMCommand
{
    @Override
    protected void run() throws CommandException
    {
        final Player player = playerSender();
        ImageUtils.ScalingType scaling = ImageUtils.ScalingType.NONE;
        URL url;
        int width = 0, height = 0;
        
        if(args.length < 1) throwInvalidArgument(I.t("You must give an URL to take the image from."));
        
        try
        {
            url = new URL(args[0]);
        }
        catch(MalformedURLException ex)
        {
            throwInvalidArgument(I.t("Invalid URL."));
            return;
        }
        
        if(args.length >= 2)
        {
            if(args.length >= 4) {
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
            }

            switch(args[1]) {
                case "resize": scaling = ImageUtils.ScalingType.CONTAINED; break;
                case "resize-stretched": scaling = ImageUtils.ScalingType.STRETCHED; break;
                case "resize-covered": scaling = ImageUtils.ScalingType.COVERED; break;
                default: throwInvalidArgument(I.t("Invalid Stretching mode.")); break;
            }
        }
        
        info(I.t("Rendering..."));
        ImageRendererExecutor.render(url, scaling, player.getUniqueId(), width, height, new WorkerCallback<ImageMap>()
        {
            @Override
            public void finished(ImageMap result)
            {
                player.sendMessage(I.t("{cst}Rendering finished!"));
                if(result.give(player))
                {
                    info(I.t("The rendered map was too big to fit in your inventory."));
                    info(I.t("Use '/maptool getremaining' to get the remaining maps."));
                }
            }

            @Override
            public void errored(Throwable exception)
            {
                player.sendMessage(I.t("{ce}Map rendering failed: {0}", exception.getMessage()));

                PluginLogger.warning("Rendering from {0} failed: {1}: {2}",
                        player.getName(),
                        exception.getClass().getCanonicalName(),
                        exception.getMessage());
            }
        });
    }

    @Override
    public boolean canExecute(CommandSender sender)
    {
        return Permissions.NEW.grantedTo(sender);
    }
}
