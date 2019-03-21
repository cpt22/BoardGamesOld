package com.cptingle.BoardGames.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.cptingle.BoardGames.framework.Game;

public class RegionSerializer {

	private int x1, y1, z1, x2, y2, z2;
	private long width, height, length;
	
	private Material[][][] blocks;
	private byte[][][] data;
	
	public RegionSerializer(World world, Location p1, Location p2) {
		
		this.x1 = p1.getBlockX();
        this.y1 = p1.getBlockY();
        this.z1 = p1.getBlockZ();

        this.x2 = p2.getBlockX();
        this.y2 = p2.getBlockY();
        this.z2 = p2.getBlockZ();

        this.width  = (x2 - x1) + 1;
        this.height = (y2 - y1) + 1;
        this.length = (z2 - z1) + 1;

        int w = (int) width;
        int h = (int) height;
        int l = (int) length;
        
        this.blocks = new Material[w][h][l];
        this.data   = new byte[w][h][l];
	}
	
	public void serialize(Game game) {
		Serializer s = new Serializer(game);
		s.start();
	}
	
	public void deserialize(Game game) {
		Deserializer d = new Deserializer(game);
		d.start();
	}
	
	private class Serializer implements Runnable {
		private Game game;
		private long total;
		
		public Serializer(Game game) {
			this.game = game;
		}
		
		public void start() {
			game.setEnabled(false);
			
			total = 0;
			game.scheduleTask(this, 1);
		}
		
		@Override
        public void run() {
            int y = (int) (total / (width*length));
            int z = (int) ((total % (width*length)) / width);
            int x = (int) ((total % (width*length)) % width);
            
            long max = width*height*length;
            int amount = (int) Math.min(20, (max - total));
            
            for (int i = 0; i < amount; i++) {
                Block b = game.getWorld().getBlockAt(x,y,z);
                blocks[x][y][z] = b.getType();
                data[x][y][z]   = b.getData();

                x = (int) ((x+1) % width);
                y = (int) ((y+1) % height);
                z = (int) ((z+1) % length);
            }
            
            total += amount;
            
            if (total == max) {
                game.setEnabled(true);
                return;
            }
            
            game.scheduleTask(this, 1);
        }
	}
	
	private class Deserializer implements Runnable
    {
        private Game game;
        private long total;
        
        public Deserializer(Game game) {
            this.game = game;
        }
        
        public void start() {
            // Disable the arena while serializing.
            game.setEnabled(false);
            
            // Start serializing!
            total = 0;
            game.scheduleTask(this, 1);
        }
        
        @Override
        public void run() {
            int y = (int) (total / (width*length));
            int z = (int) ((total % (width*length)) / width);
            int x = (int) ((total % (width*length)) % width);
            
            long max = width*height*length;
            int amount = (int) Math.min(20, (max - total));
            
            for (int i = 0; i < 20; i++) {
                Block b = game.getWorld().getBlockAt(x,y,z);
                b.setType(blocks[x][y][z]);
            }
            
            total += amount;
            
            if (total == max) {
                game.setEnabled(true);
                return;
            }
            
            game.scheduleTask(this, 1);
        }
    }
}
