package com.kltyton.eugeneshorsewhistle.whistle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HorseNbtDataSaveEvent {
    private final List<byte[]> nbtDataList = new ArrayList<>();

    // 保存马实体的 NBT 数据
    public void saveNbtData(AbstractHorse horse) {
        byte[] existingData = null;
        for (byte[] data : nbtDataList) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
                NbtAccounter nbtAccounter = new NbtAccounter(2097152L, 512);
                CompoundTag existingTag = NbtIo.readCompressed(inputStream, nbtAccounter);
                if (existingTag.getUUID("UUID").equals(horse.getUUID())) {
                    existingData = data;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // 更新
        if (existingData != null) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                NbtIo.writeCompressed(horse.saveWithoutId(new CompoundTag()), outputStream);
                int index = nbtDataList.indexOf(existingData);
                nbtDataList.set(index, outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 如果找不到现有的数据，则添加新的数据
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                NbtIo.writeCompressed(horse.saveWithoutId(new CompoundTag()), outputStream);
                nbtDataList.add(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public List<byte[]> getNbtDataList() {
        return nbtDataList;
    }
}
