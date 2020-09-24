package com.drunkshulker.bartender.client.ipc.message;

import com.drunkshulker.bartender.client.ipc.IPCHandler;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;
import net.minecraft.util.math.BlockPos;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class DimensionUpdateMessage implements MappedBusMessage {
    Charset charset = StandardCharsets.UTF_8;

    public static final int TYPE = IPCHandler.DIMENSION_UPDATE;

    private int x;
    byte[] senderName;

    public DimensionUpdateMessage() {}

    public DimensionUpdateMessage(int x, String senderName) {
        this.x=x;
        this.senderName = senderName.getBytes(StandardCharsets.UTF_8);
    }

    public int type() {
        return TYPE;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName.getBytes(StandardCharsets.UTF_8);
    }

    public String getSenderName() {
        return charset.decode(ByteBuffer.wrap(senderName)).toString();
    }

    @Override
    public String toString() {
        return "DimensionUpdateMessage ["+charset.decode(ByteBuffer.wrap(senderName)).toString()+"] ["+x+"]";
    }

    public void write(MemoryMappedFile mem, long pos) {

        mem.putInt(pos, x);
        mem.putInt(pos + 4, senderName.length);

        mem.setBytes(pos + 8 , senderName, 0, senderName.length);
    }

    public void read(MemoryMappedFile mem, long pos) {

        x = mem.getInt(pos);

        int bufferSize = mem.getInt(pos + 4);

        senderName = new byte[bufferSize];
        mem.getBytes(pos + 8 , senderName, 0, senderName.length);
    }

}