package dev.heliosclient.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface AccessorWorldRenderer {

    @Accessor
    Frustum getFrustum();

    @Accessor
    void setFrustum(Frustum frustum);
}
