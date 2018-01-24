package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BakedQuad
{
    /**
     * Joined 4 vertex records, each stores packed data according to the VertexFormat of the quad. Vanilla minecraft
     * uses DefaultVertexFormats.BLOCK, Forge uses (usually) ITEM, use BakedQuad.getFormat() to get the correct format.
     */
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    protected final TextureAtlasSprite sprite;

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn)
    {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = spriteIn;
    }

    public TextureAtlasSprite getSprite()
    {
        return this.sprite;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        return this.face;
    }
}
