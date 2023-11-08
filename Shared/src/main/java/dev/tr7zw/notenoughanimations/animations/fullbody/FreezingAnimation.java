package dev.tr7zw.notenoughanimations.animations.fullbody;

import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.animations.BasicAnimation;
import dev.tr7zw.notenoughanimations.animations.BodyPart;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;

public class FreezingAnimation extends BasicAnimation {

    @Override
    public boolean isEnabled() {
        return NEAnimationsLoader.config.freezingAnimation;
    }

    @Override
    public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
        return entity.canFreeze() && entity.level.getBlockStatesIfLoaded(entity.getBoundingBox().deflate(1.0E-6D))
        .anyMatch(blockState -> (blockState.is(Blocks.POWDER_SNOW) || blockState.is(Blocks.POWDER_SNOW_CAULDRON)));
    }

    private BodyPart[] parts = new BodyPart[] { BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM };

    @Override
    public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
        return parts;
    }

    @Override
    public int getPriority(AbstractClientPlayer entity, PlayerData data) {
        return 400;
    }

    @Override
    public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel<AbstractClientPlayer> model,
            BodyPart part, float delta, float tickCounter) {

        if (part == BodyPart.LEFT_ARM) {
            float position = (float) (Math.random() / 10 + -1.3f);
            AnimationUtil.applyArmTransforms(model, HumanoidArm.LEFT, -0.6f, 0.2f, position);
        }
        if (part == BodyPart.RIGHT_ARM) {
            float position = (float) (Math.random() / 10 + -1.0f);
            AnimationUtil.applyArmTransforms(model, HumanoidArm.RIGHT, -0.5f, 0.2f, position);
        }
    }

}
