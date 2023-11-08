package dev.tr7zw.notenoughanimations.animations.hands;

import java.util.HashSet;
import java.util.Set;

import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.animations.BasicAnimation;
import dev.tr7zw.notenoughanimations.animations.BodyPart;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LookAtItemAnimation extends BasicAnimation {

    private Set<Item> holdingItems = new HashSet<>();
    
    @Override
    public boolean isEnabled() {
        bind();
        return NEAnimationsLoader.config.holdUpAllItems || !holdingItems.isEmpty();
    }
    
    private void bind() {
        holdingItems.clear();
        Item invalid = Registry.ITEM.get(new ResourceLocation("minecraft", "air"));
        for (String itemId : NEAnimationsLoader.config.holdingItems) {
            try {
                Item item = Registry.ITEM.get(new ResourceLocation(itemId.split(":")[0], itemId.split(":")[1]));
                if (invalid != item)
                    holdingItems.add(item);
            } catch (Exception ex) {
                NEAnimationsLoader.LOGGER.info("Unknown item to add to the holding list: " + itemId);
            }
        }
    }

    @Override
    public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
        ItemStack itemInRightHand = entity.getItemInHand(entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        ItemStack itemInLeftHand = entity.getItemInHand(entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        boolean rightArm = holdingItems.contains(itemInRightHand.getItem()) || (NEAnimationsLoader.config.holdUpAllItems &&  !itemInRightHand.isEmpty() && (!entity.swinging || entity.getMainArm() != HumanoidArm.RIGHT));
        boolean leftArm = holdingItems.contains(itemInLeftHand.getItem()) || (NEAnimationsLoader.config.holdUpAllItems && !itemInLeftHand.isEmpty() && (!entity.swinging || entity.getMainArm() != HumanoidArm.LEFT));
        if(rightArm && leftArm && !entity.swinging) { // can't be both hands while swinging
            target = bothHands;
            return true;
        }
        if(rightArm && !(entity.swinging && entity.swingingArm == (entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND))) {
            target = right;
            return true;
        }
        if(leftArm && !(entity.swinging && entity.swingingArm == (entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND))) {
            target = left;
            return true;
        }
        return false;
    }

    private final BodyPart[] bothHands = new BodyPart[] {BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM};
    private final BodyPart[] left = new BodyPart[] {BodyPart.LEFT_ARM};
    private final BodyPart[] right = new BodyPart[] {BodyPart.RIGHT_ARM};
    private BodyPart[] target = bothHands;
    
    @Override
    public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
        return target;
    }

    @Override
    public int getPriority(AbstractClientPlayer entity, PlayerData data) {
        return 300;
    }

    @Override
    public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel<AbstractClientPlayer> model,
            BodyPart part, float delta, float tickCounter) {
        HumanoidArm arm = part == BodyPart.LEFT_ARM ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            AnimationUtil.applyArmTransforms(model, arm, -(Mth.lerp(-1f * (entity.getXRot() - 90f) / 180f, 1f, 1.5f)), -0.2f,
                    0.3f);
    }

}
