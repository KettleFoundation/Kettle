package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldEventListener, IResourceManagerReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");

    /** A reference to the Minecraft object. */
    private final Minecraft mc;

    /** The RenderEngine instance used by RenderGlobal */
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient world;
    private Set<RenderChunk> chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.<RenderGlobal.ContainerLocalRenderInformation>newArrayListWithCapacity(69696);
    private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
    private ViewFrustum viewFrustum;

    /** The star GL Call list */
    private int starGLCallList = -1;

    /** OpenGL sky list */
    private int glSkyList = -1;

    /** OpenGL sky list 2 */
    private int glSkyList2 = -1;
    private final VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;

    /**
     * counts the cloud render updates. Used with mod to stagger some updates
     */
    private int cloudTickCounter;
    private final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.<Integer, DestroyBlockProgress>newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.<BlockPos, ISound>newHashMap();
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
    private Framebuffer entityOutlineFramebuffer;

    /** Stores the shader group for the entity_outline shader */
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private ChunkRenderDispatcher renderDispatcher;
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;

    /** Render entities startup counter (init value=2) */
    private int renderEntitiesStartupCounter = 2;

    /** Count entities total */
    private int countEntitiesTotal;

    /** Count entities rendered */
    private int countEntitiesRendered;

    /** Count entities hidden */
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    private boolean displayListEntitiesDirty = true;
    private boolean entityOutlinesRendered;
    private final Set<BlockPos> setLightUpdates = Sets.<BlockPos>newHashSet();

    public RenderGlobal(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();

        if (this.vboEnabled)
        {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        }
        else
        {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }

        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons()
    {
        TextureMap texturemap = this.mc.getTextureMapBlocks();

        for (int i = 0; i < this.destroyBlockIcons.length; ++i)
        {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }

    /**
     * Creates the entity outline shader to be stored in RenderGlobal.entityOutlineShader
     */
    public void makeEntityOutlineShader()
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null)
            {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }

            ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

            try
            {
                this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
                this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
            catch (JsonSyntaxException jsonsyntaxexception)
            {
                LOGGER.warn("Failed to load shader: {}", resourcelocation, jsonsyntaxexception);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        }
        else
        {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    public void renderEntityOutlineFramebuffer()
    {
        if (this.isRenderEntityOutlines())
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }

    protected boolean isRenderEntityOutlines()
    {
        return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null;
    }

    private void generateSky2()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.sky2VBO != null)
        {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled)
        {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, -16.0F, true);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList2, 4864);
            this.renderSky(bufferbuilder, -16.0F, true);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void generateSky()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.skyVBO != null)
        {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled)
        {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(bufferbuilder, 16.0F, false);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList, 4864);
            this.renderSky(bufferbuilder, 16.0F, false);
            tessellator.draw();
            GlStateManager.glEndList();
        }
    }

    private void renderSky(BufferBuilder bufferBuilderIn, float posY, boolean reverseX)
    {
        int i = 64;
        int j = 6;
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64)
        {
            for (int l = -384; l <= 384; l += 64)
            {
                float f = (float)k;
                float f1 = (float)(k + 64);

                if (reverseX)
                {
                    f1 = (float)k;
                    f = (float)(k + 64);
                }

                bufferBuilderIn.pos((double)f, (double)posY, (double)l).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)l).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)(l + 64)).endVertex();
                bufferBuilderIn.pos((double)f, (double)posY, (double)(l + 64)).endVertex();
            }
        }
    }

    private void generateStars()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.starVBO != null)
        {
            this.starVBO.deleteGlBuffers();
        }

        if (this.starGLCallList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        if (this.vboEnabled)
        {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(bufferbuilder);
            bufferbuilder.finishDrawing();
            bufferbuilder.reset();
            this.starVBO.bufferData(bufferbuilder.getByteBuffer());
        }
        else
        {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.glNewList(this.starGLCallList, 4864);
            this.renderStars(bufferbuilder);
            tessellator.draw();
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(BufferBuilder bufferBuilderIn)
    {
        Random random = new Random(10842L);
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i)
        {
            double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D)
            {
                d4 = 1.0D / Math.sqrt(d4);
                d0 = d0 * d4;
                d1 = d1 * d4;
                d2 = d2 * d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j)
                {
                    double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(@Nullable WorldClient worldClientIn)
    {
        if (this.world != null)
        {
            this.world.removeEventListener(this);
        }

        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.setWorld(worldClientIn);
        this.world = worldClientIn;

        if (worldClientIn != null)
        {
            worldClientIn.addEventListener(this);
            this.loadRenderers();
        }
        else
        {
            this.chunksToUpdate.clear();
            this.renderInfos.clear();

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }

            if (this.renderDispatcher != null)
            {
                this.renderDispatcher.stopWorkerThreads();
            }

            this.renderDispatcher = null;
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.world != null)
        {
            if (this.renderDispatcher == null)
            {
                this.renderDispatcher = new ChunkRenderDispatcher();
            }

            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            Blocks.LEAVES2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            if (flag && !this.vboEnabled)
            {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            }
            else if (!flag && this.vboEnabled)
            {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }

            if (flag != this.vboEnabled)
            {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();

            synchronized (this.setTileEntities)
            {
                this.setTileEntities.clear();
            }

            this.viewFrustum = new ViewFrustum(this.world, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.world != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    protected void stopChunkUpdates()
    {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.entityOutlineShader != null)
            {
                this.entityOutlineShader.createBindFramebuffers(width, height);
            }
        }
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        if (this.renderEntitiesStartupCounter > 0)
        {
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.world.profiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.world, this.mc.fontRenderer, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            this.world.profiler.endStartSection("global");
            List<Entity> list = this.world.getLoadedEntityList();
            this.countEntitiesTotal = list.size();

            for (int i = 0; i < this.world.weatherEffects.size(); ++i)
            {
                Entity entity1 = this.world.weatherEffects.get(i);
                ++this.countEntitiesRendered;

                if (entity1.isInRangeToRender3d(d0, d1, d2))
                {
                    this.renderManager.renderEntityStatic(entity1, partialTicks, false);
                }
            }

            this.world.profiler.endStartSection("entities");
            List<Entity> list1 = Lists.<Entity>newArrayList();
            List<Entity> list2 = Lists.<Entity>newArrayList();
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
            {
                Chunk chunk = this.world.getChunkFromBlockCoords(renderglobal$containerlocalrenderinformation.renderChunk.getPosition());
                ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];

                if (!classinheritancemultimap.isEmpty())
                {
                    for (Entity entity2 : classinheritancemultimap)
                    {
                        boolean flag = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2.isRidingOrBeingRiddenBy(this.mc.player);

                        if (flag)
                        {
                            boolean flag1 = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping() : false;

                            if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag1) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.world.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity2))))
                            {
                                ++this.countEntitiesRendered;
                                this.renderManager.renderEntityStatic(entity2, partialTicks, false);

                                if (this.isOutlineActive(entity2, entity, camera))
                                {
                                    list1.add(entity2);
                                }

                                if (this.renderManager.isRenderMultipass(entity2))
                                {
                                    list2.add(entity2);
                                }
                            }
                        }
                    }
                }
            }

            blockpos$pooledmutableblockpos.release();

            if (!list2.isEmpty())
            {
                for (Entity entity3 : list2)
                {
                    this.renderManager.renderMultipass(entity3, partialTicks);
                }
            }

            if (this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered))
            {
                this.world.profiler.endStartSection("entityOutlines");
                this.entityOutlineFramebuffer.framebufferClear();
                this.entityOutlinesRendered = !list1.isEmpty();

                if (!list1.isEmpty())
                {
                    GlStateManager.depthFunc(519);
                    GlStateManager.disableFog();
                    this.entityOutlineFramebuffer.bindFramebuffer(false);
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);

                    for (int j = 0; j < list1.size(); ++j)
                    {
                        this.renderManager.renderEntityStatic(list1.get(j), partialTicks, false);
                    }

                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.depthMask(false);
                    this.entityOutlineShader.render(partialTicks);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.depthFunc(515);
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                }

                this.mc.getFramebuffer().bindFramebuffer(false);
            }

            this.world.profiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();

            for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos)
            {
                List<TileEntity> list3 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();

                if (!list3.isEmpty())
                {
                    for (TileEntity tileentity2 : list3)
                    {
                        TileEntityRendererDispatcher.instance.render(tileentity2, partialTicks, -1);
                    }
                }
            }

            synchronized (this.setTileEntities)
            {
                for (TileEntity tileentity : this.setTileEntities)
                {
                    TileEntityRendererDispatcher.instance.render(tileentity, partialTicks, -1);
                }
            }

            this.preRenderDamagedBlocks();

            for (DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values())
            {
                BlockPos blockpos = destroyblockprogress.getPosition();

                if (this.world.getBlockState(blockpos).getBlock().hasTileEntity())
                {
                    TileEntity tileentity1 = this.world.getTileEntity(blockpos);

                    if (tileentity1 instanceof TileEntityChest)
                    {
                        TileEntityChest tileentitychest = (TileEntityChest)tileentity1;

                        if (tileentitychest.adjacentChestXNeg != null)
                        {
                            blockpos = blockpos.offset(EnumFacing.WEST);
                            tileentity1 = this.world.getTileEntity(blockpos);
                        }
                        else if (tileentitychest.adjacentChestZNeg != null)
                        {
                            blockpos = blockpos.offset(EnumFacing.NORTH);
                            tileentity1 = this.world.getTileEntity(blockpos);
                        }
                    }

                    IBlockState iblockstate = this.world.getBlockState(blockpos);

                    if (tileentity1 != null && iblockstate.hasCustomBreakingProgress())
                    {
                        TileEntityRendererDispatcher.instance.render(tileentity1, partialTicks, destroyblockprogress.getPartialBlockDamage());
                    }
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    /**
     * Checks if the given entity should have an outline rendered.
     */
    private boolean isOutlineActive(Entity entityIn, Entity viewer, ICamera camera)
    {
        boolean flag = viewer instanceof EntityLivingBase && ((EntityLivingBase)viewer).isPlayerSleeping();

        if (entityIn == viewer && this.mc.gameSettings.thirdPersonView == 0 && !flag)
        {
            return false;
        }
        else if (entityIn.isGlowing())
        {
            return true;
        }
        else if (this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() && entityIn instanceof EntityPlayer)
        {
            return entityIn.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entityIn.getEntityBoundingBox()) || entityIn.isRidingOrBeingRiddenBy(this.mc.player);
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        int i = this.viewFrustum.renderChunks.length;
        int j = this.getRenderedChunks();
        return String.format("C: %d/%d %sD: %d, L: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.setLightUpdates.size(), this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
    }

    protected int getRenderedChunks()
    {
        int i = 0;

        for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
        {
            CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;

            if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty())
            {
                ++i;
            }
        }

        return i;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden;
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator)
    {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        this.world.profiler.startSection("camera");
        double d0 = viewEntity.posX - this.frustumUpdatePosX;
        double d1 = viewEntity.posY - this.frustumUpdatePosY;
        double d2 = viewEntity.posZ - this.frustumUpdatePosZ;

        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D)
        {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }

        this.world.profiler.endStartSection("renderlistcamera");
        double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d3, d4, d5);
        this.world.profiler.endStartSection("cull");

        if (this.debugFixedClippingHelper != null)
        {
            Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = frustum;
        }

        this.mc.mcProfiler.endStartSection("culling");
        BlockPos blockpos1 = new BlockPos(d3, d4 + (double)viewEntity.getEyeHeight(), d5);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
        BlockPos blockpos = new BlockPos(MathHelper.floor(d3 / 16.0D) * 16, MathHelper.floor(d4 / 16.0D) * 16, MathHelper.floor(d5 / 16.0D) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double)viewEntity.rotationPitch != this.lastViewEntityPitch || (double)viewEntity.rotationYaw != this.lastViewEntityYaw;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = (double)viewEntity.rotationPitch;
        this.lastViewEntityYaw = (double)viewEntity.rotationYaw;
        boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");

        if (!flag && this.displayListEntitiesDirty)
        {
            this.displayListEntitiesDirty = false;
            this.renderInfos = Lists.<RenderGlobal.ContainerLocalRenderInformation>newArrayList();
            Queue<RenderGlobal.ContainerLocalRenderInformation> queue = Queues.<RenderGlobal.ContainerLocalRenderInformation>newArrayDeque();
            Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.mc.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D));
            boolean flag1 = this.mc.renderChunksMany;

            if (renderchunk != null)
            {
                boolean flag2 = false;
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = new RenderGlobal.ContainerLocalRenderInformation(renderchunk, (EnumFacing)null, 0);
                Set<EnumFacing> set1 = this.getVisibleFacings(blockpos1);

                if (set1.size() == 1)
                {
                    Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
                    set1.remove(enumfacing);
                }

                if (set1.isEmpty())
                {
                    flag2 = true;
                }

                if (flag2 && !playerSpectator)
                {
                    this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
                }
                else
                {
                    if (playerSpectator && this.world.getBlockState(blockpos1).isOpaqueCube())
                    {
                        flag1 = false;
                    }

                    renderchunk.setFrameIndex(frameCount);
                    queue.add(renderglobal$containerlocalrenderinformation3);
                }
            }
            else
            {
                int i = blockpos1.getY() > 0 ? 248 : 8;

                for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; ++j)
                {
                    for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; ++k)
                    {
                        RenderChunk renderchunk1 = this.viewFrustum.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));

                        if (renderchunk1 != null && camera.isBoundingBoxInFrustum(renderchunk1.boundingBox))
                        {
                            renderchunk1.setFrameIndex(frameCount);
                            queue.add(new RenderGlobal.ContainerLocalRenderInformation(renderchunk1, (EnumFacing)null, 0));
                        }
                    }
                }
            }

            this.mc.mcProfiler.startSection("iteration");

            while (!queue.isEmpty())
            {
                RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = queue.poll();
                RenderChunk renderchunk3 = renderglobal$containerlocalrenderinformation1.renderChunk;
                EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation1.facing;
                this.renderInfos.add(renderglobal$containerlocalrenderinformation1);

                for (EnumFacing enumfacing1 : EnumFacing.values())
                {
                    RenderChunk renderchunk2 = this.getRenderChunkOffset(blockpos, renderchunk3, enumfacing1);

                    if ((!flag1 || !renderglobal$containerlocalrenderinformation1.hasDirection(enumfacing1.getOpposite())) && (!flag1 || enumfacing2 == null || renderchunk3.getCompiledChunk().isVisible(enumfacing2.getOpposite(), enumfacing1)) && renderchunk2 != null && renderchunk2.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(renderchunk2.boundingBox))
                    {
                        RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = new RenderGlobal.ContainerLocalRenderInformation(renderchunk2, enumfacing1, renderglobal$containerlocalrenderinformation1.counter + 1);
                        renderglobal$containerlocalrenderinformation.setDirection(renderglobal$containerlocalrenderinformation1.setFacing, enumfacing1);
                        queue.add(renderglobal$containerlocalrenderinformation);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.endStartSection("captureFrustum");

        if (this.debugFixTerrainFrustum)
        {
            this.fixTerrainFrustum(d3, d4, d5);
            this.debugFixTerrainFrustum = false;
        }

        this.mc.mcProfiler.endStartSection("rebuildNear");
        Set<RenderChunk> set = this.chunksToUpdate;
        this.chunksToUpdate = Sets.<RenderChunk>newLinkedHashSet();

        for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 : this.renderInfos)
        {
            RenderChunk renderchunk4 = renderglobal$containerlocalrenderinformation2.renderChunk;

            if (renderchunk4.needsUpdate() || set.contains(renderchunk4))
            {
                this.displayListEntitiesDirty = true;
                BlockPos blockpos2 = renderchunk4.getPosition().add(8, 8, 8);
                boolean flag3 = blockpos2.distanceSq(blockpos1) < 768.0D;

                if (!renderchunk4.needsImmediateUpdate() && !flag3)
                {
                    this.chunksToUpdate.add(renderchunk4);
                }
                else
                {
                    this.mc.mcProfiler.startSection("build near");
                    this.renderDispatcher.updateChunkNow(renderchunk4);
                    renderchunk4.clearNeedsUpdate();
                    this.mc.mcProfiler.endSection();
                }
            }
        }

        this.chunksToUpdate.addAll(set);
        this.mc.mcProfiler.endSection();
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos)
    {
        VisGraph visgraph = new VisGraph();
        BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk chunk = this.world.getChunkFromBlockCoords(blockpos);

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15)))
        {
            if (chunk.getBlockState(blockpos$mutableblockpos).isOpaqueCube())
            {
                visgraph.setOpaqueCube(blockpos$mutableblockpos);
            }
        }
        return visgraph.getVisibleFacings(pos);
    }

    @Nullable

    /**
     * Returns RenderChunk offset from given RenderChunk in given direction, or null if it can't be seen by player at
     * given BlockPos.
     */
    private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing)
    {
        BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);

        if (MathHelper.abs(playerPos.getX() - blockpos.getX()) > this.renderDistanceChunks * 16)
        {
            return null;
        }
        else if (blockpos.getY() >= 0 && blockpos.getY() < 256)
        {
            return MathHelper.abs(playerPos.getZ() - blockpos.getZ()) > this.renderDistanceChunks * 16 ? null : this.viewFrustum.getRenderChunk(blockpos);
        }
        else
        {
            return null;
        }
    }

    private void fixTerrainFrustum(double x, double y, double z)
    {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f1.transpose();
        Matrix4f matrix4f2 = new Matrix4f();
        Matrix4f.mul(matrix4f1, matrix4f, matrix4f2);
        matrix4f2.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 8; ++i)
        {
            Matrix4f.transform(matrix4f2, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
            this.debugTerrainMatrix[i].x /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].y /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].z /= this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0F;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks)
    {
        float f = (float)((double)entityIn.prevRotationPitch + (double)(entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float f1 = (float)((double)entityIn.prevRotationYaw + (double)(entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            f += 180.0F;
        }

        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        return new Vector3f(f3 * f4, f5, f2 * f4);
    }

    public int renderBlockLayer(BlockRenderLayer blockLayerIn, double partialTicks, int pass, Entity entityIn)
    {
        RenderHelper.disableStandardItemLighting();

        if (blockLayerIn == BlockRenderLayer.TRANSLUCENT)
        {
            this.mc.mcProfiler.startSection("translucent_sort");
            double d0 = entityIn.posX - this.prevRenderSortX;
            double d1 = entityIn.posY - this.prevRenderSortY;
            double d2 = entityIn.posZ - this.prevRenderSortZ;

            if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)
            {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int k = 0;

                for (RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
                {
                    if (renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15)
                    {
                        this.renderDispatcher.updateTransparencyLater(renderglobal$containerlocalrenderinformation.renderChunk);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.startSection("filterempty");
        int l = 0;
        boolean flag = blockLayerIn == BlockRenderLayer.TRANSLUCENT;
        int i1 = flag ? this.renderInfos.size() - 1 : 0;
        int i = flag ? -1 : this.renderInfos.size();
        int j1 = flag ? -1 : 1;

        for (int j = i1; j != i; j += j1)
        {
            RenderChunk renderchunk = (this.renderInfos.get(j)).renderChunk;

            if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn))
            {
                ++l;
                this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
            }
        }

        this.mc.mcProfiler.func_194339_b(() ->
        {
            return "render_" + blockLayerIn;
        });
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return l;
    }

    @SuppressWarnings("incomplete-switch")
    private void renderBlockLayer(BlockRenderLayer blockLayerIn)
    {
        this.mc.entityRenderer.enableLightmap();

        if (OpenGlHelper.useVbo())
        {
            GlStateManager.glEnableClientState(32884);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(32886);
        }

        this.renderContainer.renderChunkLayer(blockLayerIn);

        if (OpenGlHelper.useVbo())
        {
            for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements())
            {
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                int k1 = vertexformatelement.getIndex();

                switch (vertexformatelement$enumusage)
                {
                    case POSITION:
                        GlStateManager.glDisableClientState(32884);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k1);
                        GlStateManager.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GlStateManager.glDisableClientState(32886);
                        GlStateManager.resetColor();
                }
            }
        }

        this.mc.entityRenderer.disableLightmap();
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn)
    {
        while (iteratorIn.hasNext())
        {
            DestroyBlockProgress destroyblockprogress = iteratorIn.next();
            int k1 = destroyblockprogress.getCreationCloudUpdateTick();

            if (this.cloudTickCounter - k1 > 400)
            {
                iteratorIn.remove();
            }
        }
    }

    public void updateClouds()
    {
        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0)
        {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }

        if (!this.setLightUpdates.isEmpty() && !this.renderDispatcher.hasNoFreeRenderBuilders() && this.chunksToUpdate.isEmpty())
        {
            Iterator<BlockPos> iterator = this.setLightUpdates.iterator();

            while (iterator.hasNext())
            {
                BlockPos blockpos = iterator.next();
                iterator.remove();
                int k1 = blockpos.getX();
                int l1 = blockpos.getY();
                int i2 = blockpos.getZ();
                this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, false);
            }
        }
    }

    private void renderSkyEnd()
    {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        this.renderEngine.bindTexture(END_SKY_TEXTURES);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int k1 = 0; k1 < 6; ++k1)
        {
            GlStateManager.pushMatrix();

            if (k1 == 1)
            {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 2)
            {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 3)
            {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k1 == 4)
            {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (k1 == 5)
            {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    public void renderSky(float partialTicks, int pass)
    {
        if (this.mc.world.provider.getDimensionType().getId() == 1)
        {
            this.renderSkyEnd();
        }
        else if (this.mc.world.provider.isSurfaceWorld())
        {
            GlStateManager.disableTexture2D();
            Vec3d vec3d = this.world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            float f = (float)vec3d.x;
            float f1 = (float)vec3d.y;
            float f2 = (float)vec3d.z;

            if (pass != 2)
            {
                float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                f = f3;
                f1 = f4;
                f2 = f5;
            }

            GlStateManager.color(f, f1, f2);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color(f, f1, f2);

            if (this.vboEnabled)
            {
                this.skyVBO.bindBuffer();
                GlStateManager.glEnableClientState(32884);
                GlStateManager.glVertexPointer(3, 5126, 12, 0);
                this.skyVBO.drawArrays(7);
                this.skyVBO.unbindBuffer();
                GlStateManager.glDisableClientState(32884);
            }
            else
            {
                GlStateManager.callList(this.glSkyList);
            }

            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.world.provider.calcSunriseSunsetColors(this.world.getCelestialAngle(partialTicks), partialTicks);

            if (afloat != null)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float f6 = afloat[0];
                float f7 = afloat[1];
                float f8 = afloat[2];

                if (pass != 2)
                {
                    float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                    float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                    f6 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
                int l1 = 16;

                for (int j2 = 0; j2 <= 16; ++j2)
                {
                    float f21 = (float)j2 * ((float)Math.PI * 2F) / 16.0F;
                    float f12 = MathHelper.sin(f21);
                    float f13 = MathHelper.cos(f21);
                    bufferbuilder.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            float f16 = 1.0F - this.world.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            float f17 = 30.0F;
            this.renderEngine.bindTexture(SUN_TEXTURES);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(-f17), 100.0D, (double)(-f17)).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos((double)f17, 100.0D, (double)(-f17)).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos((double)f17, 100.0D, (double)f17).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)(-f17), 100.0D, (double)f17).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();
            f17 = 20.0F;
            this.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
            int k1 = this.world.getMoonPhase();
            int i2 = k1 % 4;
            int k2 = k1 / 4 % 2;
            float f22 = (float)(i2 + 0) / 4.0F;
            float f23 = (float)(k2 + 0) / 2.0F;
            float f24 = (float)(i2 + 1) / 4.0F;
            float f14 = (float)(k2 + 1) / 2.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(-f17), -100.0D, (double)f17).tex((double)f24, (double)f14).endVertex();
            bufferbuilder.pos((double)f17, -100.0D, (double)f17).tex((double)f22, (double)f14).endVertex();
            bufferbuilder.pos((double)f17, -100.0D, (double)(-f17)).tex((double)f22, (double)f23).endVertex();
            bufferbuilder.pos((double)(-f17), -100.0D, (double)(-f17)).tex((double)f24, (double)f23).endVertex();
            tessellator.draw();
            GlStateManager.disableTexture2D();
            float f15 = this.world.getStarBrightness(partialTicks) * f16;

            if (f15 > 0.0F)
            {
                GlStateManager.color(f15, f15, f15, f15);

                if (this.vboEnabled)
                {
                    this.starVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else
                {
                    GlStateManager.callList(this.starGLCallList);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d3 = this.mc.player.getPositionEyes(partialTicks).y - this.world.getHorizon();

            if (d3 < 0.0D)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);

                if (this.vboEnabled)
                {
                    this.sky2VBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else
                {
                    GlStateManager.callList(this.glSkyList2);
                }

                GlStateManager.popMatrix();
                float f18 = 1.0F;
                float f19 = -((float)(d3 + 65.0D));
                float f20 = -1.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
            }

            if (this.world.provider.isSkyColored())
            {
                GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
            }
            else
            {
                GlStateManager.color(f, f1, f2);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float)(d3 - 16.0D)), 0.0F);
            GlStateManager.callList(this.glSkyList2);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
        }
    }

    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_)
    {
        if (this.mc.world.provider.isSurfaceWorld())
        {
            if (this.mc.gameSettings.shouldRenderClouds() == 2)
            {
                this.renderCloudsFancy(partialTicks, pass, p_180447_3_, p_180447_5_, p_180447_7_);
            }
            else
            {
                GlStateManager.disableCull();
                int k1 = 32;
                int l1 = 8;
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                this.renderEngine.bindTexture(CLOUDS_TEXTURES);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                Vec3d vec3d = this.world.getCloudColour(partialTicks);
                float f = (float)vec3d.x;
                float f1 = (float)vec3d.y;
                float f2 = (float)vec3d.z;

                if (pass != 2)
                {
                    float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                    float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                    float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                    f = f3;
                    f1 = f4;
                    f2 = f5;
                }

                float f9 = 4.8828125E-4F;
                double d5 = (double)((float)this.cloudTickCounter + partialTicks);
                double d3 = p_180447_3_ + d5 * 0.029999999329447746D;
                int i2 = MathHelper.floor(d3 / 2048.0D);
                int j2 = MathHelper.floor(p_180447_7_ / 2048.0D);
                d3 = d3 - (double)(i2 * 2048);
                double lvt_22_1_ = p_180447_7_ - (double)(j2 * 2048);
                float f6 = this.world.provider.getCloudHeight() - (float)p_180447_5_ + 0.33F;
                float f7 = (float)(d3 * 4.8828125E-4D);
                float f8 = (float)(lvt_22_1_ * 4.8828125E-4D);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                for (int k2 = -256; k2 < 256; k2 += 32)
                {
                    for (int l2 = -256; l2 < 256; l2 += 32)
                    {
                        bufferbuilder.pos((double)(k2 + 0), (double)f6, (double)(l2 + 32)).tex((double)((float)(k2 + 0) * 4.8828125E-4F + f7), (double)((float)(l2 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                        bufferbuilder.pos((double)(k2 + 32), (double)f6, (double)(l2 + 32)).tex((double)((float)(k2 + 32) * 4.8828125E-4F + f7), (double)((float)(l2 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                        bufferbuilder.pos((double)(k2 + 32), (double)f6, (double)(l2 + 0)).tex((double)((float)(k2 + 32) * 4.8828125E-4F + f7), (double)((float)(l2 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                        bufferbuilder.pos((double)(k2 + 0), (double)f6, (double)(l2 + 0)).tex((double)((float)(k2 + 0) * 4.8828125E-4F + f7), (double)((float)(l2 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                    }
                }

                tessellator.draw();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
            }
        }
    }

    /**
     * Checks if the given position is to be rendered with cloud fog
     */
    public boolean hasCloudFog(double x, double y, double z, float partialTicks)
    {
        return false;
    }

    private void renderCloudsFancy(float partialTicks, int pass, double x, double y, double z)
    {
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = 12.0F;
        float f1 = 4.0F;
        double d3 = (double)((float)this.cloudTickCounter + partialTicks);
        double d4 = (x + d3 * 0.029999999329447746D) / 12.0D;
        double d5 = z / 12.0D + 0.33000001311302185D;
        float f2 = this.world.provider.getCloudHeight() - (float)y + 0.33F;
        int k1 = MathHelper.floor(d4 / 2048.0D);
        int l1 = MathHelper.floor(d5 / 2048.0D);
        d4 = d4 - (double)(k1 * 2048);
        d5 = d5 - (double)(l1 * 2048);
        this.renderEngine.bindTexture(CLOUDS_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d vec3d = this.world.getCloudColour(partialTicks);
        float f3 = (float)vec3d.x;
        float f4 = (float)vec3d.y;
        float f5 = (float)vec3d.z;

        if (pass != 2)
        {
            float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
            float f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
            float f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
            f3 = f6;
            f4 = f7;
            f5 = f8;
        }

        float f25 = f3 * 0.9F;
        float f26 = f4 * 0.9F;
        float f27 = f5 * 0.9F;
        float f9 = f3 * 0.7F;
        float f10 = f4 * 0.7F;
        float f11 = f5 * 0.7F;
        float f12 = f3 * 0.8F;
        float f13 = f4 * 0.8F;
        float f14 = f5 * 0.8F;
        float f15 = 0.00390625F;
        float f16 = (float)MathHelper.floor(d4) * 0.00390625F;
        float f17 = (float)MathHelper.floor(d5) * 0.00390625F;
        float f18 = (float)(d4 - (double)MathHelper.floor(d4));
        float f19 = (float)(d5 - (double)MathHelper.floor(d5));
        int i2 = 8;
        int j2 = 4;
        float f20 = 9.765625E-4F;
        GlStateManager.scale(12.0F, 1.0F, 12.0F);

        for (int k2 = 0; k2 < 2; ++k2)
        {
            if (k2 == 0)
            {
                GlStateManager.colorMask(false, false, false, false);
            }
            else
            {
                switch (pass)
                {
                    case 0:
                        GlStateManager.colorMask(false, true, true, true);
                        break;

                    case 1:
                        GlStateManager.colorMask(true, false, false, true);
                        break;

                    case 2:
                        GlStateManager.colorMask(true, true, true, true);
                }
            }

            for (int l2 = -3; l2 <= 4; ++l2)
            {
                for (int i3 = -3; i3 <= 4; ++i3)
                {
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float f21 = (float)(l2 * 8);
                    float f22 = (float)(i3 * 8);
                    float f23 = f21 - f18;
                    float f24 = f22 - f19;

                    if (f2 > -5.0F)
                    {
                        bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f9, f10, f11, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (f2 <= 5.0F)
                    {
                        bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 8.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F - 9.765625E-4F), (double)(f24 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f3, f4, f5, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (l2 > -1)
                    {
                        for (int j3 = 0; j3 < 8; ++j3)
                        {
                            bufferbuilder.pos((double)(f23 + (float)j3 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)j3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)j3 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)j3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)j3 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)j3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)j3 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)j3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l2 <= 1)
                    {
                        for (int k3 = 0; k3 < 8; ++k3)
                        {
                            bufferbuilder.pos((double)(f23 + (float)k3 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)k3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)k3 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 8.0F)).tex((double)((f21 + (float)k3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 8.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)k3 + 1.0F - 9.765625E-4F), (double)(f2 + 4.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)k3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + (float)k3 + 1.0F - 9.765625E-4F), (double)(f2 + 0.0F), (double)(f24 + 0.0F)).tex((double)((f21 + (float)k3 + 0.5F) * 0.00390625F + f16), (double)((f22 + 0.0F) * 0.00390625F + f17)).color(f25, f26, f27, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (i3 > -1)
                    {
                        for (int l3 = 0; l3 < 8; ++l3)
                        {
                            bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)l3 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)l3 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)l3 + 0.0F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)l3 + 0.0F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)l3 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (i3 <= 1)
                    {
                        for (int i4 = 0; i4 < 8; ++i4)
                        {
                            bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 4.0F), (double)(f24 + (float)i4 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 4.0F), (double)(f24 + (float)i4 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 8.0F), (double)(f2 + 0.0F), (double)(f24 + (float)i4 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 8.0F) * 0.00390625F + f16), (double)((f22 + (float)i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferbuilder.pos((double)(f23 + 0.0F), (double)(f2 + 0.0F), (double)(f24 + (float)i4 + 1.0F - 9.765625E-4F)).tex((double)((f21 + 0.0F) * 0.00390625F + f16), (double)((f22 + (float)i4 + 0.5F) * 0.00390625F + f17)).color(f12, f13, f14, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }

                    tessellator.draw();
                }
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public void updateChunks(long finishTimeNano)
    {
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);

        if (!this.chunksToUpdate.isEmpty())
        {
            Iterator<RenderChunk> iterator = this.chunksToUpdate.iterator();

            while (iterator.hasNext())
            {
                RenderChunk renderchunk1 = iterator.next();
                boolean flag1;

                if (renderchunk1.needsImmediateUpdate())
                {
                    flag1 = this.renderDispatcher.updateChunkNow(renderchunk1);
                }
                else
                {
                    flag1 = this.renderDispatcher.updateChunkLater(renderchunk1);
                }

                if (!flag1)
                {
                    break;
                }

                renderchunk1.clearNeedsUpdate();
                iterator.remove();
                long k1 = finishTimeNano - System.nanoTime();

                if (k1 < 0L)
                {
                    break;
                }
            }
        }
    }

    public void renderWorldBorder(Entity entityIn, float partialTicks)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        WorldBorder worldborder = this.world.getWorldBorder();
        double d3 = (double)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (entityIn.posX >= worldborder.maxX() - d3 || entityIn.posX <= worldborder.minX() + d3 || entityIn.posZ >= worldborder.maxZ() - d3 || entityIn.posZ <= worldborder.minZ() + d3)
        {
            double d4 = 1.0D - worldborder.getClosestDistance(entityIn) / d3;
            d4 = Math.pow(d4, 4.0D);
            double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
            double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
            double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.renderEngine.bindTexture(FORCEFIELD_TEXTURES);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int k1 = worldborder.getStatus().getColor();
            float f = (float)(k1 >> 16 & 255) / 255.0F;
            float f1 = (float)(k1 >> 8 & 255) / 255.0F;
            float f2 = (float)(k1 & 255) / 255.0F;
            GlStateManager.color(f, f1, f2, (float)d4);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float f3 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = 128.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.setTranslation(-d5, -d6, -d7);
            double d8 = Math.max((double)MathHelper.floor(d7 - d3), worldborder.minZ());
            double d9 = Math.min((double)MathHelper.ceil(d7 + d3), worldborder.maxZ());

            if (d5 > worldborder.maxX() - d3)
            {
                float f7 = 0.0F;

                for (double d10 = d8; d10 < d9; f7 += 0.5F)
                {
                    double d11 = Math.min(1.0D, d9 - d10);
                    float f8 = (float)d11 * 0.5F;
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 256.0D, d10 + d11).tex((double)(f3 + f8 + f7), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10 + d11).tex((double)(f3 + f8 + f7), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.maxX(), 0.0D, d10).tex((double)(f3 + f7), (double)(f3 + 128.0F)).endVertex();
                    ++d10;
                }
            }

            if (d5 < worldborder.minX() + d3)
            {
                float f9 = 0.0F;

                for (double d12 = d8; d12 < d9; f9 += 0.5F)
                {
                    double d15 = Math.min(1.0D, d9 - d12);
                    float f12 = (float)d15 * 0.5F;
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12).tex((double)(f3 + f9), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 256.0D, d12 + d15).tex((double)(f3 + f12 + f9), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12 + d15).tex((double)(f3 + f12 + f9), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(worldborder.minX(), 0.0D, d12).tex((double)(f3 + f9), (double)(f3 + 128.0F)).endVertex();
                    ++d12;
                }
            }

            d8 = Math.max((double)MathHelper.floor(d5 - d3), worldborder.minX());
            d9 = Math.min((double)MathHelper.ceil(d5 + d3), worldborder.maxX());

            if (d7 > worldborder.maxZ() - d3)
            {
                float f10 = 0.0F;

                for (double d13 = d8; d13 < d9; f10 += 0.5F)
                {
                    double d16 = Math.min(1.0D, d9 - d13);
                    float f13 = (float)d16 * 0.5F;
                    bufferbuilder.pos(d13, 256.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 256.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d13 + d16, 0.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d13, 0.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 128.0F)).endVertex();
                    ++d13;
                }
            }

            if (d7 < worldborder.minZ() + d3)
            {
                float f11 = 0.0F;

                for (double d14 = d8; d14 < d9; f11 += 0.5F)
                {
                    double d17 = Math.min(1.0D, d9 - d14);
                    float f14 = (float)d17 * 0.5F;
                    bufferbuilder.pos(d14, 256.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 256.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 0.0F)).endVertex();
                    bufferbuilder.pos(d14 + d17, 0.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 128.0F)).endVertex();
                    bufferbuilder.pos(d14, 0.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 128.0F)).endVertex();
                    ++d14;
                }
            }

            tessellator.draw();
            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    private void preRenderDamagedBlocks()
    {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
    }

    private void postRenderDamagedBlocks()
    {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilderIn, Entity entityIn, float partialTicks)
    {
        double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;

        if (!this.damagedBlocks.isEmpty())
        {
            this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.preRenderDamagedBlocks();
            bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
            bufferBuilderIn.setTranslation(-d3, -d4, -d5);
            bufferBuilderIn.noColor();
            Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext())
            {
                DestroyBlockProgress destroyblockprogress = iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                double d6 = (double)blockpos.getX() - d3;
                double d7 = (double)blockpos.getY() - d4;
                double d8 = (double)blockpos.getZ() - d5;
                Block block = this.world.getBlockState(blockpos).getBlock();

                if (!(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull))
                {
                    if (d6 * d6 + d7 * d7 + d8 * d8 > 1024.0D)
                    {
                        iterator.remove();
                    }
                    else
                    {
                        IBlockState iblockstate = this.world.getBlockState(blockpos);

                        if (iblockstate.getMaterial() != Material.AIR)
                        {
                            int k1 = destroyblockprogress.getPartialBlockDamage();
                            TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[k1];
                            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                            blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.world);
                        }
                    }
                }
            }

            tessellatorIn.draw();
            bufferBuilderIn.setTranslation(0.0D, 0.0D, 0.0D);
            this.postRenderDamagedBlocks();
        }
    }

    /**
     * Draws the selection box for the player.
     */
    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks)
    {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            if (iblockstate.getMaterial() != Material.AIR && this.world.getWorldBorder().contains(blockpos))
            {
                double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(this.world, blockpos).grow(0.0020000000949949026D).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha)
    {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        buffer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
    }

    public static void renderFilledBox(AxisAlignedBB aabb, float red, float green, float blue, float alpha)
    {
        renderFilledBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, red, green, blue, alpha);
    }

    public static void renderFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder builder, double p_189693_1_, double p_189693_3_, double p_189693_5_, double p_189693_7_, double p_189693_9_, double p_189693_11_, float red, float green, float blue, float alpha)
    {
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_3_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_1_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_5_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
        builder.pos(p_189693_7_, p_189693_9_, p_189693_11_).color(red, green, blue, alpha).endVertex();
    }

    private void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately)
    {
        this.viewFrustum.markBlocksForUpdate(minX, minY, minZ, maxX, maxY, maxZ, updateImmediately);
    }

    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        int k1 = pos.getX();
        int l1 = pos.getY();
        int i2 = pos.getZ();
        this.markBlocksForUpdate(k1 - 1, l1 - 1, i2 - 1, k1 + 1, l1 + 1, i2 + 1, (flags & 8) != 0);
    }

    public void notifyLightSet(BlockPos pos)
    {
        this.setLightUpdates.add(pos.toImmutable());
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
     */
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1, false);
    }

    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos)
    {
        ISound isound = this.mapSoundPositions.get(pos);

        if (isound != null)
        {
            this.mc.getSoundHandler().stopSound(isound);
            this.mapSoundPositions.remove(pos);
        }

        if (soundIn != null)
        {
            ItemRecord itemrecord = ItemRecord.getBySound(soundIn);

            if (itemrecord != null)
            {
                this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
            }

            ISound positionedsoundrecord = PositionedSoundRecord.getRecordSoundRecord(soundIn, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            this.mapSoundPositions.put(pos, positionedsoundrecord);
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }

        this.setPartying(this.world, pos, soundIn != null);
    }

    private void setPartying(World p_193054_1_, BlockPos pos, boolean p_193054_3_)
    {
        for (EntityLivingBase entitylivingbase : p_193054_1_.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(pos)).grow(3.0D)))
        {
            entitylivingbase.setPartying(pos, p_193054_3_);
        }
    }

    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {
    }

    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, final double x, final double y, final double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        try
        {
            this.spawnParticle0(id, ignoreRange, p_190570_3_, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", Integer.valueOf(id));

            if (parameters != null)
            {
                crashreportcategory.addCrashSection("Parameters", parameters);
            }

            crashreportcategory.addDetail("Position", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(x, y, z);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnParticle0(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        return this.spawnParticle0(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Nullable
    private Particle spawnParticle0(int particleID, boolean ignoreRange, boolean minParticles, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (this.mc != null && entity != null && this.mc.effectRenderer != null)
        {
            int k1 = this.calculateParticleLevel(minParticles);
            double d3 = entity.posX - xCoord;
            double d4 = entity.posY - yCoord;
            double d5 = entity.posZ - zCoord;

            if (ignoreRange)
            {
                return this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            }
            else if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)
            {
                return null;
            }
            else
            {
                return k1 > 1 ? null : this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            }
        }
        else
        {
            return null;
        }
    }

    private int calculateParticleLevel(boolean p_190572_1_)
    {
        int k1 = this.mc.gameSettings.particleSetting;

        if (p_190572_1_ && k1 == 2 && this.world.rand.nextInt(10) == 0)
        {
            k1 = 1;
        }

        if (k1 == 1 && this.world.rand.nextInt(3) == 0)
        {
            k1 = 2;
        }

        return k1;
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityAdded(Entity entityIn)
    {
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityRemoved(Entity entityIn)
    {
    }

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists()
    {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        switch (soundID)
        {
            case 1023:
            case 1028:
            case 1038:
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    double d3 = (double)pos.getX() - entity.posX;
                    double d4 = (double)pos.getY() - entity.posY;
                    double d5 = (double)pos.getZ() - entity.posZ;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    double d7 = entity.posX;
                    double d8 = entity.posY;
                    double d9 = entity.posZ;

                    if (d6 > 0.0D)
                    {
                        d7 += d3 / d6 * 2.0D;
                        d8 += d4 / d6 * 2.0D;
                        d9 += d5 / d6 * 2.0D;
                    }

                    if (soundID == 1023)
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else if (soundID == 1038)
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else
                    {
                        this.world.playSound(d7, d8, d9, SoundEvents.ENTITY_ENDERDRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {
        Random random = this.world.rand;

        switch (type)
        {
            case 1000:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1001:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1002:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1003:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1004:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1005:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1006:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1007:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1008:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1009:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
                break;

            case 1010:
                if (Item.getItemById(data) instanceof ItemRecord)
                {
                    this.world.playRecord(blockPosIn, ((ItemRecord)Item.getItemById(data)).getSound());
                }
                else
                {
                    this.world.playRecord(blockPosIn, (SoundEvent)null);
                }

                break;

            case 1011:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1012:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1013:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1014:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1015:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1018:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1019:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1021:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1022:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1024:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1025:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1026:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1027:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1029:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1030:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1031:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1032:
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
                break;

            case 1033:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1034:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1035:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1036:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1037:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2000:
                int j2 = data % 3 - 1;
                int k1 = data / 3 % 3 - 1;
                double d11 = (double)blockPosIn.getX() + (double)j2 * 0.6D + 0.5D;
                double d13 = (double)blockPosIn.getY() + 0.5D;
                double d15 = (double)blockPosIn.getZ() + (double)k1 * 0.6D + 0.5D;

                for (int l2 = 0; l2 < 10; ++l2)
                {
                    double d16 = random.nextDouble() * 0.2D + 0.01D;
                    double d19 = d11 + (double)j2 * 0.01D + (random.nextDouble() - 0.5D) * (double)k1 * 0.5D;
                    double d22 = d13 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d25 = d15 + (double)k1 * 0.01D + (random.nextDouble() - 0.5D) * (double)j2 * 0.5D;
                    double d27 = (double)j2 * d16 + random.nextGaussian() * 0.01D;
                    double d29 = -0.03D + random.nextGaussian() * 0.01D;
                    double d30 = (double)k1 * d16 + random.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d19, d22, d25, d27, d29, d30);
                }

                return;

            case 2001:
                Block block = Block.getBlockById(data & 4095);

                if (block.getDefaultState().getMaterial() != Material.AIR)
                {
                    SoundType soundtype = block.getSoundType();
                    this.world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
                break;

            case 2002:
            case 2007:
                double d9 = (double)blockPosIn.getX();
                double d10 = (double)blockPosIn.getY();
                double d12 = (double)blockPosIn.getZ();

                for (int k2 = 0; k2 < 8; ++k2)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d9, d10, d12, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.SPLASH_POTION));
                }

                float f5 = (float)(data >> 16 & 255) / 255.0F;
                float f = (float)(data >> 8 & 255) / 255.0F;
                float f1 = (float)(data >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int j3 = 0; j3 < 100; ++j3)
                {
                    double d18 = random.nextDouble() * 4.0D;
                    double d21 = random.nextDouble() * Math.PI * 2.0D;
                    double d24 = Math.cos(d21) * d18;
                    double d26 = 0.01D + random.nextDouble() * 0.5D;
                    double d28 = Math.sin(d21) * d18;
                    Particle particle1 = this.spawnParticle0(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d9 + d24 * 0.1D, d10 + 0.3D, d12 + d28 * 0.1D, d24, d26, d28);

                    if (particle1 != null)
                    {
                        float f4 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float)d18);
                    }
                }

                this.world.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                double d3 = (double)blockPosIn.getX() + 0.5D;
                double d4 = (double)blockPosIn.getY();
                double d5 = (double)blockPosIn.getZ() + 0.5D;

                for (int l1 = 0; l1 < 8; ++l1)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d3, d4, d5, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, Item.getIdFromItem(Items.ENDER_EYE));
                }

                for (double d14 = 0.0D; d14 < (Math.PI * 2D); d14 += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -5.0D, 0.0D, Math.sin(d14) * -5.0D);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d3 + Math.cos(d14) * 5.0D, d4 - 0.4D, d5 + Math.sin(d14) * 5.0D, Math.cos(d14) * -7.0D, 0.0D, Math.sin(d14) * -7.0D);
                }

                return;

            case 2004:
                for (int i3 = 0; i3 < 20; ++i3)
                {
                    double d17 = (double)blockPosIn.getX() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d20 = (double)blockPosIn.getY() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d23 = (double)blockPosIn.getZ() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.world.spawnParticle(EnumParticleTypes.FLAME, d17, d20, d23, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;

            case 2005:
                ItemDye.spawnBonemealParticles(this.world, blockPosIn, data);
                break;

            case 2006:
                for (int i2 = 0; i2 < 200; ++i2)
                {
                    float f2 = random.nextFloat() * 4.0F;
                    float f3 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d6 = (double)(MathHelper.cos(f3) * f2);
                    double d7 = 0.01D + random.nextDouble() * 0.5D;
                    double d8 = (double)(MathHelper.sin(f3) * f2);
                    Particle particle = this.spawnParticle0(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d6 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d8 * 0.1D, d6, d7, d8);

                    if (particle != null)
                    {
                        particle.multiplyVelocity(f2);
                    }
                }

                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.HOSTILE, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 3000:
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                break;

            case 3001:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.rand.nextFloat() * 0.3F, false);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        if (progress >= 0 && progress < 10)
        {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(Integer.valueOf(breakerId));

            if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ())
            {
                destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(Integer.valueOf(breakerId), destroyblockprogress);
            }

            destroyblockprogress.setPartialBlockDamage(progress);
            destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
        }
        else
        {
            this.damagedBlocks.remove(Integer.valueOf(breakerId));
        }
    }

    public boolean hasNoChunkUpdates()
    {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
    }

    public void setDisplayListEntitiesDirty()
    {
        this.displayListEntitiesDirty = true;
    }

    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd)
    {
        synchronized (this.setTileEntities)
        {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    class ContainerLocalRenderInformation
    {
        final RenderChunk renderChunk;
        final EnumFacing facing;
        byte setFacing;
        final int counter;

        private ContainerLocalRenderInformation(RenderChunk renderChunkIn, EnumFacing facingIn, @Nullable int counterIn)
        {
            this.renderChunk = renderChunkIn;
            this.facing = facingIn;
            this.counter = counterIn;
        }

        public void setDirection(byte p_189561_1_, EnumFacing p_189561_2_)
        {
            this.setFacing = (byte)(this.setFacing | p_189561_1_ | 1 << p_189561_2_.ordinal());
        }

        public boolean hasDirection(EnumFacing p_189560_1_)
        {
            return (this.setFacing & 1 << p_189560_1_.ordinal()) > 0;
        }
    }
}
