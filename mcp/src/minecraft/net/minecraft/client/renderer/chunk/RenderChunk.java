package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RenderChunk
{
    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
    private final ReentrantLock lockCompileTask = new ReentrantLock();
    private final ReentrantLock lockCompiledChunk = new ReentrantLock();
    private ChunkCompileTaskGenerator compileTask;
    private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
    private final int index;
    private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];
    public AxisAlignedBB boundingBox;
    private int frameIndex = -1;
    private boolean needsUpdate = true;
    private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
    private final BlockPos.MutableBlockPos[] mapEnumFacing = new BlockPos.MutableBlockPos[6];
    private boolean needsImmediateUpdate;
    private ChunkCache worldView;

    public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, int indexIn)
    {
        for (int i = 0; i < this.mapEnumFacing.length; ++i)
        {
            this.mapEnumFacing[i] = new BlockPos.MutableBlockPos();
        }

        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;

        if (OpenGlHelper.useVbo())
        {
            for (int j = 0; j < BlockRenderLayer.values().length; ++j)
            {
                this.vertexBuffers[j] = new VertexBuffer(DefaultVertexFormats.BLOCK);
            }
        }
    }

    public boolean setFrameIndex(int frameIndexIn)
    {
        if (this.frameIndex == frameIndexIn)
        {
            return false;
        }
        else
        {
            this.frameIndex = frameIndexIn;
            return true;
        }
    }

    public VertexBuffer getVertexBufferByLayer(int layer)
    {
        return this.vertexBuffers[layer];
    }

    /**
     * Sets the RenderChunk base position
     */
    public void setPosition(int x, int y, int z)
    {
        if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ())
        {
            this.stopCompileTask();
            this.position.setPos(x, y, z);
            this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                this.mapEnumFacing[enumfacing.ordinal()].setPos(this.position).move(enumfacing, 16);
            }

            this.initModelviewMatrix();
        }
    }

    public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = generator.getCompiledChunk();

        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT))
        {
            this.preRenderBlocks(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), this.position);
            generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT).setVertexState(compiledchunk.getState());
            this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), compiledchunk);
        }
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = new CompiledChunk();
        int i = 1;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);
        generator.getLock().lock();

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            generator.setCompiledChunk(compiledchunk);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph lvt_9_1_ = new VisGraph();
        HashSet lvt_10_1_ = Sets.newHashSet();

        if (!this.worldView.isEmpty())
        {
            ++renderChunksUpdated;
            boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1))
            {
                IBlockState iblockstate = this.worldView.getBlockState(blockpos$mutableblockpos);
                Block block = iblockstate.getBlock();

                if (iblockstate.isOpaqueCube())
                {
                    lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
                }

                if (block.hasTileEntity())
                {
                    TileEntity tileentity = this.worldView.getTileEntity(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity != null)
                    {
                        TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.<TileEntity>getRenderer(tileentity);

                        if (tileentityspecialrenderer != null)
                        {
                            compiledchunk.addTileEntity(tileentity);

                            if (tileentityspecialrenderer.isGlobalRenderer(tileentity))
                            {
                                lvt_10_1_.add(tileentity);
                            }
                        }
                    }
                }

                BlockRenderLayer blockrenderlayer1 = block.getBlockLayer();
                int j = blockrenderlayer1.ordinal();

                if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);

                    if (!compiledchunk.isLayerStarted(blockrenderlayer1))
                    {
                        compiledchunk.setLayerStarted(blockrenderlayer1);
                        this.preRenderBlocks(bufferbuilder, blockpos);
                    }

                    aboolean[j] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, this.worldView, bufferbuilder);
                }
            }

            for (BlockRenderLayer blockrenderlayer : BlockRenderLayer.values())
            {
                if (aboolean[blockrenderlayer.ordinal()])
                {
                    compiledchunk.setLayerUsed(blockrenderlayer);
                }

                if (compiledchunk.isLayerStarted(blockrenderlayer))
                {
                    this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
        this.lockCompileTask.lock();

        try
        {
            Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
            Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
            set.removeAll(this.setTileEntities);
            set1.removeAll(lvt_10_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_10_1_);
            this.renderGlobal.updateTileEntities(set1, set);
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    protected void finishCompileTask()
    {
        this.lockCompileTask.lock();

        try
        {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    public ReentrantLock getLockCompileTask()
    {
        return this.lockCompileTask;
    }

    public ChunkCompileTaskGenerator makeCompileTaskChunk()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;

        try
        {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK, this.getDistanceSq());
            this.rebuildWorldView();
            chunkcompiletaskgenerator = this.compileTask;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator;
    }

    private void rebuildWorldView()
    {
        int i = 1;
        this.worldView = new ChunkCache(this.world, this.position.add(-1, -1, -1), this.position.add(16, 16, 16), 1);
    }

    @Nullable
    public ChunkCompileTaskGenerator makeCompileTaskTransparency()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;

        try
        {
            if (this.compileTask == null || this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.PENDING)
            {
                if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
                {
                    this.compileTask.finish();
                    this.compileTask = null;
                }

                this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
                this.compileTask.setCompiledChunk(this.compiledChunk);
                chunkcompiletaskgenerator = this.compileTask;
                return chunkcompiletaskgenerator;
            }

            chunkcompiletaskgenerator = null;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator;
    }

    protected double getDistanceSq()
    {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        double d0 = this.boundingBox.minX + 8.0D - entityplayersp.posX;
        double d1 = this.boundingBox.minY + 8.0D - entityplayersp.posY;
        double d2 = this.boundingBox.minZ + 8.0D - entityplayersp.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private void preRenderBlocks(BufferBuilder bufferBuilderIn, BlockPos pos)
    {
        bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
        bufferBuilderIn.setTranslation((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
    }

    private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, BufferBuilder bufferBuilderIn, CompiledChunk compiledChunkIn)
    {
        if (layer == BlockRenderLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer))
        {
            bufferBuilderIn.sortVertexData(x, y, z);
            compiledChunkIn.setState(bufferBuilderIn.getVertexState());
        }

        bufferBuilderIn.finishDrawing();
    }

    private void initModelviewMatrix()
    {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        float f = 1.000001F;
        GlStateManager.translate(-8.0F, -8.0F, -8.0F);
        GlStateManager.scale(1.000001F, 1.000001F, 1.000001F);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }

    public void multModelviewMatrix()
    {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.lockCompiledChunk.lock();

        try
        {
            this.compiledChunk = compiledChunkIn;
        }
        finally
        {
            this.lockCompiledChunk.unlock();
        }
    }

    public void stopCompileTask()
    {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }

    public void deleteGlResources()
    {
        this.stopCompileTask();
        this.world = null;

        for (int i = 0; i < BlockRenderLayer.values().length; ++i)
        {
            if (this.vertexBuffers[i] != null)
            {
                this.vertexBuffers[i].deleteGlBuffers();
            }
        }
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public void setNeedsUpdate(boolean immediate)
    {
        if (this.needsUpdate)
        {
            immediate |= this.needsImmediateUpdate;
        }

        this.needsUpdate = true;
        this.needsImmediateUpdate = immediate;
    }

    public void clearNeedsUpdate()
    {
        this.needsUpdate = false;
        this.needsImmediateUpdate = false;
    }

    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    public boolean needsImmediateUpdate()
    {
        return this.needsUpdate && this.needsImmediateUpdate;
    }

    public BlockPos getBlockPosOffset16(EnumFacing facing)
    {
        return this.mapEnumFacing[facing.ordinal()];
    }

    public World getWorld()
    {
        return this.world;
    }
}
