package com.drunkshulker.bartender.client.ipc.message;

import com.drunkshulker.bartender.client.ipc.IPCHandler;
import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class AddEnemyMessage implements MappedBusMessage {
    Charset charset = StandardCharsets.UTF_8;

    public static final int TYPE = IPCHandler.ADD_ENEMY;

    private int x,y,z;
    byte[] enemyName;

    public AddEnemyMessage() {}

    public AddEnemyMessage(int x, int y, int z, String enemyName) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.enemyName = enemyName.getBytes(StandardCharsets.UTF_8);
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

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName.getBytes(StandardCharsets.UTF_8);
    }

    public String getEnemyName() {
        return charset.decode(ByteBuffer.wrap(enemyName)).toString();
    }

    @Override
    public String toString() {
        return "AddEnemyMessage ["+charset.decode(ByteBuffer.wrap(enemyName)).toString()+"] ["+x+","+y+","+z+"]";
    }

    public void write(MemoryMappedFile mem, long pos) {

        mem.putInt(pos, x);
        mem.putInt(pos + 4, y);
        mem.putInt(pos + 8, z);
        mem.putInt(pos + 12, enemyName.length);

        mem.setBytes(pos + 16 , enemyName, 0, enemyName.length);
    }

    public void read(MemoryMappedFile mem, long pos) {

        x = mem.getInt(pos);
        y = mem.getInt(pos + 4);
        z = mem.getInt(pos + 8);

        int bufferSize = mem.getInt(pos + 12);


        enemyName = new byte[bufferSize];
        mem.getBytes(pos + 16 , enemyName, 0, enemyName.length);
    }
}