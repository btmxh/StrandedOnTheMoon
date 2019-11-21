import codinggame.map.MapTile;

public void main() {
	int direction = UP;
	while(true) {
		int[] off = getOffset(direction);
		MapTile tile = getRadar().getTileAt(getX() + off[0], getY() + off[1]);
		if(tile != null? tile.isSolid():false) {
			 direction++;
			 direction %= 4;
		}
		go(1, direction);
	}
}

private int[] getOffset(final int direction) {
	switch (direction) {
		case UP: return new int[]{0, 1};
		case DOWN: return new int[]{0, -1};
		case LEFT: return new int[]{-1, 0};
		case RIGHT: return new int[]{1, 0};
	}
	return null;
}
