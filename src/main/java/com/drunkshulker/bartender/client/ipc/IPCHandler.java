package com.drunkshulker.bartender.client.ipc;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.ipc.message.*;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Bodyguard;
import io.mappedbus.MappedBusMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.EOFException;

public class IPCHandler {

    final static public int
            ADD_ENEMY = 0,
            REMOVE_ENEMY = 1,
            ASK_COORDS = 2,
            SEND_COORDS = 3,
            FALLBACK = 4,
            CHORUS = 5,
            START_MINING_OBBY = 6,
            END_ALL_TASKS = 7,
            GET_TO_GROUND_LEVEL = 8,
            DIMENSION_UPDATE = 9,
            TAKEOFF = 11,
            ASK_TPA_FROM_MAIN = 12,
            GO_TO = 10,
            DISCONNECT = 13;

    @SubscribeEvent
    public void listener(TickEvent.ClientTickEvent event) {
        try{
            if (Bartender.IPC_READER.next()) {
                if(Minecraft.getMinecraft().player==null) return;
                MappedBusMessage message = null;

                switch (Bartender.IPC_READER.readType()){
                    case ADD_ENEMY:
                        message = new AddEnemyMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.addEnemy(((AddEnemyMessage) message).getEnemyName(), false);
                        break;
                    case REMOVE_ENEMY:
                        message = new RemoveEnemyMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.removeEnemy(((RemoveEnemyMessage) message).getEnemyName(), false);
                        break;
                    case ASK_COORDS:
                        message = new AskCoordsMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.receiveCoordsRequest();
                        break;
                    case SEND_COORDS:
                        message = new SendCoordsMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.receiveCoordMessage(((SendCoordsMessage)message).getSenderName(), ((SendCoordsMessage)message).getBlockPos());
                        break;
                    case FALLBACK:
                        message = new FallbackMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.fallBack(false);
                        break;
                    case CHORUS:
                        message = new ChorusMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.eatChorus(false);
                        break;
                    case START_MINING_OBBY:
                        message = new StartMiningObbyMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.startMineNearbyBlocks(Blocks.OBSIDIAN, false);
                        break;
                    case END_ALL_TASKS:
                        message = new EndAllTasksMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.endAllTasks(false);
                        break;
                    case GET_TO_GROUND_LEVEL:
                        message = new DigToGroundLevelMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.receiveGoToGroundCommand();
                        break;
                    case DIMENSION_UPDATE:
                        message = new DimensionUpdateMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.receiveDimensionUpdate(((DimensionUpdateMessage)message).getSenderName(), ((DimensionUpdateMessage)message).getX());
                        break;
                    case GO_TO:
                        message = new GoToMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.receiveGoToCommand(((GoToMessage)message).getBlockPos());
                        break;
                    case TAKEOFF:
                        message = new TakeoffMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.takeOff();
                        break;
                    case ASK_TPA_FROM_MAIN:
                        message = new AskTpaFromMainMessage();
                        Bartender.IPC_READER.readMessage(message);
                        Bodyguard.reveiveMainTpaRequest(((AskTpaFromMainMessage)message).getSenderName());
                        break;
                    case DISCONNECT:
                        message = new DisconnectMessage();
                        Bartender.IPC_READER.readMessage(message);
                        BaseFinder.logOut("Group disconnect");
                        break;
                    default:
                        break;
                }
            }
        }
        catch (EOFException e){}
    }
    
    public static void push(int ipcMessageCode, String text, int x, int y, int z){
        try {
            switch (ipcMessageCode){
                case ADD_ENEMY:
                    Bartender.IPC_WRITER.write(new AddEnemyMessage(x,y,z,text));
                    break;
                case REMOVE_ENEMY:
                    Bartender.IPC_WRITER.write(new RemoveEnemyMessage(x,y,z,text));
                    break;
                case ASK_COORDS:
                    Bartender.IPC_WRITER.write(new AskCoordsMessage(x,y,z,text));
                    break;
                case SEND_COORDS:
                    Bartender.IPC_WRITER.write(new SendCoordsMessage(x,y,z,text));
                    break;
                case FALLBACK:
                    Bartender.IPC_WRITER.write(new FallbackMessage(x,y,z));
                    break;
                case CHORUS:
                    Bartender.IPC_WRITER.write(new ChorusMessage(x,y,z));
                    break;
                case START_MINING_OBBY:
                    Bartender.IPC_WRITER.write(new StartMiningObbyMessage(x,y,z));
                    break;
                case END_ALL_TASKS:
                    Bartender.IPC_WRITER.write(new EndAllTasksMessage(x,y,z));
                    break;
                case GET_TO_GROUND_LEVEL:
                    Bartender.IPC_WRITER.write(new DigToGroundLevelMessage(x,y,z));
                    break;
                case DIMENSION_UPDATE:
                    Bartender.IPC_WRITER.write(new DimensionUpdateMessage(x,text));
                    break;
                case GO_TO:
                    Bartender.IPC_WRITER.write(new GoToMessage(x,y,z, text));
                    break;
                case TAKEOFF:
                    Bartender.IPC_WRITER.write(new TakeoffMessage(x,y,z));
                    break;
                case ASK_TPA_FROM_MAIN:
                    Bartender.IPC_WRITER.write(new AskTpaFromMainMessage(x,y,z,text));
                    break;
                case DISCONNECT:
                    Bartender.IPC_WRITER.write(new DisconnectMessage(x,y,z));
                    break;
                default:
                    break;
            }
        } catch (EOFException e) {}
    }

}
