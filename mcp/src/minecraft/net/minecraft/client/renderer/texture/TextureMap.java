package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
    private final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final ITextureMapPopulator iconCreator;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;

    public TextureMap(String basePathIn)
    {
        this(basePathIn, (ITextureMapPopulator)null);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn)
    {
        this.listAnimatedSprites = Lists.<TextureAtlasSprite>newArrayList();
        this.mapRegisteredSprites = Maps.<String, TextureAtlasSprite>newHashMap();
        this.mapUploadedSprites = Maps.<String, TextureAtlasSprite>newHashMap();
        this.missingImage = new TextureAtlasSprite("missingno");
        this.basePath = basePathIn;
        this.iconCreator = iconCreatorIn;
    }

    private void initMissingImage()
    {
        int[] aint = TextureUtil.MISSING_TEXTURE_DATA;
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        int[][] aint1 = new int[this.mipmapLevels + 1][];
        aint1[0] = aint;
        this.missingImage.setFramesTextureData(Lists.<int[][]>newArrayList(aint1));
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        if (this.iconCreator != null)
        {
            this.loadSprites(resourceManager, this.iconCreator);
        }
    }

    public void loadSprites(IResourceManager resourceManager, ITextureMapPopulator iconCreatorIn)
    {
        this.mapRegisteredSprites.clear();
        iconCreatorIn.registerSprites(this);
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager)
    {
        int i = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipmapLevels;

        for (Entry<String, TextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet())
        {
            TextureAtlasSprite textureatlassprite = entry.getValue();
            ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);
            IResource iresource = null;

            try
            {
                PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourcelocation));
                iresource = resourceManager.getResource(resourcelocation);
                boolean flag = iresource.getMetadata("animation") != null;
                textureatlassprite.loadSprite(pngsizeinfo, flag);
            }
            catch (RuntimeException runtimeexception)
            {
                LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                continue;
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
                continue;
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }

            j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getIconWidth()), Integer.lowestOneBit(textureatlassprite.getIconHeight()));

            if (j1 < k)
            {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation, Integer.valueOf(textureatlassprite.getIconWidth()), Integer.valueOf(textureatlassprite.getIconHeight()), Integer.valueOf(MathHelper.log2(k)), Integer.valueOf(MathHelper.log2(j1)));
                k = j1;
            }

            stitcher.addSprite(textureatlassprite);
        }

        int l = Math.min(j, k);
        int i1 = MathHelper.log2(l);

        if (i1 < this.mipmapLevels)
        {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, Integer.valueOf(this.mipmapLevels), Integer.valueOf(i1), Integer.valueOf(l));
            this.mipmapLevels = i1;
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);

        try
        {
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
        TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        Map<String, TextureAtlasSprite> map = Maps.<String, TextureAtlasSprite>newHashMap(this.mapRegisteredSprites);

        for (TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots())
        {
            if (textureatlassprite1 == this.missingImage || this.generateMipmaps(resourceManager, textureatlassprite1))
            {
                String s = textureatlassprite1.getIconName();
                map.remove(s);
                this.mapUploadedSprites.put(s, textureatlassprite1);

                try
                {
                    TextureUtil.uploadTextureMipmap(textureatlassprite1.getFrameTextureData(0), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                    crashreportcategory.addCrashSection("Atlas path", this.basePath);
                    crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
                    throw new ReportedException(crashreport);
                }

                if (textureatlassprite1.hasAnimationMetadata())
                {
                    this.listAnimatedSprites.add(textureatlassprite1);
                }
            }
        }

        for (TextureAtlasSprite textureatlassprite2 : map.values())
        {
            textureatlassprite2.copyFrom(this.missingImage);
        }
    }

    private boolean generateMipmaps(IResourceManager resourceManager, final TextureAtlasSprite texture)
    {
        ResourceLocation resourcelocation = this.getResourceLocation(texture);
        IResource iresource = null;
        label62:
        {
            boolean flag;

            try
            {
                iresource = resourceManager.getResource(resourcelocation);
                texture.loadSpriteFrames(iresource, this.mipmapLevels + 1);
                break label62;
            }
            catch (RuntimeException runtimeexception)
            {
                LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                flag = false;
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
                flag = false;
                return flag;
            }
            finally
            {
                IOUtils.closeQuietly((Closeable)iresource);
            }

            return flag;
        }

        try
        {
            texture.generateMipmaps(this.mipmapLevels);
            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
            crashreportcategory.addDetail("Sprite name", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconName();
                }
            });
            crashreportcategory.addDetail("Sprite size", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconWidth() + " x " + texture.getIconHeight();
                }
            });
            crashreportcategory.addDetail("Sprite frames", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getFrameCount() + " frames";
                }
            });
            crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
            throw new ReportedException(crashreport);
        }
    }

    private ResourceLocation getResourceLocation(TextureAtlasSprite p_184396_1_)
    {
        ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
        return new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", this.basePath, resourcelocation.getResourcePath(), ".png"));
    }

    public TextureAtlasSprite getAtlasSprite(String iconName)
    {
        TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

        if (textureatlassprite == null)
        {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations()
    {
        TextureUtil.bindTexture(this.getGlTextureId());

        for (TextureAtlasSprite textureatlassprite : this.listAnimatedSprites)
        {
            textureatlassprite.updateAnimation();
        }
    }

    public TextureAtlasSprite registerSprite(ResourceLocation location)
    {
        if (location == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location);

            if (textureatlassprite == null)
            {
                textureatlassprite = TextureAtlasSprite.makeAtlasSprite(location);
                this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
            }

            return textureatlassprite;
        }
    }

    public void tick()
    {
        this.updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn)
    {
        this.mipmapLevels = mipmapLevelsIn;
    }

    public TextureAtlasSprite getMissingSprite()
    {
        return this.missingImage;
    }
}
