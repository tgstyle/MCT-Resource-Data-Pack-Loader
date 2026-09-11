package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ContentContainerItem extends Item {
    private final ItemDef def;
    private final ContainerDef container;

    public ContentContainerItem(ItemDef def, ContainerDef container, Properties properties) {
        super(properties.stacksTo(1));
        this.def = def;
        this.container = container;
    }

    public ItemDef getDef() { return def; }

    public ContainerDef container() { return container; }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (level.isClientSide() || !(player instanceof ServerPlayer server)) { return InteractionResultHolder.success(held); }
        open(server, held);
        return InteractionResultHolder.consume(held);
    }

    public void open(ServerPlayer player, ItemStack held) {
        ContentPouchInventory inventory = new ContentPouchInventory(held, container);
        MenuProvider provider = new MenuProvider() {
            @Override @Nonnull public Component getDisplayName() { return held.getHoverName(); }

            @Override @Nonnull public AbstractContainerMenu createMenu(int id, @Nonnull Inventory bag, @Nonnull Player opening) {
                return new ContentContainerMenu(id, bag, inventory, container, held);
            }
        };
        NetworkHooks.openScreen(player, provider, extra -> ContentContainerMenu.write(extra, container));
    }
}
