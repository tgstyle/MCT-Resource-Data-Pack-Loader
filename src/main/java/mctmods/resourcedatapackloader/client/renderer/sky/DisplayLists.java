package mctmods.resourcedatapackloader.client.renderer.sky;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class DisplayLists {
    private final int count;
    private int base = -1;

    DisplayLists(int count) { this.count = count; }

    int generate() {
        base = GLAllocation.generateDisplayLists(count);
        return base;
    }

    int base() { return base; }

    boolean empty() { return base < 0; }

    void release() {
        if (base < 0) { return; }
        GLAllocation.deleteDisplayLists(base, count);
        base = -1;
    }
}
