package betterquesting.api2.client.gui.panels.content;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.ValueFuncIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.utils.EntityPlayerPreview;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class PanelPlayerPortrait implements IGuiPanel
{
	private final IGuiRect transform;
	private boolean enabled = true;
	
	private final AbstractClientPlayerEntity player;
	
	private final IValueIO<Float> basePitch;
	private final IValueIO<Float> baseYaw;
	private IValueIO<Float> pitchDriver;
	private IValueIO<Float> yawDriver;
	
	private float zDepth = 100F;
	
	public PanelPlayerPortrait(IGuiRect rect, UUID playerID, String username)
	{
		this(rect, new EntityPlayerPreview(Minecraft.getInstance().world, new GameProfile(playerID, username)));
	}
	
	public PanelPlayerPortrait(IGuiRect rect, AbstractClientPlayerEntity player)
	{
		this.transform = rect;
		this.player = new EntityPlayerPreview((ClientWorld)player.world, player.getGameProfile());
		this.player.limbSwing = 0F;
		this.player.limbSwingAmount = 0F;
		this.player.rotationYawHead = 0F;
		
		ResourceLocation resource = this.player.getLocationSkin();
		
		if(Minecraft.getInstance().getTextureManager().getTexture(resource) == null)
		{
			AbstractClientPlayerEntity.getDownloadImageSkin(resource, player.getGameProfile().getName());
		}
		
		this.basePitch = new ValueFuncIO<>(() -> 15F);
		this.pitchDriver = basePitch;
		
		this.baseYaw = new ValueFuncIO<>(() -> -30F);
		this.yawDriver = baseYaw;
	}
	
	public PanelPlayerPortrait setRotationFixed(float pitch, float yaw)
	{
		this.pitchDriver = basePitch;
		this.yawDriver = baseYaw;
		basePitch.writeValue(pitch);
		baseYaw.writeValue(yaw);
		return this;
	}
	
	public PanelPlayerPortrait setRotationDriven(IValueIO<Float> pitch, IValueIO<Float> yaw)
	{
		this.pitchDriver = pitch == null? basePitch : pitch;
		this.yawDriver = yaw == null? baseYaw : yaw;
		return this;
	}
	
	public PanelPlayerPortrait setDepth(float z)
	{
		this.zDepth = z;
		return this;
	}
	
	@Override
	public void initPanel()
	{
	}
	
	@Override
	public void setEnabled(boolean state)
	{
		this.enabled = state;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	@Override
	public IGuiRect getTransform()
	{
		return transform;
	}
	
	@Override
	public void drawPanel(int mx, int my, float partialTick)
	{
		IGuiRect bounds = this.getTransform();
		GlStateManager.pushMatrix();
		RenderUtils.startScissor(new GuiRectangle(bounds));
		
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		int scale = Math.min(bounds.getWidth(), bounds.getHeight());
		RenderUtils.RenderEntity(bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2 + (int)(scale*1.5F), zDepth, scale, yawDriver.readValue(), pitchDriver.readValue(), player);
		
		RenderUtils.endScissor();
		GlStateManager.popMatrix();
	}
}
