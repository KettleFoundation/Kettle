package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot
{
    public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return false;
    }

    protected void drawBackground()
    {
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.getListEntry(slotIndex).drawEntry(slotIndex, xPos, yPos, this.getListWidth(), heightIn, mouseXIn, mouseYIn, this.isMouseYWithinSlotBounds(mouseYIn) && this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == slotIndex, partialTicks);
    }

    protected void updateItemPos(int entryID, int insideLeft, int yPos, float partialTicks)
    {
        this.getListEntry(entryID).updatePosition(entryID, insideLeft, yPos, partialTicks);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        if (this.isMouseYWithinSlotBounds(mouseY))
        {
            int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (i >= 0)
            {
                int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
                int l = mouseX - j;
                int i1 = mouseY - k;

                if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1))
                {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(int x, int y, int mouseEvent)
    {
        for (int i = 0; i < this.getSize(); ++i)
        {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = x - j;
            int i1 = y - k;
            this.getListEntry(i).mouseReleased(i, x, y, mouseEvent, l, i1);
        }

        this.setEnabled(true);
        return false;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry
    {
        void updatePosition(int slotIndex, int x, int y, float partialTicks);

        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks);

        boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY);

        void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}
