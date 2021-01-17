package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Cosmetic {

    public static boolean useHat = false;
    public static boolean useCape = false;
    public static Item currentHat = Items.NETHER_STAR;

    
    static boolean you = true;
    static boolean friends = false;
    static boolean group = false;
    static boolean others = false;

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
				case "hat":
					useHat = setting.value == 1;
					break;
                case "cape":
                    if(useCape!=(setting.value == 1)&&Bartender.MC.player!=null&&setting.value == 0){
                        Bartender.msg("Please restart your game if you want to see other capes like optifine cape");
                    }
                    useCape = setting.value == 1;
                    break;
				case "friends":
					friends = setting.value == 1;
					break;
				case "group":
					group = setting.value == 1;
					break;
				case "self":
					you = setting.value == 1;
					break;
				case "others":
					others = setting.value == 1;
					break;
				default:
					break;
			}
		}
	}

    ItemStack hat = null;

    @SubscribeEvent
    public void render(RenderPlayerEvent.Pre e) {
    	if(!useHat)return;
        if (Bartender.MC.player == null || nameShouldCancel(e.getEntityPlayer().getDisplayNameString())) return;
        hat = e.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        e.getEntityPlayer().inventory.armorInventory.set(EntityEquipmentSlot.HEAD.getIndex(), new ItemStack(currentHat));
    }

    private boolean nameShouldCancel(String name) {
        if (!you && name.equals(Bartender.MC.player.getDisplayNameString())) return true;
        if (!group && PlayerGroup.members.contains(name) && !name.equals(Bartender.MC.player.getDisplayNameString())) return true;
        if (!friends && PlayerFriends.friends.contains(name) && !name.equals(Bartender.MC.player.getDisplayNameString())) return true;
        if (!friends && PlayerFriends.impactFriends.contains(name)&& !name.equals(Bartender.MC.player.getDisplayNameString())) return true;
        if (!others&&!name.equals(Bartender.MC.player.getDisplayNameString())) return true;
        return false;
    }

    @SubscribeEvent
    public void render(RenderPlayerEvent.Post e) {
		if(!useHat)return;
        if (Bartender.MC.player == null || hat == null || nameShouldCancel(e.getEntityPlayer().getDisplayNameString()))
            return;
        e.getEntityPlayer().inventory.armorInventory.set(EntityEquipmentSlot.HEAD.getIndex(), hat);
    }
}
