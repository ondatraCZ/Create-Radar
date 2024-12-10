package com.happysg.radar.block.radar.bearing;

import com.jozufozu.flywheel.util.Color;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


public record RadarTrack(UUID entityId, Vec3 position, long scannedTime, Color color, boolean contraption) {

    public static int BLUE = 255;
    public static int WHITE = 16777215;
    public static int RED = 16711680;
    public static int GREEN = 65280;
    public static int YELLOW = 16776960;

    public RadarTrack(Entity entity) {
        this(entity.getUUID(), getPosition(entity), entity.level().getGameTime(), getColor(entity), isContraption(entity));
    }

    private static Vec3 getPosition(Entity entity) {
        if (entity instanceof AbstractContraptionEntity entity1)
            return entity1.getContraption().anchor.getCenter();
        return entity.position();
    }

    private static Color getColor(Entity entity) {
        if (entity instanceof Player)
            return new Color(BLUE);
        if (entity instanceof Animal)
            return new Color(GREEN);
        if (entity instanceof Enemy)
            return new Color(RED);
        return new Color(WHITE);
    }

    private static boolean isContraption(Entity entity) {
        return entity instanceof AbstractContraptionEntity;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("entityId", entityId);
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);
        tag.putLong("scannedTime", scannedTime);
        tag.putInt("color", color.getRGB());
        tag.putBoolean("contraption", contraption);
        return tag;
    }

    public static RadarTrack deserializeNBT(CompoundTag tag) {
        return new RadarTrack(tag.getUUID("entityId"), new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")), tag.getLong("scannedTime"), new Color(tag.getInt("color")), tag.getBoolean("contraption"));
    }

    public static CompoundTag serializeNBTList(Collection<RadarTrack> tracks) {
        ListTag list = new ListTag();
        for (RadarTrack track : tracks) {
            list.add(track.serializeNBT());
        }
        CompoundTag tag = new CompoundTag();
        tag.put("tracks", list);
        return tag;
    }

    public static List<RadarTrack> deserializeListNBT(CompoundTag tag) {
        List<RadarTrack> tracks = new ArrayList<>();
        ListTag list = tag.getList("tracks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            tracks.add(RadarTrack.deserializeNBT(list.getCompound(i)));
        }
        return tracks;
    }
}
