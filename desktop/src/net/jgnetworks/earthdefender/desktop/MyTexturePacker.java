package net.jgnetworks.earthdefender.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class MyTexturePacker {
    public static void main (String[] args) throws Exception {
       TexturePacker.process("E:/coding/EarthDefender/android/assets/player/playerShip/", 
       		"E:/coding/EarthDefender/android/assets/player/playerShip/", "playerShipPack");
    }
}
