package com.drunkshulker.bartender.client.ipc.message;

import com.drunkshulker.bartender.client.ipc.IPCHandler;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;
import net.minecraft.util.math.BlockPos;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class SendCoordsMessage implements MappedBusMessage {
    Charset charset = StandardCharsets.UTF_8;

    public static final int TYPE = IPCHandler.SEND_COORDS;

    private int x,y,z;
    byte[] senderName;

    public SendCoordsMessage() {}

    public SendCoordsMessage(int x, int y, int z, String senderName) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.senderName = senderName.getBytes(StandardCharsets.UTF_8);
    }

    public int type() {
        return TYPE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName.getBytes(StandardCharsets.UTF_8);
    }

    public String getSenderName() {
        return charset.decode(ByteBuffer.wrap(senderName)).toString();
    }

    @Override
    public String toString() {
        return "SendCoordsMessage ["+charset.decode(ByteBuffer.wrap(senderName)).toString()+"] ["+x+","+y+","+z+"]";
    }

    public void write(MemoryMappedFile mem, long pos) {

        mem.putInt(pos, x);
        mem.putInt(pos + 4, y);
        mem.putInt(pos + 8, z);
        mem.putInt(pos + 12, senderName.length);

        mem.setBytes(pos + 16 , senderName, 0, senderName.length);
    }

    public void read(MemoryMappedFile mem, long pos) {

        x = mem.getInt(pos);
        y = mem.getInt(pos + 4);
        z = mem.getInt(pos + 8);

        int bufferSize = mem.getInt(pos + 12);

        senderName = new byte[bufferSize];
        mem.getBytes(pos + 16 , senderName, 0, senderName.length);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(x,y,z);
    }
}