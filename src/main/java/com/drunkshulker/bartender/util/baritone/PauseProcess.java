package com.drunkshulker.bartender.util.baritone;

import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import com.drunkshulker.bartender.client.module.Aura;
import com.drunkshulker.bartender.client.module.AutoEat;
import com.drunkshulker.bartender.client.module.SafeTotemSwap;

public class PauseProcess implements IBaritoneProcess {

    @Override
    public boolean isActive() {
        return AutoEat.eating|| Aura.creeperTarget!=null;
    }

    @Override
    public PathingCommand onTick(boolean b, boolean b1) {
        return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void onLostControl() {}

    @Override
    public String displayName0() {
        return "Paused";
    }
}
