package com.drunkshulker.bartender.client.ipc.message;

import com.drunkshulker.bartender.client.ipc.IPCHandler;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;


public class DisconnectMessage implements MappedBusMessage {

    public static final int TYPE = IPCHandler.DISCONNECT;

    private int x,y,z;

    public DisconnectMessage() {}

    public DisconnectMessage(int x, int y, int z) {
        this.x=x;
        this.y=y;
        this.z=z;
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

    @Override
    public String toString() {
        return "DisconnectMessage ["+x+","+y+","+z+"]";
    }

    public void write(MemoryMappedFile mem, long pos) {
        mem.putInt(pos, x);
        mem.putInt(pos + 4, y);
        mem.putInt(pos + 8, z);
    }

    public void read(MemoryMappedFile mem, long pos) {
        x = mem.getInt(pos);
        y = mem.getInt(pos + 4);
        z = mem.getInt(pos + 8);
    }
}